---
title: 当你访问www.baidu.com发生了什么
date: 2017-04-28 21:09:15
tags:  [DNS,TCP,UDP]
categories: 网络
---
>当你访问一个网站的时候，具体都发生了些什么？除了所知道的大概的流程外，我今天要在抓包进行细致的分析一下。我以www.baidu.com为例，进行抓包分析。

#### 1.知识点回顾
在正式开始之前，我们先回顾一下基本的知识点。主要是TCP的三次握手四次挥手的过程。
那我们就看一下tcp报文段的格式。  
<!--more-->
* 源端口地址：16位
* 目的端口地址：16位
* 序号，也就是seq：32位。连接建立时候双方使用各自的随机数产生器产生一个初始序号（initial sequence number,ISN）
* 确认号：32位，表示接收方期望接收的报文段。
* 首部字段：4位
* 保留：6位
* 控制：6位。包括URG:紧急指针有效 ACK:确认是有效的 PSH:请求推送 RST:连接复位 SYN:同步序号，在建立连接时使用 FIN:终止连接
* 窗口大小：16位
* 校验和：16位
* 紧急指针 ：16位 指向紧急数据对的末尾，也就是从数据的开始到该指针的位置为紧急数据。  
* 选项：40字节  

**我们要特别注意，确认号ack和控制标记ACK的区别。**  
好的，我们看一下三次握手的示意图。
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_22-12-50.png)  
四次挥手的示意图。  
![](http://oc6shen8h.bkt.clouddn.com/2017-04-30_21-20-54.png)
#### 2.准备工作
我们先看下本机的IP 地址和DNS SERVER。目的是便于下面的分析。内容如下：
![IP and DNS ](http://oc6shen8h.bkt.clouddn.com/dnserver.png)  
#### 3.DNS解析
ok,现在我们正式开始。  
当你的小手在浏览器输入完www.baidu.com之后，你一哆嗦敲下了回车，这时，一系列动作已经悄然展开。   
应用程序目前就是浏览器，将你的www.baidu.com传递给应用层，到达应用层后加上应用层首部也就是https get报文段，发送到网站根目录，请求index文件。然后传入到传输层，但是TCP没有建立，所以http get先缓存。执行tcp建立，需要ip层。但是因为baidu是域名不是ip地址，并且在DNS缓存中也没有找到对应的ip,所以ip层也无法执行。这是ip数据包缓存，返回到应用层DNS。
这也就是我们先看到的DNS的原因。这也是可以理解的吗，毕竟你输入的是域名，又不是ip地址，人家是不认识你的吗。所以，DNS--地址解析协议就开始工作了，它要还原你的真面目，也就是说别觉得在城里待了几天就是Tony啊之类的跟我装X,还是给我换回你原来的张三，王二狗的名字。应用层的DNS请求，想要找到www.baidu.com的ip。  
![DNS](http://oc6shen8h.bkt.clouddn.com/dns.png)  

下面就到了传输层。  

![](http://oc6shen8h.bkt.clouddn.com/udp.png)  
我们可以看到采用的是udp协议，包括源端口号，目的端口号53这也是DNS的默认端口号，长度，检验位等信息。  
接着就到了IP层。

![](http://oc6shen8h.bkt.clouddn.com/ip.png)  

这时我们看到了源IP地址和目的IP地址。有没有很熟悉的感觉？  
这个源IP地址就是我们的本机的IP地址，这个其实也没什么好值得一说的。但是这个目的IP地址就是我们的*DNS服务器*的地址。之前我一度在疑惑,IP地址不应该是到网络层才需要用到的吗？那么一个服务最原始的IP地址是哪里来的呢？比如说，我们的DNS的目的就是找到域名对应的IP地址，但是连接到DNS服务器也是需要IP的呀。。。。。。  
上面我们发现了IP层的目的地址和DNS Server的地址一致，所以我认为，对于应用层协议来说，都会有一个ip地址来支持该协议的。比如说，邮件的SMTP协议，每一家公司都会指定他们公司的一个IP来支持。 

IP层之后，我们就到了数据链路层。 就变成了MAC地址到MAC地址的连接了。

![](http://oc6shen8h.bkt.clouddn.com/%E6%95%B0%E6%8D%AE%E9%93%BE%E8%B7%AF%E5%B1%82.png)  
你以为上面的这些内容做完就结束了吗？too young,too simple!!! 这才是刚刚开始啊。

接下来，作为礼尚往来，DNS 服务器该给你一个回话了吧。同理该回话也是得必然经历数据链路层、IP层、传输层最后到应用层DNS。只不过这次是先到数据链路层依次向上的,并且目的地址和源地址整好与发送过程相反。  

![](http://oc6shen8h.bkt.clouddn.com/response.png)  
我们可以看到answer的内容，会发现一个www.shifen.com的域名，它其实就是baidu域名的别名。下面的就是获取到的IP地址。好，下面我们就验证一下。  
![](http://oc6shen8h.bkt.clouddn.com/%E8%AF%81%E5%AE%9E.png)  
我们发现不论是ping baidu.com 还是ping shifen.com 或者是直接ping ip其实都是一样的。也就是说我们取到的结果是正确的。那么所有的访问就结束了？？too young,too naive!!!这只是DNS的解析结束了，真正的数据请求还没有开始呢。。。。。。  
  
#### 4.TCP的三次握手
接下来应该就是三次握手连接的建立。因为现在的baidu都是采用的https安全的传输协议。所以我们在抓包的时候选择查看tsl协议的内容，然后右键"follow TCP Stream".  

![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-10-30.png)  
我们看到前面三条就是tcp的三次握手，下面我们具体看一下里面的内容。  
1. 首先是客户端向服务器发送SYN=1和seq号。表示请求建立连接。
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-16-21.png)    
2.接下来就是服务器向客户端发送ACK+SYN和服务端的seq信号及应答ack。ack号为客户端发送的seq+1。ACK=1，SYN=1表示同意建立连接。
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-21-52.png)  
3.客户端继续发送ack 和客户端的seq=上一次seq+1。ack=服务端的seq+1.并且设置ACK=1.  
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-31-04.png)  

至此三次握手就结束了，然后就是正式的数据传输。 下图为应用层的内容，因为采用的是https，所以传输的安全加密的包括加密算法等信息。客户端向服务器端发送一个hello.
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-39-21.png)  

服务器向客户端发送一个hello
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-43-44.png)  

接下来正式传输数据。 
![](http://oc6shen8h.bkt.clouddn.com/2017-04-29_21-46-16.png)