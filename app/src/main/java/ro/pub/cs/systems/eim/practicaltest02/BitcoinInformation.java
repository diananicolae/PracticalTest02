package ro.pub.cs.systems.eim.practicaltest02;

public class BitcoinInformation {
    private final String updated;
    private final String usdRate;
    private final String eurRate;
    private final long timeStamp;

    public BitcoinInformation(String updated, String usdRate, String eurRate, long timeStamp) {
        this.updated = updated;
        this.usdRate = usdRate;
        this.eurRate = eurRate;
        this.timeStamp = timeStamp;
    }

    public String getUpdated() {
        return updated;
    }

    public String getUsdRate() {
        return usdRate;
    }

    public String getEurRate() {
        return eurRate;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "BitcoinInformation{" + "updated='" + updated + '\'' + ", usdRate='" + usdRate + '\'' + ", eurRate='" + eurRate + '\'' + '}';
    }
}
