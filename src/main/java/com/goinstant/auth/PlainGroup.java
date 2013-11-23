package com.goinstant.auth;

import com.goinstant.auth.Group;
import com.goinstant.auth.PlainThing;

public class PlainGroup extends PlainThing implements Group  {
    public PlainGroup(String id) {
        super(id, null);
    }

    public PlainGroup(String id, String displayName) {
        super(id, displayName);
    }

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
