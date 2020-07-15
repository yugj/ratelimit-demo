# 限流测试demo
## 启动命令
- 控制台启动命令：
```
java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -Dcsp.sentinel.log.dir=/data/logs/csp -jar sentinel-dashboard-1.6.3-zk.jar

```

server.port为控制台占用的端口

csp.sentinel.dashboard.server为控制台地址

csp.sentinel.log.dir指定日志文件路径

默认用户名密码为sentinel

点击编辑[流控规则]，基于现有数据 实时编辑或新增流控规则

- 应用启动参数新增
```
-Dproject.name={appname} -Dcsp.sentinel.app.type=1 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dcsp.sentinel.log.dir=/data/logs/{profile}/csp

```
其中

project.name为应用名称

csp.sentinel.app.type值等于1标识网关应用

csp.sentinel.dashboard.server 配置控制台地址

csp.sentinel.log.dir指定日志文件路径

{profile}:环境标识

hotfix demo:

java -Dproject.name=sentinel-mvc-server -Dcsp.sentinel.dashboard.server=localhost:19004 -Dcsp.sentinel.log.dir=/data/logs/local/csp -jar sentinel-mvc-server-1.1.0.jar



上述参数所有均可通过 JVM -D 参数指定。除 project.name 以及日志的配置项（如 csp.sentinel.log.dir）之外，其余参数还可通过 properties 文件指定，路径为 ${user_home}/logs/csp/${project.name}.properties


## sentinel-mvc-server

```
curl localhost:9000/hello/xx
```


## zuul-ratelimit
```
curl localhost:9001/hello2/xx

```

## zuul-sentinel

```
curl localhost:9002/hello2/xx
```

## 压测
zuul ratelimit

```
 ab -n 100 -c 100 -H "xx-userid:88" http://localhost:9001/hello2/xx
```

zuul sentinel

```
yugj$ ab -n 100 -c 100 -H "xx-userid:88" http://localhost:9002/hello2/xx
```

对比 sentinel 单机模式在内存中规则，性能和稳定性相对ratelimit 通过redis好，
当然这个只是针对我们系统不需要精确计算集群限流阈值场景，ratelimit现在版本已经取消了
内存作为存储规则资源，这个方面感觉sentinel更人性化，规则存储支持外部，规则计算统计
可以直接在内存，可选的支持集群模式

