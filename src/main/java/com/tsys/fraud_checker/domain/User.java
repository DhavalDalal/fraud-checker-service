package com.tsys.fraud_checker.domain;

public class User {
    public final String appName;
    public final String appId;
    public final String password;

    public User(String appName, String appId, String password) {
        this.appName = appName;
        this.appId = appId;
        this.password = password;
    }
}
