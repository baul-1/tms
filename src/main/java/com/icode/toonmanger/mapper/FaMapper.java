package com.icode.toonmanger.mapper;



import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface FaMapper {


    @Select("select * from company order by companypkey desc limit #{start},#{limit}")
    List<CMap> listCompany(int start , int limit);

    @Select("select ti.name titlename, e.name episodename, ws.*, wc.* from work w, workcause wc, workresult ws , task t , episode e , title ti\n" +
            "where  w.workpkey = wc.workpkey and wc.fp = ws.workresultpkey and w.taskpkey = t.taskpkey  and t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey\n" +
            "and wc.status = 'DONE'")
    List<CMap> listWorkResultForAI();

    @Select("select * from entryversion ev, entry e  where \n" +
            "ev.entrypkey = e.entrypkey and \n" +
            "ev.entryversionpkey = #{entryversionpkey}")
    CMap getEntryVersion(CParam param);


    @Select("select ev.entryversionpkey from entryversion ev, entry e, work w, task t , taskmanager tm where\n" +
            "ev.entrypkey = e.entrypkey and e.workpkey = w.workpkey and w.taskpkey = t.taskpkey and t.taskmanagerpkey = tm.taskmanagerpkey\n" +
            "and ev.token = #{uploadtoken} and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL'")
    CMap getTmEntryVersionPkey(CParam param);

    @Select("select ev.entryversionpkey from entryversion ev, entry e, work w , worker wk where \n" +
            "ev.entrypkey = e.entrypkey and e.workpkey = w.workpkey and w.workerpkey = wk.workerpkey \n" +
            "and ev.token = #{uploadtoken} and wk.workerpkey = #{workerpkey} and wk.status = 'NORMAL'")
    CMap getMeEntryVersionPkey(CParam param);


    @Select("select * from entryversion where entryversionpkey = #{entryversionpkey} and src is not null")
    CMap getEntryVersionUploadCheck(CMap param);

    @Select("select te.taskentrypkey from task t , taskmanager tm , taskentry te where t.taskmanagerpkey = tm.taskmanagerpkey and t.taskpkey = te.taskpkey and tm.taskmanagerpkey = #{taskmanagerpkey} and tm.status = 'NORMAL' and te.token = #{uploadtoken}")
    CMap getTaskEntryPkey(CParam param);

    @Select("select * from taskentry where taskentrypkey = #{taskentrypkey} and token = 'use'")
    CMap getTaskEntryUploadCheck(CMap param);


    @Select("select b.* from\n" +
            "(select distinct rel, d.updatesetpkey from entryversion ev,entry e,(select us.updatesetpkey,us.name,uev.entryversionpkey, case when us.updatesetpkey = uev.updatesetpkey then 'NEW' else 'OLD' end c \n" +
            "from updateset us,(select a1.* from updatesetentryversion a1,updateset a2 where a1.updatesetpkey = a2.updatesetpkey and a2.workpkey = #{workpkey} ) uev where us.workpkey = #{workpkey} and us.updatesetpkey >= uev.updatesetpkey ) d \n" +
            "where ev.entryversionpkey = d.entryversionpkey and ev.entrypkey = e.entrypkey and d.updatesetpkey = #{updatesetpkey}) a, (select * from entry e where e.entrypkey = #{entrypkey} and e.isfile = 'N') b\n" +
            "where a.rel = b.entrypkey ")
    CMap getEntryDirectory(CParam param);



    @Select("select * from taskentry where taskentrypkey = #{taskentrypkey} and isfile = 'N'")
    CMap getEntryCDirectory(CParam param);

    @Select("select * from taskentry where rel = #{taskentrypkey} order by isfile")
    List<CMap> listTaskEntryForRel(CMap param);


    @Select("select * from (select d.updatesetpkey ,e.*, max(case when d.status = 'DELETE' then 9999999999999999999 else d.entryversionpkey end) entryversionpkey from entryversion ev,entry e,\n" +
            "(select us.updatesetpkey,us.name,uev.entryversionpkey, uev.modifydate, uev.size, uev.status, uev.entrypkey , uev.src, case when us.updatesetpkey = uev.updatesetpkey then 'NEW' else 'OLD' end c\n" +
            "from updateset us,(select usev.updatesetpkey, ev.* from updatesetentryversion usev ,updateset us , entryversion ev where usev.updatesetpkey = us.updatesetpkey and usev.entryversionpkey = ev.entryversionpkey and us.workpkey = #{workpkey} ) uev\n" +
            "where us.workpkey = #{workpkey} and us.updatesetpkey >= uev.updatesetpkey ) d\n" +
            "where ev.entryversionpkey = d.entryversionpkey and ev.entrypkey = e.entrypkey and d.updatesetpkey = #{updatesetpkey} group by entrypkey having entryversionpkey != 9999999999999999999) a,\n" +
            "entryversion ev where a.entryversionpkey = ev.entryversionpkey order  by a.isfile, a.entrypkey ")
    List<CMap> listUpdateSetEntry(CMap param);



    @Select("select * from (select d.updatesetpkey ,e.*, max(case when d.status = 'DELETE' then 9999999999999999999 else d.entryversionpkey end) entryversionpkey from entryversion ev,entry e,\n" +
            "(select us.updatesetpkey,us.name,uev.entryversionpkey, uev.modifydate, uev.size, uev.status, uev.entrypkey , uev.src, case when us.updatesetpkey = uev.updatesetpkey then 'NEW' else 'OLD' end c\n" +
            "from updateset us,(select usev.updatesetpkey, ev.* from updatesetentryversion usev ,updateset us , entryversion ev where usev.updatesetpkey = us.updatesetpkey and usev.entryversionpkey = ev.entryversionpkey and us.workpkey = #{workpkey} ) uev\n" +
            "where us.workpkey = #{workpkey} and us.updatesetpkey >= uev.updatesetpkey ) d\n" +
            "where ev.entryversionpkey = d.entryversionpkey and ev.entrypkey = e.entrypkey and d.updatesetpkey = #{updatesetpkey} group by entrypkey having entryversionpkey != 9999999999999999999) a,\n" +
            "entryversion ev where a.entryversionpkey = ev.entryversionpkey and a.rel = #{entrypkey} and a.isfile = 'Y'")
    List<CMap> listUpdateSetEntryFile(CMap param);



    @Select("select * from (select d.updatesetpkey ,e.*, max(case when d.status = 'DELETE' then 9999999999999999999 else d.entryversionpkey end) entryversionpkey from entryversion ev,entry e,\n" +
            "(select us.updatesetpkey,us.name,uev.entryversionpkey, uev.modifydate, uev.size, uev.status, uev.entrypkey , uev.src, case when us.updatesetpkey = uev.updatesetpkey then 'NEW' else 'OLD' end c\n" +
            "from updateset us,(select usev.updatesetpkey, ev.* from updatesetentryversion usev ,updateset us , entryversion ev where usev.updatesetpkey = us.updatesetpkey and usev.entryversionpkey = ev.entryversionpkey and us.workpkey = #{workpkey} ) uev\n" +
            "where us.workpkey = #{workpkey} and us.updatesetpkey >= uev.updatesetpkey ) d\n" +
            "where ev.entryversionpkey = d.entryversionpkey and ev.entrypkey = e.entrypkey and d.updatesetpkey = #{updatesetpkey} group by entrypkey having entryversionpkey != 9999999999999999999) a,\n" +
            "entryversion ev where a.entryversionpkey = ev.entryversionpkey and a.rel = #{entrypkey} and a.isfile = 'N' order  by a.isfile, a.entrypkey")
    List<CMap> listlistUpadteSetRelForder(CMap param);


    @Select("select us.*, concat(ti.name, '_', e.name , '_', \n" +
            "case \n" +
            "\twhen t.kind = 'A' then '밑색' \n" +
            "    when t.kind = 'B' then '채색' \n" +
            "    when t.kind = 'ETC' then '기타' \n" +
            "    else '기타' "+
            "    end\n" +
            ", '_', us.name\n" +
            ") zipname\n" +
            "from updateset us, work w, task t, episode e, title ti where us.workpkey = w.workpkey and w.taskpkey = t.taskpkey and t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey  and us.updatesetpkey = #{updatesetpkey}")
    CMap getUpdateSet(CParam param);


    @Select("select te.* from taskentry te, task t where te.taskpkey = t.taskpkey and te.taskpkey = #{taskpkey} and t.status = 'DONE' order by te.isfile")
    List<CMap> listTaskEntry(CParam param);

    @Select("select concat(ti.name, '_', e.name, '_',t.name ) zipname from task t, episode e, title ti where t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey and t.taskpkey = #{taskpkey} and t.status ='DONE'")
    CMap getTaskEntryAllZipName(CParam param);


    @Select("select concat(te.name ) zipname from taskentry te, task t, episode e, title ti where te.taskpkey = t.taskpkey and t.episodepkey = e.episodepkey and e.titlepkey = ti.titlepkey and te.taskentrypkey = #{taskentrypkey} and t.status ='DONE'")
    CMap getTaskEntryFolderZipName(CParam param);

    @Select("select * from taskentry where taskentrypkey = #{taskentrypkey} and isfile = 'Y'")
    CMap getTaskEntryFile(CParam param);


}
