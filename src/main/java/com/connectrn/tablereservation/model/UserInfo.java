package com.connectrn.tablereservation.model;

import static java.util.Objects.requireNonNull;

public class UserInfo {

    public static final int MAX_NAME_LENGTH = 128;

    private final String name;
    private final boolean admin;

    public UserInfo(String name, boolean admin) {
        this.name = requireNonNull(name, "name is null");
        this.admin = admin;
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("name length exceeds " + MAX_NAME_LENGTH);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", admin=" + admin +
                '}';
    }

}