delimiter $$
CREATE TRIGGER COMPANY_INSERT_CREATEDATE
    BEFORE INSERT ON company
    for each row
    begin
		declare cd datetime;

		if(str_to_date(new.createdate, '%Y%m%d%H%i%s') <=> null)then
		    set cd = str_to_date(new.createdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'COMPANY_INSERT_CREATEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.createdate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'COMPANY_INSERT_CREATEDATE',  mysql_errno = 57891;
        end if;

    END$$


delimiter $$
CREATE TRIGGER COMPANYWORKERPOOL_INSERT_JOINDATE
    BEFORE INSERT ON companyworkerpool
    for each row
    begin
		declare jd datetime;

		if(str_to_date(new.joindate, '%Y%m%d%H%i%s') <=> null)then
		    set jd = str_to_date(new.joindate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'COMPANYWORKERPOOL_INSERT_JOINDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.joindate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'EPISODE_INSERT_CREATEDATE',  mysql_errno = 57891;
        end if;

    END$$

delimiter $$
CREATE  TRIGGER EPISODE_INSERT_CREATEDATE
    BEFORE INSERT ON episode
    for each row
    begin
		declare cd datetime;

		if(str_to_date(new.createdate, '%Y%m%d%H%i%s') <=> null)then
		    set cd = str_to_date(new.createdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'EPISODE_INSERT_CREATEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.createdate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'EPISODE_INSERT_CREATEDATE',  mysql_errno = 57891;
        end if;

    END$$

delimiter $$
CREATE  TRIGGER TASK_INSERT_CREATEDATE
    BEFORE INSERT ON task
    for each row
    begin
		declare cd datetime;

		if(  str_to_date(new.createdate, '%Y%m%d%H%i%s') <=> null )then
		    set cd = str_to_date(new.createdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_CREATEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.createdate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_CREATEDATE',  mysql_errno = 58100;
        end if;

    END$$

delimiter $$
CREATE TRIGGER TASK_INSERT_ENDDATE
    BEFORE INSERT ON task
    for each row
    begin
		declare ed datetime;

		if(  str_to_date(new.enddate, '%Y%m%d%H%i%s') <=> null )then
		    set ed = str_to_date(new.enddate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_ENDDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.enddate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_ENDDATE',  mysql_errno = 58102;
        end if;

    END$$

delimiter $$
CREATE  TRIGGER TASK_INSERT_DONEDATE
    BEFORE INSERT ON task
    for each row
    begin
		declare dd datetime;

		if(  str_to_date(new.donedate, '%Y%m%d%H%i%s') <=> null )then
		    set dd = str_to_date(new.donedate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_DONEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.enddate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_ENDDATE',  mysql_errno = 58103;
        end if;

    END$$

delimiter $$
CREATE  TRIGGER TASK_INSERT_STARTDATE
    BEFORE INSERT ON task
    for each row
    begin
		declare sd datetime;

		if(  str_to_date(new.startdate, '%Y%m%d%H%i%s') <=> null )then
		    set sd = str_to_date(new.startdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_STARTDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.startdate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'TASK_INSERT_STARTDATE',  mysql_errno = 20001;
        end if;

    END$$

delimiter $$
CREATE TRIGGER TASKMANAGER_INSERT_CREATEDATE
    BEFORE INSERT ON taskmanager
    for each row
    begin
		declare cd datetime;

		if(str_to_date(new.createdate, '%Y%m%d%H%i%s') <=> null)then
		    set cd = str_to_date(new.createdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'TASKMANAGER_INSERT_CREATEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.createdate, '%Y%m%d%H%i%s') < current_timestamp())then
			SIGNAL SQLSTATE '45000'  SET message_text = 'TASKMANAGER_INSERT_CREATEDATE',  mysql_errno = 57891;
        end if;

    END$$

    delimiter $$
    CREATE TRIGGER TITLE_INSERT_CREATEDATE
    BEFORE INSERT ON title
    for each row
    begin
		declare cd datetime;

		if(str_to_date(new.createdate, '%Y%m%d%H%i%s') <=> null)then
		    set cd = str_to_date(new.createdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'TITLE_INSERT_CREATEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.createdate, '%Y%m%d%H%i%s') < current_timestamp())then
			SIGNAL SQLSTATE '45000'  SET message_text = 'TITLE_INSERT_CREATEDATE',  mysql_errno = 57891;
        end if;


    END$$

delimiter $$
CREATE TRIGGER WORK_INSERT_PROGRESS
    BEFORE INSERT ON work
    for each row
    begin
		if(new.progress != 0)then
			SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_PROGRESS',  mysql_errno = 18218;
        end if;

    END$$

delimiter $$
CREATE TRIGGER WORK_INSERT_CREATEDATE
    BEFORE INSERT ON work
    for each row
    begin
		declare cd datetime;

		if( str_to_date(new.createdate, '%Y%m%d%H%i%s') <=> null )then
		    set cd = str_to_date(new.createdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_CREATEDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.createdate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_CREATEDATE',  mysql_errno = 57891;
        end if;

END$$

delimiter $$
CREATE  TRIGGER WORK_INSERT_STARTDATE
    BEFORE INSERT ON work
    for each row
    begin
		declare sd datetime;

		if( str_to_date(new.startdate, '%Y%m%d%H%i%s') <=> null )then
		    set sd = str_to_date(new.startdate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_STARTDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.startdate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_STARTDATE',  mysql_errno = 57891;
        end if;

END$$

delimiter $$
CREATE TRIGGER WORK_INSERT_ENDDATE
    BEFORE INSERT ON work
    for each row
    begin
		declare ed datetime;

		if( str_to_date(new.enddate, '%Y%m%d%H%i%s') <=> null )then
		    set ed = str_to_date(new.enddate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_ENDDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.enddate, '%Y%m%d%H%i%s') < current_timestamp()) then
			SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_INSERT_ENDDATE',  mysql_errno = 57891;
        end if;

END$$

delimiter $$
CREATE trigger WORK_INSERT_START_END
	BEFORE INSERT on work
    for each row
    begin
		if(str_to_date(new.startdate, '%Y%m%d%H%i%s') > str_to_date(new.enddate, '%Y%m%d%H%i%s'))then
			signal sqlstate '45000' set message_text = 'WORK_INSERT_START_END', mysql_errno = 57891;
        end if;
    end$$

delimiter $$
CREATE TRIGGER `WORK_UPDATE_PROGRESS` BEFORE UPDATE ON `work` FOR EACH ROW begin
		if((old.progress > new.progress) or (new.progress > 100) or new.progress <=> null )then
			SIGNAL SQLSTATE '45000'  SET message_text = 'WORK_UPDATE_PROGRESS',  mysql_errno = 18218;
        end if;
    END$$

    CREATE DEFINER=`icode`@`%` TRIGGER WORKERGROUP_INSERT_JOINDATE
    BEFORE INSERT ON workergroup
    for each row
    begin
		declare jd datetime;

		if(str_to_date(new.joindate, '%Y%m%d%H%i%s') <=> null)then
		    set jd = str_to_date(new.joindate, '%Y%m%d%H%i%s');
		    SIGNAL SQLSTATE '45000'  SET message_text = 'WORKERGROUP_INSERT_JOINDATE',  mysql_errno = 18218;
		elseif(str_to_date(new.joindate, '%Y%m%d%H%i%s') < current_timestamp())then
			SIGNAL SQLSTATE '45000'  SET message_text = 'WORKERGROUP_INSERT_JOINDATE',  mysql_errno = 57891;
        end if;

    END$$