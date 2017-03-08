处理器属性文件存放目录
==============

- DataProcessor需要一个properties文件，文件中最好有enable属性，可以控制处理器是否启用，如果不配置，则默认启用，在DataProcessor的propertiesFileName方法中指明properties文件的名字
