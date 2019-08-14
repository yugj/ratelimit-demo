# 限流测试demo
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
ab -n 1 -c 1 -H "xx-userid:88" http://localhost:9001/hello2/xx

```

zuul sentinel

```
ab -n 1 -c 1 -H "xx-userid:88" http://localhost:9002/hello2/xx

```

对比 sentinel 单机模式在内存中规则，性能和稳定性相对ratelimit 通过redis好，
当然这个只是针对我们系统不需要精确计算集群限流阈值场景，ratelimit现在版本已经取消了
内存作为存储规则资源，这个方面感觉sentinel更人性化，规则存储支持外部，规则计算统计
可以直接在内存，可选的支持集群模式

