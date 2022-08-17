package com.icode.toonmanger.config;

import org.apache.ibatis.session.SqlSessionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;

@RestControllerAdvice
public class ExceptionWebFilter {
    

    @ExceptionHandler(Exception.class)
    public CResult ExceptionHandler(Exception ex) {
        CResult ret = new CResult();
        ret.setStatus("error");
        Throwable ee = ex.getCause();

        if(ee instanceof SQLIntegrityConstraintViolationException){
            //ret.setMsg(((SQLIntegrityConstraintViolationException) ee).getErrorCode()+":중복이거나 올바르지 않은 입력");
            ret.setMsg("중복이거나 올바르지 않은 입력");
            return ret;
        }

        try{
            java.sql.SQLException eee = (java.sql.SQLException)ee;


            if(eee.getErrorCode() == 1264){
                ret.setMsg(eee.getErrorCode()+":값 틀림.");
                return ret;
            }

            if(eee.getErrorCode() == 20001){
                ret.setMsg(eee.getErrorCode()+":시작 날짜가 현재 날짜보다 작습니다.");
                ret.setErrorCode(eee.getErrorCode());
                return ret;
            }

        }catch(Exception e){}


        ret.setMsg(ex.getMessage());
        ex.printStackTrace();
        return ret;
    }
}

