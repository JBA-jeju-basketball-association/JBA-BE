#!/bin/bash

PROJECT_ROOT="/home/ubuntu/sirimp"
JAR_FILE="/home/ubuntu/sirimp/serverBootJar/jba_ver0328.jar"
JAR_DIR="/home/ubuntu/sirimp/serverBootJar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

# 소스하는 부분
source  /home/ubuntu/env_variables.sh

TIME_NOW=$(date +%c)

echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
if [ ! -d $JAR_DIR ]; then
    mkdir -p $JAR_DIR
fi
cp $PROJECT_ROOT/building/*.jar $JAR_FILE

#codedeploy bashrc를 읽어오지 못해 해당 파일 로드하게 작업
#source ~/.bash_profile

echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
 nohup java -jar -Duser.timezone=Asia/Seoul $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG