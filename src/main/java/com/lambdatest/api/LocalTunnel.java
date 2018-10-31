package com.lambdatest.api;

import com.fizzed.jne.JNE;
import com.fizzed.jne.Options;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocalTunnel
  extends ApiFactory
{
  public boolean isTunnelRunning = false;
  @Deprecated
  public boolean jenkinsStartedTunnel = false;
  public boolean pluginStartedTheTunnel = false;
  public Process tunnelProcess = null;
  public int tunnelID = -1;
  private String username;
  private String apikey;
  private String tunnelname = "";
  private static final Logger log = Logger.getLogger(LocalTunnel.class.getName());
  private final String TUNNEL_VERSION = "v0.9.3";
  private final String NODE_VERSION = "v6.11.2";
  
  public LocalTunnel(String username, String apikey, String tunnelname)
  {
    super("tunnels", username, apikey);
    this.tunnelname = tunnelname;
    setupClass(username, apikey);
    init();
  }
  
  public LocalTunnel(String username, String apikey)
  {
    super("tunnels", username, apikey);
    setupClass(username, apikey);
    init();
  }
  
  private void setupClass(String username, String apikey)
  {
    this.username = username;
    this.apikey = apikey;
  }
  
  public void init()
  {
    queryTunnel();
  }
  
  @Deprecated
  public boolean queryTunnelOld()
    throws JSONException
  {
    String json = "";
    try
    {
      json = req.get("?num=1&active=true");
      JSONObject res = new JSONObject(json);
      JSONArray tunnels = res.getJSONArray("tunnels");
      boolean isActive = false;
      for (int i = 0; i < tunnels.length(); i++)
      {
        JSONObject tunnel = tunnels.getJSONObject(i);
        tunnelID = tunnel.getInt("tunnel_id");
        isActive = tunnel.getBoolean("active");
      }
      isTunnelRunning = isActive;
      return isActive;
    }
    catch (JSONException jsone) {}
    return false;
  }
  
  public boolean queryTunnel()
  {
    if (tunnelID < 0)
    {
      if ((!tunnelname.equals("")) && (tunnelname != null)) {
        tunnelID = getTunnelID(tunnelname);
      } else {
        tunnelID = getTunnelID();
      }
      log.finer("tunnelId: " + tunnelID);
    }
    if (tunnelID > -1)
    {
      String json = "";
      json = req.get("/" + Integer.toString(tunnelID));
      log.finest(json);
      try
      {
        JSONObject res = new JSONObject(json);
        boolean isActive = res.getBoolean("active");
        log.finer("isActive: " + isActive);
        isTunnelRunning = isActive;
        return isActive;
      }
      catch (JSONException jsone)
      {
        log.fine("got jsonexception");
        return false;
      }
    }
    return false;
  }
  
  private int getTunnelID()
    throws JSONException
  {
    String json = "";
    json = req.get("?num=1&active=true");
    if (json.isEmpty()) {
      return -1;
    }
    try
    {
      JSONObject res = new JSONObject(json);
      JSONArray tunnels = res.getJSONArray("tunnels");
      JSONObject unnamedTunnel = null;
      for (int i = 0; i < tunnels.length(); i++)
      {
        JSONObject tunnel = tunnels.getJSONObject(i);
        if (tunnel.isNull("tunnel_name"))
        {
          unnamedTunnel = tunnel;
          break;
        }
      }
      if (unnamedTunnel != null) {
        return unnamedTunnel.getInt("tunnel_id");
      }
      return -1;
    }
    catch (JSONException jsone)
    {
      jsone.printStackTrace();
    }
    return -1;
  }
  
  private int getTunnelID(String tunnelname)
    throws JSONException
  {
    String json = "";
    json = req.get("?active=true");
    if (json.isEmpty()) {
      return -1;
    }
    try
    {
      JSONObject res = new JSONObject(json);
      JSONArray tunnels = res.getJSONArray("tunnels");
      JSONObject namedTunnel = null;
      for (int i = 0; i < tunnels.length(); i++)
      {
        JSONObject tunnel = tunnels.getJSONObject(i);
        if ((!tunnel.isNull("tunnel_name")) && (tunnel.getString("tunnel_name").equals(tunnelname)))
        {
          namedTunnel = tunnel;
          break;
        }
      }
      if (namedTunnel != null) {
        return namedTunnel.getInt("tunnel_id");
      }
      return -1;
    }
    catch (JSONException jsone)
    {
      jsone.printStackTrace();
    }
    return -1;
  }
  
  private String buildParamString(Map<String, String> params)
  {
    params.put(" --username ", username);
    params.put(" --authkey ", apikey);
    if ((tunnelname != null) && (!tunnelname.equals(""))) {
      params.put(" --tunnelname ", tunnelname);
    }
    if (req.useProxy)
    {
      params.put(" --proxyPort ", Integer.toString(req.proxyPort));
      String proxyUrl = req.proxyUrl;
      if (req.useProxyCredentials) {
        proxyUrl = req.proxyUsername + ":" + req.proxyPassword + "@" + proxyUrl;
      }
      params.put(" --proxyIp ", proxyUrl);
    }
    String tunnelParams = "";
    for (Entry<String, String> entry : params.entrySet()) {
      tunnelParams = tunnelParams + (String)entry.getKey() + (String)entry.getValue();
    }
    return tunnelParams;
  }
  
  private void run(ProcessBuilder tunnelProcessBuilder)
    throws IOException
  {
    log.fine("starting local tunnel");
    tunnelProcess = tunnelProcessBuilder.start();
    jenkinsStartedTunnel = true;
    pluginStartedTheTunnel = true;
  }
  
  private void start(String tunnelLaunchCommand, Map<String, String> params)
    throws IOException
  {
    String tunnelParams = buildParamString(params);
    ArrayList<String> tunnelCommand = new ArrayList();
    tunnelCommand.add(tunnelLaunchCommand);
    tunnelCommand.addAll(Arrays.asList(tunnelParams.split("\\s+")));
    log.finer("tunnel launch command: \"" + tunnelLaunchCommand + tunnelParams + "\"");
    run(new ProcessBuilder(new String[0]).command(tunnelCommand));
  }
  
  private void start(String node, String cmd_start_js, Map<String, String> params)
    throws IOException
  {
    String tunnelParams = buildParamString(params);
    ArrayList<String> tunnelCommand = new ArrayList();
    tunnelCommand.add(node);
    tunnelCommand.add(cmd_start_js);
    tunnelCommand.addAll(Arrays.asList(tunnelParams.split("\\s+")));
    log.finer("tunnel launch command: \"" + node + " " + cmd_start_js + " " + tunnelParams + "\"");
    run(new ProcessBuilder(new String[0]).command(tunnelCommand));
  }
  
  public void start(String localTunnelPath)
    throws IOException
  {
    start(localTunnelPath, new HashMap());
  }
  
  @Deprecated
  public void start()
    throws IOException
  {
    start("cbt_tunnels", new HashMap());
  }
  
  public void start(boolean useBinary)
    throws IOException, URISyntaxException
  {
    start(useBinary, true);
  }
  
  public void start(boolean useBinary, boolean bypass)
    throws URISyntaxException, IOException
  {
    HashMap params = new HashMap();
    params.put(" --bypass ", Boolean.toString(bypass));
    if (useBinary)
    {
      Options binarySearchOptions = new Options();
      String tunnelPath = "/cbt_tunnels/v0.9.3";
      binarySearchOptions = binarySearchOptions.setResourcePrefix(tunnelPath.toString());
      File binary = JNE.requireExecutable("cbt_tunnels", binarySearchOptions);
      start(binary.getPath(), params);
    }
    else
    {
      start("cbt_tunnels", params);
    }
  }
  
  public void stop()
    throws IOException, InterruptedException
  {
    queryTunnel();
    log.fine("about to kill local tunnel");
    
    String json = req.delete("/" + Integer.toString(tunnelID));
    if ((pluginStartedTheTunnel) && (tunnelProcess != null)) {
      tunnelProcess.destroy();
    }
    log.fine("done killing local tunnel");
  }
}

/* Location:
 * Qualified Name:     com.crossbrowsertesting.api.LocalTunnel
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */