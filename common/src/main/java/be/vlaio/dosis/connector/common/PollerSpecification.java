package be.vlaio.dosis.connector.common;

public class PollerSpecification {
    private String name;
    private String url;
    private int itemLimit;

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

    public PollerSpecification(String name, String url, int itemLimit) {
        this.name = name;
        this.url = url;
        this.itemLimit = itemLimit;
    }

    public PollerSpecification() {
    }
}
