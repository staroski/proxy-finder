package br.com.staroski.proxy;

public final class ProxySetting implements Comparable<ProxySetting> {

    public final String name;
    public final String host;
    public final int port;

    ProxySetting(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public int compareTo(ProxySetting other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ProxySetting) {
            ProxySetting that = (ProxySetting) obj;
            return this.toString().equals(that.toString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + toString().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name + " = " + host + ":" + port;
    }
}
