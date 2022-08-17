package com.icode.toonmanger.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.config.CResult;
import com.icode.toonmanger.mapper.MapperForCompany;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServiceForCompany {

    @Autowired
    MapperForCompany mapper;

    @Transactional
    public void addWorker(CParam param, String companypkey) {
        param.put("companypkey",companypkey);
        param.put("workerpw", BCrypt.hashpw(param.getS("workerpw"),BCrypt.gensalt()));
       var addWorkercnt = mapper.addWorker(param);
       if(addWorkercnt==1){
           mapper.joinWorkerToCompany(param.getS("workerpkey"),companypkey);
       }
    }

    public void addStyle(CParam param, String companypkey) {
        int cnt = mapper.addCharacterStyle(param);
        if(cnt == 1){
            String d = param.getS("colorlist");
            ObjectMapper jackson = new ObjectMapper();

            try {
                Map[] aed = jackson.readValue(d, Map[].class);
                for (Map m:aed) {
                    param.putAll(m);
                    mapper.addCharacterStyleColor(param);
                }

            } catch (JsonProcessingException e) {
                //throw new RuntimeException("#Color fff");
            }


        }

    }

    @Transactional
    public void addCharacterStyleColor(CParam param){
        int cnt = mapper.setCharacterStyleVersionUp(param);
        if(cnt == 1){
            String d = param.getS("colorlist");
            ObjectMapper jackson = new ObjectMapper();

            try {
                CMap[] aed = jackson.readValue(d, CMap[].class);
                for (Map m:aed) {
                    param.putAll(m);
                    mapper.addColor(param);
                }

            } catch (JsonProcessingException e) {
                //throw new RuntimeException("#Color fff");
            }
        }
    }



}

