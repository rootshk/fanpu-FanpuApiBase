#!/bin/sh

#sh deploy.sh test start master
#start info restart stop status
#调用示例
#sh deploy.sh dev start master
#params 1 = dev|test|staging|product
#params 2 = start|stop|restart|status|info|rollback
#params 3 = master?



env=$1
handle=$2
branch=$3
pull=$4

if [ ! $env ]
then
  echo "======================================================="
  echo "缺少参数"
  echo "sh deploy.sh"
  echo "{dev|test|product}"
  echo "{start|stop|restart|rollback|info|status}"
  echo "git branch"
  echo "======================================================="
  exit
fi
if [  ! $handle ]
then
  echo "======================================================="
  echo "缺少参数"
  echo "sh deploy.sh"
  echo "{dev|test|product}"
  echo "{start|stop|restart|rollback|info|status}"
  echo "git branch"
  echo "======================================================="
  exit
fi

if [ ! $branch ]
then
  exit
fi

if [ $env = "test" ]
then
  ip="14.29.210.96"
  user="prod"
elif [ $env = "product" ]
then
  ip=""
  user="prod"
else
  echo "没有找到发布环境"
  exit
fi

git fetch origin
git checkout $branch
git pull origin $branch

echo ""
echo "                  确认信息"
echo "============*******************================="
echo "发布环境：${env}"
echo "IP地址  ：${ip}"
echo "发布用户：${user}"
echo "发布分支：${branch}"
echo "执行动作：${handle}"
echo "============*******************================="
echo ""
echo ""


if [ ! $pull ]
then
  pull="y"
fi

myProject=.
rootDir=/opt/deploy
app_home=$rootDir/DeepTechSpaceServer
app_name=DeepTechSpaceServer-${branch}.jar
jar_file=$myProject/target/$app_name

if [ "$pull"x = "y"x ]
then
  cd $myProject
  git fetch origin
  git checkout $branch
  git pull origin $branch
  mvn clean package -Dmaven.test.skip=true
  mv target/DeepTechSpaceServer.jar target/DeepTechSpaceServer-${branch}.jar
  scp $jar_file deploy/apiService.sh $user@$ip:$app_home/
fi
echo "启动项目中..."
ssh $user@$ip sh $app_home/apiService.sh $handle $app_home $app_name $pull $env
echo "启动项目完毕..."
