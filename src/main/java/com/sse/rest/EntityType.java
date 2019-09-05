package com.sse.rest;

import com.sse.domain.*;

public enum EntityType {
    account,
    asset,
    user,
    location,
    session;

    private final Class clazz;

    EntityType() {
        this.clazz = getTypeClass();
    }

    public Class getClazz() {
        return clazz;
    }

    private Class getTypeClass(){
        return this.name().equalsIgnoreCase("account")?
                Account.class:this.name().equalsIgnoreCase("asset")? Asset.class:
                this.name().equalsIgnoreCase("user")?User.class:
                        this.name().equalsIgnoreCase("location")? Location.class: Session.class;
    }
}
