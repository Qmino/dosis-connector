package be.vlaio.dosis.connector.common;

public class PollerSpecification {
    private String name;
    private String url;
    private int itemLimit;
    private int backoffBase = 10;
    private int backoffExponent = 3;
    private int backoffMaxRetries = 10;

    public PollerSpecification(String name, String url, int itemLimit, int backoffBase,
                               int backoffExponent, int backoffMaxRetries) {
        this.name = name;
        this.url = url;
        this.itemLimit = itemLimit;
        this.backoffBase = backoffBase;
        this.backoffExponent = backoffExponent;
        this.backoffMaxRetries = backoffMaxRetries;
    }

    public PollerSpecification() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getItemLimit() {
        return itemLimit;
    }

    public void setItemLimit(int itemLimit) {
        this.itemLimit = itemLimit;
    }

    public int getBackoffMaxRetries() {
        return backoffMaxRetries;
    }

    public double getBackoffBase() {
        return backoffBase;
    }

    public int getBackoffExponent() {
        return backoffExponent;
    }

    public void setBackoffBase(int backoffBase) {
        this.backoffBase = backoffBase;
    }

    public void setBackoffExponent(int backoffExponent) {
        this.backoffExponent = backoffExponent;
    }

    public void setBackoffMaxRetries(int backoffMaxRetries) {
        this.backoffMaxRetries = backoffMaxRetries;
    }
}
