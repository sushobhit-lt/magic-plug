package lt;

import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;


public class Utils {
    public boolean connectionSuccessful = false;

    public String Authenticate(String requestURL, String username, String apikey) {
        String json = "";
        JSONObject auth = new JSONObject();
        auth.put("username", username);
        auth.put("token", apikey);
        json = auth.toString();
        StringBuffer jsonString = new StringBuffer();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("cache-control", "no-cache");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(json);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            //throw new RuntimeException(e.getMessage());
        }
        return jsonString.toString();
    }

    public String getBuild(String requestUrl) {
        System.out.print(requestUrl);
        try {
            String requestString = requestUrl;
            URL url = new URL(requestString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
//	      if (conn.getResponseCode() != 200) {
//	        throw new IOException(conn.getResponseMessage());

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            return sb.toString();
        } catch (IOException ioe) {
        }

        return "";

    }

    public String get(String urlStr, Map<String, String> params) {
        urlStr = urlStr + "?";
        int index = 1;
        try {
            for (Entry<String, String> entry : params.entrySet()) {
                urlStr = urlStr + (String) entry.getKey() + "=" + URLEncoder.encode((String) entry.getValue(), "UTF-8");
                if (index < params.size()) {
                    urlStr = urlStr + "&";
                }
                index++;
            }
        } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
        }
        return getBuild(urlStr);
    }

    public boolean IsValidCredential(String username, String apikey) {
        int userId = 0;
        String email = "";
        String json = "";
        json = Authenticate(Constants.authUrl, username, apikey);
        System.out.print(json);
        try {
            JSONObject results = new JSONObject(json);
            userId = results.getInt("id");
            email = results.getString("email");
            if ((userId > 0) && (email != null) && (!email.equals(""))) {
                connectionSuccessful = true;
                return true;
            }
            connectionSuccessful = false;
            return false;
        } catch (JSONException je) {
            connectionSuccessful = false;
        }
        return false;
    }

}
