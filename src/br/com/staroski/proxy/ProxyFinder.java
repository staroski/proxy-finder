package br.com.staroski.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.UIManager;

public final class ProxyFinder {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            ProxyFinder finder = new ProxyFinder();
            ProxyFrame frame = new ProxyFrame(finder);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public ProxySetting[] find() throws IOException {
        Set<ProxySetting> proxies = new TreeSet<>();
        String[] dnsSufixes = findDnsSufixes();
        for (String dns : dnsSufixes) {
            proxies.addAll(findProxies(dns));
        }
        return proxies.toArray(new ProxySetting[proxies.size()]);
    }

    private String[] findDnsSufixes() throws UnknownHostException, SocketException {
        String hostName = InetAddress.getLocalHost().getHostName().toLowerCase();
        Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
        if (ifs == null) {
            return new String[0];
        }
        Set<String> sufixes = new TreeSet<>();
        for (NetworkInterface iF : Collections.list(ifs)) {
            if (!iF.isUp()) {
                continue;
            }
            for (InetAddress address : Collections.list(iF.getInetAddresses())) {
                if (address.isMulticastAddress()) {
                    continue;
                }
                String name = address.getHostName().toLowerCase();
                if (name.startsWith(hostName)) {
                    String dnsSuffix = name.substring(hostName.length());
                    if (dnsSuffix.startsWith(".")) {
                        sufixes.add(dnsSuffix);
                    }
                }
            }
        }
        return sufixes.toArray(new String[sufixes.size()]);
    }

    private Set<ProxySetting> findProxies(String dns) throws IOException {
        Set<ProxySetting> proxies = new TreeSet<>();
        List<String> lines = readWPAD("http://wpad" + dns + "/wpad.dat");
        for (String line : lines) {
            ProxySetting proxy = validateProxy(line);
            if (proxy != null) {
                proxies.add(proxy);
            }
        }
        return proxies;
    }

    // reads the lines of the specified Web Proxy Auto Discovery file
    private List<String> readWPAD(String path) throws MalformedURLException, IOException {
//        System.out.println("reading " + path);
        List<String> lines = new ArrayList<>();
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        InputStream input = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    private ProxySetting validateProxy(String line) {
        String lower = line.toLowerCase();
        if (lower.contains("var ") && lower.contains("proxy ")) {
            String localhost = "localhost";
            String domain = "((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}";
            String ip = "(([0-9]{1,3}\\.){3})[0-9]{1,3}";
            String port = "[0-9]{1,5}";
            String regex = "(" + localhost + "|" + domain + "|" + ip + ")" + ":" + port;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                int start = line.indexOf("var") + 3;
                int end = line.indexOf("=", start);
                String name = line.substring(start, end).trim();
                String[] hostPort = matcher.group().split(":");
                return new ProxySetting(name, hostPort[0], Integer.parseInt(hostPort[1]));
            }
        }
        return null;
    }
}
