package com.icode.toonmanger.controller;

import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.config.CResult;
import com.icode.toonmanger.config.PostParam;
import com.icode.toonmanger.mapper.MapperForCompany;
import com.icode.toonmanger.security.User;
import com.icode.toonmanger.service.ServiceForCompany;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;


@RestController
@RequestMapping(value = "/com")
public class CompanyController extends Controller{

    @Autowired
    MapperForCompany mapper;


    @Autowired
    ServiceForCompany service;



    @RequestMapping(value = "/login.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult login(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("name","pw");

        CMap company = mapper.getCompanyByName(param);


        if(company == null){
            ret.put("status","FAIL");
        }else {
            if(BCrypt.checkpw(param.getS("pw"), company.getS("compw"))){
                company.remove("compw");

                try {
                    String token = makeSession("COM", company);
                    ret.put("status", "SUCCESS");
                    ret.put("t", token);

                }catch(Exception e){
                    ret.put("status","FAIL2");
                }
            }else{
                ret.put("status","FAIL");
            }
        }

        ret.setStatus("OK");

        return ret;
    }


    @RequestMapping(value = "/addTitle.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addTitle(@PostParam CParam param){

            CResult ret = new CResult();
            User companyuser = param.getUser();

            param.validateEmpty("name", "t");


            mapper.addTitle(param.getS("name"),param.getS("thumb"),companyuser.getCompanyPkey());

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/modifyTitle.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult editTitle(@PostParam CParam param){
            CResult ret = new CResult();

            param.validateEmpty("titlepkey","name", "t");


            User companyuser = param.getUser();

            mapper.modifyTitle(param.getS("name"),param.getS("titlepkey"),companyuser.getCompanyPkey(), param.getS("thumb"));

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/setTitleStatusToInvisible.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult  setTitleStatusToInvisible(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("titlepkey", "t");


            User companyuser = param.getUser();

            mapper.setTitleStatus(param.getS("titlepkey"),companyuser.getCompanyPkey(),"INVISIBLE");

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/setTitleStatusToNormal.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setTitleStatusToNormal(@PostParam CParam param){

            CResult ret = new CResult();

            param.validateEmpty("titlepkey", "t");

            User companyuser = param.getUser();

            mapper.setTitleStatus(param.getS("titlepkey"),companyuser.getCompanyPkey(),"REG");

            ret.setStatus("OK");

            return ret;

    }




    @RequestMapping(value = "/addEpisode.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addEpisode(@PostParam CParam param){


            CResult ret = new CResult();



            param.validateEmpty("name", "titlepkey", "t");


            User companyuser = param.getUser();

            mapper.addEpisode(param.getS("name"),param.getS("titlepkey"),companyuser.getCompanyPkey());

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/modifyEpisode.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult modifyEpisode(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("name", "titlepkey", "t");

            User companyuser = param.getUser();
            mapper.modifyEpisode(param.getS("episodepkey"),param.getS("name"),param.getS("titlepkey"),companyuser.getCompanyPkey());
            ret.setStatus("OK");

            return ret;

    }



    @RequestMapping(value = "/setEpisodeStatusToInvisible.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setEpisodeStatusToInvisible(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("episodepkey", "t");

            User companyuser = param.getUser();

            mapper.setEpisodeStatus(param.getS("episodepkey"),companyuser.getCompanyPkey(),"INVISIBLE");

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/setEpisodeStatusToNormal.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setEpisodeStatusToNormal(@PostParam CParam param){


            CResult ret = new CResult();
            param.validateEmpty("episodepkey", "t");



            User companyuser = param.getUser();

            mapper.setEpisodeStatus(param.getS("episodepkey"),companyuser.getCompanyPkey(),"NORMAL");

            ret.setStatus("OK");

            return ret;

    }

    @RequestMapping(value = "/setEpisodeStatusToReg.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setEpisodeStatusToReg(@PostParam CParam param){


        CResult ret = new CResult();
        param.validateEmpty("episodepkey", "t");


        User companyuser = param.getUser();

        mapper.setEpisodeStatus(param.getS("episodepkey"),companyuser.getCompanyPkey(),"REG");

        ret.setStatus("OK");

        return ret;

    }




    @RequestMapping(value = "/addTaskManager.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addTaskManager(@PostParam CParam param){


            CResult ret = new CResult();


            param.validateEmpty("name", "tmid", "tmpw", "t");

            User companyuser = param.getUser();


            mapper.addTaskManager(param.getS("name"),param.getS("tmid"), BCrypt.hashpw(param.getS("tmpw"),BCrypt.gensalt()),companyuser.getCompanyPkey());


            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/modifyTaskManager.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult modifyTaskManager(@PostParam CParam param){

            CResult ret = new CResult();

            User companyuser = param.getUser();

            param.validateEmpty("name", "tmid", "tmpw", "t");

            mapper.modifyTaskManager(param.getS("taskmanagerpkey"),param.getS("name"),param.getS("tmid"),BCrypt.hashpw(param.getS("tmpw"),BCrypt.gensalt()),companyuser.getCompanyPkey());
            ret.setStatus("OK");

            return ret;

    }

    @RequestMapping(value = "/setTaskManagerStatusToNotAllowed.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setTaskManagerStatusToNotAllowed(@PostParam CParam param){


            CResult ret = new CResult();


            param.validateEmpty("taskmanagerpkey", "t");


            User companyuser = param.getUser();

            mapper.setTaskManagerStatus(param.getS("taskmanagerpkey"),companyuser.getCompanyPkey(),"NOTALLOWED");

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/setTaskManagerStatusToNormal.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult setTaskManagerStatusToNormal(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("taskmanagerpkey","t");

            User companyuser = param.getUser();

            mapper.setTaskManagerStatus(param.getS("taskmanagerpkey"),companyuser.getCompanyPkey(),"NORMAL");

            ret.setStatus("OK");

            return ret;

    }



    @RequestMapping(value = "/addCharacter.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addCharacter(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("name", "t");

            User companyuser = param.getUser();

            mapper.addCharacter(param.getS("name"),param.getS("thumb") , companyuser.getCompanyPkey());

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/modifyCharacter.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult modifyCharacter(@PostParam CParam param){

            CResult ret = new CResult();

            param.validateEmpty("name", "characterpkey", "t");

            User companyuser = param.getUser();
            mapper.modifyCharacter(param.getS("characterpkey"),param.getS("name"),companyuser.getCompanyPkey(), param.getS("thumb"));
            ret.setStatus("OK");

            return ret;

    }



    @RequestMapping(value = "/addWorker.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addWorker(@PostParam CParam param){

            CResult ret = new CResult();

            param.validateEmpty("workerid", "name", "workerpw", "t");


            User companyuser = param.getUser();

            service.addWorker(param, companyuser.getCompanyPkey());


            ret.setStatus("OK");

            return ret;

    }




    @RequestMapping(value = "/assignWorkerToTaskManager.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult assignWorkerToTaskManager(@PostParam CParam param){


            CResult ret = new CResult();

            param.validateEmpty("workerpkey", "taskmanagerpkey", "t");

            User companyuser = param.getUser();

            mapper.assignWorkerToTaskManager(companyuser.getCompanyPkey(),param.getS("taskmanagerpkey"),param.getS("workerpkey"));

            ret.setStatus("OK");

            return ret;

    }


    @RequestMapping(value = "/addStyle.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult addStyle(@PostParam CParam param){

            CResult ret = new CResult();

            param.validateEmpty("characterpkey","name","imagesrc", "t");


            User companyuser = param.getUser();

            service.addStyle(param,companyuser.getCompanyPkey());


            ret.setStatus("OK");

            return ret;

    }

    @RequestMapping(value = "/updatePalette.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult updatePalette(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("characterstylepkey","colorlist", "t");

        User companyuser = param.getUser();
        param.put("companypkey" , companyuser.getCompanyPkey());

        service.addCharacterStyleColor(param);

        ret.setStatus("OK");

        return ret;

    }

    @RequestMapping(value = "/dismissWorkerToTaskManager.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult dismissWorkerToTaskManager(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("taskmanagerpkey","workerpkey", "t");

        User companyuser = param.getUser();
        param.put("companypkey" , companyuser.getCompanyPkey());
        mapper.setWorkGroupStatusToNotteam(param);

        ret.setStatus("OK");
        return ret;

    }


    @RequestMapping(value = "/rejoinWorkerToWorkgroup.*", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult rejoinWorkerToWorkgroup(@PostParam CParam param){

        CResult ret = new CResult();

        param.validateEmpty("taskmanagerpkey","workerpkey", "t");

        User companyuser = param.getUser();
        param.put("companypkey" , companyuser.getCompanyPkey());
        mapper.rejoinWorkerToWorkgroup(param);

        ret.setStatus("OK");
        return ret;

    }




}
