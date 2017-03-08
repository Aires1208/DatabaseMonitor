插件metric文件存放目录
==============

- metric文件格式为yaml, 文件命名格式为*.metric
	- 版本文件目录 conf\metric
- metric文件说明
	- neTypeId 必填 采集网元对象的标示，全局唯一
	- metrics  采集网元对象的采集项
	- metricName 选填 采集项的名字
	- metricId 必填 采集项ID，用于区分不同的collector
	- granularity 必填 采集项采集粒度，单位秒
	- status 选填 采集项状态，0为激活，不写时默认激活

