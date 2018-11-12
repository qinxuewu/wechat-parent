# wechat-parent

#### 项目介绍
基于Spring Cloud微服务化开发平台，核心技术采用Spring Boot2以及Spring Cloud 相关核心组件，前端采用Vue的微信公众号管理系统。
主要是用于学习使用

#### 模块说明
软件架构说明
```
wechat-renren                   #后台管理模块(采用人人开源项目基础架构)
wechat-api                     #api接口模块
wechat-api-gateway             #Zuul路由网关过滤器
wechat-common                  #公共模块
wechat-eureka                  #eureka 注册中心
wechat-feign-interface         #feign调用接口模块（调用外部接口和内部服务接口。配置有差异）
wechat-monitoring              #基于SpringBoot Admin2.0的服务监控
wechat-web                     #web前端模块 
```

### 打包部署

wechat-eureka 注册中心打包部署
```
第一步  mvn package -Dmaven.test.skip=true
第二步  linux服务启动  nohup java -Xms256m -Xmx256m -jar wechat-eureka-1.0.jar  &  （后台运行）
```

wechat-monitoring 监控部署运行
```
 nohup java -Xms256m -Xmx256m -jar wechat-monitoring-1.0  & 

```


