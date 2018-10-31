package com.lambdatest.api;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;

public class UnirestRequest
{
  String username = null;
  String password = null;
  boolean useProxy = false;
  String proxyUrl;
  int proxyPort;
  boolean useProxyCredentials = false;
  String proxyUsername;
  String proxyPassword;
  private String requestURL = "https://crossbrowsertesting.com/api/v3/";
  private HttpRequest req;
  
  UnirestRequest(String path, String username, String password)
  {
    this.username = username;
    this.password = password;
    init(path);
  }
  
  public UnirestRequest(String path)
  {
    init(path);
  }
  
  private void init(String path)
  {
    requestURL += path;
    
    RequestConfig globalConfig = RequestConfig.custom().setCookieSpec("ignoreCookies").build();
    HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
    Unirest.setHttpClient(httpclient);
  }
  
  public void setProxy(String url, int port)
  {
    proxyUrl = url;
    proxyPort = port;
    useProxy = true;
    Unirest.setProxy(new HttpHost(proxyUrl, proxyPort));
  }
  
  public void setProxyCredentials(String username, String password)
  {
    proxyUsername = username;
    proxyPassword = password;
    useProxyCredentials = true;
    
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
    clientBuilder.useSystemProperties();
    clientBuilder.setProxy(new HttpHost(proxyUrl, proxyPort));
    clientBuilder.setDefaultCredentialsProvider(credsProvider);
    clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
    
    //Lookup<AuthSchemeProvider> authProviders = RegistryBuilder.create().register("Basic", new BasicSchemeFactory()).build();
    Lookup<AuthSchemeProvider> authProviders = RegistryBuilder.<AuthSchemeProvider>create()
            .register("BASIC", new BasicSchemeFactory())
            .build();
    clientBuilder.setDefaultAuthSchemeRegistry(authProviders);
    Unirest.setHttpClient(clientBuilder.build());
  }
  
  private String doRequest(HttpRequest req, Map<String, Object> params)
  {
    if ((username != null) && (password != null)) {
      req = req.basicAuth(username, password);
    }
    try
    {
      if (params != null)
      {
        Entry<String, Object> entry;
        String listValue;
        if (req.getHttpMethod() == HttpMethod.GET)
        {
          for (Iterator localIterator1 = params.entrySet().iterator(); localIterator1.hasNext();)
          {
            entry = (Entry)localIterator1.next();
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            Iterator localIterator2;
            if ((value instanceof Collection)) {
              for (localIterator2 = ((Collection)value).iterator(); localIterator2.hasNext();)
              {
                listValue = (String)localIterator2.next();
                req = req.queryString(key, listValue);
              }
            } else {
              req = req.queryString(key, value);
            }
          }
          return ((JsonNode)req.asJson().getBody()).toString();
        }
        MultipartBody requestWithBody = null;
        for (Entry<String, Object> entry1 : params.entrySet())
        {
          String key = (String)entry1.getKey();
          Object value = entry1.getValue();
          if ((value instanceof Collection)) {
            for (Object listValue1 : (Collection)value) {
              if (requestWithBody == null) {
                requestWithBody = ((HttpRequestWithBody)req).field(key, listValue1);
              } else {
                requestWithBody = requestWithBody.field(key, listValue1);
              }
            }
          } else if (requestWithBody == null) {
            requestWithBody = ((HttpRequestWithBody)req).field(key, value);
          } else {
            requestWithBody = requestWithBody.field(key, value);
          }
        }
        return ((JsonNode)requestWithBody.asJson().getBody()).toString();
      }
      return ((JsonNode)req.asJson().getBody()).toString();
    }
    catch (UnirestException e) {}
    return "";
  }
  
  public String get(String urlStr)
  {
    return get(urlStr, null, false);
  }
  
  @Deprecated
  public String get(String urlStr, Map<String, String> params)
  {
    String url = requestURL + urlStr;
    HttpRequest req = Unirest.get(url);
    return doRequest(req, Collections.unmodifiableMap(params));
  }
  
  public String get(String urlStr, Map<String, Object> params, boolean containsLists)
  {
    String url = requestURL + urlStr;
    HttpRequest req = Unirest.get(url);
    return doRequest(req, params);
  }
  
  public String post(String urlStr)
  {
    return post(urlStr, null, false);
  }
  
  @Deprecated
  public String post(String urlStr, Map<String, String> params)
  {
    String url = requestURL + urlStr;
    HttpRequestWithBody req = Unirest.post(url);
    return doRequest(req, Collections.unmodifiableMap(params));
  }
  
  public String post(String urlStr, Map<String, Object> params, boolean containsLists)
  {
    String url = requestURL + urlStr;
    HttpRequestWithBody req = Unirest.post(url);
    return doRequest(req, params);
  }
  
  public String put(String urlStr)
  {
    return put(urlStr, null, false);
  }
  
  @Deprecated
  public String put(String urlStr, Map<String, String> params)
  {
    String url = requestURL + urlStr;
    HttpRequestWithBody req = Unirest.put(url);
    return doRequest(req, Collections.unmodifiableMap(params));
  }
  
  public String put(String urlStr, Map<String, Object> params, boolean containsLists)
  {
    String url = requestURL + urlStr;
    HttpRequestWithBody req = Unirest.put(url);
    return doRequest(req, params);
  }
  
  public String delete(String urlStr)
  {
    return delete(urlStr, null, false);
  }
  
  @Deprecated
  public String delete(String urlStr, Map<String, String> params)
  {
    String url = requestURL + urlStr;
    HttpRequestWithBody req = Unirest.delete(url);
    return doRequest(req, Collections.unmodifiableMap(params));
  }
  
  public String delete(String urlStr, Map<String, Object> params, boolean containsLists)
  {
    String url = requestURL + urlStr;
    HttpRequestWithBody req = Unirest.delete(url);
    return doRequest(req, params);
  }
}

/* Location:
 * Qualified Name:     com.lambdatest.api.UnirestRequest
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */