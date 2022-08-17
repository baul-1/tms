package com.icode.toonmanger.mapper;

import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SelectMapperForWorker {

    @Select("select ti.name titlename, ti.thumb , e.name episodename, t.kind, w.*from work w , task t , episode e , title ti where w.taskpkey = t.taskpkey and t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey and w.workerpkey = #{workerpkey} and w.status not in ('NOTREADY', 'DONE','FAIL','CANCEL') order by w.workpkey desc")
    List<CMap> listWorkToDo(String workerpkey, int start, int limit);

    @Select("select ti.name titlename, ti.thumb , e.name episodename, t.kind, w.* from work w , task t , episode e , title ti where w.taskpkey = t.taskpkey and t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey and w.workerpkey = #{workerpkey} and w.status in ('DONE','FAIL','CANCEL') order by w.workpkey desc")
    List<CMap> listWorkGone(String workerpkey, int start, int limit);

    @Select("select wc.workcausepkey, wc.status workcausestatus, wc.createdate workcausecreatedate, wc.executedate, wc.workpkey, wc.fk kind, wc.comment, wc.feedback,\n" +
            "ws.workresultpkey, ws.createdate workresultcreatedate, ws.requestcutcount,\n" +
            "wsd.workschedulepkey, wsd.createdate workschedulecreatedate, wsd.enddate \n" +
            "from work w join workcause wc on w.workpkey = wc.workpkey \n" +
            "left join workresult ws on wc.fp = ws.workresultpkey and wc.fk = ws.kind\n" +
            "left join workschedule wsd on wc.fp = wsd.workschedulepkey and wc.fk = wsd.kind\n" +
            "where w.workpkey = #{workpkey}")
    List<CMap> listWorkCause(CMap param);


    @Select("select wk.name workername, w.name as workname, w.accepteddate, w.donedate, t.taskpkey,  w.workpkey, ti.name as titlename, e.name as episodename, \n" +
            "t.kind , t.name as taskname, w.startdate , w.enddate , w.confirmcutcount,  w.progress, w.priority, w.status, tm.name tmname\n" +
            "from work w \n" +
            "join worker wk on w.workerpkey = wk.workerpkey  \n" +
            "join task t on w.taskpkey = t.taskpkey\n" +
            "join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey \n" +
            "join workergroup wg on t.taskmanagerpkey = wg.taskmanagerpkey and wg.workerpkey = w.workerpkey\n" +
            "join episode e on e.episodepkey = t.episodepkey\n" +
            "join title ti on ti.titlepkey = e.titlepkey\n" +
            "where wg.workerpkey = #{workerpkey} and w.workpkey = #{workpkey} and wg.status in ('NORMAL', 'AWARE') and w.status != 'NOTREADY' ")
    CMap getWork(CParam param);


    @Select("select wc.workcausepkey, wc.status workcausestatus, wc.createdate workcausecreatedate, wc.executedate, wc.workpkey, wc.fk kind, wc.comment, wc.feedback, \n" +
            "us.updatesetpkey, us.name updatesetname, ws.workresultpkey, ws.createdate workresultcreatedate, ws.requestcutcount, \n" +
            "wsd.workschedulepkey, wsd.createdate workschedulecreatedate, wsd.enddate\n" +
            "from work w join workcause wc on w.workpkey = wc.workpkey \n" +
            "left join workresult ws on wc.fp = ws.workresultpkey and wc.fk = ws.kind\n" +
            "left join workschedule wsd on wc.fp = wsd.workschedulepkey and wc.fk = wsd.kind\n" +
            "left join updateset us on ws.updatesetpkey = us.updatesetpkey\n" +
            "where w.workpkey = #{workpkey}")
    List<CMap> listWorkResult(CParam param);


    @Select("select c.* from work w join task t on w.taskpkey = t.taskpkey \n" +
            "join workergroup wg on w.workerpkey = wg.workerpkey and t.taskmanagerpkey = wg.taskmanagerpkey \n" +
            "join workcharacterstyle wcs on w.workpkey = wcs.workpkey join characterstyle cs on wcs.characterstylepkey = cs.characterstylepkey\n" +
            "join characters c on c.characterpkey = cs.characterpkey\n" +
            "where w.workerpkey = #{workerpkey} and w.workpkey = #{workpkey} and wg.status IN ('NORMAL','AWARE')\n" +
            "group by cs.characterpkey")
    List<CMap> getWorkCharacter (CParam param);

    @Select("select cs.characterstylepkey, cs.name, cs.createdate, cs.status, cs.imagesrc,  max(wcs.version) version , cs.characterpkey  from workcharacterstyle wcs, characterstyle cs, characters c where wcs.characterstylepkey = cs.characterstylepkey and cs.characterpkey = c.characterpkey and wcs.workpkey = #{workpkey} and c.characterpkey = #{characterpkey} group by wcs.characterstylepkey")
    List<CMap> listCharacterStyle(CMap param);

    @Select("select * from color where characterstylepkey = #{characterstylepkey} and version = #{version}")
    List<CMap> listStyleColor(CMap param);



    @Select("select c.* from characters c where c.characterpkey in (\n" +
            "select cs.characterpkey from work w \n" +
            "join workergroup wg on w.workerpkey = wg.workerpkey\n" +
            "join workcharacterstyle wcs on w.workpkey = wcs.workpkey\n" +
            "join characterstyle cs on wcs.characterstylepkey = cs.characterstylepkey\n" +
            "join characters c on c.characterpkey = cs.characterpkey\n" +
            "where w.workpkey = #{workpkey} and wg.status in ('NORMAL','AWARE') and wg.workerpkey = #{workerpkey}\n" +
            ")")
    List<CMap> listWorkCharacter(CParam param);


    @Select("select cs.* from work w \n" +
            "join workcharacterstyle wcs on w.workpkey = wcs.workpkey \n" +
            "join characterstyle cs on wcs.characterstylepkey = cs.characterstylepkey\n" +
            "join characters c on  c.characterpkey = cs.characterpkey\n" +
            "where w.workpkey = #{workpkey} and c.characterpkey =  #{characterpkey}")
    List<CMap> listWorkCharacterStyle(String workpkey, String characterpkey);


    @Select("select * from color where characterstylepkey = #{characterStylepkey}")
    List<CMap> listWorkCharacterStyleColor(String characterStylepkey);








    @Select("select w.* from workergroup wg join task t on wg.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey and w.workerpkey = wg.workerpkey \n" +
            "where wg.workerpkey = #{workerpkey} and t.status IN ('REG','ING') and w.status not in ('NOTREADY','DONE','CANCEL','FAIL') and w.priority = 300")
    List<CMap> listWorkPriority(String workerpkey);


    @Select("select w.*, t.kind from workergroup wg , task t , work w where wg.taskmanagerpkey = t.taskmanagerpkey and wg.workerpkey = w.workerpkey and t.taskpkey = w.taskpkey\n" +
            "and w.workerpkey = #{workerpkey} and t.status in ('REG','ING') and w.status = 'REG' and wg.status = 'NORMAL'")
    List<CMap> listWorkREG(String workerpkey);


    @Select("select w.* from workergroup wg join task t on wg.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey and w.workerpkey = wg.workerpkey \n" +
            "where wg.workerpkey = #{workerpkey} and t.status IN ('REG','ING') and w.status = 'ING' order by w.enddate")
    List<CMap> listWorkAscEnddate(String workerpkey);


    @Select("select count(wc.workcausepkey) reqcount from  work w join task t on t.taskpkey = w.taskpkey join workergroup wg on w.workerpkey = wg.workerpkey and t.taskmanagerpkey = wg.taskmanagerpkey join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workerpkey = #{workerpkey} and w.status in ('REG','ACCEPT','ING') and wc.status = 'PENDING' and wc.fk = 'RESULT' and wg.status in ('NORMAL', 'AWARE')")
    CMap getWorkResultReqCount(String workerpkey);

    @Select("select count(wc.workcausepkey) reqcount from  work w join task t on t.taskpkey = w.taskpkey join workergroup wg on w.workerpkey = wg.workerpkey and t.taskmanagerpkey = wg.taskmanagerpkey join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workerpkey = #{workerpkey} and w.status in ('REG','ACCEPT','ING') and wc.status = 'PENDING' and wc.fk = 'SCHEDULE' and wg.status in ('NORMAL', 'AWARE')")
    CMap getWorkScheduleReqCount(String workerpkey);


    @Select("select count(wc.workcausepkey) denycount from  work w join task t on t.taskpkey = w.taskpkey join workergroup wg on w.workerpkey = wg.workerpkey and t.taskmanagerpkey = wg.taskmanagerpkey join workcause wc on w.workpkey = wc.workpkey \n" +
            "where w.workerpkey = #{workerpkey} and w.status in ('REG','ACCEPT') and wc.status = 'PENDING' and wc.fk = 'CANCEL' and wg.status in ('NORMAL', 'AWARE')")
    CMap getWorkDenyReqCount(String workerpkey);


    @Select("select case when sum(w.confirmcutcount) <=> null then 0 else sum(w.confirmcutcount) end  confirmcutcount from workergroup wg join task t on wg.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey and w.workerpkey = wg.workerpkey \n" +
            "where wg.workerpkey = #{workerpkey} and w.status in ('DONE','FAIL')  and \n" +
            "w.donedate >= concat(date_format(now(),'%Y%m'),'01') and w.donedate < concat(date_format(date_add(now(), interval+1 month),'%Y%m'),'01')")
    CMap getWorkConfirmCutCount(String workerpkey);


    @Select("select date_format(now(),'%Y%m%d') date")
    CMap getNowDate();

    @Select("select us.* from work w, worker wk, updateset us where \n" +
            "w.workerpkey = wk.workerpkey and w.workpkey = us.workpkey \n" +
            "and w.workpkey = #{workpkey} and wk.workerpkey = #{workerpkey} and wk.status = 'NORMAL' and w.status != 'NOTREADY' ")
    List<CMap> listUpdateSet(CParam param);


    @Select("select * from (select d.updatesetpkey ,e.*, max(case when d.status = 'DELETE' then 9999999999999999999 else d.entryversionpkey end) entryversionpkey from entryversion ev,entry e,\n" +
            "(select us.updatesetpkey,us.name,uev.entryversionpkey, uev.modifydate, uev.size, uev.status, uev.entrypkey , uev.src, case when us.updatesetpkey = uev.updatesetpkey then 'NEW' else 'OLD' end c\n" +
            "from updateset us,(select usev.updatesetpkey, ev.* from updatesetentryversion usev ,updateset us , entryversion ev where usev.updatesetpkey = us.updatesetpkey and usev.entryversionpkey = ev.entryversionpkey and us.workpkey = #{workpkey} ) uev\n" +
            "where us.workpkey = #{workpkey} and us.updatesetpkey >= uev.updatesetpkey ) d\n" +
            "where ev.entryversionpkey = d.entryversionpkey and ev.entrypkey = e.entrypkey and d.updatesetpkey = #{updatesetpkey} group by entrypkey having entryversionpkey != 9999999999999999999) a,\n" +
            "entryversion ev where a.entryversionpkey = ev.entryversionpkey order  by a.isfile, a.entrypkey  ")
    List<CMap> listUpdateSetEntry(CMap param);


    @Select("select sum( case when  ev.status = 'NORMAL' and ev.token != 'use'  then 1 else 0 end) entryversionsum from updatesetentryversion usev , entryversion ev, updateset us , work w , workergroup wg   where  \n" +
            "usev.entryversionpkey = ev.entryversionpkey and usev.updatesetpkey = us.updatesetpkey and us.workpkey = w.workpkey and w.workerpkey = wg.workerpkey and usev.updatesetpkey = #{updatesetpkey} and wg.workerpkey = #{workerpkey} and wg.status in ('NORMAL', 'AWARE');")
    CMap getEntryUploadAllCheck(CParam param);


}
