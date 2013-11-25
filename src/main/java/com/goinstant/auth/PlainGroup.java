package com.goinstant.auth;

import com.goinstant.auth.Group;
import com.goinstant.auth.PlainThing;

public class PlainGroup extends PlainThing implements Group  {
    /**
     * Create a plain-old-Group object.
     *
     * Defaults displayName to be the id.
     *
     * @param id Permanent identifier for this Group.
     */
    public PlainGroup(String id) {
        super(id, null);
    }

    /**
     * Create a plain-old-Group object.
     *
     * @param id Permanent identifier for this Group.
     * @param displayName The visible name for this Group (null OK).
     */
    public PlainGroup(String id, String displayName) {
        super(id, displayName);
    }

    /**
     * Since Groups go into a Set, provide equality on the group ID.
     */
    @Override
    public boolean equals(Object b) {
        try {
            Group group = (Group)b;
            return this.equals(group);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Group group) {
        return this.getID().equals(group.getID());
    }
}
