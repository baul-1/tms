package com.icode.toonmanger.controller;

import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.config.CResult;
import com.icode.toonmanger.config.GetParam;
import com.icode.toonmanger.mapper.SelectMapperForCompany;
import com.icode.toonmanger.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/com")
public class ComSelectController {

    @Autowired
    SelectMapperForCompany selectMapper;



    @RequestMapping(value = "/listTitle.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTitleWith(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<CMap> list = selectMapper.listTitleAll(companyuser.getCompanyPkey(), param.getI("start", 0), param.getI("limit", 0));

        String next = nextCheckList(selectMapper.listTitleAll(companyuser.getCompanyPkey(), param.getI("start", 0) + param.getI("limit", 0), param.getI("limit", 0)));

        ret.put("titlelist",list);
        ret.put("next" , next);

        return ret;


    }

    @RequestMapping(value = "/listTitleVisible.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTitleWithStatus(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<String> statusArr = Arrays.asList("NORMAL","INACTIVE","REG");

        List<CMap> list = selectMapper.listTitleWithStatus(companyuser.getCompanyPkey(),statusArr, param.getI("start" , 0), param.getI("limit", 0));

        String next = nextCheckList(selectMapper.listTitleWithStatus(companyuser.getCompanyPkey(),statusArr, param.getI("start", 0) + param.getI("limit", 0) ,param.getI("limit", 0) ));

        ret.put("next" , next);

        ret.put("titlelist",list);


        return ret;


    }


    @RequestMapping(value = "/getTitle.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getTitle(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");

        CMap item = selectMapper.getTitle(param.getS("titlepkey"),companyuser.getCompanyPkey());

        ret.put("title",item);

        return ret;


    }




    @RequestMapping(value = "/listEpisodeAll.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listEpisodeAll(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<CMap> episodeList = selectMapper.listEpisodeAll(param.getS("titlepkey"),companyuser.getCompanyPkey(),param.getI("start", 0), param.getI("limit", 0));

        for (CMap episode : episodeList ){
            List<CMap> taskList = selectMapper.listTaskAll(episode);
            for(CMap task : taskList){
                List<CMap> workList = selectMapper.listWorkAll(task);
                task.put("worklist", workList);
            }
            episode.put("tasklist",taskList);
        }

        String next = nextCheckList(selectMapper.listEpisodeAll(param.getS("titlepkey"),companyuser.getCompanyPkey(),param.getI("start", 0) + param.getI("limit", 0), param.getI("limit", 0)));

        ret.put("next" , next);

        ret.put("episodelist",episodeList);

        return ret;


    }



    @RequestMapping(value = "/listEpisodeVisible.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listEpisodeVisible(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<String> statusArr = Arrays.asList("NORMAL", "REG");
        List<CMap> episodeList = selectMapper.listEpisodeVisible(param.getS("titlepkey"),companyuser.getCompanyPkey(),statusArr,param.getI("start", 0),param.getI("limit", 0));

        for (CMap episode : episodeList ){
            List<CMap> taskList = selectMapper.listTaskAll(episode);
            for(CMap task : taskList){
                List<CMap> workList = selectMapper.listWorkAll(task);
                task.put("worklist", workList);
            }
            episode.put("tasklist",taskList);
        }

        String next = nextCheckList(selectMapper.listEpisodeVisible(param.getS("titlepkey"),companyuser.getCompanyPkey(),statusArr,param.getI("start", 0) + param.getI("limit", 0), param.getI("limit", 0)));
        ret.put("next" , next);
        ret.put("episodelist",episodeList);

        return ret;


    }


    @RequestMapping(value = "/getEpisode.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getEpisode(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");

        CMap item = selectMapper.getEpisode(param.getS("episodepkey"),companyuser.getCompanyPkey());

        ret.put("episode",item);

        return ret;
    }

    @RequestMapping(value = "/listTaskManager.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listTaskManager(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<CMap> list = selectMapper.listTaskManager(param, companyuser.getCompanyPkey(), param.getI("start", 0),param.getI("limit", 0));
        String next = nextCheckList(selectMapper.listTaskManager(param, companyuser.getCompanyPkey(), param.getI("start", 0) + param.getI("limit", 0), param.getI("limit", 0)));

        ret.put("next" , next);
        ret.put("taskmanagerlist",list);

        return ret;


    }

    @RequestMapping(value = "/listCharacter.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listCharacter(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<CMap> list = selectMapper.listCharacter(companyuser.getCompanyPkey(), param.getI("start", 0), param.getI("limit", 0));
        String next = nextCheckList(selectMapper.listCharacter(companyuser.getCompanyPkey(), param.getI("start", 0) + param.getI("limit", 0), param.getI("limit", 0)));

        ret.put("next", next);

        ret.put("characterlist",list);

        return ret;


    }

    @RequestMapping(value = "/getCharacter.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getCharacter(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");

        CMap item = selectMapper.getCharacter(param);


        List<CMap> stylelist = selectMapper.listCharacterStyle(item);

        for(CMap style:stylelist){
            List<CMap> colorlist = selectMapper.listCharacterStylePalete(style);
            style.put("palette",colorlist);
        }

        item.put("stylelist",stylelist);


        ret.put("character",item);

        return ret;

    }




    @RequestMapping(value = "/listWorker.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorker(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");


        List<CMap> list = selectMapper.listWorker(companyuser.getCompanyPkey(), param.getI("start", 0),param.getI("limit", 0));
        String next = nextCheckList(selectMapper.listWorker(companyuser.getCompanyPkey(), param.getI("start", 0) + param.getI("limit", 0) ,param.getI("limit", 0)));

        ret.put("next", next);
        ret.put("workerlist",list);

        return ret;


    }

    @RequestMapping(value = "/getWorker.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getWorker(@GetParam  CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");

        CMap item = selectMapper.getWorker(param, companyuser.getCompanyPkey());

        ret.put("worker",item);

        return ret;
    }


    @RequestMapping(value = "/listWorkerInTeam.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorkerInTeam(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");

        List<CMap> list = selectMapper.listWorkerInTeam(param.getS("taskmanagerpkey"), companyuser.getCompanyPkey(), param.getI("start",0), param.getI("limit", 0));
        CMap tmName = selectMapper.getTmName(param.getS("taskmanagerpkey"), companyuser.getCompanyPkey());

        String next = nextCheckList(selectMapper.listWorkerInTeam(param.getS("taskmanagerpkey"), companyuser.getCompanyPkey(), param.getI("start",0) + param.getI("limit", 0), param.getI("limit", 0)));

        ret.put("next", next);
        ret.put("memberlist",list);
        ret.put("tmname",tmName.getS("tmname"));

        return ret;

    }


    @RequestMapping(value = "/listWorkgroupForWorker.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorkgroupForWorker(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t", "start", "limit");

        List<CMap> list = selectMapper.listWorkgroupForWorker(param.getS("workerpkey"), companyuser.getCompanyPkey() , param.getI("start",0) , param.getI("limit",0));
        CMap workerName = selectMapper.getWorkerName(param.getS("workerpkey"), companyuser.getCompanyPkey());
        String next = nextCheckList(selectMapper.listWorkgroupForWorker(param.getS("workerpkey"), companyuser.getCompanyPkey() , param.getI("start",0)  + param.getI("limit", 0), param.getI("limit",0)));

        ret.put("next", next);
        ret.put("workergrouplist",list);
        ret.put("workername", workerName.getS("workername"));

        return ret;

    }


    @RequestMapping(value = "/getDashboard.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult getDashboard(@GetParam CParam param) {
        CResult ret = new CResult();

        User companyuser = param.getUser();

        param.validateEmpty("t");

        param.put("companypkey",companyuser.getCompanyPkey());

        List<CMap> aa = selectMapper.listTm(companyuser.getCompanyPkey());

        for(CMap taskManager : aa){
            List<CMap> cutCountList = selectMapper.listTaskManagerCutCount(taskManager.getS("taskmanagerpkey"),param.getS("year"));
            taskManager.put("cutscountlist", cutCountList);
        }

        ret.put("titlecount",selectMapper.getTitleCount(companyuser.getCompanyPkey()).getS("count"));
        ret.put("episodecount",selectMapper.getEpisodeCount(companyuser.getCompanyPkey()).getS("count"));
        ret.put("tmcount",selectMapper.getTaskManagerCount(companyuser.getCompanyPkey()).getS("count"));
        ret.put("workercount",selectMapper.getWorkerCount(companyuser.getCompanyPkey()).getS("count"));
        ret.put("currenttasklist",selectMapper.listCurrentTask(companyuser.getCompanyPkey()));
        ret.put("upcomingtasklist",selectMapper.listUpComingTask(companyuser.getCompanyPkey()));
        ret.put("taskmanageractivities", aa);
        ret.put("activities", selectMapper.listStartDoneIngCount(param));
        ret.put("nowdate", selectMapper.getNowDate().getS("date"));

        return ret;
    }


    @RequestMapping(value = "/listTaskForWorkDoneCount.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listEpisodeTaskKindWorkDoneCount(@GetParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("t", "episodepkey" ,  "start", "limit");

        User companyuser = param.getUser();


        List<CMap> listEpisodeTaskKindWorkDoneCount =
                selectMapper.listEpisodeTaskKindWorkDoneCount(companyuser.getCompanyPkey(), param.getS("episodepkey"), param.getI("start", 0) , param.getI("limit",0));

        String next =
                nextCheckList(selectMapper.listEpisodeTaskKindWorkDoneCount(companyuser.getCompanyPkey(), param.getS("episodepkey"), param.getI("start", 0) + param.getI("limit",0) , param.getI("limit",0)));

        ret.put("next", next);
        ret.put("eppisodecountlist", listEpisodeTaskKindWorkDoneCount);

        return ret;

    }

//    // ????
//    @RequestMapping(value = "/listEpisodeTaskKindEntry.*", method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @CrossOrigin(origins = "*")
//    public CResult listEpisodeTaskKindEntry(@GetParam CParam param){
//
//        CResult ret = new CResult();
//
//        param.validateEmpty("t", "start", "limit");
//
//
//        User companyuser = param.getUser();
//
//        param.put("companypkey", companyuser.getCompanyPkey());
//
//        List<CMap> list = selectMapper.listEpisodeTaskWorkDone(param);
//
//        for (CMap map : list){
//            List<CMap> listEntry = selectMapper.listEntry(map);
//            map.put("entrylist",listEntry);
//        }
//
//        ret.put("episodeentrylist", list);
//
//        return ret;
//
//    }
//
//
//    // ????
//    @RequestMapping(value = "/listEpisodeTaskEntry.*", method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    @CrossOrigin(origins = "*")
//    public CResult listEpisodeTaskEntry(@GetParam CParam param){
//
//        CResult ret = new CResult();
//
//        param.validateEmpty("t");
//
//        User companyuser = param.getUser();
//
//        param.put("companypkey", companyuser.getCompanyPkey());
//
//        List<CMap> list = selectMapper.listEpisodeTaskWorkDone(param);
//
//        for (CMap map : list){
//            List<CMap> listEntry = selectMapper.listEntry(map);
//            map.put("entrylist",listEntry);
//        }
//
//        ret.put("episodeentrylist", list);
//
//        return ret;
//
//    }


    @RequestMapping(value = "/listTaskEntry.*", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin("*")
    public CResult listTaskEntry(@GetParam CParam param){
        CResult ret = new CResult();

        param.validateEmpty( "t", "taskpkey");

        User companyuser = param.getUser();
        param.put("companypkey", companyuser.getCompanyPkey());

        List<CMap> list = selectMapper.listTaskEntry(param );

        ret.put("taskentrylist", list);

        return  ret;
    }


    public String nextCheckList(List<CMap> list){
        String next = "N";
        if(list.size() > 0 ) next = "Y";
        return next;
    }





}
