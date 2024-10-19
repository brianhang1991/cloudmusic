package com.example.cloudmusic.contant;

public enum ServerConstant {

    URL_LOAD_ONLINE_MUSIC("http://10.0.2.2:8000/play"),
    ;

    private final String desc;

    ServerConstant(String desc) {
        this.desc = desc;
    }

    public String getDesc(){
        return desc;
    }
}
