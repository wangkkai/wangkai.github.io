---
title: ubuntu下shell提示unexpected operator
date: 2017-04-19 14:39:20
tags: shell
categories: linux
---
今天用ubuntu写了个shell,运行的时候总是报如下的错误，因为之前执行shell都是采用./xx.sh的方式，所以从来没有遇见过这个问题。  
![](http://oc6shen8h.bkt.clouddn.com/error.jpg)  
上网查了一下，原因是ubuntu默认的执行shell不是bash而是dash.所以只要将dash禁用即可，命令如下：
```
sudo dpkg-reconfigure dash 
```
之后选择NO即可。  
![](http://oc6shen8h.bkt.clouddn.com/dash.jpg)  
修改之后会显示  
![](http://oc6shen8h.bkt.clouddn.com/repacle.jpg)  
说明成功！