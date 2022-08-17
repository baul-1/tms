package com.icode.toonmanger.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.mapper.MapperForWorker;
import com.icode.toonmanger.mapper.SelectMapperForWorker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ServiceForWorker {

    @Autowired
    MapperForWorker mapper;

    @Autowired
    SelectMapperForWorker selectMapper;

    @Transactional
    public void addWorkResult(CParam param){
        int wResult = mapper.addWorkResult(param);
        if ( wResult != 1) throw new RuntimeException("addWorkResultError");

        mapper.addWorkCauseForWorkResult(param);
    }

    @Transactional
    public void addWorkSchedule(CParam param){
        int wResult =  mapper.addWorkSchedule(param);

        if (wResult != 1 ) throw new RuntimeException("addWorkScheduleError");

        mapper.addWorkCauseForWorkSchedule(param);
    }

    @Transactional
    public void requestCancelWork(CParam param){
        int wResult = mapper.addWorkCancelForWorkResult(param);

        if(wResult != 1) throw new RuntimeException("addWorkCancelForWorkResultError");

        mapper.addWorkCancelForWorkCause(param);

    }

    @Transactional
    public List<CMap>  addEntry(CParam param){
        String json = param.getS("entrylist");

        int uResult = mapper.addUpdateSet(param);

        if(uResult != 1) throw new RuntimeException("updateSetInsertError");



        List<CMap> entryList = arrayJsonParser(json);
        entryList = addEntry(entryList, param);

        return entryList;
    }

    public List<CMap> arrayJsonParser(String jsonList){

        List<CMap> entryList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JSONArray jsonArray = objectMapper.convertValue(jsonList, JSONArray.class);
            for (Object object : jsonArray){
                CMap entry = jsonParser((JSONObject) object);
                entryList.add(entry);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return entryValidateParent(entryList);

    }

    public CMap jsonParser(JSONObject object){
        CMap map = new CMap();

        Set<String> keys = object.keySet();
        for(String key : keys){
            map.put(key, object.get(key));
        }
        return map;
    }

    public List<CMap> entryValidateParent(List<CMap> entryList){

        for (CMap entry : entryList){
            int entryNum = Integer.parseInt(entry.getS("entrynum"));
            int parentNum = Integer.parseInt(entry.getS("parentnum"));

            if (entryNum <= parentNum) throw  new RuntimeException("entryValidateError");

        }

        Collections.sort(entryList, new Comparator<CMap>() {

            @Override
            public int compare(CMap map1, CMap map2) {
                int cnt1 = Integer.parseInt(map1.getS("entrynum"));
                int cnt2 = Integer.parseInt(map2.getS("entrynum"));

                return cnt1 < cnt2 ? -1 : cnt1> cnt2 ? 1 : 0;
            }

        });

        return entryList;
    }


    @Transactional
    List<CMap> addEntry(List<CMap> entryList, CParam param){

        List<CMap> uploadEntryList = new ArrayList<>();


        for (CMap entry : entryList){


            String uuid = null;
            String isFile = entry.getS("isfile");
            String parentNum = entry.getS("parentnum");
            String entrypkey = entry.getS("entrypkey");
            String relpkey = entry.getS("rel");
            CMap resultEntry = new CMap();

            if("Y".equals(isFile)){
                uuid = UUID.randomUUID().toString();
            }else{
                uuid = "use";
            }
            entry.putAll(param);
            entry.put("uploadtoken", uuid);

            if("-1".equals(entrypkey)){

                if("-2".equals(relpkey)){
                    for(CMap cMap : entryList){
                        String entryNum = cMap.getS("entrynum");
                        if(parentNum.equals(entryNum)){
                            relpkey = cMap.getS("entrypkey");
                        }
                    }
                }

                entry.put("rel",relpkey);

                int eResult = mapper.addEntry(entry);
                if( eResult != 1) throw  new RuntimeException("addEntryInsertError");

                int evResult = mapper.addEntryVersion(entry);
                if(evResult != 1) throw  new RuntimeException("addEntryVersionInsertError");

            }else {

                int evResult = mapper.addEntryVersionUp(entry);

                if( evResult != 1) throw  new RuntimeException("addEntryVersionUpInsertError");

                mapper.setEntryCurrent(entry);

            }

            mapper.addUpdateSetEntryVersion(entry);

            if("Y".equals(isFile)){

                resultEntry.put("entrynum", entry.getS("entrynum"));
                resultEntry.put("uploadtoken", entry.getS("uploadtoken"));

                if("NORMAL".equals(entry.getS("status"))) uploadEntryList.add(resultEntry);
            }

        }

        return  uploadEntryList;
    }

}
