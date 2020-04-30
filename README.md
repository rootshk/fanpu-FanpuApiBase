### DeepTechSpace服务端


### 项目依赖：
```
1. jdk8
3. postgresql
4. redis 
```

#### controller　内容定义
```aidl
1. controller/web 下存放着管理后台的api 必须以 /web开头
2. controller/api 下存放着小程序或者APP的接口，必须以 /api开头
3. controller/open 下存放着对外的接口（比如支付回调），必须以 /open开头
注意在生产环境下。外网仅可以访问　/api /open二种开头的接口。
```

#### API规范
```aidl
1. Controller必须加上　@Api　说明具体的业务名称。
2. Controller下面的方法，只允许指定一种请求类型，并且遵守restful api规范
3. Controller下面的方法, 只允许指定一个url请求方式
4. Controller下面的方法, 必须添加　@ApiOperation　并且说明操作内容
```


#### 启动说明：
```
 默认端口：6202
 正常启动：java -jar AdminServer.jar
 
 自定义参数启动（直接启动即可，会自动创建表）：
 nohup java -jar AdminServer.jar --spring.config.location=application.properties 2>&1 &
　省内存启动方式
  nohup java -server -ms128m -mx256m -Xmn96m -jar AdminServer-master.jar --spring.config.location=application_ColoredContactLenses.yml 2>&1 &
```