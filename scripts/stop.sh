#!/bin/bash

PROJECT_ROOT="/home/ubuntu/sirimp"
JAR_FILE="/home/ubuntu/sirimp/serverBootJar/jba_ver0328.jar"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)


CURRENT_PID=$(pgrep -f $JAR_FILE)


if [ -z $CURRENT_PID ]; then
  echo "$TIME_NOW > 현재 실행중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 실행중인 $CURRENT_PID 애플리케이션 종료 " >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
fi