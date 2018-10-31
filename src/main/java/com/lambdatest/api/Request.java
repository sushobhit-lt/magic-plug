package com.lambdatest.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.codec.binary.Base64;

@Deprecated
public class Request
{
  String username = null;
  String password = null;
  boolean useProxy = false;
  String proxyUrl;
  int proxyPort;
  boolean useProxyCredentials = false;
  String proxyUsername;
  String proxyPassword;
  //private String requestURL = "https://crossbrowsertesting.com/api/v3/";
  private String requestURL = "https://dev-ml.lambdatest.com/api/v1/capability/";
  
  Request(String path, String username, String password)
  {
    this.username = username;
    this.password = password;
    
    requestURL += path;
  }
  
  public Request(String path)
  {
    requestURL += path;
  }
  
  public void setProxy(String url, int port)
  {
    proxyUrl = url;
    proxyPort = port;
    useProxy = true;
  }
  
  public void setProxyCredentials(String username, String password)
  {
    proxyUsername = username;
    proxyPassword = password;
    useProxyCredentials = true;
  }
  
  public String get(String urlStr)
  {
    try
    {
      String requestString = requestURL + urlStr;
      URL url = new URL(requestString);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      if (useProxy)
      {
        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
        conn = (HttpURLConnection)url.openConnection(proxy);
        if (useProxyCredentials) {
          Authenticator.setDefault(new SimpleAuthenticator(proxyUsername, proxyPassword));
        }
      }
      conn.setRequestMethod("GET");
      if ((username != null) && (password != null))
      {
        String userpassEncoding = Base64.encodeBase64String((username + ":" + password).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + userpassEncoding);
      }
      if (conn.getResponseCode() != 200) {
        throw new IOException(conn.getResponseMessage());
      }
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }
      rd.close();
      conn.disconnect();
      return sb.toString();
    }
    catch (IOException ioe) {}
    return "";
  }
  
  public String get(String urlStr, Map<String, String> params)
  {
    urlStr = urlStr + "?";
    int index = 1;
    try
    {
      for (Entry<String, String> entry : params.entrySet())
      {
        urlStr = urlStr + (String)entry.getKey() + "=" + URLEncoder.encode((String)entry.getValue(), "UTF-8");
        if (index < params.size()) {
          urlStr = urlStr + "&";
        }
        index++;
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException1) {}
    return get(urlStr);
  }
  
  private String doRequestWithFormParams(String method, String urlStr, Map<String, Object> params)
  {
    String urlParameters = "";
    try
    {
      if ((params != null) && (!params.isEmpty())) {
        for (Entry<String, Object> entry : params.entrySet())
        {
          String key = (String)entry.getKey();
          Object value = entry.getValue();
          if (!urlParameters.isEmpty()) {
            urlParameters = urlParameters + "&";
          }
          if ((value instanceof Collection)) {
            for (Object listValue : (Collection)value)
            {
              if (!urlParameters.isEmpty()) {
                urlParameters = urlParameters + "&";
              }
              urlParameters = urlParameters + key + "=" + listValue;
            }
          } else {
            urlParameters = urlParameters + key + "=" + value;
          }
        }
      }
      byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
      URL url = new URL(requestURL + urlStr);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      if (useProxy)
      {
        Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
        conn = (HttpURLConnection)url.openConnection(proxy);
        if (useProxyCredentials) {
          Authenticator.setDefault(new SimpleAuthenticator(proxyUsername, proxyPassword));
        }
      }
      conn.setRequestMethod(method);
      if ((username != null) && (password != null))
      {
        String userpassEncoding = Base64.encodeBase64String((username + ":" + password).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + userpassEncoding);
      }
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("charset", "utf-8");
      conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
      
      wr.write(postData);
      wr.flush();
      wr.close();
      if (conn.getResponseCode() != 200) {
        throw new IOException(conn.getResponseMessage());
      }
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }
      rd.close();
      conn.disconnect();
      return sb.toString();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
    return "";
  }
  
  public String post(String urlStr, Map<String, String> params)
  {
    return doRequestWithFormParams("POST", urlStr, Collections.unmodifiableMap(params));
  }
  
  public String post(String urlStr, Map<String, Object> params, boolean containsLists)
  {
    String url = requestURL + urlStr;
    return doRequestWithFormParams("POST", urlStr, params);
  }
  
  public String put(String urlStr, Map<String, String> params)
  {
    return doRequestWithFormParams("PUT", urlStr, Collections.unmodifiableMap(params));
  }
  
  public String delete(String urlStr)
  {
    return delete(urlStr, null, false);
  }
  
  public String delete(String urlStr, Map<String, String> params)
  {
    return doRequestWithFormParams("DELETE", urlStr, Collections.unmodifiableMap(params));
  }
  
  public String delete(String urlStr, Map<String, Object> params, boolean containsLists)
  {
    return doRequestWithFormParams("DELETE", urlStr, params);
  }
}

/* Location:
 * Qualified Name:     com.crossbrowsertesting.api.Request
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */