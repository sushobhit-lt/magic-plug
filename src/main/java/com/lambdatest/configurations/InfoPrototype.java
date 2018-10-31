package com.lambdatest.configurations;

abstract class InfoPrototype
{
  private String name;
  //private String api_name;
  //private String device = "";
  
  public InfoPrototype(String name)
  {
    //this.api_name = api_name;
    this.name = name;
  }
  
//  public InfoPrototype(String name)
//  {
//    //this.api_name = api_name;
//    this.name = name;
//    //this.device = device;
//  }
  
  public String getName()
  {
    return name;
  }
  
// public String getApiName()
//  {
//    return api_name;
//  }
  
  public boolean isMobile()
  {
    //if ((device != null) && (device.equals("mobile"))) {
      return true;
    //}
    //return false;
  }

  @Override
  public String toString() {
    return "InfoPrototype{" +
            "name='" + name + '\'' +
            //", api_name='" + api_name + '\'' +
           // ", device='" + device + '\'' +
            '}';
  }
}

/* Location:
 * Qualified Name:     com.lambdatest.configurations.InfoPrototype
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */