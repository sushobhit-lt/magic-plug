package com.lambdatest.api;

public abstract class ApiFactory
{
  protected Request req;
  private String url;
  
  public ApiFactory(String url)
  {
    this.url = url;
    req = new Request(url);
  }
  
  public ApiFactory(String url, String username, String password)
  {
    this.url = url;
    req = new Request(url, username, password);
  }
  
  public void setRequest(String username, String apikey)
  {
    req = new Request(url, username, apikey);
  }
  
  public Request getRequest()
  {
    return req;
  }
  
  public boolean useProxy()
  {
    return req.useProxy;
  }
  
  public int proxyPort()
  {
    return req.proxyPort;
  }
  
  public String proxyUrl()
  {
    return req.proxyUrl;
  }
  
  public boolean useProxyCredentials()
  {
    return req.useProxyCredentials;
  }
  
  public String proxyUsername()
  {
    return req.proxyUsername;
  }
  
  public String proxyPassword()
  {
    return req.proxyPassword;
  }
  
  public abstract void init();
}

/* Location:
 * Qualified Name:     com.crossbrowsertesting.api.ApiFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */