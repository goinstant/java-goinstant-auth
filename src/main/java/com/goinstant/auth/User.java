package com.goinstant.auth;

import java.util.Set;
import java.util.Map;

import com.goinstant.auth.Group;

/**
 * Defines properties of a User in GoInstant.
 *
 * For more information on users, please see the GoInstant guide on <a href="https://developer.goinstant.com/v1/security_and_auth/guides/users_and_authentication.html">Users And Authentication</a>
 *
 * See {@link PlainUser} for a concrete implementation.
 */
public interface User {
    /**
     * Unique, permanent ID for this user.
     *
     * Numeric IDs make good permanent identifiers. Usernames and email
     * addresses not so much, since they're liable to change.
     */
    public String getID();

    /**
     * The domain for this user, typically the DNS domain of your website.
     */
    public String getDomain();

    /**
     * Display name for this user.
     */
    public String getDisplayName();

    /**
     * Groups this user belongs to.
     */
    public Set<Group> getGroups();

    /**
     * Any additional properties about this user that should be included in the token.
     */
    public Map<String,Object> getCustomClaims();
}
