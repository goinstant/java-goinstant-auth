package com.goinstant.auth;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A Plain User implementation.
 *
 * Fields have protected visibility to allow easy subclassing.
 */
public class PlainUser extends PlainThing implements User {

    /**
     * Domain of this user.
     */
    protected String domain;

    /**
     * Groups this user belongs to (or null).
     */
    protected Set<Group> groups;

    /**
     * Empty set of Groups.
     */
    public static final Set<Group> NO_GROUPS = Collections.emptySet();

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
     * Get the domain of this user and its groups.
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Get the groups this user belongs to.
     * Can be empty-set, but always non-null.
     */
    public Set<Group> getGroups() {
        return this.groups != null ? this.groups : NO_GROUPS;
    }
}
