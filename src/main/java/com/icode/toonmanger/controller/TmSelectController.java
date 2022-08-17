package com.icode.toonmanger.controller;

import com.icode.toonmanger.config.*;
import com.icode.toonmanger.mapper.SelectMapperForTaskManager;
import com.icode.toonmanger.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Consumer;

@RestController
@RequestMapping(value = "/tm")
public class TmSelectController {

    @Autowired
    SelectMapperForTaskManager selectMapper;



    @RequestMapping(value = "/listTask.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTask(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();


        List<CMap> list = selectMapper.listTask(taskUser.getTaskManagerPkey(),taskUser.getTaskManagerPkey(),param.getS("fromdate"),param.getS("todate"));

        list.forEach(new Consumer<CMap>() {
            @Override
            public void accept(CMap cMap) {
                List<CMap> workList = selectMapper.listWorkInTask(cMap.getS("taskpkey"));
                for (CMap work: workList ){
                    List<CMap> workCauseList = selectMapper.listWorkCause(work);
                    work.put("workcauselist", workCauseList);
                }
                cMap.put("worklist",workList);
            }
        });
        ret.put("tasklist",list);
        ret.put("nowdate", selectMapper.getNowDate().getS("date"));
        return ret;
    }

    @RequestMapping(value = "/listTaskFromTo.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTaskFromTo(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("t", "startdate", "enddate");
        
        User taskUser = param.getUser();

        List<CMap> taskList = selectMapper.listTaskFromTo(taskUser.getTaskManagerPkey(), param.getS("startdate"), param.getS("enddate"), param.getI("start",0), param.getI("limit",0) );

        for (CMap task : taskList) {
            List<CMap> workList = selectMapper.listWorkInTaskFromTo(task.getS("taskpkey"), param.getS("startdate"), param.getS("enddate"));
            for (CMap work : workList) {
                List<CMap> workCauseList = selectMapper.listWorkCause(work);
                work.put("workcauselist", workCauseList);
            }
            task.put("worklist", workList);
        }

        ret.put("tasklist",taskList);
        ret.put("nowdate", selectMapper.getNowDate().getS("date"));


        return  ret;
    }



    @RequestMapping(value = "/listWorkInTask.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWork(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> list = selectMapper.listWorkInTask(taskUser.getTaskManagerPkey());

        ret.put("worklist",list);
        return ret;
    }

    @RequestMapping(value = "/listTitle.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTitle(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> list = selectMapper.listTitle(taskUser.getCompanyPkey(),0,9999);
        ret.put("titlelist",list);

        return ret;


    }




    @RequestMapping(value = "/listEpisode.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listEpisode(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> list = selectMapper.listEpisode(param.getS("titlepkey"),taskUser.getCompanyPkey(),0,9999);

        ret.put("episodelist",list);

        return ret;


    }



    @RequestMapping(value = "/listCharacter.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listCharacter(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();


        List<CMap> list = selectMapper.listCharacter(taskUser.getCompanyPkey(), 0,9999);

        ret.put("characterlist",list);

        return ret;


    }


    @RequestMapping(value = "/listMember.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listMember(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> list = selectMapper.listWorker(taskUser.getTaskManagerPkey());

        ret.put("workerlist",list);

        return ret;

    }

    @RequestMapping(value = "/listNormalMember.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listNormalMember(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> list = selectMapper.listNormalMember(taskUser.getTaskManagerPkey());

        ret.put("workerlist",list);

        return ret;

    }

    @RequestMapping(value = "/getWorker.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getWorker(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();


        CMap item = selectMapper.getWorker(param, taskUser.getCompanyPkey());

        ret.put("worker",item);

        return ret;
    }


    @RequestMapping(value = "/listTaskManagerMember.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTaskManagerMember(@GetParam CParam param) {
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> list = selectMapper.listTaskManagerMember(param.getS("taskmanagerpkey"), taskUser.getCompanyPkey());

        ret.put("memberList",list);

        return ret;

    }


    @RequestMapping(value="/getWorkDetail.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getWorkDetail(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        param.put("taskmanagerpkey", taskUser.getTaskManagerPkey());

        CMap work = selectMapper.getWork(param);

        List<CMap> listWorkCause = selectMapper.listWorkCause(param);

        List<CMap> characterList  = selectMapper.listWorkCharacter(param);



       for(CMap  character : characterList){
            param.put("characterpkey",character.getS("characterpkey"));
            List<CMap>  styleList = selectMapper.listCharacterStyle(param);

            for (CMap style : styleList ){
                List<CMap> colorList = selectMapper.listStyleColor(style);
                style.put("palette", colorList);
            }
            character.put("stylelist",styleList);
        }

        work.put("workcauselist", listWorkCause);
        work.put("characterlist", characterList);


        ret.put("work", work);

        return ret;
    }


    @RequestMapping(value = "getTaskDetail.*",method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getTaskDetail(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        param.put("taskmanagerpkey", taskUser.getTaskManagerPkey());

        CMap taskDetail = selectMapper.getTaskDetail(param);
        taskDetail.put("worklist",selectMapper.listWorkInTask(param.getS("taskpkey")));

        ret.put("taskdetail",taskDetail);

        return  ret;
    }


    @RequestMapping(value="/listCompanyCharacterStyle.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listCompanyCharacterStyle(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        List<CMap> characterList = selectMapper.listTmCharacter(taskUser.getTaskManagerPkey());

        for (CMap character : characterList){
            List<CMap> styleList = selectMapper.listTmcharacterstyle(character);
            character.put("stylelist", styleList);
        }

        ret.put("characterlist", characterList);

        return ret;
    }


    @RequestMapping(value = "/getDashboard.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getDashboard(@GetParam CParam param ){
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();

        ret.put("highprioritytasklist", selectMapper.listHighPriorityTask(taskUser.getTaskManagerPkey()));
        ret.put("tasklist", selectMapper.listTaskProgress(taskUser.getTaskManagerPkey()));
        ret.put("resultreqlist", selectMapper.getResultReqCount(taskUser.getTaskManagerPkey()));
        ret.put("schedulereqlist", selectMapper.getScheduleReqCount(taskUser.getTaskManagerPkey()));
        ret.put("denyreqlist", selectMapper.getDenyReqCount(taskUser.getTaskManagerPkey()));
        ret.put("recentworklist", selectMapper.listRecentWork(taskUser.getTaskManagerPkey()));
        ret.put("memberlist", selectMapper.listMember(taskUser.getTaskManagerPkey()));
        ret.put("nowdate", selectMapper.getNowDate().getS("date"));

        return ret;
    }



    @RequestMapping(value = "/listEntryVersionCommit.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listEntryVersionCommit(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty("t");

        User taskUser = param.getUser();
        param.put("taskmanagerpkey", taskUser.getTaskManagerPkey());

        List<CMap> updateSetList = selectMapper.listUpdateSet(param);

        for (CMap updateSet : updateSetList){

            List<CMap> listUpdateSetEntry = selectMapper.listUpdateSetEntry(updateSet);

            updateSet.put("entrylist" , listUpdateSetEntry);
        }

        ret.put("entryversioncommitlist", updateSetList);


        return ret;
    }







    @RequestMapping(value = "/taskEntryCheckUploadDone.*", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin("*")
    public CResult taskEntryCheckUploadDone(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty( "t");

        User user = param.getUser();
        param.put("taskmanagerpkey", user.getTaskManagerPkey());

        CMap check =   selectMapper.getTaskEntryUploadAllCheck(param);

        if("0".equals(check.getS("checksum"))){
            ret.put("result", "done");
        }else{
            ret.put("result", "fail");
        }

        return  ret;
    }

    @RequestMapping(value = "/listTaskEntry.*", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin("*")
    public CResult listTaskEntry(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty( "t", "taskpkey");

        User user = param.getUser();
        param.put("taskmanagerpkey", user.getTaskManagerPkey());

        List<CMap> list = selectMapper.listTaskEntry(param);

        ret.put("taskentrylist", list);

        return  ret;
    }






}
