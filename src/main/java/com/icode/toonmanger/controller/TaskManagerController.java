package com.icode.toonmanger.controller;


import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.config.CResult;
import com.icode.toonmanger.config.PostParam;
import com.icode.toonmanger.mapper.MapperForTaskManager;
import com.icode.toonmanger.security.User;
import com.icode.toonmanger.service.ServiceForTaskManager;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;


@RestController
@RequestMapping(value = "/tm")
public class TaskManagerController extends  Controller {

    @Autowired
    MapperForTaskManager mapper;
    @Autowired
    ServiceForTaskManager service;

    @PostMapping
    public Mono<Map<String, String>> getMap(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getFormData()
                .flatMap(formData -> {
                    String formDataTest = formData.getFirst("test");
                    String result = Objects.requireNonNullElse(formDataTest, "you failed!");
                    return Mono.just(Map.of("result", result));
                });
    }


    @RequestMapping(value = "/login.*",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult login(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("comname", "tmid","pw");

        CMap tm =  mapper.getTaskmanagerByTmId(param);

        if(tm == null){
            ret.put("status","FAIL");
        }else {
             if(BCrypt.checkpw(param.getS("pw"), tm.getS("tmpw"))){
                 tm.remove("tmpw");
                 try{
                     String token = makeSession("TM",tm);
                     ret.put("status","SUCCESS");
                     ret.put("t", token);
                 }catch (Exception e){
                     ret.put("status", "FAIL2");
                 }
             }else{
                 ret.put("status","FAIL");
             }
        }

        return ret;
    }




    @RequestMapping(value = "/addTask.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addTask(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("name", "startdate", "enddate", "kind", "episodepkey", "t");

            User taskManagerUser = param.getUser();
            param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

            mapper.addTask(param);

            ret.put("taskpkey" , param.getS("taskpkey"));
            ret.setStatus("OK");

            return ret;

    }



    @RequestMapping(value = "/addWork.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addWork(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("name", "startdate", "enddate", "taskpkey", "workerpkey",  "priority", "t");

            User taskManagerUser = param.getUser();
            param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());


            List<CMap> entryList =  service.addWork(param);

            ret.put("workpkey",param.getS("workpkey"));
            ret.put("entrylist", entryList);

            ret.setStatus("OK");

            return ret;

    }

    @RequestMapping(value = "/checkUploadDone.*", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin("*")
    public CResult checkUploadDone(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("workpkey", "t");

        User user = param.getUser();
        param.put("taskmanagerpkey", user.getTaskManagerPkey());

        CMap result =  service.checkUploadDone(param);

        ret.put("result", result.getS("result"));

        ret.setStatus("OK");

        return  ret;
    }




    @RequestMapping(value = "/setWorkPriority.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setWorkPriority(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("workpkey", "priority", "t");

            User taskManagerUser = param.getUser();
            param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

            mapper.setWorkPriority(param);

            ret.setStatus("OK");

            return ret;

    }



    @RequestMapping(value = "/modifyWork.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult modifyWork(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("workpkey", "workname", "t");

            User taskManagerUser = param.getUser();
            param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

            mapper.modifyWork(param);
            ret.setStatus("OK");

            return ret;

    }



    @RequestMapping(value = "/cancelWorkForNotStarted.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult cancelWorkForNotStarted(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("workpkey", "t");

            User taskManagerUser = param.getUser();
            param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());
            param.put("status","CANCEL");

            service.cancleWork(param);

            ret.setStatus("OK");

            return ret;

    }

    @RequestMapping(value = "/addCharacter.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addCharacter(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("name", "t");

            User taskManagerUser = param.getUser();

            mapper.addCharacter(param.getS("name"),taskManagerUser.getCompanyPkey());

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/modifyCharacter.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult modifyCharacter(@PostParam CParam param){

            CResult ret = new CResult();

            param.validateEmpty("name", "characterpkey", "t");


            User taskManagerUser = param.getUser();

            mapper.modifyCharacter(param.getS("characterpkey"),param.getS("name"),taskManagerUser.getCompanyPkey());

            ret.setStatus("OK");

            return ret;

    }




    @RequestMapping(value = "/addWorkCharacterStyleFromCompanyCharacter.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addTaskCharacterStyle(@PostParam CParam param){


        CResult ret = new CResult();

        param.validateEmpty("workpkey", "characterstylepkey", "t");

        User taskManagerUser = param.getUser();

        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.addWorkCharacterStyleFromCompanyCharacter(param);

        ret.setStatus("OK");

        return ret;

    }


    @RequestMapping(value = "/addStyle.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addStyle(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("name","characterpkey","imagesrc", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());;

        mapper.addCharacterStyle(param);

        ret.setStatus("OK");
        return ret;

    }



    @RequestMapping(value = "/addColorOnPalette.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addColorOnPalette(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("characterstylepkey","colorlist", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.addCharacterStyleColor(param);

        ret.setStatus("OK");

        return ret;

    }



    @RequestMapping(value = "/setWorkResultForWorkCauseStatusToDone.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setWorkResultForWorkCauseStatusToDone(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("workresultpkey", "workpkey", "kind", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        service.setWorkResultForWorkCauseStatusToDone(param);

        ret.setStatus("OK");
        return ret;

    }


    @RequestMapping(value = "/setWorkResultForWorkCauseStatusToRejected.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setWorkResultForWorkCauseStatusToRejected(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("workcausepkey", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.setWorkCauseStatusToRejected(param);

        ret.setStatus("OK");
        return ret;

    }




    @RequestMapping(value = "/setWorkScheduleForWorkCauseStatusToDone.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setWorkScheduleForWorkCauseStatusToDone(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("workschedulepkey", "workpkey", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        service.setWorkScheduleForWorkCauseStatusToDone(param);

        ret.setStatus("OK");
        return ret;

    }



    @RequestMapping(value = "/setWorkScheduleForWorkCauseStatusToRejected.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setWorkScheduleForWorkCauseStatusToRejected(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("workcausepkey", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.setWorkCauseStatusToRejected(param);

        ret.setStatus("OK");
        return ret;

    }





    @RequestMapping(value = "/doneCancelRequest.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult doneCancelRequest(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("workpkey", "workcausepkey", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        service.acceptCancelWorkRequest(param);

        ret.setStatus("OK");
        return ret;

    }


    @RequestMapping(value = "/rejectCancelRequest.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult rejectCancelRequest(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("workpkey", "workcausepkey", "t");

        User taskManagerUser = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.setWorkCancelForWorkCauseStatusToRejected(param);


        ret.setStatus("OK");
        return ret;

    }




    @RequestMapping(value = "/setMemberStatusToAware.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setMemberStatusToAware(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("workerpkey", "t");

        User taskManagerUser  = param.getUser();
        param.put("taskmanagerpkey" , taskManagerUser.getTaskManagerPkey());

        mapper.setWorkGroupStatusToAware(param);

        ret.setStatus("OK");
        return ret;
    }



    @RequestMapping(value = "/setMemberStatusToReqKick.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setMemberStatusToReqKick(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("workerpkey", "t");

        User taskManagerUser  = param.getUser();
        param.put("taskmanagerpkey" , taskManagerUser.getTaskManagerPkey());

        int result = mapper.setWorkGroupStatusToReqkick(param);
        if(result == 0 ) ret.put("RESULT","WORKING");

        ret.setStatus("OK");

        return ret;
    }

    @RequestMapping(value = "/deleteTask.*",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult deleteTask(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("taskpkey", "t");

        User taskManagerUser  = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.deleteTask(param);

        ret.setStatus("OK");

        return ret;
    }

    @RequestMapping(value = "/setTaskStatusToDone.*",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setTaskStatusToDone(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("taskpkey", "entrylist", "t");

        User taskManagerUser  = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());


        List<CMap> entryList =  service.setTaskStatusToDone(param);

        ret.put("entrylist", entryList);
        ret.setStatus("OK");

        return ret;
    }


    @RequestMapping(value = "/setMemberStatusToNormal.*",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setMemberStatusToNormal(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("workerpkey", "t");

        User taskManagerUser  = param.getUser();
        param.put("taskmanagerpkey", taskManagerUser.getTaskManagerPkey());

        mapper.setMemberStatusToNormal(param);

        ret.setStatus("OK");

        return ret;
    }









}
