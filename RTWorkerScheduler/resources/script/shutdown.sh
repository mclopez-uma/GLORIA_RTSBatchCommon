#!/bin/sh

kill -9 `ps -ef|grep -v grep | grep 'RTWorkerSheduler.jar' | awk '{print $2}'`
