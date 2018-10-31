package com.lambdatest.api;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

final class SimpleAuthenticator
  extends Authenticator
{
  private String username;
  private String password;
  
  SimpleAuthenticator(String username, String password)
  {
    this.username = username;
    this.password = password;
  }
  
  protected PasswordAuthentication getPasswordAuthtication()
  {
    return new PasswordAuthentication(username, password.toCharArray());
  }
}

/* Location:
 * Qualified Name:     com.lambdatest.api.SimpleAuthenticator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */