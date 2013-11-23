package com.goinstant.auth;

import java.util.Set;
import java.util.Map;

import com.goinstant.auth.Group;

public interface User {
    public String getID();
    public String getDomain();
    public String getDisplayName();

    public Set<Group> getGroups();
    public Map<String,Object> getCustomClaims();
}
