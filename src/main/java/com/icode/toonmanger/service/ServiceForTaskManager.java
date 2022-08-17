package com.icode.toonmanger.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.mapper.MapperForTaskManager;
import com.icode.toonmanger.mapper.SelectMapperForTaskManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceForTaskManager {

    @Autowired
    MapperForTaskManager mapper;

    @Autowired
    SelectMapperForTaskManager selectMapper;

    @Transactional
    public void cancleWork(CParam param) {

       mapper.setWorkStatusEndFlags(param);

    }
    @Transactional
    public List<CMap> addWork(CParam param){

        List<CMap> entryList = null;
        int wResult =  mapper.addWork(param);

        if( wResult != 1) throw new RuntimeException("addWorkInsertError");

        mapper.addWorkSchedule(param);

        String json = param.getS("entrylist");

        if(!"".equals(json)){
            mapper.addUpdateSet(param);
            entryList =  arrayJsonParser(json);
            entryList = addEntry(entryList, param);

        }

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

    public List<CMap> entryValidateParent( List<CMap> entryList){

        for (CMap entry : entryList){
            int entryNum = Integer.parseInt(entry.getS("entrynum"));
            int parentNum = Integer.parseInt(entry.getS("parentnum"));

            if (entryNum <= parentNum){
                throw  new RuntimeException("entryValidateError");
            }
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
    List<CMap> addEntry(List<CMap> entryList, CMap param){

        List<CMap> uploadEntryList = new ArrayList<>();


        for (CMap entry : entryList){

            String uuid = null;
            String isFile = entry.getS("isfile");
            String parentNum = entry.getS("parentnum");
            String relpkey = null;
            CMap resultEntry = new CMap();

            entry.put("taskmanagerpkey", param.getS("taskmanagerpkey"));
            entry.put("workpkey", param.getS("workpkey"));
            entry.put("updatesetpkey", param.getS("updatesetpkey"));


            for (CMap cMap : entryList){
                String entryNum = cMap.getS("entrynum");
                if(parentNum.equals(entryNum)){
                    relpkey = cMap.getS("entrypkey");
                }
            }

            if(relpkey == null){
                entry.put("rel", "-1");
            }else{
                entry.put("rel", relpkey);
            }

            if("Y".equals(isFile)){
                uuid = UUID.randomUUID().toString();
            }else{
                uuid = "use";
            }

            entry.put("uploadtoken", uuid);

            int eResult =  mapper.addEntry(entry);
            if( eResult != 1) throw  new RuntimeException("addEntryInsertError");

            int evResult = mapper.addEntryVersion(entry);
            if( evResult != 1) throw  new RuntimeException("addEntryVersionInsertError");

            mapper.addUpdateSetEntryVersion(entry);

            if("Y".equals(isFile)){
                resultEntry.put("entrynum", entry.getS("entrynum"));
                resultEntry.put("uploadtoken", entry.getS("uploadtoken"));

                uploadEntryList.add(resultEntry);
            }

        }

        return  uploadEntryList;

    }


    @Transactional
    public void setWorkResultForWorkCauseStatusToDone(CParam param){

        int result = mapper.setWorkResultForWorkCauseStatusToDone(param);

        if( result != 1) throw new RuntimeException("setWorkResultForWorkCauseStatusToDoneError");

        if("A".equals(param.getS("kind"))){
             mapper.setWorkStatusToDone(param);
        }else{
            mapper.setWorkStatusToFail(param);
        }
    }


    @Transactional
    public void setWorkScheduleForWorkCauseStatusToDone(CParam param){
        int result = mapper.setWorkScheduleForWorkCauseStatusToDone(param);
        if (result != 1) throw new RuntimeException("setWorkScheduleForWorkCauseStatusToDoneError");
        mapper.setWorkEndDate(param);
    }



    @Transactional
    public void addCharacterStyle(CParam param){
        int result = mapper.addCharacterStyle(param);
        if(result != 1) throw new RuntimeException("addCharacterStyleError");
        mapper.addWorkCharacterStyleFromCompanyCharacter(param);

    }

    @Transactional
    public void acceptCancelWorkRequest(CParam param){
        int result = mapper.setWorkCancelForWorkCauseStatusToDone(param);
        if(result != 1) throw new RuntimeException("setWorkCancelForWorkCauseStatusToDoneError");
            mapper.setAcceptWorkStatusToCancel(param);
    }


    @Transactional
    public void addCharacterStyleColor(CParam param){
        int cnt = mapper.setCharacterStyleVersionUp(param);
        if(cnt != 1) throw new RuntimeException("setCharacterStyleVersionUpError");

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



    public CMap checkUploadDone(CParam param){

        CMap ret = new CMap();

        CMap entryUploadCheck = selectMapper.getEntryUploadAllCheck(param);

        if("0".equals(entryUploadCheck.getS("entryversionsum"))){
            mapper.setWorkStatusNotRead(param);
            ret.put("result" , "done");
        }else{
            ret.put("result" , "fail");
        }

        return ret;
    }

    @Transactional
    public List<CMap>  setTaskStatusToDone(CParam param){
        String json = param.getS("entrylist");

        int tResult = mapper.setTaskStatusToDone(param);

        if( tResult != 1) throw  new RuntimeException("setTaskStatusToDoneError");

        List<CMap> entryList = arrayJsonParser(json);
        entryList = addTaskEntry(entryList, param);

        return entryList;
    }

    @Transactional
    List<CMap> addTaskEntry(List<CMap> entryList, CParam param){

        List<CMap> uploadEntryList = new ArrayList<>();


        for (CMap entry : entryList){

            String uuid = UUID.randomUUID().toString();
            String isFile = entry.getS("isfile");
            String parentNum = entry.getS("parentnum");
            String relpkey = null;
            CMap resultEntry = new CMap();

            entry.put("taskmanagerpkey", param.getS("taskmanagerpkey"));
            entry.put("taskpkey", param.getS("taskpkey"));



            for (CMap cMap : entryList){
                String entryNum = cMap.getS("entrynum");
                if(parentNum.equals(entryNum)){
                    relpkey = cMap.getS("taskentrypkey");
                }
            }

            if(relpkey == null){
                entry.put("rel", "-1");
            }else{
                entry.put("rel", relpkey);
            }

            if("N".equals(isFile)){

                mapper.addTaskEntryFolder(entry);

            }else{

                entry.put("uploadtoken", uuid);

                mapper.addTaskEntryFile(entry);

                resultEntry.put("entrynum", entry.getS("entrynum"));
                resultEntry.put("uploadtoken", entry.getS("uploadtoken"));

                uploadEntryList.add(resultEntry);
            }

        }



        return  uploadEntryList;
    }

}
