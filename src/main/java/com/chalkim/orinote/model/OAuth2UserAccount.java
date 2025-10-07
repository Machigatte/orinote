package com.chalkim.orinote.model;

public class OAuth2UserAccount {
    private String provider;
    private String idFromProvider;
    private Long userId;

    public OAuth2UserAccount() {}
    public OAuth2UserAccount(String provider, String idFromProvider, Long userId) {
        this.provider = provider;
        this.idFromProvider = idFromProvider;
        this.userId = userId;
    }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getIdFromProvider() { return idFromProvider; }
    public void setIdFromProvider(String idFromProvider) { this.idFromProvider = idFromProvider; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
