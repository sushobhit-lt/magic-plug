package com.lambdatest.configurations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatingSystem
  extends InfoPrototype
{
  @Deprecated
  public List<Resolution> resolutions = new ArrayList();
  @Deprecated
  public Map<String, Resolution> resolutions2 = new HashMap();
  public List<Browser> browsers = new ArrayList();
  public Map<String, Browser> browsers2 = new HashMap();
  private String platform;
  private String deviceName;
  private String platformVersion;
  private String platformName;

  public OperatingSystem(String name)
  {
    super(name);
  }
  
//  public OperatingSystem(String api_name, String name, String device, String platform)
//  {
//    super(name);
//    setPlatform(platform);
//  }
//
//  public OperatingSystem(String api_name, String name, String device, String deviceName, String platformVersion, String platformName)
//  {
//    super(name);
//    setDeviceName(deviceName);
//    setPlatformVersion(platformVersion);
//    setPlatformName(platformName);
//  }
  
  public String getPlatform()
  {
    if (!isMobile()) {
      return platform;
    }
    return "";
  }
  
  public String getDeviceName()
  {
    if (isMobile()) {
      return deviceName;
    }
    return "";
  }
  
  public String getPlatformVersion()
  {
    if (isMobile()) {
      return platformVersion;
    }
    return "";
  }
  
  public String getPlatformName()
  {
    if (isMobile()) {
      return platformName;
    }
    return "";
  }
  
  public void setPlatform(String platform)
  {
    if (!isMobile()) {
      this.platform = platform;
    }
  }
  
  public void setDeviceName(String deviceName)
  {
    if (isMobile()) {
      this.deviceName = deviceName;
    }
  }
  
  public void setPlatformName(String platformName)
  {
    if (isMobile()) {
      this.platformName = platformName;
    }
  }
  
  public void setPlatformVersion(String platformVersion)
  {
    if (isMobile()) {
      this.platformVersion = platformVersion;
    }
  }
    public void setBrowser(Browser browser)
    {
        this.browsers.add(browser);
     }
    public void setBrowser2(String browser,Browser boj)
    {
        this.browsers2.put(browser,boj);
    }
    public void setResolution(Resolution resolution )
    {
        this.resolutions.add(resolution);
    }
    public void setResolution2(String resName, Resolution resolution )
    {
        this.resolutions2.put(resName,resolution);
    }

    @Override
    public String toString() {
        return "OperatingSystem{" +
                "resolutions=" + resolutions +
                ", resolutions2=" + resolutions2 +
                ", browsers=" + browsers +
                ", browsers2=" + browsers2 +
                ", platform='" + platform + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", platformVersion='" + platformVersion + '\'' +
                ", platformName='" + platformName + '\'' +
                '}';
    }
}

/* Location:
 * Qualified Name:     com.lambdatest.configurations.OperatingSystem
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */