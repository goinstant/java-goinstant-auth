package com.goinstant.auth;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.goinstant.auth.User;
import com.goinstant.auth.Group;
import com.goinstant.auth.PlainThing;

public class PlainUser extends PlainThing implements User  {

    protected String domain;
    protected Set<Group> groups;

    static Set<Group> NO_GROUPS = Collections.emptySet();

    /**
     * Create a plain-old-User object.
     *
     * @param id Permanent identifier for this user.
     * @param domain The domain name in which the user and group IDs are defined.
     * @param displayName The visible name for this user (null OK).
     * @param groups groups this user belongs to (null OK).
     */
    public PlainUser(String id, String domain, String displayName, Set<Group> groups) {
        super(id, displayName);
        this.domain = domain;
        this.groups = groups;
    }

    /**
     * Create a plain-old-User object.
     *
     * @param id Permanent identifier for this user.
     * @param domain The domain name in which the user and group IDs are defined.
     * @param displayName The visible name for this user (null OK).
     */
    public PlainUser(String id, String domain, String displayName) {
        super(id, displayName);
        this.domain = domain;
    }

    /**
     * Create a plain-old-User object.
     *
     * The displayName for this user will be set to the provided id.
     *
     * @param id Permanent identifier for this user.
     * @param domain The domain name in which the user and group IDs are defined.
     */
    public PlainUser(String id, String domain) {
        super(id, null);
        this.domain = domain;
    }

    /**
     * @return domain of this user and its groups
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * @return groups this user belongs to (can be empty-set, but always non-null).
     */
    public Set<Group> getGroups() {
        return this.groups != null ? this.groups : NO_GROUPS;
    }
}
