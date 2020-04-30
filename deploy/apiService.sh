#!/bin/sh

#JDK所在路径
JAVA_HOME="/opt/java/current"
PROJECT_NAME="DeepTechSpaceServer"

#Java程序所在的目录（classes的上一级目录）
APP_HOME=$2
#需要启动的Java主程序（main方法类）
APP_MAINCLASS=$3
#是否上传新的jar包
APP_PULL=$4
#环境
SERVER_ENV=$5

mkdir -p $APP_HOME/release
mkdir -p $APP_HOME/current
mkdir -p $APP_HOME/tmpdir

#上一版本的Java主程序（main方法类）
PRE_APP_MAINCLASS=`ls $APP_HOME/current | grep $PROJECT_NAME`
if [  ! $PRE_APP_MAINCLASS ]
then
  PRE_APP_MAINCLASS="temp.jar"
fi

#java虚拟机启动参数
if [ $SERVER_ENV = "product" ]
then
  #java虚拟机启动参数
  JAVA_OPTS=" -server -Xms512m -Xmx1024m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=5 -Djava.io.tmpdir=$APP_HOME/tmpdir"
else
  JAVA_OPTS=" -server -ms128m -mx256m -Xmn96m -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=5 -Djava.io.tmpdir=$APP_HOME/tmpdir"
fi
#内存不够,设置小一点
#JAVA_OPTS=" -server -Xms512m -Xmx1024m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=5 -Djava.io.tmpdir=$APP_HOME/tmpdir"

cd $APP_HOME/current

psid=0

initfile() {
    releaseFile=$APP_HOME/release/$APP_MAINCLASS-`date  "+%Y-%m-%d_%H:%M:%S"`
    cp -r $APP_HOME/$APP_MAINCLASS $releaseFile
    rm -rf $APP_HOME/current/$PRE_APP_MAINCLASS
    ln -s $releaseFile $APP_HOME/current/$APP_MAINCLASS
    rm -rf $APP_HOME/$APP_MAINCLASS
}

checkpid() {
   javaps=`$JAVA_HOME/bin/jps -l | grep $PROJECT_NAME`
   #PRE_APP_MAINCLASS=`$JAVA_HOME/bin/jps | grep $PROJECT_NAME | awk '{print $2}'`
   if [ -n "$javaps" ]; then
      psid=`echo $javaps | awk '{print $1}'`

   else
      psid=0
   fi
}

###################################
#(函数)启动程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示程序已启动
#3. 如果程序没有被启动，则执行启动命令行
#4. 启动命令执行后，再次调用checkpid函数
#5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
#注意：echo -n 表示打印字符后，不换行
#注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
start() {
  checkpid
  if [ "$APP_PULL"x = "y"x ]
  then
    initfile
  fi

  if [ $psid -ne 0 ]; then
     kill -9 $psid
  fi
  APP_MAINCLASS=`ls $APP_HOME/current | grep $PROJECT_NAME`
  echo -n "Starting $APP_MAINCLASS ..."
  nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar $APP_HOME/current/$APP_MAINCLASS --spring.profiles.active=$SERVER_ENV  >/dev/null 2>&1 &
  #setsid $JAVA_HOME/bin/java $JAVA_OPTS -jar $APP_HOME/current/$APP_MAINCLASS  &

  checkpid
  if [ $psid -ne 0 ]; then
     echo "(pid=$psid) [OK]"
  else
     echo "[Failed]"
  fi
}

###################################
#(函数)停止程序
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
#3. 使用kill -9 pid命令进行强制杀死进程
#4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
#5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
#6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
#注意：echo -n 表示打印字符后，不换行
#注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
   checkpid

   if [ $psid -ne 0 ]; then
      echo -n "Stopping $PRE_APP_MAINCLASS ...(pid=$psid) "
#      su - $RUNNING_USER -c "kill -9 $psid"
      kill -9 $psid
      if [ $? -eq 0 ]; then
         echo "[OK]"
      else
         echo "[Failed]"
      fi

      checkpid
      if [ $psid -ne 0 ]; then
         stop
      fi
   else
      echo "================================"
      echo "warn: $APP_PREFIX is not running"
      echo "================================"
   fi
}

###################################
#(函数)检查程序运行状态
#
#说明：
#1. 首先调用checkpid函数，刷新$psid全局变量
#2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
#3. 否则，提示程序未运行
###################################
status() {
   checkpid

   if [ $psid -ne 0 ];  then
      echo "$APP_MAINCLASS is running! (pid=$psid)"
   else
      echo "$APP_MAINCLASS is not running"
   fi
}

###################################
#(函数)打印系统环境参数
###################################
info() {
   echo "System Information:"
   echo "****************************"
   echo `head -n 1 /etc/issue`
   echo `uname -a`
   echo
   echo "JAVA_HOME=$JAVA_HOME"
   echo `$JAVA_HOME/bin/java -version`
   echo
   echo "APP_HOME=$APP_HOME"
   echo "APP_MAINCLASS=$APP_MAINCLASS"
   echo "****************************"
}

rollback() {
  stop
  ROLLBACK_PROJECT_ADDRESS=`ls $APP_HOME/release -t | awk 'NR==2'`
  ROLLBACK_PROJECT_NAME=`echo ${ROLLBACK_PROJECT_ADDRESS%%jar*}jar`
  rm -rf $APP_HOME/current/$PRE_APP_MAINCLASS
  ln -s $APP_HOME/release/$ROLLBACK_PROJECT_ADDRESS $APP_HOME/current/$ROLLBACK_PROJECT_NAME
  start
}

###################################
#读取脚本的第一个参数($1)，进行判断
#参数取值范围：{start|stop|restart|status|info}
#如参数不在指定范围之内，则打印帮助信息
###################################
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     start
     ;;
   'status')
     status
     ;;
   'rollback')
     rollback
    ;;
   'info')
     info
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|status|info|rollback}"
     exit 1
esac
exit 0
