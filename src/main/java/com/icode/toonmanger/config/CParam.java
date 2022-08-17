package com.icode.toonmanger.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icode.toonmanger.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class CParam extends CMap{
    CMap head = new CMap();
    RedisTemplate<String, Object> redis;


    public CParam(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    public void validateEmpty(String... keys) throws CParamValidateException{
        for (String key: keys) {
            if(!isEmpty(key)){
                throw new CParamValidateException(key);
            }
        }

    }





    public void setByRequestParam(Map r){
        this.putAll(r);
        return;
    }


    public User getUser(){
        User ret = null;
        String token = this.getS("t");

        String rkey = "ICODE:SESSION:" + token;

        redis.expire(rkey, Duration.ofDays(1));

        HashOperations<String,String,String> der = redis.opsForHash();
        String kind = der.get(rkey,"kind");

        if("COM".equals(kind)){
            String pkey = der.get(rkey,"companypkey");

            ret = new User(der.get(rkey,"name"));
            ret.setApkey(pkey);

            this.put("companypkey", ret.getCompanyPkey());
        }


        if("TM".equals(kind)){
            String pkey = der.get(rkey,"taskmanagerpkey");
            String companypkey = der.get(rkey,"companypkey");

            ret = new User(der.get(rkey,"name"));
            ret.setApkey(companypkey);
            ret.setBpkey(pkey);

            this.put("companypkey", ret.getCompanyPkey());
            this.put("taskmanagerpkey", ret.getTaskManagerPkey());
        }

        if("ME".equals(kind)){
            String pkey = der.get(rkey,"workerpkey");
            ret = new User(der.get(rkey,"name"));
            ret.setCpkey(pkey);
            this.put("workerpkey", ret.getWorkerPkey());
        }

        return ret;
    }


    public boolean isEmpty(String key) {
        String aa = this.getS(key,"0-12judhioewd789iohfug5tbh4o3itghf");

        if( "0-12judhioewd789iohfug5tbh4o3itghf".equals(aa)) return false;
        if("".equals(aa.trim())) return false;

        return true;
    }




    public class CParamValidateException extends RuntimeException {
        String problem = "";
        public CParamValidateException(String key) {
            this.problem = key;
        }

        @Override
        public String getMessage() {
            return this.problem;
        }
    }
}
