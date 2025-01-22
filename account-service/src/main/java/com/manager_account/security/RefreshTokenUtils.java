package com.manager_account.security;

import java.util.UUID;

public class RefreshTokenUtils {

    public static String createRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
