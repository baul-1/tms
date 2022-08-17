package com.icode.toonmanger.config;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public class CResult extends CMap{
    CMap head = new CMap();



    public CResult(){
        setStatus("ok");
    }


    public void setByRequestParam(Map r){
        this.putAll(r);return;
    }


    public void setStatus(String status){
        this.head.put("status",status);
    }

    public  void setErrorCode(int code){ this.head.put("errorcode", code); }

    public void setMsg(String msg){
        this.head.put("msg",msg);
    }
    public void setHead(String key,String value){
        this.head.put(key,value);
    }

    public CMap getHead(){
        return this.head;
    }


}
