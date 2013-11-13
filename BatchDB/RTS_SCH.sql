CREATE TABLE `gloria`.`SchTimeFrame` (
  `id` bigint(20) NOT NULL auto_increment COMMENT 'DB identifier',
  `dateIni` TIMESTAMP NOT NULL COMMENT 'Beginning of the TimeFrame',
  `dateEnd` TIMESTAMP NOT NULL COMMENT 'End of the TimeFrame',
  `uuidOp` VARCHAR(255) DEFAULT NULL COMMENT 'Observing plan UUID in this TimeFrame',
  PRIMARY KEY (`id`),
  UNIQUE(`dateIni`),
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

CREATE TABLE `gloria`.`ObservingPlan` (
  `id` bigint(20) NOT NULL auto_increment COMMENT 'DB identifier',
  `uuid` VARCHAR(255)  NOT NULL  COMMENT 'Observing plan UUID',
  `offshoreOpInfo` VARCHAR(2024) COMMENT 'Information about local OP info',
  `user` VARCHAR(255)  NOT NULL COMMENT 'Observing plan user',
  `priority` INT  NOT NULL  COMMENT 'Priority',
  `state` INT  NOT NULL,
  `type` INT  NOT NULL, 
  `file` varchar(255) NOT NULL COMMENT 'Observing Plan xml file name',
  `description` VARCHAR(1024) COMMENT 'Observing Plan description' ,
  `comment` VARCHAR(1024) COMMENT 'Any interesting comment about the observing plan' ,
  `receivedDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Date when the OP was received',
  `advertDeadline` TIMESTAMP NULL DEFAULT NULL  COMMENT 'Advertisement deadline NEW!!!',
  `advertOffshoreDeadline` TIMESTAMP NULL DEFAULT NULL  COMMENT 'Advertisement deadline for offshore system',
  `advertDateIni` TIMESTAMP NULL DEFAULT NULL COMMENT 'Beginning of the advertisement evaluation process NEW !!!',
  `advertDateEnd` TIMESTAMP NULL DEFAULT NULL COMMENT 'Ending of the advertisement evaluation process NEW !!!',
  `offeredDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Date when the OP was offered  NEW !!!',
  `scheduleDateIni` TIMESTAMP NULL DEFAULT NULL COMMENT 'Beginning of the scheduled date interval',
  `scheduleDateEnd` TIMESTAMP NULL DEFAULT NULL COMMENT 'Ending of the scheduled date interval',
  `execDateIni` TIMESTAMP NULL DEFAULT NULL COMMENT 'Beginning of the observing plan execution',
  `execDateEnd` TIMESTAMP NULL DEFAULT NULL  COMMENT 'Endind of the observing plan execution',
  `execDuration` INT DEFAULT 0 COMMENT 'Execution Duration',
  `execDateObservationSession` TIMESTAMP NULL DEFAULT NULL COMMENT 'Observation session execution 00:00:00 or 12:00:00',
  `execDeadline` TIMESTAMP NULL DEFAULT NULL  COMMENT 'OP execution deadline. Used by the images retriever',
  `offerDeadline` TIMESTAMP NULL DEFAULT NULL  COMMENT 'Deadline for the offering process.',
  `predAstr` TIMESTAMP NULL DEFAULT NULL COMMENT 'Astronomical prediction',
  `predDuration` INT DEFAULT 0 COMMENT 'Predicted Duration',
  `eventAdvertReplyDeadline` TIMESTAMP NULL DEFAULT NULL COMMENT 'Event Advertisement Reply DeadLine',
  `eventAdvertReplyDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Event Advertisement Reply Date',
  `eventAdvertReplyAccepted` INT  NOT NULL COMMENT 'Event Advertisement Reply Accepted flag',
  `eventOfferConfirmDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Event Offer Confirmation Date',
  `eventOfferConfirmAccepted` INT  NOT NULL COMMENT 'Event Offer Confirmation flag',
  `eventOffshoreReqDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Event Offshore Request Date',
  `eventOffshoreConfirmDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Event Offshore Confirm Date',
  `eventFinishEventDate` TIMESTAMP NULL DEFAULT NULL COMMENT 'Event Finish Event Date',
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

insert into gloria.Task (id, name, enable, provider, sleepTime) values (2, 'WorkerOffshoreRetriever', 1, 'eu.gloria.rt.worker.offshore.WorkerOffshoreRetriever', 5000);
insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'ProviderOffshorePluginRetriever', 'eu.gloria.rt.worker.offshore.acp.OffshoreRetrieverACP', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'proxyHost', 'localhost', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'proxyPort', '8080', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'proxyAppName', 'RTIDB', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'proxyHttps', 'false', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (2, 'imageMagickScale', '800x600', 0);

insert into gloria.Task (id, name, enable, provider, sleepTime) values (3, 'WorkerOffshorePublisher', 1, 'eu.gloria.rt.worker.offshore.WorkerOffshorePublisher', 5000);
insert into gloria.TaskProperty (idtask, name, value, type) values (3, 'ProviderOffshorePluginPublisher', 'eu.gloria.rt.worker.offshore.acp.OffshorePublisherACP', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (3, 'xmlPath', '/usr/share/gloria/rts/repositories/xml/', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (3, 'opXSD', '/usr/share/gloria/rts/tau/resources/gloria_rti_plan.xsd', 0);

insert into gloria.Task (id, name, enable, provider, sleepTime) values (1, 'Scheduler', 1, 'eu.gloria.rt.worker.scheduler.core.WorkerScheduler', 5000);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'PredictionMsecCameraSettings', '5000', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'PredictionMsecFilterMove', '15000', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'PredictionMsecLooseness', '10000', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'PredictionMsecMountMove', '60000', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'XmlPath', '/usr/share/gloria/rts/repositories/tau_ops_xml/', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'XsdFile', '/usr/share/gloria/rts/tau/resources/gloria_rti_plan.xsd', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'MaxCountOpSession', '100', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'MaxCountOpUser', '100', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'MaxSharedTimeSession', '0', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'MaxShareTimeUser', '0', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'DaysFutures', '7', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'DaysScheduling', '1', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'IsNightTelescope', 'true', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'AdvertAcceptedToQueue', 'true', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'SharedTimeFrame', 'monday,*;tuesday,*;wednesday,*;thursday,*;friday,*;saturday,*;sunday,*', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'TimeLimitToday', '', 0);
insert into gloria.TaskProperty (idtask, name, value, type) values (1, 'TimeLimitExec', '', 0);
