package com.icode.toonmanger.security;

public class User {

    String pkey;
    String name;

    String apkey="0";
    String bpkey="0";
    String cpkey="0";

    public User (){}

    public User(String name){
        this.name = name;
    }

    public void setApkey(String apkey) {
        this.apkey = apkey;
    }
    public void setBpkey(String bpkey) {
        this.bpkey = bpkey;
    }
    public void setCpkey(String cpkey) {
        this.cpkey = cpkey;
    }

    public String getCompanyPkey(){
        return apkey;
    }
    public String getTaskManagerPkey(){
        return bpkey;
    }
    public String getWorkerPkey(){
        return cpkey;
    }


    public String getPkey(){return this.pkey;}

    public String getName(){return this.name;}

    public String getType(){
        return "COMPANY";
    }

}
