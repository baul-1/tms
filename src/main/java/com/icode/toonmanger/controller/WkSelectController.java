package com.icode.toonmanger.controller;

import com.icode.toonmanger.config.*;
import com.icode.toonmanger.mapper.SelectMapperForWorker;
import com.icode.toonmanger.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/me")
public class WkSelectController {

    @Autowired
    SelectMapperForWorker selectMapper;


    @RequestMapping(value = "/listWorkToDo.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorkToDo(@GetParam CParam param) {
        CResult ret = new CResult();

        User WorkerUser = param.getUser();

        param.validateEmpty("t");

        List<CMap> list = selectMapper.listWorkToDo(WorkerUser.getWorkerPkey(), param.getI("start", 0), param.getI("limit",0));

        for(CMap work : list){
            List<CMap> workCauseList = selectMapper.listWorkCause(work);
            work.put("workcauselist",workCauseList);
        }

        ret.put("worklist", list);
        return ret;
    }

    @RequestMapping(value = "/listWorkGone.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorkGone(@GetParam CParam param) {
        CResult ret = new CResult();


        User WorkerUser = param.getUser();

        param.validateEmpty("t");

        param.put("workerpkey",WorkerUser.getWorkerPkey());

        List<CMap> list = selectMapper.listWorkGone(WorkerUser.getWorkerPkey(), param.getI("start", 0), param.getI("limit",0));

        for(CMap work : list){
            List<CMap> workCauseList = selectMapper.listWorkCause(work);
            work.put("workcauselist",workCauseList);
        }

        ret.put("worklist", list);
        return ret;
    }


    @RequestMapping(value="/getWorkDetail.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getWorkDetail(@GetParam CParam param){
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");
        param.put("workerpkey", companyuser.getWorkerPkey());

        CMap work = selectMapper.getWork(param);

        List<CMap> workresult = selectMapper.listWorkResult(param) ;
        List<CMap> characterList  = selectMapper.getWorkCharacter(param);



        for(CMap  character : characterList){
            param.put("characterpkey",character.getS("characterpkey"));
            List<CMap>  styleList = selectMapper.listCharacterStyle(param);
            for (CMap style : styleList ){
                List<CMap> colorList = selectMapper.listStyleColor(style);
                style.put("palette", colorList);
            }
            character.put("stylelist",styleList);
        }

        work.put("workcauselist", workresult);
        work.put("characterlist", characterList);


        ret.put("work", work);
        ret.put("nowdate", selectMapper.getNowDate().getS("date"));

        return ret;
    }


    @RequestMapping(value = "/listWorkCharacterStyle.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorkCharacterStyle(@GetParam CParam param ){
        CResult ret = new CResult();

        User WorkerUser = param.getUser();

        param.validateEmpty("t");
        param.put("workerpkey" , WorkerUser.getWorkerPkey());

        List<CMap> listCharacter = selectMapper.listWorkCharacter(param);
        for(CMap character : listCharacter){
            List<CMap> listCharacterStyle = selectMapper.listWorkCharacterStyle(param.getS("workpkey"), character.getS("characterpkey"));
            for(CMap style : listCharacterStyle){
                List<CMap> listCharacterStyleColor = selectMapper.listWorkCharacterStyleColor(style.getS("characterstylepkey"));
                style.put("palette",listCharacterStyleColor);
            }
            character.put("stylelist", listCharacterStyle);
        }

        ret.put("characterlist", listCharacter);

        return ret;
    }


    @RequestMapping(value = "/getDashboard.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getDashboard(@GetParam CParam param ){
        CResult ret = new CResult();

        User WorkerUser = param.getUser();

        param.validateEmpty("t");


        ret.put("listWorkPriority", selectMapper.listWorkPriority(WorkerUser.getWorkerPkey()));
        ret.put("listWorkREG", selectMapper.listWorkREG(WorkerUser.getWorkerPkey()));
        ret.put("listWorkAscEnddate", selectMapper.listWorkAscEnddate(WorkerUser.getWorkerPkey()));
        ret.put("resultreqcount", selectMapper.getWorkResultReqCount(WorkerUser.getWorkerPkey()).getS("reqcount"));
        ret.put("schedulereqcount",selectMapper.getWorkScheduleReqCount(WorkerUser.getWorkerPkey()).getS("reqcount"));
        ret.put("denyreqcount",selectMapper.getWorkDenyReqCount(WorkerUser.getWorkerPkey()).get("denycount"));
        ret.put("confirmcutcount", selectMapper.getWorkConfirmCutCount(WorkerUser.getWorkerPkey()).getS("confirmcutcount"));
        ret.put("nowdate", selectMapper.getNowDate().getS("date"));

        return ret;
    }


    @RequestMapping(value = "/listEntryVersionCommit.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listEntryVersionCommit(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("t");

        User WorkerUser = param.getUser();
        param.put("workerpkey", WorkerUser.getWorkerPkey());

        List<CMap> updateSetList = selectMapper.listUpdateSet(param);

        for (CMap updateSet : updateSetList){

            List<CMap> listUpdateSetEntry = selectMapper.listUpdateSetEntry(updateSet);

            updateSet.put("entrylist" , listUpdateSetEntry);
        }

        ret.put("entryversioncommitlist", updateSetList);

        return ret;
    }


    @RequestMapping(value = "/checkUploadDone.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult checkUploadDone(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("updatesetpkey", "t");

        User WorkerUser = param.getUser();
        param.put("workerpkey", WorkerUser.getWorkerPkey());

        CMap  entryUploadCheck = selectMapper.getEntryUploadAllCheck(param);

        if("0".equals(entryUploadCheck.getS("entryversionsum"))){
            ret.put("result" , "done");
        }else{
            ret.put("result" , "fail");
        }

        return  ret;
    }

    public String nextCheckList(List<CMap> list){
        String next = "N";
        if(list.size() > 0 ) next = "Y";
        return next;
    }






}
