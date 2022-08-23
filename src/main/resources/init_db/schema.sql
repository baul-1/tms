
CREATE SCHEMA IF NOT EXISTS `tms` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `tms`.`company` (
  `companypkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `compw` varchar(200) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `seat` int(11) DEFAULT 2,
  `managerseat` int(11) DEFAULT 2,
  PRIMARY KEY (`companypkey`),
  UNIQUE KEY `name_index_id` (`name`)
)  ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tms`.`characters` (
  `characterpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `companypkey` bigint(20) NOT NULL,
  `thumb` varchar(255) DEFAULT NULL,
  `kind` varchar(45) DEFAULT NULL,
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  PRIMARY KEY (`characterpkey`),
  KEY `companypkey` (`companypkey`),
  CONSTRAINT `characters_ibfk_1` FOREIGN KEY (`companypkey`) REFERENCES `company` (`companypkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`characterstyle` (
  `characterstylepkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `imagesrc` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL DEFAULT 0,
  `characterpkey` bigint(20) NOT NULL,
  PRIMARY KEY (`characterstylepkey`),
  KEY `characterpkey` (`characterpkey`),
  CONSTRAINT `characterstyle_ibfk_1` FOREIGN KEY (`characterpkey`) REFERENCES `characters` (`characterpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`color` (
  `colorpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `status` varchar(25) NOT NULL DEFAULT 'NORMAL',
  `code` varchar(2000) NOT NULL,
  `category` varchar(50) NOT NULL,
  `colorhex` varchar(8) NOT NULL,
  `version` int(11) NOT NULL,
  `characterstylepkey` bigint(20) NOT NULL,
  PRIMARY KEY (`colorpkey`),
  KEY `characterstylepkey` (`characterstylepkey`),
  CONSTRAINT `color_ibfk_1` FOREIGN KEY (`characterstylepkey`) REFERENCES `characterstyle` (`characterstylepkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS `tms`.`taskmanager` (
  `taskmanagerpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `tmid` varchar(100) NOT NULL,
  `tmpw` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `companypkey` bigint(20) NOT NULL,
  PRIMARY KEY (`taskmanagerpkey`),
  UNIQUE KEY `compkey_tmid_id` (`tmid`,`companypkey`),
  KEY `companypkey` (`companypkey`),
  CONSTRAINT `taskmanager_ibfk_1` FOREIGN KEY (`companypkey`) REFERENCES `company` (`companypkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`title` (
  `titlepkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'REG',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `companypkey` bigint(20) NOT NULL,
  `thumb` varchar(2000) NOT NULL DEFAULT 'NOFILE',
  `author` varchar(45) DEFAULT NULL,
  `genre` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`titlepkey`),
  KEY `companypkey` (`companypkey`),
  CONSTRAINT `title_ibfk_1` FOREIGN KEY (`companypkey`) REFERENCES `company` (`companypkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tms`.`worker` (
  `workerpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `workerid` varchar(200) NOT NULL,
  `workerpw` varchar(200) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `lastactivitydate` varchar(14) DEFAULT NULL,
  PRIMARY KEY (`workerpkey`),
  UNIQUE KEY `workerid_id` (`workerid`)

) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`companyworkerpool` (
  `companypkey` bigint(20) NOT NULL,
  `workerpkey` bigint(20) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `joindate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  PRIMARY KEY (`companypkey`,`workerpkey`),
  KEY `workerpkey` (`workerpkey`),
  CONSTRAINT `companyworkerpool_ibfk_1` FOREIGN KEY (`companypkey`) REFERENCES `company` (`companypkey`),
  CONSTRAINT `companyworkerpool_ibfk_2` FOREIGN KEY (`workerpkey`) REFERENCES `worker` (`workerpkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tms`.`workergroup` (
  `taskmanagerpkey` bigint(20) NOT NULL,
  `companypkey` bigint(20) NOT NULL,
  `workerpkey` bigint(20) NOT NULL,
  `joindate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`taskmanagerpkey`,`companypkey`,`workerpkey`),
  KEY `workerpkey` (`workerpkey`),
  KEY `companypkey` (`companypkey`,`workerpkey`),
  CONSTRAINT `workergroup_ibfk_2` FOREIGN KEY (`companypkey`, `workerpkey`) REFERENCES `companyworkerpool` (`companypkey`, `workerpkey`),
  CONSTRAINT `workergroup_ibfk_3` FOREIGN KEY (`taskmanagerpkey`) REFERENCES `taskmanager` (`taskmanagerpkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='status\r\n\r\n''NORMAL'',\r\n''AWARE'', 배정중단\r\n''REQKICK'',  지워주세요\r\n''NOTTEAM''\r\n\r\n\r\n';

CREATE TABLE IF NOT EXISTS `tms`.`episode` (
  `episodepkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(2000) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'REG',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `titlepkey` bigint(20) NOT NULL,
  PRIMARY KEY (`episodepkey`),
  KEY `titlepkey` (`titlepkey`),
  CONSTRAINT `episode_ibfk_1` FOREIGN KEY (`titlepkey`) REFERENCES `title` (`titlepkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`task` (
  `taskpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'REG',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `startdate` varchar(14) NOT NULL DEFAULT '99991212121212',
  `enddate` varchar(14) NOT NULL DEFAULT '99991212121212',
  `donedate` varchar(14) NOT NULL DEFAULT '99991231',
  `kind` varchar(20) NOT NULL,
  `taskmanagerpkey` bigint(20) NOT NULL,
  `episodepkey` bigint(20) NOT NULL,
  `commemo` varchar(45) DEFAULT NULL,
  `tmmemo` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`taskpkey`),
  KEY `episodepkey` (`episodepkey`),
  KEY `taskmanagerpkey` (`taskmanagerpkey`),
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`episodepkey`) REFERENCES `episode` (`episodepkey`),
  CONSTRAINT `task_ibfk_2` FOREIGN KEY (`taskmanagerpkey`) REFERENCES `taskmanager` (`taskmanagerpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS `tms`.`taskentry` (
  `taskentrypkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `current` int(11) NOT NULL DEFAULT 1,
  `latest` int(11) NOT NULL DEFAULT 1,
  `isfile` varchar(20) NOT NULL DEFAULT 'Y',
  `size` bigint(20) NOT NULL DEFAULT 0,
  `modifydate` bigint(20) NOT NULL DEFAULT 0,
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `rel` bigint(20) DEFAULT NULL,
  `src` varchar(255) DEFAULT NULL,
  `token` varchar(250) NOT NULL,
  `taskpkey` bigint(20) NOT NULL,
  PRIMARY KEY (`taskentrypkey`),
  KEY `taskpkey` (`taskpkey`),
  KEY `rel` (`rel`),
  CONSTRAINT `taskentry_ibfk_1` FOREIGN KEY (`taskpkey`) REFERENCES `task` (`taskpkey`),
  CONSTRAINT `taskentry_ibfk_2` FOREIGN KEY (`rel`) REFERENCES `taskentry` (`taskentrypkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`work` (
  `workpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL DEFAULT 'REG',
  `priority` int(11) NOT NULL DEFAULT 100,
  `startdate` varchar(14) NOT NULL DEFAULT '99991212121212',
  `enddate` varchar(14) NOT NULL DEFAULT '99991212121212',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `progress` smallint(11) unsigned NOT NULL DEFAULT 0,
  `confirmcutcount` int(11) NOT NULL DEFAULT 0,
  `relpkey` bigint(20) DEFAULT NULL,
  `taskpkey` bigint(20) NOT NULL,
  `workerpkey` bigint(20) NOT NULL,
  `companypkey` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `accepteddate` varchar(14) NOT NULL DEFAULT '99991231',
  `donedate` varchar(14) NOT NULL DEFAULT '99991231',
  PRIMARY KEY (`workpkey`),
  KEY `taskpkey` (`taskpkey`),
  KEY `relpkey` (`relpkey`),
  KEY `workerpkey` (`workerpkey`),
  KEY `companypkey` (`companypkey`,`workerpkey`),
  CONSTRAINT `work_ibfk_1` FOREIGN KEY (`taskpkey`) REFERENCES `task` (`taskpkey`),
  CONSTRAINT `work_ibfk_3` FOREIGN KEY (`companypkey`, `workerpkey`) REFERENCES `companyworkerpool` (`companypkey`, `workerpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`workcharacterstyle` (
  `workpkey` bigint(20) NOT NULL,
  `characterstylepkey` bigint(20) NOT NULL,
  `version` int(11) NOT NULL DEFAULT 1,
  `status` varchar(14) NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`workpkey`,`characterstylepkey`,`version`),
  KEY `characterstylepkey` (`characterstylepkey`),
  CONSTRAINT `workcharacterstyle_ibfk_1` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`),
  CONSTRAINT `workcharacterstyle_ibfk_2` FOREIGN KEY (`characterstylepkey`) REFERENCES `characterstyle` (`characterstylepkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`workcause` (
  `workcausepkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `executedate` varchar(14) NOT NULL DEFAULT '99991231',
  `workerpkey` bigint(20) NOT NULL,
  `workpkey` bigint(20) NOT NULL,
  `fp` bigint(20) NOT NULL,
  `fk` varchar(20) NOT NULL,
  `ck` bigint(20) NOT NULL DEFAULT 0,
  `comment` varchar(200) DEFAULT NULL,
  `feedback` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`workcausepkey`),
  UNIQUE KEY `fp` (`fp`,`fk`),
  UNIQUE KEY `workpkey` (`workpkey`,`ck`),
  UNIQUE KEY `fp_2` (`fp`,`fk`),
  UNIQUE KEY `workpkey_2` (`workpkey`,`ck`),
  KEY `workerpkey` (`workerpkey`),
  CONSTRAINT `workcause_ibfk_1` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`),
  CONSTRAINT `workcause_ibfk_2` FOREIGN KEY (`workerpkey`) REFERENCES `worker` (`workerpkey`),
  CONSTRAINT `workcause_ibfk_3` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`),
  CONSTRAINT `workcause_ibfk_4` FOREIGN KEY (`workerpkey`) REFERENCES `worker` (`workerpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`workschedule` (
  `workschedulepkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `enddate` varchar(14) NOT NULL,
  `kind` varchar(20) NOT NULL DEFAULT 'SCHEDULE',
  `workpkey` bigint(20) NOT NULL,
  PRIMARY KEY (`workschedulepkey`,`kind`),
  KEY `workpkey` (`workpkey`),
  CONSTRAINT `workschedule_ibfk_1` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tms`.`entry` (
  `entrypkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `current` int(11) NOT NULL DEFAULT 1,
  `latest` int(11) NOT NULL DEFAULT 1,
  `isfile` varchar(20) NOT NULL DEFAULT 'Y',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `workpkey` bigint(20) NOT NULL,
  `rel` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`entrypkey`),
  KEY `workpkey` (`workpkey`),
  KEY `rel` (`rel`),
  CONSTRAINT `entry_ibfk_1` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`),
  CONSTRAINT `entry_ibfk_2` FOREIGN KEY (`rel`) REFERENCES `entry` (`entrypkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS `tms`.`entryversion` (
  `entryversionpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `src` varchar(200) DEFAULT NULL,
  `size` bigint(20) NOT NULL,
  `crc32` varchar(50) NOT NULL DEFAULT '0',
  `version` int(11) NOT NULL DEFAULT 1,
  `modifydate` bigint(20) NOT NULL,
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `entrypkey` bigint(20) NOT NULL,
  `token` varchar(100) NOT NULL,
  PRIMARY KEY (`entryversionpkey`),
  KEY `entrypkey` (`entrypkey`),
  CONSTRAINT `entryversion_ibfk_1` FOREIGN KEY (`entrypkey`) REFERENCES `entry` (`entrypkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`updateset` (
  `updatesetpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `kind` varchar(10) NOT NULL DEFAULT 'TM',
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `checkview` varchar(10) NOT NULL DEFAULT 'N',
  `workpkey` bigint(20) NOT NULL,
  `companypkey` bigint(20) DEFAULT NULL,
  `workerpkey` bigint(20) DEFAULT NULL,
  `name` varchar(50) NOT NULL DEFAULT '초기',
  PRIMARY KEY (`updatesetpkey`),
  KEY `workpkey` (`workpkey`),
  KEY `companypkey` (`companypkey`,`workerpkey`),
  CONSTRAINT `updateset_ibfk_1` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`),
  CONSTRAINT `updateset_ibfk_2` FOREIGN KEY (`companypkey`, `workerpkey`) REFERENCES `companyworkerpool` (`companypkey`, `workerpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `tms`.`updatesetentryversion` (
  `updatesetpkey` bigint(20) NOT NULL,
  `entryversionpkey` bigint(20) NOT NULL,
  PRIMARY KEY (`updatesetpkey`,`entryversionpkey`),
  KEY `entryversionpkey` (`entryversionpkey`),
  CONSTRAINT `updatesetentryversion_ibfk_1` FOREIGN KEY (`updatesetpkey`) REFERENCES `updateset` (`updatesetpkey`),
  CONSTRAINT `updatesetentryversion_ibfk_2` FOREIGN KEY (`entryversionpkey`) REFERENCES `entryversion` (`entryversionpkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS `tms`.`workresult` (
  `workresultpkey` bigint(20) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL DEFAULT 'NORAML',
  `createdate` varchar(14) NOT NULL DEFAULT (date_format(current_timestamp(),'%Y%m%d%H%i%s')),
  `requestcutcount` int(11) NOT NULL DEFAULT 0,
  `kind` varchar(20) NOT NULL DEFAULT 'RESULT',
  `workpkey` bigint(20) NOT NULL,
  `updatesetpkey` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`workresultpkey`,`kind`),
  KEY `workpkey` (`workpkey`),
  KEY `updatesetpkey` (`updatesetpkey`),
  CONSTRAINT `workresult_ibfk_1` FOREIGN KEY (`workpkey`) REFERENCES `work` (`workpkey`),
  CONSTRAINT `workresult_ibfk_2` FOREIGN KEY (`updatesetpkey`) REFERENCES `updateset` (`updatesetpkey`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;











