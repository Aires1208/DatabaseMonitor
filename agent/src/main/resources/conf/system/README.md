系统文件及任务列表文件存放目录
==============

## 如何创建采集任务：
### 1. 调用REST接口，参考itm-agent REST API http://127.0.0.1:8207/api-doc/index.html
### 2. 编写任务列表文件，文件名tasklist.yml，注意所有itm-agent的采集任务都保存在这个文件，启动后自动加载任务
- 版本文件目录 conf\system
- metricId 必填 任务对应的采集项ID
- neId 必填 任务对应的网元ID
- granularity 必填 任务采集粒度，单位秒
- properties 选填 任务的采集属性，会传递到对应的Collector类中
- tags 选填 任务tag信息