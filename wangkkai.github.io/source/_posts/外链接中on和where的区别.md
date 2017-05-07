---
title: 外连接中on和where的区别
date: 2017-03-01 16:49:05
tags: [on,where]
categories: 数据库
---
表tab1 tab2内容如下：

![](http://oc6shen8h.bkt.clouddn.com/%E8%A1%A81.jpg)

where语句执行结果： 

![](http://oc6shen8h.bkt.clouddn.com/where.jpg)

on语句执行结果：

![](http://oc6shen8h.bkt.clouddn.com/on.jpg)

可以看到，on的选择结果不论是true或者false，join的一个表（本例子中左表）都是会作为结果的。