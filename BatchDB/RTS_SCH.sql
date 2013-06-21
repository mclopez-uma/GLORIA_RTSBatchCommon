CREATE TABLE `gloria`.`SchTimeFrame` (
  `dateIni` TIMESTAMP NOT NULL COMMENT 'Beginning of the TimeFrame',
  `dateEnd` TIMESTAMP NOT NULL COMMENT 'End of the TimeFrame',
  `uuidOp` VARCHAR(255)  NOT NULL,
  UNIQUE(`uuidOp`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

CREATE TABLE `gloria`.`RepObservingPlan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(255)  NOT NULL,
  `owner` INT  NOT NULL,
  `type` INT  NOT NULL,
  `user` VARCHAR(255)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE(`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `gloria`.`RepFile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(255)  NOT NULL,
  `type` INT  NOT NULL,
  `contentType` INT  NOT NULL,
  `date` TIMESTAMP,
  `opId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE(`uuid`),
  CONSTRAINT `fk_repfile_repobservingplan` FOREIGN KEY (`opId`) REFERENCES `RepObservingPlan` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `gloria`.`RepFileFormat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `format` INT  NOT NULL,
  `fileId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_repfileformat_repfile` FOREIGN KEY (`fileId`) REFERENCES `RepFile` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `gloria`.`Repository` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1024)  NOT NULL,
  `cod` INT  NOT NULL,
  `connUrl` VARCHAR(2048)  NOT NULL,
  `publicUrl` VARCHAR(2048)  NOT NULL,
  `description` VARCHAR(2048)  NOT NULL,
  `active` INT  NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `gloria`.`RepProperty` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(1024)  NOT NULL,
  `value` VARCHAR(1024)  NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `gloria`.`Advertisement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(255)  NOT NULL,
  `file` VARCHAR(255)  NOT NULL,
  `user` VARCHAR(255)  NOT NULL,
  `priority` VARCHAR(255)  NOT NULL,
  `received` TIMESTAMP  NOT NULL,
  `deadline` TIMESTAMP  NOT NULL,
  `processIni` TIMESTAMP NULL DEFAULT NULL,
  `processEnd` TIMESTAMP NULL DEFAULT NULL,
  `predIntervalIni` TIMESTAMP NULL DEFAULT NULL,
  `predIntervalEnd` TIMESTAMP NULL DEFAULT NULL,
  `predAstr` TIMESTAMP NULL DEFAULT NULL,
  `state` INT  NOT NULL,
  `comment` VARCHAR(1024),
  PRIMARY KEY (`id`),
  UNIQUE(`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

CREATE TABLE `gloria`.`ObservingPlan` (
  `id` bigint(20) NOT NULL auto_increment COMMENT 'DB identifier',
  `uuid` VARCHAR(255)  NOT NULL  COMMENT 'Observing plan UUID',
  `user` VARCHAR(255)  NOT NULL COMMENT 'Observing plan user',
  `priority` VARCHAR(255)  NOT NULL  COMMENT 'Priority',
  `state` INT  NOT NULL,
  `file` varchar(255) NOT NULL COMMENT 'Observing Plan xml file name',
  `comment` VARCHAR(1024) COMMENT 'Any interesting comment about the observing plan' ,
  `scheduleDateIni` TIMESTAMP NOT NULL COMMENT 'Beginning of the scheduled date interval',
  `scheduleDateEnd` TIMESTAMP NOT NULL COMMENT 'Ending of the scheduled date interval',
  `execDateIni` TIMESTAMP NULL DEFAULT NULL COMMENT 'Beginning of the observing plan execution',
  `execDateEnd` TIMESTAMP NULL DEFAULT NULL  COMMENT 'Endind of the observing plan execution',
  PRIMARY KEY  (`id`),
  UNIQUE(`uuid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;


CREATE TABLE `gloria`.`Task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `enable` tinyint(1) NOT NULL,
  `provider` varchar(250) NOT NULL,
  `sleepTime` INT NOT NULL,
  `start` TIMESTAMP NULL DEFAULT NULL,
  `stop` TIMESTAMP NULL DEFAULT NULL,
  `last_exec` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;


CREATE TABLE `gloria`.`TaskProperty` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idtask` bigint(20) NOT NULL,
  `name` varchar(250) NOT NULL,
  `value` varchar(2048) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_taskprop_task` FOREIGN KEY (`idtask`) REFERENCES `Task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

insert into gloria.Task (id, name, enable, provider, sleepTime) values (1, 'WorkerAdvertisementManager', 1, 'eu.gloria.rt.worker.advertisement.validator.WorkerAdvertisementManager' , 10000);

insert into gloria.Task (id, name, enable, provider, sleepTime) values (2, 'WorkerOffshoreRetriever', 1, 'eu.gloria.rt.worker.offshore.WorkerOffshoreRetriever' , 10000);

insert into gloria.Task (id, name, enable, provider, sleepTime) values (3, 'WorkerOffshorePublisher', 1, 'eu.gloria.rt.worker.offshore.WorkerOffshorePublisher' , 10000);

insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'xmlPath', '/usr/share/gloria/rts/repositories/tmp/advertisement/', 0);

insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'opXSD', '/usr/share/gloria/rts/repositories/tmp/cfg/gloria_rti_plan.xsd', 0);

insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'ProviderOffshorePluginRetriever', 'eu.gloria.rt.worker.offshore.gen.OffshoreRetrieverGEN', 0);

insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'ProviderOffshorePluginPublisher', 'eu.gloria.rt.worker.offshore.gen.OffshorePublisherGEN', 0);

insert into gloria.TaskProperty (idtask, name, value, type) values (3, 'xmlPath', '/usr/share/gloria/rts/repositories/tmp/advertisement/', 0);

insert into gloria.TaskProperty (idtask, name, value, type) values (3, 'opXSD', '/usr/share/gloria/rts/repositories/tmp/cfg/gloria_rti_plan.xsd', 0);

