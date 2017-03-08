
dbmonitor-agent是可独立运行的采集微服务，由基础框架和各种插件组成。
dbmonitor-agent基础框架对外提供REST接口，内部包含任务调度功能，并提供插件接口，支持定制采集器插件和采集数据处理器插件。
dbmonitor-agent可以和应用一起部署进行本机采集，也可远程部署采集（根据采集插件的功能），并将采集数据上报到ITMP微服务、ELK或者数据库（可定制不同的数据处理插件）

## 如何创建采集任务：
### 1. 调用REST接口，参考itm-agent REST API http://127.0.0.1:38205/api-doc/index.html
### 2. 编写任务列表文件，文件名格式为*.task，启动后自动加载任务
- 版本文件目录 conf\task
- metricId 必填 任务对应的采集项ID
- neId 必填 任务对应的网元ID
- granularity 必填 任务采集粒度，单位秒
- properties 选填 任务的采集属性，会传递到对应的Collector类中
- tags 选填 任务tag信息
