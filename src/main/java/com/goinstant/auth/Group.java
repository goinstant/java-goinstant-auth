package com.goinstant.auth;

import java.util.Map;

public interface Group {
    public String getID();
    public String getDisplayName();
    public Map<String,Object> getCustomClaims();
}
