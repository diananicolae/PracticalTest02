package ro.pub.cs.systems.eim.practicaltest02;

public class BitcoinInformation {
    private final String updated;
    private final String usdRate;
    private final String eurRate;

    public BitcoinInformation(String updated, String usdRate, String eurRate) {
        this.updated = updated;
        this.usdRate = usdRate;
        this.eurRate = eurRate;
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

    @Override
    public String toString() {
        return "BitcoinInformation{" + "updated='" + updated + '\'' + ", usdRate='" + usdRate + '\'' + ", eurRate='" + eurRate + '\'' + '}';
    }
}
