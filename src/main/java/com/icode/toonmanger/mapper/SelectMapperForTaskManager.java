package com.icode.toonmanger.mapper;



import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface SelectMapperForTaskManager {

    @Select("select *,(select count(*) cnt from episode where episode.titlepkey = title.titlepkey and episode.status = 'NORMAL') episodecnt from title where companypkey = #{companypkey} order by titlepkey desc limit #{start},#{limit}")
    List<CMap> listTitle(String companypkey, int start, int limit);

    @Select("select * from episode where titlepkey = (select titlepkey from title where titlepkey = #{titlepkey} and companypkey = #{companypkey}) and status = 'NORMAL' order by episodepkey desc")
    List<CMap> listEpisode(String titlepkey, String companypkey, int start, int limit);

    @Select("select *,(select count(*) from characterstyle where characterstyle.characterpkey = characters.characterpkey) stylecnt from characters where companypkey = #{companypkey}")
    List<CMap> listCharacter(String companypkey, int start, int limit);


    @Select("select w.*, wk.name workername from work w join worker wk on w.workerpkey = wk.workerpkey where w.taskpkey = #{taskpkey} and w.status != 'NOTREADY' order by startdate asc")
    List<CMap> listWorkInTask(String taskpkey);

    @Select("select w.*, wk.name workername from work w , worker wk where w.workerpkey = wk.workerpkey and w.taskpkey = 70 and w.status != 'NOTREADY' and ( (w.startdate >= #{startdate} and w.startdate <= #{enddate} ) or (w.enddate >= #{startdate} and w.enddate <= #{enddate}) ) order by w.startdate asc ")
    List<CMap> listWorkInTaskFromTo(String taskpkey, String startdate, String enddate);


    @Select("select z.workerid, czp.status, czp.joindate, z.workerpkey, z.createdate, z.name  from worker z,workergroup czp where z.workerpkey = czp.workerpkey and  taskmanagerpkey = #{taskmanagerpkey} and czp.status != 'NOTTEAM'")
    List<CMap> listWorker(String taskmanagerpkey);

    @Select("select z.workerid, czp.status, czp.joindate, z.workerpkey, z.createdate, z.name  from worker z,workergroup czp where z.workerpkey = czp.workerpkey and  taskmanagerpkey = #{taskmanagerpkey} and czp.status = 'NORMAL'")
    List<CMap> listNormalMember(String taskmanagerpkey);

    @Select("select * from worker where workerpkey =#{workerpkey} and  companypkey = #{companypkey}")
    CMap getWorker(CMap param, String companypkey);


    @Select("select * from workgroup where taskmanagerpkey = #{taskmanagerpkey}")
    List<CMap> listTaskManagerMember(String taskmanagerpkey, String companypkey);


    @Select("select a.*, truncate((case when b.progress <=> null then 0 else b.progress end) * 100, 0)  progress  from \n" +
            "(select ti.titlepkey, ti.name titlename, e.episodepkey, e.name episodename,\n" +
            " t.taskpkey, t.name taskname, t.status taskstatus, t.createdate taskcreatedate, t.startdate taskstartdate, t.enddate taskenddate, t.donedate taskdonedate, t.kind taskkind, t.taskmanagerpkey,\n" +
            " t.commemo, t.tmmemo\n" +
            "from task t join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey join episode e on e.episodepkey = t.episodepkey \n" +
            "join title ti on ti.titlepkey = e.titlepkey \n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status ='NORMAL'\n" +
            "order by t.taskpkey) a\n" +
            "\n" +
            "left outer join\n" +
            "\n" +
            "(select t.taskpkey, ifnull(round((sum(case when w.status in ('DONE','FAIL','CANCEL') then 1 else 0 end) / sum(case when w.status != 'NOTREADY' then 1 else 0 end) ) ,2), 0)progress\n" +
            "from taskmanager tm, task t, work w   \n" +
            "where tm.taskmanagerpkey = t.taskmanagerpkey and  t.taskpkey = w.taskpkey and  tm.status = 'NORMAL' and  tm.taskmanagerpkey = #{taskmanagerpkey} group by t.taskpkey) b\n" +
            "on a.taskpkey = b.taskpkey order by a.taskpkey desc" )
    List<CMap> listTask(String companypkey, String taskmanagerpkey, String startdate, String enddate);


    @Select("select a.*, truncate((case when b.progress <=> null then 0 else b.progress end) * 100, 0)  progress from (select ti.titlepkey, ti.name titlename, e.episodepkey, e.name episodename,\n" +
            "t.taskpkey, t.name taskname, t.status taskstatus, t.createdate taskcreatedate, t.startdate taskstartdate, t.enddate taskenddate, t.donedate taskdonedate, t.kind taskkind, t.taskmanagerpkey,\n" +
            "t.commemo, t.tmmemo \n" +
            "from taskmanager tm, title ti, episode e, task t where tm.taskmanagerpkey = t.taskmanagerpkey and  t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey \n" +
            "and tm.taskmanagerpkey = #{taskmanagerpkey} and ( (t.startdate >= #{startdate} and t.startdate <= #{enddate}) or (t.enddate >= #{startdate} and t.enddate <= #{enddate}) ) \n" +
            "union\n" +
            "select ti.titlepkey, ti.name titlename, e.episodepkey, e.name episodename,\n" +
            "t.taskpkey, t.name taskname, t.status taskstatus, t.createdate taskcreatedate, t.startdate taskstartdate, t.enddate taskenddate, t.donedate taskdonedate, t.kind taskkind, t.taskmanagerpkey,\n" +
            "t.commemo, t.tmmemo  \n" +
            "from taskmanager tm, title ti, episode e, task t, work w  where tm.taskmanagerpkey = t.taskmanagerpkey and  t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey  and t.taskpkey = w.taskpkey \n" +
            "and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and  ( (w.startdate >= #{startdate} and w.startdate <= #{enddate} ) or (w.enddate >= #{startdate} and w.enddate <= #{enddate}) ) group by t.taskpkey) a\n" +
            "\n" +
            "left outer join\n" +
            " \n" +
            "(select t.taskpkey, ifnull(round((sum(case when w.status in ('DONE','FAIL','CANCEL') then 1 else 0 end) / sum(case when w.status != 'NOTREADY' then 1 else 0 end) ) ,2), 0)progress\n" +
            "from taskmanager tm, task t, work w   \n" +
            "where tm.taskmanagerpkey = t.taskmanagerpkey and  t.taskpkey = w.taskpkey and  tm.status = 'NORMAL' and  tm.taskmanagerpkey = #{taskmanagerpkey} group by t.taskpkey) b\n" +
            "on a.taskpkey = b.taskpkey order by a.taskpkey asc")
    List<CMap> listTaskFromTo(String taskmanagerpkey, String startdate, String enddate, int start, int limit);



    @Select("select wk.name as workername, w.accepteddate, w.donedate, w.name as workname, w.workpkey, ti.name as titlename, e.name as episodename, \n" +
            "t.kind , t.name as taskname, w.startdate , w.enddate , w.confirmcutcount,  w.progress, w.priority, w.status \n" +
            "from work w\n" +
            "join worker wk on w.workerpkey = wk.workerpkey \n" +
            "join task t on w.taskpkey = t.taskpkey \n" +
            "join episode e on e.episodepkey = t.episodepkey\n" +
            "join title ti on ti.titlepkey = e.titlepkey\n" +
            "where t.taskmanagerpkey = (select taskmanagerpkey from taskmanager where taskmanagerpkey = #{taskmanagerpkey} and status  = 'NORMAL') and w.workpkey = #{workpkey} and w.status != 'NOTREADY' ")
    CMap getWork(CParam param);


    @Select("select wc.workcausepkey, wc.status workcausestatus, wc.createdate workcausecreatedate, wc.executedate, wc.workpkey, wc.fk kind, wc.comment, wc.feedback, \n" +
            "us.updatesetpkey, us.name updatesetname, ws.workresultpkey, ws.createdate workresultcreatedate, ws.requestcutcount, \n" +
            "wsd.workschedulepkey, wsd.createdate workschedulecreatedate, wsd.enddate\n" +
            "from work w join workcause wc on w.workpkey = wc.workpkey \n" +
            "left join workresult ws on wc.fp = ws.workresultpkey and wc.fk = ws.kind\n" +
            "left join workschedule wsd on wc.fp = wsd.workschedulepkey and wc.fk = wsd.kind\n" +
            "left join updateset us on ws.updatesetpkey = us.updatesetpkey\n" +
            "where w.workpkey = #{workpkey}")
    List<CMap> listWorkCause(CMap param);


    @Select("select * from characters where \n" +
            "characterpkey in (SELECT characterpkey FROM characterstyle WHERE characterstylepkey in (select characterstylepkey from workcharacterstyle where workpkey = #{workpkey} ))\n" +
            "and companypkey = (select companypkey from taskmanager where taskmanagerpkey = #{taskmanagerpkey} and status = 'NORMAL')")
    List<CMap> listWorkCharacter(CParam param);


    @Select("select cs.characterstylepkey, cs.name, cs.createdate, cs.status, cs.imagesrc,  max(wcs.version) version , cs.characterpkey  from workcharacterstyle wcs, characterstyle cs, characters c where wcs.characterstylepkey = cs.characterstylepkey and cs.characterpkey = c.characterpkey and wcs.workpkey = #{workpkey} and c.characterpkey = #{characterpkey} group by wcs.characterstylepkey")
    List<CMap> listCharacterStyle(CMap param);


    @Select("select * from color where characterstylepkey = #{characterstylepkey} and version = #{version}")
    List<CMap> listStyleColor(CMap param);


    @Select("select * from characters where companypkey =  (select companypkey from taskmanager where taskmanagerpkey = #{taskmanagerpkey} and status = 'NORMAL' )")
    List<CMap> listTmCharacter(String taskmanagerpkey);


    @Select("select * from characterstyle where characterpkey = #{characterpkey}")
    List<CMap> listTmcharacterstyle(CMap param);


    @Select("select w.* from taskmanager tm join task t  on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and  tm.status = 'NORMAL' and t.status in ('REG','ING') and w.status not in ('DONE','FAIL', 'CANCEL', 'NOTREADY') and w.priority = 300")
    List<CMap> listHighPriorityTask(String taskmanagerpkey);


    @Select("select t.*, truncate( (case when sum(case when w.status in ('DONE','FAIL','CANCEL') then 1 else 0 end) / count(w.taskpkey) <=> null then 0 \n" +
            "else sum(case when w.status in ('DONE','FAIL','CANCEL') then 1 else 0 end) / count(w.taskpkey) end) * 100 , 0)  progress \n" +
            "from taskmanager tm join task t on tm.taskmanagerpkey = t.taskmanagerpkey left outer join work w on t.taskpkey = w.taskpkey \n" +
            "where tm.status = 'NORMAL' and  tm.taskmanagerpkey = #{taskmanagerpkey} and w.status != 'NOTREADY' group by t.taskpkey order by t.taskpkey")
    List<CMap> listTaskProgress(String taskmanagerpkey);


    @Select("select w.*,  wk.name workername , wc.* from taskmanager tm join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey join workcause wc on w.workpkey = wc.workpkey join worker wk on wk.workerpkey = w.workerpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and w.status in ('REG','ACCEPT', 'ING') and wc.status = 'PENDING' and wc.fk = 'RESULT' ")
    List<CMap> getResultReqCount(String taskmanagerpkey);

    @Select("select w.*,  wk.name workername , wc.* from taskmanager tm join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey join workcause wc on w.workpkey = wc.workpkey join worker wk on wk.workerpkey = w.workerpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and w.status in ('REG','ACCEPT', 'ING') and wc.status = 'PENDING' and wc.fk = 'SCHEDULE' ")
    List<CMap> getScheduleReqCount(String taskmanagerpkey);


    @Select("select  w.*,  wk.name workername , wc.* from taskmanager tm join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey join workcause wc on w.workpkey = wc.workpkey join worker wk on wk.workerpkey = w.workerpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and w.status in ('REG','ACCEPT') and wc.status = 'PENDING' and wc.fk = 'CANCEL'")
    List<CMap> getDenyReqCount(String taskmanagerpkey);


    @Select("select w.* from taskmanager tm join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and w.status = 'ING' order by enddate asc")
    List<CMap> listRecentWork(String taskmanagerpkey);


    @Select("select wk.workerpkey, wk.name, count(case when w.status not in ('DONE', 'FAIL','CANCEL', 'NOTREADY' ) then w.workpkey else null end) count \n" +
            "from taskmanager tm join workergroup wg on tm.taskmanagerpkey = wg.taskmanagerpkey\n" +
            "join task t on tm.taskmanagerpkey = t.taskmanagerpkey \n" +
            "join work w on t.taskpkey = w.taskpkey and wg.workerpkey = w.workerpkey\n" +
            "join worker wk on w.workerpkey = wk.workerpkey\n" +
            "where tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and wg.status in ('NORMAL','AWARE') group by w.workerpkey")
    List<CMap> listMember(String taskmanagerpkey);


    @Select("SELECT ti.titlepkey, ti.name titlename, e.episodepkey, e.name episodename,\n" +
            "t.taskpkey, t.name taskname, t.status taskstatus, t.createdate taskcreatedate, t.startdate taskstartdate, t.enddate taskenddate, \n" +
            "t.donedate taskdonedate, t.kind taskkind, t.taskmanagerpkey, t.commemo, t.tmmemo,\n" +
            "truncate((case when count(case when w.status in ('DONE','FAIL','CANCEL') then w.workpkey else null end)  / count(case when w.status != 'NOTREADY' then w.workpkey else null end) <=> null then 0\n" +
            "else count(case when w.status in ('DONE','FAIL','CANCEL')  then w.workpkey else null end)  / count(case when w.status != 'NOTREADY' then w.workpkey else null end) end ) * 100, 0)  progress \n" +
            "FROM task t left outer join  work w on t.taskpkey = w.taskpkey join taskmanager tm on t.taskmanagerpkey = tm.taskmanagerpkey \n" +
            "join episode e on t.episodepkey = e.episodepkey join title ti on ti.titlepkey = e.titlepkey\n" +
            "where t.taskpkey = #{taskpkey} and t.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL'  group  by w.taskpkey")
    CMap getTaskDetail(CParam param);


    @Select("select date_format(now(),'%Y%m%d') date")
    CMap getNowDate();


    @Select("select sum( case when ev.token != 'use' then 1 else 0 end ) entryversionsum from work w join task t on w.taskpkey = t.taskpkey join taskmanager tm on t.taskmanagerpkey = t.taskmanagerpkey\n" +
            "left join entry e on w.workpkey = e.workpkey left join entryversion ev on e.entrypkey = ev.entrypkey where\n" +
            "w.workpkey= #{workpkey} and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL'")
    CMap getEntryUploadAllCheck(CMap param);

    @Select("select sum(case when te.token != 'use' then 1 else 0 end) checksum from taskentry te, task t, taskmanager tm  where te.taskpkey = t.taskpkey and t.taskmanagerpkey = tm.taskmanagerpkey\n" +
            "and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and te.taskpkey = #{taskpkey} and te.isfile = 'Y'")
    CMap getTaskEntryUploadAllCheck(CParam param);


    @Select("select us.* from work w, task t, taskmanager tm , updateset us where \n" +
            "w.taskpkey = t.taskpkey and t.taskmanagerpkey = tm.taskmanagerpkey and w.workpkey = us.workpkey \n" +
            "and w.workpkey = #{workpkey} and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and w.status != 'NOTREADY'")
    List<CMap> listUpdateSet(CParam param);


    @Select("select * from (select d.updatesetpkey ,e.*, max(case when d.status = 'DELETE' then 9999999999999999999 else d.entryversionpkey end) entryversionpkey from entryversion ev,entry e,\n" +
            "(select us.updatesetpkey,us.name,uev.entryversionpkey, uev.modifydate, uev.size, uev.status, uev.entrypkey , uev.src, case when us.updatesetpkey = uev.updatesetpkey then 'NEW' else 'OLD' end c\n" +
            "from updateset us,(select usev.updatesetpkey, ev.* from updatesetentryversion usev ,updateset us , entryversion ev where usev.updatesetpkey = us.updatesetpkey and usev.entryversionpkey = ev.entryversionpkey and us.workpkey = #{workpkey} ) uev\n" +
            "where us.workpkey = #{workpkey} and us.updatesetpkey >= uev.updatesetpkey ) d\n" +
            "where ev.entryversionpkey = d.entryversionpkey and ev.entrypkey = e.entrypkey and d.updatesetpkey = #{updatesetpkey} group by entrypkey having entryversionpkey != 9999999999999999999) a,\n" +
            "entryversion ev where a.entryversionpkey = ev.entryversionpkey order  by a.isfile, a.entrypkey ")
    List<CMap> listUpdateSetEntry(CMap param);


    @Select("select te.* from taskentry te , task t, taskmanager tm where te.taskpkey = t.taskpkey and t.taskmanagerpkey = tm.taskmanagerpkey and te.taskpkey = #{taskpkey} and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' order by te.isfile")
    List<CMap> listTaskEntry(CParam param);


}
