package com.chalkim.orinote.dao;

import com.chalkim.orinote.model.OAuth2UserAccount;

public interface OAuth2UserAccountDao {
    OAuth2UserAccount findByProviderAndId(String provider, String idFromProvider);
    void save(OAuth2UserAccount account);
}
