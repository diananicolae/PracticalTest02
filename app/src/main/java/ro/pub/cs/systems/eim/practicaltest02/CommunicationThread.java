package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client");

            // Read the city and informationType values sent by the client
            String coin = bufferedReader.readLine();
            if (coin == null || coin.isEmpty() ) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client");
                return;
            }

            // It checks whether the serverThread has already received the weather forecast information for the given city.
            HashMap<String, BitcoinInformation> data = serverThread.getData();
            BitcoinInformation bitcoinInformation;

            if (data.containsKey(coin) && (System.currentTimeMillis() - data.get(coin).getTimeStamp() < 1000)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                bitcoinInformation = data.get(coin);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";

                // make the HTTP request to the web service
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);

                // Parse the page source code into a JSONObject and extract the needed information
                JSONObject content = new JSONObject(pageSourceCode);
                String updated = content.getJSONObject(Constants.TIME).getString(Constants.UPDATED);
                String usdRate = content.getJSONObject(Constants.BPI).getJSONObject(Constants.USD).getString(Constants.RATE);
                String eurRate = content.getJSONObject(Constants.BPI).getJSONObject(Constants.EUR).getString(Constants.RATE);


                // Create a WeatherForecastInformation object with the information extracted from the JSONObject
                bitcoinInformation = new BitcoinInformation(updated, usdRate, eurRate, System.currentTimeMillis());

                // Cache the information for the given city
                serverThread.setData(coin, bitcoinInformation);
            }

            if (bitcoinInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Bitcoin Information is null!");
                return;
            }

            // Send the information back to the client
            String result;
            switch (coin) {
                case Constants.ALL:
                    result = bitcoinInformation.toString();
                    break;
                case Constants.USD:
                    result = bitcoinInformation.getUsdRate() + " updated: " + bitcoinInformation.getUpdated();
                    break;
                case Constants.EUR:
                    result = bitcoinInformation.getEurRate() + " updated: " + bitcoinInformation.getUpdated();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type";
            }

            // Send the result back to the client
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
