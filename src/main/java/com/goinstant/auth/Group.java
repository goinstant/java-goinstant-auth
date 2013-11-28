package com.goinstant.auth;

import java.util.Map;

/**
 * Groups are used by GoInstant to enforce ACLs.
 *
 * For more information on groups, please see the GoInstant guide on <a href="https://developer.goinstant.com/v1/security_and_auth/guides/creating_and_managing_acl.html">Creating and Managing ACLs</a>
 *
 * See {@link PlainGroup} for a concrete implementation.
 */
public interface Group {
    /**
     * Unique ID for this group.
     */
    public String getID();

    /**
     * Display name for this group.
     */
    public String getDisplayName();

    /**
     * Any additional properties about this group that should be included in the token.
     */
    public Map<String,Object> getCustomClaims();
}
