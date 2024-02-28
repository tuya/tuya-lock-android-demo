package com.tuya.lock.demo.zigbee.bean;

public class RemoteOpenState {
    public String way;
    public String user;

    public String type;

    public RemoteOpenState(String way, String user) {
        this.way = way;
        this.user = user;
        this.type = "password";
    }
}
