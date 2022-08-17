package com.icode.toonmanger.mapper;


import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface MapperForWorker {



    @Insert("insert into work(workpkey,startdate,enddate,taskpkey,workerpkey,companypkey,name,progress) select workpkey,startdate,enddate,taskpkey,workerpkey,companypkey,name,case when progress > #{progress} then -1 when #{progress} >99 then -1 else #{progress} end from work where workpkey = #{workpkey} and workerpkey = #{workerpkey} on duplicate key update progress = values(progress)")
    int setProgress(CParam map);


    @Insert("insert into workresult(requestcutcount, workpkey, updatesetpkey ) select #{requestcutcount},s.workpkey, #{updatesetpkey} from\n" +
            "(select w.workpkey, sum( case when wc.workpkey = #{workpkey} and wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum, sum(case when wc.status = 'DONE'  and wc.fk = 'RESULT' then 1 else 0 end) donesum \n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "left outer join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workpkey = #{workpkey} and wg.workerpkey  = #{workerpkey} and w.status = 'ING'  and wg.status in ('NORMAL','AWARE')) s where s.pensum =  0 and s.donesum = 0")
    @Options(useGeneratedKeys=true,keyProperty = "workresultpkey")
    int addWorkResult(CParam param);


    @Insert("insert into workcause (workpkey, workerpkey,fp,fk, comment) select  s.workpkey, #{workerpkey}, #{workresultpkey}, 'RESULT', #{comment} from\n" +
            "(select w.workpkey, sum( case when wc.workpkey = #{workpkey} and wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum, sum(case when wc.status = 'DONE'  and wc.fk = 'RESULT' then 1 else 0 end) donesum \n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "left outer join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workpkey = #{workpkey} and wg.workerpkey  = #{workerpkey} and  w.status = 'ING'  and wg.status in ('NORMAL','AWARE')) s where s.pensum =  0 and s.donesum = 0")
    int addWorkCauseForWorkResult(CParam map);



    @Insert("insert into workschedule (enddate, kind, workpkey) select #{enddate}, 'SCHEDULE' ,s.workpkey from\n" +
            "(select w.workpkey, sum( case when wc.workpkey = #{workpkey} and wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum\n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "left outer join workcause wc on wc.workpkey = w.workpkey \n" +
            "where wg.workerpkey = #{workerpkey} and w.workpkey = #{workpkey} and  wg.status in ('NORMAL','AWARE') and  w.status in ('REG','ACCEPT','ING') ) s where s.pensum =  0 ")
    @Options(useGeneratedKeys = true, keyProperty = "workschedulepkey")
    int addWorkSchedule(CParam param);


    @Insert("insert into workcause (workpkey, workerpkey,fp,fk, comment) select  s.workpkey, #{workerpkey} , #{workschedulepkey}, 'SCHEDULE', #{comment} from \n" +
            "(select w.workpkey, sum( case when wc.workpkey = #{workpkey} and wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum\n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "left outer join workcause wc on wc.workpkey = w.workpkey \n" +
            "where wg.workerpkey = #{workerpkey} and w.workpkey = #{workpkey} and  wg.status in ('NORMAL','AWARE') and  w.status in ('REG','ACCEPT','ING') ) s where s.pensum =  0 ")
    int addWorkCauseForWorkSchedule(CParam map);







    @Update("update work set progress = case when #{progress} > progress and #{progress} < 100 then #{progress} else progress end where workpkey = #{workpkey} and workerpkey = \n" +
            "(select workerpkey from workergroup where workerpkey = #{workerpkey} and (status = 'NORMAL' or status ='AWARE') and \n" +
            " taskmanagerpkey = (select taskmanagerpkey from task where taskpkey =  (select taskpkey from work where workpkey = #{workpkey})))\n" +
            " and status = 'ING'")
    int updateWorkProgress(CParam param);



    @Insert("insert into workresult (workpkey,kind) select s.workpkey , 'CANCEL' from\n" +
            "(select w.workpkey, sum( case when wc.workpkey = #{workpkey} and wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum\n" +
            "from work w join  task t on  w.taskpkey = t.taskpkey  join workergroup wg on w.workerpkey = wg.workerpkey and t.taskmanagerpkey = wg.taskmanagerpkey\n" +
            "left outer join workcause wc on wc.workpkey = w.workpkey \n" +
            "where w.workpkey = #{workpkey} and  wg.workerpkey = #{workerpkey} and  w.status in ('REG','ACCEPT') and wg.status in ('NORMAL','AWARE')) s where s.pensum = 0")
    @Options(useGeneratedKeys=true, keyProperty = "workresultpkey")
    int addWorkCancelForWorkResult(CParam param);



    @Insert("insert into workcause (workpkey, workerpkey,fp,fk, comment) select  s.workpkey,  #{workerpkey}, #{workresultpkey}, 'CANCEL', #{comment} from\n" +
            "(select  w.workpkey, sum( case when wc.workpkey = #{workpkey} and wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum, sum(case when wc.status = 'DONE' and wc.fk = 'CANCEL' then 1 else 0 end) cansum \n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey \n" +
            "left outer join workcause wc on wc.workpkey = w.workpkey\n" +
            "where wg.workerpkey = #{workerpkey} and w.workpkey = #{workpkey}  and wg.status in ('NORMAL','AWARE') and w.status in ('REG', 'ACCPET') ) s where s.pensum =  0 and s.cansum = 0")
    int addWorkCancelForWorkCause(CParam map);


    @Select("select * from worker where workerid = #{meid} and status = 'NORMAL'")
    CMap getWorkerById(CParam param);


    @Update("update work set status = 'ACCEPT', accepteddate = date_format(current_timestamp(),'%Y%m%d%H%i%s') where workpkey = #{workpkey} and workerpkey = #{workerpkey} and status = 'REG'")
    int acceptWork(CParam map);

    @Update("update entryversion set src = #{src}, token = 'use' where entryversionpkey = #{entryversionpkey}")
    int setEntryVersionSrc(CParam param);


    @Insert("insert into updateset (kind, workpkey, name, companypkey, workerpkey) select 'ME', s.workpkey, #{updatesetname}, cwp.companypkey, cwp.workerpkey from\n" +
            "(select w.workpkey, sum( case when wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum, sum(case when wc.status = 'DONE'  and wc.fk = 'RESULT' then 1 else 0 end) donesum \n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "left outer join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workpkey = #{workpkey} and wg.workerpkey  = #{workerpkey} and  w.status = 'ING'  and wg.status in ('NORMAL','AWARE') ) s, companyworkerpool cwp \n" +
            "where s.pensum =  0 and s.donesum = 0 and cwp.workerpkey = #{workerpkey}")
    @Options(useGeneratedKeys = true , keyProperty = "updatesetpkey")
    int addUpdateSet(CParam param);


    @Insert("insert into entryversion (size, version, modifydate, entrypkey, token, status ) select #{size}, v.maxver + 1, #{modifydate}, #{entrypkey}, #{uploadtoken}, #{status} from\n" +
            "(select w.workpkey, sum( case when wc.status = 'PENDING' and wc.ck = 0 then 1 else 0 end ) pensum, sum(case when wc.status = 'DONE'  and wc.fk = 'RESULT' then 1 else 0 end) donesum \n" +
            "from work w join task t on w.taskpkey = t.taskpkey join workergroup wg on wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "left outer join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workpkey = #{workpkey} and wg.workerpkey  = #{workerpkey} and  w.status not in  ('DONE', 'FAIL', 'CANCEL')  and wg.status in ('NORMAL','AWARE') ) s, companyworkerpool cwp,\n" +
            "(select max(ev.version) maxver from entry e, entryversion ev where e.entrypkey = ev.entrypkey and e.entrypkey = #{entrypkey}) v\n" +
            "where s.pensum =  0 and s.donesum = 0 and cwp.workerpkey = #{workerpkey}")
    @Options(useGeneratedKeys = true, keyProperty = "entryversionpkey")
    int addEntryVersionUp(CMap param);

    @Update("update entry e, (select entrypkey, version from entryversion where entryversionpkey = #{entryversionpkey}) v set e.current = v.version , e.latest = v.version  where e.entrypkey = v.entrypkey")
    void setEntryCurrent(CMap param);

    @Insert("insert into entry (name, isfile, workpkey, rel) \n" +
            "select #{name}, #{isfile}, w.workpkey, #{rel} \n" +
            "from work w ,task t , workergroup wg where w.taskpkey = t.taskpkey and t.taskmanagerpkey = wg.taskmanagerpkey and w.workerpkey = wg.workerpkey and w.workpkey = #{workpkey}\n" +
            "and w.workerpkey = #{workerpkey} and wg.status in ('NORMAL','AWARE')")
    @Options(useGeneratedKeys = true, keyProperty = "entrypkey")
    int addEntry(CMap param);

    @Insert("insert into entryversion (size, modifydate, entrypkey, token)  values  ( #{size}, #{modifydate}, #{entrypkey}, #{uploadtoken})")
    @Options(useGeneratedKeys = true, keyProperty = "entryversionpkey")
    int addEntryVersion(CMap param);
    ;

    @Insert("insert into updatesetentryversion (updatesetpkey, entryversionpkey) values (#{updatesetpkey}, #{entryversionpkey})")
    void addUpdateSetEntryVersion(CMap param);



}
