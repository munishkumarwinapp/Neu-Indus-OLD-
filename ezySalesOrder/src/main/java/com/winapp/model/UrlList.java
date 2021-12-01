package com.winapp.model;

public class UrlList {
    private String url;
    private String id;
    private String def_name;

    public void setDef_name(String def_name){
        this.def_name=def_name;
    }

    public String getDef_name() {
        return def_name;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getId(){
        return id;
    }
    public void setUrl(String url){
        this.url=url;
    }
    public String getUrl(){
        return url;
    }
}
