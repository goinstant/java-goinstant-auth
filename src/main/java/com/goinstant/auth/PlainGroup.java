package com.goinstant.auth;

import java.lang.Comparable;

import com.goinstant.auth.Group;
import com.goinstant.auth.PlainThing;

/**
 * A Plain Group implementation.
 *
 * Implements Comparable so it can be inserted into TreeSets.
 */
public class PlainGroup extends PlainThing implements Group, Comparable<Group> {

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
    public boolean equals(Object that) {
        try {
            Group group = (Group)that;
            return this.equals(group);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Group that) {
        return this.compareTo(that) == 0;
    }

    /**
     * Fullfill Comparable.
     */
    public int compareTo(Group that) {
        return this.getID().compareTo(that.getID());
    }
}
