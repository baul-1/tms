package com.icode.toonmanger.mapper;


import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface MapperForTaskManager {

    @Insert("insert into task(name, startdate,enddate,kind,taskmanagerpkey,episodepkey) values(#{name},#{startdate},#{enddate},#{kind},#{taskmanagerpkey},#{episodepkey})")
    @Options(useGeneratedKeys=true,keyProperty = "taskpkey")
    int addTask(CParam param);


    @Insert("insert into work(name,startdate,enddate, status, taskpkey,workerpkey,companypkey,priority) select #{name},#{startdate},#{enddate}, 'NOTREADY',(select taskpkey from task where taskmanagerpkey = wg.taskmanagerpkey and taskpkey = #{taskpkey} and status != 'DONE' and startdate <= #{startdate}),workerpkey,companypkey,#{priority} from workergroup wg where taskmanagerpkey = #{taskmanagerpkey} and status='NORMAL' and workerpkey = #{workerpkey}")
    @Options(useGeneratedKeys=true,keyProperty = "workpkey")
    int addWork(CParam param);

    @Insert("insert into workschedule (enddate, status, workpkey) select #{enddate}, 'INIT', w.workpkey from \n" +
            "work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and w.workpkey = #{workpkey} and tm.status = 'NORMAL' and w.status = 'REG' ")
    int addWorkSchedule (CParam param);

    @Insert("insert into updateset (workpkey) values (#{workpkey})")
    @Options(useGeneratedKeys = true, keyProperty = "updatesetpkey")
    void addUpdateSet(CParam param);



    @Update("update work w set w.status = 'REG' where w.workpkey = #{workpkey} and w.status = 'NOTREADY'")
    void setWorkStatusNotRead(CParam param);



    @Insert("insert into entry (name, isfile, workpkey, rel)  \n" +
            "select #{name}, #{isfile}, w.workpkey, #{rel} from  work w, task t, taskmanager tm where w.taskpkey = t.taskpkey \n" +
            "and t.taskmanagerpkey = tm.taskmanagerpkey and w.workpkey = #{workpkey} and t.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL'")
    @Options(useGeneratedKeys = true, keyProperty = "entrypkey")
    int addEntry(CMap param);

    @Insert("insert into entryversion (size, modifydate, entrypkey, token)  values  ( #{size}, #{modifydate}, #{entrypkey}, #{uploadtoken})")
    @Options(useGeneratedKeys = true, keyProperty = "entryversionpkey")
    int addEntryVersion(CMap param);

    @Insert("insert into updatesetentryversion (updatesetpkey, entryversionpkey) values (#{updatesetpkey}, #{entryversionpkey})")
    void addUpdateSetEntryVersion(CMap param);



    @Insert("insert into taskentry (name, isfile, rel, token, taskpkey)  select #{name}, 'N', #{rel}, 'use', taskpkey from task t , taskmanager tm where t.taskmanagerpkey = tm.taskmanagerpkey and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and t.taskpkey = #{taskpkey} and t.status = 'DONE'")
    @Options(useGeneratedKeys = true, keyProperty = "taskentrypkey")
    void  addTaskEntryFolder(CMap param);

    @Insert(" insert into taskentry (name, isfile, size, modifydate, rel, token, taskpkey)  select #{name}, #{isfile}, #{size}, #{modifydate}, #{rel}, #{uploadtoken}, taskpkey \n" +
            " from task t , taskmanager tm where t.taskmanagerpkey = tm.taskmanagerpkey and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and t.taskpkey = #{taskpkey} and t.status = 'DONE'")
    @Options(useGeneratedKeys = true, keyProperty = "taskentrypkey")
    void addTaskEntryFile(CMap param);


    @Update("update work set name = #{workname} where workpkey = #{workpkey} and taskpkey in (select taskpkey from task where taskmanagerpkey = #{taskmanagerpkey} and donedate ='99991231')")
    void modifyWork(CParam map);

    @Update("update work set status = #{status} where workpkey = #{workpkey} and taskpkey in (select taskpkey from task where taskmanagerpkey = #{taskmanagerpkey} and donedate ='99991231')")
    void setWorkStatusEndFlags(CParam map);

    @Update("update work set priority = #{priority} where workpkey = #{workpkey} and taskpkey in (select taskpkey from task where taskmanagerpkey = #{taskmanagerpkey} and donedate ='99991231')")
    int setWorkPriority(CParam param);

    @Insert("insert into characters(name,companypkey) values(#{name},#{companypkey})")
    void addCharacter(String name,String companypkey);


    @Update("update characters set name = #{name} where characterspkey = #{characterpkey} and companypkey = #{companypkey}")
    void modifyCharacter(String characterpkey,String name,String companypkey);



//    @Update("update workergroup set status = #{status} where taskmanagerpkey = #{taskmanagerpkey} and workerpkey = #{workerpkey}")
//    void setWorkergroupStatus(String workerpkey,String taskmanagerpkey,String status);


    @Insert("insert into workcharacterstyle(workpkey, characterstylepkey, version ) select w.workpkey, cs.characterstylepkey, cs.version from work w \n" +
            "join task t on w.taskpkey = t.taskpkey\n" +
            "join taskmanager tm on  t.taskmanagerpkey = tm.taskmanagerpkey \n" +
            "join characters c join characterstyle cs on c.characterpkey = cs.characterpkey and c.companypkey = tm.companypkey\n" +
            "where w.workpkey = #{workpkey} and cs.characterstylepkey = #{characterstylepkey} and t.taskmanagerpkey = #{taskmanagerpkey} and w.status not in ('NOTREADY','CANCEL', 'DONE', 'FAIL', 'ING') \n" +
            "and cs.status = 'NORMAL' and tm.status = 'NORMAL'")
    void addWorkCharacterStyleFromCompanyCharacter(CParam param);


    @Insert("insert into characterstyle(name,characterpkey, imagesrc ) select #{name}, #{characterpkey},#{imagesrc} from company where\n" +
            "companypkey = (select cp.companypkey from taskmanager tm join company cp on tm.companypkey = cp.companypkey where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL') and\n" +
            "companypkey = (select cp.companypkey from characters c join company cp on c.companypkey = cp.companypkey  where c.characterpkey = #{characterpkey}) ")
    @Options(useGeneratedKeys=true,keyProperty = "characterstylepkey")
    int addCharacterStyle(CParam param);


    @Insert("insert into color(name,colorhex,code,category,characterstylepkey) \n" +
            "SELECT #{name}, #{colorhex}, #{code}, #{category}, #{characterstylepkey} \n" +
            "from taskmanager where taskmanagerpkey = (select taskmanagerpkey from taskmanager where taskmanagerpkey = #{taskmanagerpkey} and status = 'NORMAL')\n" +
            "and companypkey = (select companypkey from characters where characterpkey =  (select characterpkey from characterstyle where  characterstylepkey = #{characterstylepkey}))")
    void addCharacterStyleColor(CParam param);





    @Update("update workcause wc, workresult ws , (select wc.workcausepkey from work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey \n" +
            "join workresult ws on ws.workpkey = w.workpkey join workcause wc on wc.fp = ws.workresultpkey \n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and w.workpkey = #{workpkey} and tm.status = 'NORMAL' and w.status = 'ING' and wc.status = 'PENDING' and wc.fk =  'RESULT' and 0 =\n" +
            "(select sum(case when status = 'DONE' and fk = 'RESULT' then 1 else 0 end) pendingcnt  from workcause where workpkey = #{workpkey} group by workpkey)) a\n" +
            "set wc.status = 'DONE', wc.executedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), wc.ck = a.workcausepkey, wc.feedback = #{workresultfeedback} where wc.workcausepkey = a.workcausepkey \n" +
            "and wc.fk = 'RESULT' and wc.fp = ws.workresultpkey")
    int setWorkResultForWorkCauseStatusToDone(CParam param);


    @Update("update work w , workresult ws, workcause wc set w.status = 'DONE',  w.confirmcutcount = ws.requestcutcount , w.progress = 100, w.donedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), wc.feedback = #{feedback}\n" +
            "where w.workpkey = (select workpkey from work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey  \n" +
            "where w.workpkey = #{workpkey} and w.status = 'ING' and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL') and ws.workresultpkey = #{workresultpkey}\n" +
            "and wc.fp = #{workresultpkey} and wc.fk = 'RESULT'")
    void setWorkStatusToDone(CParam param);


    @Update("update work w , workresult ws, workcause wc  set w.status = 'FAIL',  w.confirmcutcount = ws.requestcutcount , w.progress = 100, w.donedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), wc.feedback = #{feedback}\n" +
            "where w.workpkey = (select workpkey from work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey  \n" +
            "where w.workpkey = #{workpkey} and w.status = 'ING' and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL') and ws.workresultpkey = #{workresultpkey}\n" +
            "and wc.fp = #{workresultpkey} and wc.fk = 'RESULT'")
    void setWorkStatusToFail(CParam param);




    @Update("update workcause wc, (select wc.workcausepkey from work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey join workschedule wsd on w.workpkey = wsd.workpkey\n" +
            "join workcause wc on w.workpkey = wc.workpkey \n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and w.workpkey = #{workpkey} and wsd.workschedulepkey = #{workschedulepkey} and tm.status = 'NORMAL' and w.status in ('REG','ACCEPT','ING') \n" +
            "and wsd.status = 'NORMAL' and wc.status = 'PENDING' and wc.fk = 'SCHEDULE')  a\n" +
            "set wc.status = 'DONE', wc.executedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), wc.ck = a.workcausepkey, wc.feedback = #{feedback}  where wc.workcausepkey = a.workcausepkey")
    int setWorkScheduleForWorkCauseStatusToDone(CParam param);


    @Update("update work w, workschedule wsd set w.enddate = wsd.enddate where wsd.workschedulepkey = \n" +
            "(select wsd.workschedulepkey from workschedule wsd join workcause wc on wsd.workschedulepkey = wc.fp \n" +
            "join work w on w.workpkey = wsd.workpkey join task t on t.taskpkey = w.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey\n" +
            "where wsd.workschedulepkey = #{workschedulepkey} and w.workpkey = #{workpkey} and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and wc.status = 'DONE' and wc.fk = 'SCHEDULE' )\n" +
            "and w.workpkey = #{workpkey} and w.status in ('REG','ACCEPT', 'ING') ")
    int setWorkEndDate(CParam param);






    @Update("update workcause set status = 'REJECTED', executedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), ck = #{workcausepkey}, feedback = #{feedback}  where  workcausepkey =\n" +
            "(select wc.workcausepkey from workcause wc join work w on wc.workpkey = w.workpkey join task t on w.taskpkey = t.taskpkey join taskmanager tm on tm.taskmanagerpkey = t.taskmanagerpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and wc.workcausepkey = #{workcausepkey} and wc.status = 'PENDING' and tm.status = 'NORMAL' and w.status in ('REG','ACCEPT','ING') )")
    int setWorkCauseStatusToRejected(CParam param);






    @Update("update workcause set status = 'DONE', executedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), ck = #{workcausepkey} , feedback = #{feedback} where workcausepkey = \n" +
            "(select wc.workcausepkey from work w join workcause wc on w.workpkey = wc.workpkey join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey\n" +
            "where w.workpkey = #{workpkey} and wc.workcausepkey = #{workcausepkey} and tm.taskmanagerpkey = #{taskmanagerpkey} and w.status in ('REG','ACCEPT') AND tm.status = 'NORMAL' and wc.status = 'PENDING' and wc.fk = 'CANCEL' )")
    int setWorkCancelForWorkCauseStatusToDone(CParam param);


    @Update("update work set status = 'CANCEL' , progress = 100, donedate = date_format(current_timestamp(),'%Y%m%d%H%i%s') where workpkey = \n" +
            "(select wc.workpkey from work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on tm.taskmanagerpkey = t.taskmanagerpkey join workcause wc on wc.workpkey = w.workpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and wc.workcausepkey = #{workcausepkey} and w.workpkey = #{workpkey} and tm.status = 'NORMAL' and w.status in ('REG','ACCEPT')  and wc.status = 'DONE' and wc.fk = 'CANCEL')")
    void setAcceptWorkStatusToCancel(CParam param);





    @Update("update workcause set status = 'REJECTED', executedate = date_format(current_timestamp(),'%Y%m%d%H%i%s'), ck = #{workcausepkey} , feedback = #{feedback}  where workcausepkey = \n" +
            "(select wc.workcausepkey from work w join workcause wc on w.workpkey = wc.workpkey join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey\n" +
            "where w.workpkey = #{workpkey} and wc.workcausepkey = #{workcausepkey} and tm.taskmanagerpkey = #{taskmanagerpkey} and w.status in ('REG','ACCEPT') and tm.status = 'NORMAL' and wc.status = 'PENDING' and wc.fk = 'CANCEL' )")
    int setWorkCancelForWorkCauseStatusToRejected(CParam param);











    @Update("update workergroup wg, (select wg.taskmanagerpkey, wg.workerpkey from workergroup wg join taskmanager tm on wg.taskmanagerpkey = tm.taskmanagerpkey where tm.taskmanagerpkey = #{taskmanagerpkey} and wg.workerpkey = #{workerpkey} and tm.status = 'NORMAL' and wg.status ='NORMAL' ) a \n" +
            "set wg.status = 'AWARE' where wg.taskmanagerpkey = a.taskmanagerpkey and wg.workerpkey = a.workerpkey")
    void setWorkGroupStatusToAware(CParam param);


    @Update("update workergroup wg, (select wg.taskmanagerpkey, wg.workerpkey from workergroup wg join taskmanager tm on wg.taskmanagerpkey = tm.taskmanagerpkey \n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and wg.workerpkey = #{workerpkey} and tm.status = 'NORMAL' and wg.status ='AWARE'AND 0 =  \n" +
            "(select count(w.workpkey) workcnt from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on w.workerpkey = wg.workerpkey and t.taskmanagerpkey = wg.taskmanagerpkey \n" +
            "where wg.workerpkey =#{workerpkey} and wg.taskmanagerpkey = #{taskmanagerpkey} and w.status not in ('NOTREADY','CANCEL', 'DONE', 'FAIL') and wg.status in ('AWARE'))) a \n" +
            "set wg.status = 'REQKICK' where wg.taskmanagerpkey = a.taskmanagerpkey and wg.workerpkey = a.workerpkey")
    int setWorkGroupStatusToReqkick(CParam param);




    @Update("update characterstyle set version = version + 1 where  characterstylepkey = \n" +
            "(select cs.characterstylepkey from characterstyle cs join characters c on cs.characterpkey = c.characterpkey join company cp on c.companypkey = cp.companypkey \n" +
            "join taskmanager tm on tm.companypkey = cp.companypkey where tm.taskmanagerpkey = #{taskmanagerpkey} and cs.characterstylepkey = #{characterstylepkey} and tm.status = 'NORMAL')")
    int setCharacterStyleVersionUp(CParam param);


    @Insert("insert into color(name,colorhex,code,category,characterstylepkey, version, status) select #{name}, #{colorhex}, #{code}, #{category}, #{characterstylepkey}, cs.version, 'TASKMANAGER' \n" +
            "from characterstyle cs join characters c on cs.characterpkey = c.characterpkey join company cp on c.companypkey = cp.companypkey \n" +
            "join taskmanager tm on tm.companypkey = cp.companypkey where tm.taskmanagerpkey = #{taskmanagerpkey} and cs.characterstylepkey = #{characterstylepkey} and tm.status = 'NORMAL'")
    void addColor(CParam param);




    @Update("update task t join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey, \n" +
            "(select count(case when w.status in ('REG','ACCEPT','ING') then w.workpkey else null end) ingcnt, count(w.workpkey) workcnt\t\n" +
            "from work w left outer join task t on w.taskpkey = t.taskpkey  where w.taskpkey = #{taskpkey}) a \n" +
            "set t.status = 'DONE', t.donedate = date_format(now(),'%Y%m%d%H%i%s') where t.taskpkey = #{taskpkey} and t.taskmanagerpkey = #{taskmanagerpkey} and a.ingcnt = 0 and a.workcnt > 0 and t.status = 'ING' and t.donedate = '99991231' and tm.status = 'NORMAL'")
    int setTaskStatusToDone(CParam param);

    @Update("update workergroup wg, (select wg.taskmanagerpkey, wg.workerpkey from taskmanager tm , workergroup wg where tm.taskmanagerpkey = wg.taskmanagerpkey and tm.taskmanagerpkey = #{taskmanagerpkey} and wg.workerpkey = #{workerpkey} and tm.status = 'NORMAL') a\n" +
            "set wg.status = 'NORMAL' where wg.taskmanagerpkey = a.taskmanagerpkey and wg.workerpkey = a.workerpkey and wg.status = 'AWARE'")
    void setMemberStatusToNormal(CParam param);


    @Select("select tm.* from taskmanager tm, company cp where tm.companypkey = cp.companypkey and tm.tmid =  #{tmid} and cp.name = #{comname} and tm.status = 'NORMAL'")
    CMap getTaskmanagerByTmId(CParam param);



    @Delete("delete t from task t, taskmanager tm, (select count(w.workpkey) cnt from task t join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey  left outer join  work w on t.taskpkey = w.taskpkey  \n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and t.taskpkey = #{taskpkey} and tm.status ='NORMAL' ) a  where \n" +
            "tm.taskmanagerpkey = #{taskmanagerpkey} and t.taskpkey = #{taskpkey} and tm.status ='NORMAL' and t.status in ('REG','ING') and 0 = a.cnt")
    void deleteTask(CParam param);


    @Update("update entryversion set src = #{src}, token = 'use' where entryversionpkey = #{entryversionpkey} ")
    int setEntryVersionSrc(CParam param);


    @Update("update taskentry set src = #{src} , token = 'use' where taskentrypkey = #{taskentrypkey}")
    int setTaskEntrySrc(CMap param);

}
