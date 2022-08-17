package com.icode.toonmanger.mapper;


import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


@Mapper
public interface SelectMapperForCompany {


    @Select("select *,(select count(*) cnt from episode where episode.titlepkey = title.titlepkey) episodecnt from title where companypkey = #{companypkey} order by titlepkey desc limit #{start},#{limit}")
    List<CMap> listTitleAll(String companypkey,int start , int limit);

    @Select("<script>select *,(select count(*) cnt from episode where episode.titlepkey = title.titlepkey) episodecnt   from title where companypkey = #{companypkey} and status in (<foreach item=\"item\" separator=\",\" collection=\"statuslist\">#{item}</foreach> ) order by titlepkey desc limit #{start},#{limit}</script>")
    List<CMap> listTitleWithStatus(String companypkey,List statuslist,int start , int limit);

    @Select("select *,(select count(*) cnt from episode where episode.titlepkey = title.titlepkey) episodecnt from title where companypkey = #{companypkey} and titlepkey = #{titlepkey}")
    CMap getTitle(String titlepkey, String companypkey);

    @Select("select * from episode where titlepkey = (select titlepkey from title where titlepkey = #{titlepkey} and companypkey = #{companypkey}) order by episodepkey desc limit #{start}, #{limit}")
    List<CMap> listEpisodeAll(String titlepkey,String companypkey,int start , int limit);

    @Select("select * from task where episodepkey = #{episodepkey}")
    List<CMap> listTaskAll(CMap param);

    @Select("select * from work where taskpkey = #{taskpkey}")
    List<CMap> listWorkAll(CMap param);



    @Select("<script>select * from episode where titlepkey = (select titlepkey from title where titlepkey = #{titlepkey} and companypkey = #{companypkey}) and status in (<foreach item=\"item\" separator=\",\" collection=\"statuslist\">#{item}</foreach> ) order by episodepkey desc limit #{start}, #{limit} </script>")
    List<CMap> listEpisodeVisible(String titlepkey, String companypkey, List<String> statuslist, int start, int limit);

    @Select("select * from episode where episodepkey = #{episodepkey} and titlepkey =( select titlepkey from title where titlepkey = (select titlepkey from episode where episodepkey = #{episodepkey}) and companypkey = #{companypkey})")
    CMap getEpisode(String episodepkey, String companypkey);

    @Select("select taskmanagerpkey,tmid,name,status,createdate from taskmanager where companypkey = #{companypkey} limit #{start},#{limit}")
    List<CMap> listTaskManager(CMap param,String companypkey,int start ,int limit);


    @Select("select *,(select count(*) from characterstyle where characterstyle.characterpkey = characters.characterpkey) stylecnt from characters where companypkey = #{companypkey} limit #{start}, #{limit}")
    List<CMap> listCharacter(String companypkey,int start , int limit);

    @Select("select * from characters where characterpkey =#{characterpkey} and  companypkey = #{companypkey}")
    CMap getCharacter(CMap param);

    @Select("select * from characterstyle where characterpkey =#{characterpkey}")
    List<CMap> listCharacterStyle(CMap param);

    @Select("select * from color where characterstylepkey = #{characterstylepkey} and version = (select max(version) from color where characterstylepkey = #{characterstylepkey})")
    List<CMap> listCharacterStylePalete(CMap param);

    @Select("select z.workerpkey, z.workerid, z.name, z.status, z.createdate, z.lastactivitydate, czp.* from worker z,companyworkerpool czp where z.workerpkey = czp.workerpkey and  companypkey = #{companypkey} limit #{start},#{limit}")
    List<CMap> listWorker(String companypkey,int start , int limit);


    @Select("select * from worker where workerpkey =#{workerpkey} and  companypkey = #{companypkey}")
    CMap getWorker(CMap param,String companypkey);


    @Select("select wg.*,worker.name from worker join workergroup wg on wg.workerpkey = worker.workerpkey where taskmanagerpkey = #{taskmanagerpkey} and wg.companypkey = #{companypkey} limit #{start}, #{limit}")
    List<CMap> listWorkerInTeam(String taskmanagerpkey,String companypkey, int start, int limit);

    @Select("select tm.name tmname from company cp, taskmanager tm where cp.companypkey = tm.companypkey and tm.taskmanagerpkey = #{taskmanagerpkey} and cp.companypkey = #{companypkey}")
    CMap getTmName(String taskmanagerpkey,String companypkey);

    @Select("select wg.*,tm.name tmname from workergroup wg left outer join taskmanager tm on wg.taskmanagerpkey = tm.taskmanagerpkey  where wg.workerpkey = #{workerpkey} and tm.companypkey = #{companypkey} limit #{start}, #{limit}")
    List<CMap> listWorkgroupForWorker(String workerpkey,String companypkey , int start , int limit);

    @Select("select wk.name workername from worker wk  join companyworkerpool cwp on wk.workerpkey = cwp.workerpkey where wk.workerpkey = #{workerpkey} and cwp.companypkey = #{companypkey}")
    CMap getWorkerName(String workerpkey,String companypkey);



    @Select("select count(t.titlepkey) count from\n" +
            "(select ti.titlepkey from company cp join taskmanager tm on cp.companypkey = tm.companypkey join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey \n" +
            "join episode e on t.episodepkey = e.episodepkey join title ti on e.titlepkey = ti.titlepkey\n" +
            "where cp.companypkey = #{companypkey} and w.status not in ('NOTREADY','DONE','FAIL', 'CANCEL')\n" +
            "group by  ti.titlepkey\n" +
            "order by w.workpkey) t")
    CMap getTitleCount (String companypkey);


    @Select("select count(e.episodepkey) count from (\n" +
            "select e.* from company cp join taskmanager tm on cp.companypkey = tm.companypkey join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey \n" +
            "join episode e on t.episodepkey = e.episodepkey\n" +
            "where cp.companypkey = #{companypkey} and w.status not in ('NOTREADY','DONE','FAIL', 'CANCEL')\n" +
            "group by  e.episodepkey\n" +
            "order by w.workpkey) e")
    CMap getEpisodeCount (String companypkey);


    @Select("select count(tm.taskmanagerpkey) count from company cp join taskmanager tm on cp.companypkey =tm.companypkey where cp.companypkey = #{companypkey}")
    CMap getTaskManagerCount (String companypkey);


    @Select("select count(cpw.workerpkey) count from company cp join companyworkerpool cpw on cp.companypkey = cpw.companypkey where cp.companypkey = #{companypkey}")
    CMap getWorkerCount (String companypkey);


    @Select("select ti.titlepkey, e.episodepkey, t.taskpkey, t.taskmanagerpkey, t.kind from company cp join taskmanager tm on cp.companypkey = tm.companypkey join task t on tm.taskmanagerpkey = t.taskmanagerpkey join work w on t.taskpkey = w.taskpkey \n" +
            "join episode e on t.episodepkey = e.episodepkey join title ti on e.titlepkey = ti.titlepkey\n" +
            "where cp.companypkey = #{companypkey} and w.status not in ('NOTREADY','DONE','FAIL', 'CANCEL')\n" +
            "group by  t.taskpkey\n" +
            "order by w.workpkey")
    List<CMap> listCurrentTask(String companypkey);


    @Select("select t.* from company cp join title ti on cp.companypkey = ti.companypkey join episode e on e.titlepkey = ti.titlepkey join task t on t.episodepkey = e.episodepkey\n" +
            "where cp.companypkey = #{companypkey} and startdate > date_format(current_timestamp(), '%Y%m%d%H%i%s')")
    List<CMap> listUpComingTask(String companypkey);


    @Select("select tm.taskmanagerpkey, tm.name from company cp join taskmanager tm on cp.companypkey = tm.companypkey where cp.companypkey = #{companypkey}")
    List<CMap> listTm(String companypkey);


    @Select("select d.year, d.month,  sum( distinct case when w.donedate >= d.cd and w.donedate < d.nd then w.confirmcutcount else 0 end   ) count from task t, work w , \n" +
            "(select * from\n" +
            "(select b.year, a.month, concat(b.year, lpad(a.month,2,'0'), '01') cd, case when a.month = 12 then concat(b.year+1,'0101') else concat(b.year, lpad(a.month+1,2,'0'), '01') end nd   from \n" +
            "(select 1 month from dual\n" +
            "union\n" +
            "select 2 from dual\n" +
            "union\n" +
            "select 3 from dual\n" +
            "union\n" +
            "select 4 from dual\n" +
            "union\n" +
            "select 5 from dual\n" +
            "union\n" +
            "select 6 from dual\n" +
            "union\n" +
            "select 7 from dual\n" +
            "union\n" +
            "select 8 from dual\n" +
            "union\n" +
            "select 9 from dual\n" +
            "union\n" +
            "select 10 from dual\n" +
            "union\n" +
            "select 11 from dual\n" +
            "union\n" +
            "select 12 from dual\n" +
            ") a , (select #{year} - 1 year ) b) a\n" +
            "union\n" +
            "(select b.year, a.month, concat(b.year, lpad(a.month,2,'0'), '01') cd, case when a.month = 12 then concat(b.year+1,'0101') else concat(b.year, lpad(a.month+1,2,'0'), '01') end nd   from \n" +
            "(select 1 month from dual\n" +
            "union\n" +
            "select 2 from dual\n" +
            "union\n" +
            "select 3 from dual\n" +
            "union\n" +
            "select 4 from dual\n" +
            "union\n" +
            "select 5 from dual\n" +
            "union\n" +
            "select 6 from dual\n" +
            "union\n" +
            "select 7 from dual\n" +
            "union\n" +
            "select 8 from dual\n" +
            "union\n" +
            "select 9 from dual\n" +
            "union\n" +
            "select 10 from dual\n" +
            "union\n" +
            "select 11 from dual\n" +
            "union\n" +
            "select 12 from dual\n" +
            ") a , (select #{year} year ) b) ) d\n" +
            "where  t.taskpkey = w.taskpkey and t.taskmanagerpkey = #{taskmanagerpkey} and w.status in ('DONE', 'FAIL') \n" +
            "group by d.month, d.cd, d.nd\n" +
            "order by d.year, d.month")
    List<CMap> listTaskManagerCutCount(String taskmanagerpkey, String year);


    @Select("select d.year, d.month  month\n" +
            ",count(distinct case when t.startdate like concat(d.cm, '%') then e.titlepkey else null end)  titlestartcnt\n" +
            ",count(distinct (case when t.donedate like concat(d.cm, '%') then e.titlepkey else null end)) titledonecnt\n" +
            ",count(distinct case when t.startdate < concat(d.nm,'01')  and t.donedate >= concat(d.cm,'01') then e.titlepkey else null end) titleingcnt\n" +
            ",count(distinct case when t.startdate like concat(d.cm, '%') then t.episodepkey else null end)  episodestartcnt\n" +
            ",count(distinct (case when t.donedate like concat(d.cm, '%') then t.episodepkey else null end)) episodedonecnt\n" +
            ",count(distinct case when t.startdate < concat(d.nm,'01')  and t.donedate >= concat(d.cm,'01') then t.episodepkey else null end) episodeingcnt\n" +
            "from company cp, title ti, episode e, task t,\n" +
            "(select * from (select y.year, e.month, concat(year,lpad(e.month,2,'0')) cm, case when month = 12 then concat(y.year+1,'01') else concat(y.year,lpad(e.month+1, 2, '0')) end nm from \n" +
            "(SELECT 1 month FROM dual\n" +
            "union \n" +
            "SELECT 2 FROM dual\n" +
            "union \n" +
            "SELECT 3 FROM dual\n" +
            "union \n" +
            "SELECT 4 FROM dual\n" +
            "union \n" +
            "SELECT 5 FROM dual\n" +
            "union \n" +
            "SELECT 6 FROM dual\n" +
            "union \n" +
            "SELECT 7 FROM dual\n" +
            "union \n" +
            "SELECT 8 FROM dual\n" +
            "union \n" +
            "SELECT 9 FROM dual\n" +
            "union \n" +
            "SELECT 10 FROM dual\n" +
            "union \n" +
            "SELECT 11 FROM dual\n" +
            "union \n" +
            "SELECT 12 FROM dual\n" +
            ") e, (select #{year}-1 year from dual) y) a\n" +
            "union\n" +
            "(select y.year, e.month, concat(year,lpad(e.month,2,'0')) cm, case when month = 12 then concat(y.year+1,'01') else concat(y.year,lpad(e.month+1, 2, '0')) end nm from \n" +
            "(SELECT 1 month FROM dual\n" +
            "union \n" +
            "SELECT 2 FROM dual\n" +
            "union \n" +
            "SELECT 3 FROM dual\n" +
            "union \n" +
            "SELECT 4 FROM dual\n" +
            "union \n" +
            "SELECT 5 FROM dual\n" +
            "union \n" +
            "SELECT 6 FROM dual\n" +
            "union \n" +
            "SELECT 7 FROM dual\n" +
            "union \n" +
            "SELECT 8 FROM dual\n" +
            "union \n" +
            "SELECT 9 FROM dual\n" +
            "union \n" +
            "SELECT 10 FROM dual\n" +
            "union \n" +
            "SELECT 11 FROM dual\n" +
            "union \n" +
            "SELECT 12 FROM dual) e, (select #{year} year from dual) y )  ) d\n" +
            "where cp.companypkey = ti.companypkey and ti.titlepkey = e.titlepkey and e.episodepkey = t.episodepkey and cp.companypkey = #{companypkey}\n" +
            "group by d.month, d.cm, d.nm\n" +
            "order by d.year, d.month")
    List<CMap> listStartDoneIngCount(CMap param);

    @Select("select date_format(now(),'%Y%m%d') date")
    CMap getNowDate();

    @Select(" select a.kind, count(*) donecnt from (select  t.*\n" +
            " from episode e , task t , work w, title ti, company cp where e.episodepkey = t.episodepkey and e.titlepkey = ti.titlepkey and ti.companypkey = cp.companypkey and\n" +
            " e.episodepkey = #{episodepkey} and cp.companypkey = #{companypkey} and t.taskpkey = w.taskpkey   group by t.kind) a , work w\n" +
            " where a.taskpkey = w.taskpkey and w.status = 'DONE' group by a.taskpkey limit #{start}, #{limit} ")
    List<CMap> listEpisodeTaskKindWorkDoneCount(String companypkey, String episodepkey, int start, int limit);


    @Select("select te.* from taskentry te, task t , episode e , title ti, company cp where te.taskpkey = t.taskpkey and t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey and ti.companypkey = cp.companypkey and te.taskpkey = #{taskpkey} and cp.companypkey = #{companypkey} and cp.status = 'NORMAL' order by te.isfile ")
    List<CMap> listTaskEntry(CParam param);
}
