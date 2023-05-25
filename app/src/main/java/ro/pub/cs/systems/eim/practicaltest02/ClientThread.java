package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String coin;
    private final TextView bitcoinTextView;

    private Socket socket;

    public ClientThread(String address, int port, String coin, TextView bitcoinTextView) {
        this.address = address;
        this.port = port;
        this.coin = coin;
        this.bitcoinTextView = bitcoinTextView;
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the city and information type to the server
            printWriter.println(coin);
            printWriter.flush();
            String bitcointInformation;

            // reads the weather information from the server
            while ((bitcointInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = bitcointInformation;

                // updates the UI with the weather information. This is done using postt() method to ensure it is executed on UI thread
                bitcoinTextView.post(() -> bitcoinTextView.setText(finalizedWeateherInformation));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
