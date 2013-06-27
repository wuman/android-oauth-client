/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wuman.android.auth.oauth2.store;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Beta;
import com.google.api.client.util.Key;
import com.wuman.android.auth.oauth.OAuthHmacCredential;

/**
 * {@link Beta} <br/>
 * Persisted credential implementation to be used exclusively with {@link FileCredentialStore}.
 *
 * @author Rafael Naufal
 * @since 1.11
 */
@Beta
public class FilePersistedCredential extends GenericJson {

  /** Access token or {@code null} for none. */
  @Key("access_token")
  private String accessToken;

  /** Refresh token {@code null} for none. */
  @Key("refresh_token")
  private String refreshToken;

  /** Expiration time in milliseconds {@code null} for none. */
  @Key("expiration_time_millis")
  private Long expirationTimeMillis;
  
  @Key("token_shared_secret")
  private String tokenSharedSecret;
  
  @Key("consumer_key")
  private String consumerKey;
  
  @Key("shared_secret")
  private String sharedSecret;

  public String getAccessToken() {
      return (String) get("accessToken");
  }

  public String getRefreshToken() {
      return (String) get("refreshToken");
  }

  public Long getExpirationTimeMilliseconds() {
      return (Long) get("expirationTimeMillis");
  }
  
  public String getTokenSharedSecret() {
      return (String) get("tokenSharedSecret");
  }
  
  public String getConsumerKey() {
      return (String) get("consumerKey");
  }
  
  public String getSharedSecret() {
      return (String) get("sharedSecret");
  }

  public void setAccessToken(String accessToken) {
      set("accessToken", accessToken);
  }

  public void setRefreshToken(String refreshToken) {
      set("refreshToken", refreshToken);
  }

  public void setExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
      set("expirationTimeMillis", expirationTimeMilliseconds);
  }
  
  public void setTokenSharedSecret(String tokenSharedSecret) {
      set("tokenSharedSecret", tokenSharedSecret);
  }
  
  public void setConsumerKey(String consumerKey) {
      set("consumerKey", consumerKey);
  }
  
  public void setSharedSecret(String sharedSecret) {
      set("sharedSecret", sharedSecret);
  }

  /**
   * Store information from the credential.
   *
   * @param credential credential whose {@link Credential#getAccessToken access token},
   *        {@link Credential#getRefreshToken refresh token}, and
   *        {@link Credential#getExpirationTimeMilliseconds expiration time} need to be stored
   */
  void store(Credential credential) {
    accessToken = credential.getAccessToken();
    refreshToken = credential.getRefreshToken();
    expirationTimeMillis = credential.getExpirationTimeMilliseconds();
    if (credential instanceof OAuthHmacCredential) {
        OAuthHmacCredential oauth10aCredential = (OAuthHmacCredential) credential;
        tokenSharedSecret = oauth10aCredential.getTokenSharedSecret();
        consumerKey = oauth10aCredential.getConsumerKey();
        sharedSecret = oauth10aCredential.getSharedSecret();
    }
  }

  /**
   * @param credential credential whose {@link Credential#setAccessToken access token},
   *        {@link Credential#setRefreshToken refresh token}, and
   *        {@link Credential#setExpirationTimeMilliseconds expiration time} need to be set if the
   *        credential already exists in storage
   */
  void load(Credential credential) {
    credential.setAccessToken(accessToken);
    credential.setRefreshToken(refreshToken);
    credential.setExpirationTimeMilliseconds(expirationTimeMillis);
    if (credential instanceof OAuthHmacCredential) {
        OAuthHmacCredential oauth10aCredential = (OAuthHmacCredential) credential;
        oauth10aCredential.setTokenSharedSecret(tokenSharedSecret);
        oauth10aCredential.setConsumerKey(consumerKey);
        oauth10aCredential.setSharedSecret(sharedSecret);
    }
  }

  @Override
  public FilePersistedCredential set(String fieldName, Object value) {
    return (FilePersistedCredential) super.set(fieldName, value);
  }

  @Override
  public FilePersistedCredential clone() {
    return (FilePersistedCredential) super.clone();
  }
}
