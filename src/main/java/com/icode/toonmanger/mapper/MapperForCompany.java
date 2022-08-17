package com.icode.toonmanger.mapper;



import com.icode.toonmanger.config.CMap;
import com.icode.toonmanger.config.CParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;


@Mapper
public interface MapperForCompany {
    @Insert("insert into title(name,thumb, companypkey) values(#{name},#{thumb},#{companypkey})")
    void addTitle(String name,String thumb,String companypkey);

    @Update("update title set name=#{name}, thumb=#{thumb} where titlepkey = #{titlepkey} and companypkey = #{companypkey}")
    void modifyTitle(String name, String titlepkey, String companypkey, String thumb );

    @Update("update title set status=#{status} where titlepkey = #{titlepkey} and companypkey = #{companypkey}")
    void setTitleStatus(String titlepkey, String companypkey,String status);



    @Insert("insert into episode(name, titlepkey) select #{name},titlepkey from title where titlepkey = #{titlepkey} and companypkey = #{companypkey} and status != 'INVISIBLE'")
    void addEpisode(String name,String titlepkey,String companypkey);


    @Update("update episode set name = #{name} where episodepkey = #{episodepkey} and titlepkey = (Select titlepkey from title where titlepkey = #{titlepkey} and companypkey = #{companypkey})")
    void modifyEpisode(String episodepkey,String name, String titlepkey, String companypkey);


    @Update("update episode,title set episode.status=#{status} where episode.titlepkey = title.titlepkey and episode.episodepkey = #{episodepkey} and title.companypkey = #{companypkey}")
    void setEpisodeStatus(String episodepkey, String companypkey,String status);



    @Insert("insert into taskmanager(name,tmid,tmpw,companypkey) values(#{name},#{tmid},#{tmpw},#{companypkey})")
    void addTaskManager(String name,String tmid,String tmpw,String companypkey);


    @Update("update taskmanager set name = #{name} , tmid= #{tmid} ,tmpw=#{tmpw} where taskmanagerpkey = #{taskmanagerpkey} and companypkey = #{companypkey}")
    void modifyTaskManager(String taskmanagerpkey,String name,String tmid,String tmpw, String companypkey);

    @Update("update taskmanager set status = #{status} where taskmanagerpkey = #{taskmanagerpkey} and companypkey = #{companypkey}")
    void setTaskManagerStatus(String taskmanagerpkey, String companypkey,String status);


    @Insert("insert into characters(name, thumb, companypkey) values(#{name}, #{thumb}, #{companypkey})")
    void addCharacter(String name, String thumb, String companypkey);

    @Update("update characters set name = #{name}, thumb=#{thumb} where characterpkey = #{characterpkey} and companypkey = #{companypkey}")
    void modifyCharacter(String characterpkey,String name,String companypkey , String thumb);

    @Insert("insert into characterstyle(name,characterpkey,imagesrc) values(#{name},#{characterpkey},#{imagesrc})")
    @Options(useGeneratedKeys=true,keyProperty = "characterstylepkey")
    int addCharacterStyle(CParam param);

    @Insert("insert into color(name,colorhex,code,category,characterstylepkey) values(#{name},#{colorhex},#{code},#{category},#{characterstylepkey})")
    void addCharacterStyleColor(CParam param);



    @Insert("insert into worker(workerid,workerpw,name) values(#{workerid},#{workerpw},#{name})")
    @Options(useGeneratedKeys=true,keyProperty = "workerpkey")
    int addWorker(CParam param);

    @Insert("insert into companyworkerpool(workerpkey,companypkey) values(#{workerpkey},#{companypkey})")
    void joinWorkerToCompany(String workerpkey, String companypkey);


    //todo:: check workerpool
    @Insert("insert into workergroup(taskmanagerpkey,companypkey,workerpkey) values(#{taskmanagerpkey},#{companypkey},#{workerpkey})")
    int assignWorkerToTaskManager(String companypkey,String taskmanagerpkey,String workerpkey);




    @Update("update workergroup wg , (select wg.companypkey, wg.taskmanagerpkey, wg.workerpkey from company c join taskmanager tm on c.companypkey = tm.companypkey join workergroup wg on tm.taskmanagerpkey = wg.taskmanagerpkey    \n" +
            "where wg.companypkey = #{companypkey} and wg.taskmanagerpkey = #{taskmanagerpkey} and wg.workerpkey = #{workerpkey} and c.status = 'NORMAL' and wg.status = 'REQKICK') a\n" +
            "set status = 'NOTTEAM' where wg.companypkey = a.companypkey and wg.taskmanagerpkey = a.taskmanagerpkey and wg.workerpkey = a.workerpkey")
    void setWorkGroupStatusToNotteam(CParam param);


    @Update("update workergroup wg, \n" +
            "(select wg.companypkey, wg.taskmanagerpkey, wg.workerpkey from workergroup wg join worker w on wg.workerpkey = w.workerpkey join taskmanager tm on tm.taskmanagerpkey = wg.taskmanagerpkey join company c  on tm.companypkey = c.companypkey\n" +
            "where wg.companypkey = #{companypkey} and wg.taskmanagerpkey = #{taskmanagerpkey} and wg.workerpkey = #{workerpkey} and w.status = 'NORMAL' and tm.status = 'NORMAL' and c.status = 'NORMAL' and wg.status = 'NOTTEAM') a\n" +
            " set wg.status = 'NORMAL', wg.joindate = date_format(current_timestamp(),'%Y%m%d%H%i%s') where wg.companypkey = a.companypkey and wg.taskmanagerpkey = a.taskmanagerpkey and wg.workerpkey = a.workerpkey ")
    void rejoinWorkerToWorkgroup(CParam param);



    @Update("update characterstyle set version = version + 1 where  characterstylepkey = \n" +
            "(select cs.characterstylepkey from characterstyle cs join characters c on cs.characterpkey =  c.characterpkey join company cp on c.companypkey = cp.companypkey\n" +
            "where cp.companypkey = #{companypkey} and cs.characterstylepkey = #{characterstylepkey})")
    int setCharacterStyleVersionUp(CParam param);


    @Insert("insert into color(name,colorhex,code,category,characterstylepkey, version, status) select #{name}, #{colorhex}, #{code}, #{category}, #{characterstylepkey}, cs.version, 'COMPANY' \n" +
            "from characterstyle cs join characters c on cs.characterpkey = c.characterpkey join company cp on c.companypkey = cp.companypkey where cp.companypkey = #{companypkey} and cs.characterstylepkey = #{characterstylepkey}")
    void addColor(CParam param);


    @Select("select * from company where name = #{name} and status ='NORMAL' ")
    CMap getCompanyByName(CParam param);


}
