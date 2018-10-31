package com.lambdatest.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class PluginCheck
  extends ApiFactory
{
  private String contributer;
  private String contributerVersion;
  private String pluginVersion;
  
  public PluginCheck(String contributer, String contributerVersion, String pluginVersion)
  {
    super("plugins");
    this.contributer = contributer;
    this.contributerVersion = contributerVersion;
    this.pluginVersion = pluginVersion;
  }
  
  public void init() {}
  
  public boolean needToUpgradeFake()
    throws IOException
  {
    return false;
  }
  
  public boolean needToUpgrade()
    throws IOException
  {
    Map<String, String> params = new HashMap();
    params.put("contributer", contributer);
    params.put("contributer_version", contributerVersion);
    params.put("plugin_version", pluginVersion);
    
    String json = req.get("", params);
    JSONObject jo = new JSONObject(json);
    System.out.println(json);
    return !jo.getBoolean("safe");
  }
}

/* Location:
 * Qualified Name:     com.crossbrowsertesting.api.PluginCheck
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */