package com.example.havefun2_mobile_java.models;

import java.io.Serializable;

public class Photo implements Serializable {
    private int resourceid;

    public Photo(int resourceid) {
        this.resourceid = resourceid;
    }

    public int getResourceid() {
        return resourceid;
    }

    public void setResourceid(int resourceid) {
        this.resourceid = resourceid;
    }


}
