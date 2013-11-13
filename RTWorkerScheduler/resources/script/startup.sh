#!/bin/sh

nohup java -Djava.util.logging.config.file=./logging.properties  -jar  RTWorkerScheduler.jar &
