package com.goinstant.auth;

import java.util.Map;
import java.util.Collections;

class PlainThing {
    protected String id;
    protected String displayName;
    protected Map<String,Object> custom;

    protected static Map<String,Object> NO_CUSTOM = Collections.emptyMap();

    /**
     * Sets up this Plain Thing.
     * @param id the permanent identifier of this User/Group.
     * @param displayName (optional) display name of this User/Group (defaults to id)
     */
    protected PlainThing(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * @return the permanent identifier for this user or group.
     */
    public String getID() {
        return this.id;
    }

    /**
     * @return visible name for this user (or the ID, if no name is set)
     */
    public String getDisplayName() {
        return this.displayName != null ? this.displayName : this.id;
    }

    /**
     * Assign custom claims about this user.
     * @param custom any custom claims.  Pass null to remove all claims.
     */
    public void setCustomClaims(Map<String,Object> custom) {
        if (custom.isEmpty()) {
            this.custom = null;
        } else {
            this.custom = custom;
        }
    }

    /**
     * @return custom claims about this user
     */
    public Map<String,Object> getCustomClaims() {
        return this.custom != null ? this.custom : NO_CUSTOM;
    }
}
