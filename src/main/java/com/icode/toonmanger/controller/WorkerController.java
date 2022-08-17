package com.icode.toonmanger.controller;

import com.icode.toonmanger.config.*;
import com.icode.toonmanger.mapper.MapperForWorker;
import com.icode.toonmanger.security.User;
import com.icode.toonmanger.service.ServiceForWorker;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping(value = "/me")
public class WorkerController extends Controller {

    @Autowired
    MapperForWorker mapper;

    @Autowired
    ServiceForWorker service;


    @RequestMapping(value = "/login.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult login(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("meid","pw");

        CMap worker = mapper.getWorkerById(param);


        if(worker == null){
            ret.put("status","FAIL");
        }else {
            if(BCrypt.checkpw(param.getS("pw"),worker.getS("workerpw"))){
                worker.remove("workerpw");
                try{

                    String token = makeSession("ME", worker);
                    ret.put("status","SUCCESS");
                    ret.put("t", token);

                }catch (Exception e){
                    ret.put("status","FAIL2");
                }
            }else{
                ret.put("status","FAIL");
            }
        }

        ret.setStatus("OK");

        return ret;
    }


    @RequestMapping(value = "/acceptWork.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult acceptWork(@PostParam CParam param){

            CResult ret = new CResult();


            User WorkerUser = param.getUser();

            param.validateEmpty("workpkey", "t");


            int cnt = mapper.acceptWork(param);

            ret.setStatus("OK");

            return ret;


    }

    @RequestMapping(value = "/setWorkProgress.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setWorkProgress(@PostParam CParam param){

            CResult ret = new CResult();

            User WorkerUser = param.getUser();

            param.validateEmpty("workpkey", "progress", "t");


            int cnt = mapper.setProgress(param);

            ret.setStatus("OK");

            return ret;


    }


    @RequestMapping(value = "/addWorkResult.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addWorkResult(@PostParam CParam param ){
        CResult ret = new CResult();

        User WorkerUser = param.getUser();

        param.validateEmpty("requestcutcount", "workpkey", "updatesetpkey","t");
        param.put("workerpkey", WorkerUser.getWorkerPkey());

        service.addWorkResult(param);

        ret.setStatus("OK");

        return ret;
    }


    @RequestMapping(value = "addWorkSchedule.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addWorkSchedule(@PostParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("workpkey","enddate", "comment", "t");

        User WorkerUser = param.getUser();
        param.put("workerpkey", WorkerUser.getWorkerPkey());

        service.addWorkSchedule(param);

        ret.setStatus("OK");

        return ret;
    }


    @RequestMapping(value = "/updateWorkProgress.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult updateWorkProgress(@PostParam CParam param ){
        CResult ret = new CResult();

        User WorkerUser = param.getUser();

        param.validateEmpty("workpkey", "progress", "t");


        int cnt  = mapper.updateWorkProgress(param);

        ret.setStatus("OK");

        return ret;
    }


    @RequestMapping(value = "/requestCancelWork.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult requestCancelWork(@PostParam CParam param ){
        CResult ret = new CResult();

        param.validateEmpty("workpkey", "comment", "t");

        User WorkerUser = param.getUser();
        param.put("workerpkey", WorkerUser.getWorkerPkey());

        service.requestCancelWork(param);

        ret.setStatus("OK");

        return ret;
    }

    @RequestMapping(value = "/addEntry.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addEntryVersion(@PostParam CParam param ){
        CResult ret = new CResult();

        param.validateEmpty("workpkey", "entrylist", "t", "updatesetname");

        User WorkerUser = param.getUser();
        param.put("workerpkey", WorkerUser.getWorkerPkey());

        List<CMap> entryList = service.addEntry(param);

        ret.put("entrylist",entryList);
        ret.put("updatesetpkey", param.getS("updatesetpkey"));

        ret.setStatus("OK");

        return ret;
    }




}

