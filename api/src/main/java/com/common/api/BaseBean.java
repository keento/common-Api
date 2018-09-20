package com.common.api;


import java.io.Serializable;

public class BaseBean implements Serializable{
    public Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
