package com.icode.toonmanger.controller;

import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import com.icode.toonmanger.config.CResult;
import com.icode.toonmanger.config.GetParam;
import com.icode.toonmanger.mapper.FaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/s")
public class SelectController {

    @Autowired
    FaMapper fa;

    @RequestMapping(value = "/listCompany.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listCompany(@GetParam Map map) {
        CResult param = new CResult();
        param.setByRequestParam(map);

        List<CMap> list = fa.listCompany(0,10);

        param.put("list",list);

        return param;


    }

    @RequestMapping(value = "/listWorkResultForAI.*", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public CResult listWorkResultForAI(@GetParam CParam map) {
        CResult param = new CResult();

        map.getUser();

        param.put("list",fa.listWorkResultForAI());


        return param;


    }
}
