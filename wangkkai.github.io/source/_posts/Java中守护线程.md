---
title: Java中守护线程
date: 2017-8-29 21:59:10
tags: [线程]
categories: Java
---
>今天，做了多益网络的在线笔试，其中有一道题就是考Java的守护线程的问题。题目如下： 

```
public class Main implements Runnable{

    @Override
    public void run() {
        try{
            System.out.println(1);
        }catch (Exception e){
            System.out.println(2);
        }finally {
            System.out.println(3);
        }
    }

    public static void main(String[] args) {
        Thread thread=new Thread(new Main());
        thread.setDaemon(true);
        thread.start();
    }
}

```
问上面的这段代码的输出是什么？  
答案是：不确定！  
因为设置了守护线程，说明在JVM中如果没有了用户线程存在，那么Jvm就会停止。这段代码里只有main这个线程是用户线程，如果main线程结束了，那么就停止了。如果在一段代码里面，除了main之外还有用户线程，那么即使main线程结束了，其他用户线程还是会继续运行。所以这道题，main结束了，jvm就结束了。那么我们就没办法知道确定的输出了。具体的分析可以看下面的这篇博客。[Thread.setDaemon详解](http://blog.csdn.net/xyls12345/article/details/26256693)  下面的内容引用该博客。  

Java中线程分为两种类型：用户线程和守护线程。通过Thread.setDaemon(false)设置为用户线程；通过Thread.setDaemon(true)设置为守护线程。如果不设置次属性，默认为用户线程。



用户线程和守护线程的区别：

1.主线程结束后用户线程还会继续运行,JVM存活；主线程结束后守护线程和JVM的状态又下面第2条确定。

2.如果没有用户线程，都是守护线程，那么JVM结束（随之而来的是所有的一切烟消云散，包括所有的守护线程）。



补充说明：  
定义：守护线程--也称“服务线程”，在没有用户线程可服务时会自动离开。  
优先级：守护线程的优先级比较低，用于为系统中的其它对象和线程提供服务。  
设置：通过setDaemon(true)来设置线程为“守护线程”；  
将一个用户线程设置为守护线程的方式是在线程启动用线程对象的setDaemon方法。  
example: 垃圾回收线程就是一个经典的守护线程，当我们的程序中不再有任何运行的Thread,程序就不会再产生垃圾，垃圾回收器也就无事可做，所以当垃圾回收线程是JVM上仅剩的线程时，垃圾回收线程会自动离开。它始终在低级别的状态中运行，用于实时监控和管理系统中的可回收资源。  
生命周期：守护进程（Daemon）是运行在后台的一种特殊进程。它独立于控制终端并且周期性地执行某种任务或等待处理某些发生的事件。也就是说守护线程不依赖于终端，但是依赖于系统，与系统“同生共死”。那Java的守护线程是什么样子的呢。当JVM中所有的线程都是守护线程的时候，JVM就可以退出了；如果还有一个或以上的非守护线程则JVM不会退出。
```
import java.io.IOException;
class TestMain4 extends Thread {
   public void run() {            //永真循环线程
       for(int i=0;;i++){
           try {
               Thread.sleep(1000);
           } catch (InterruptedException ex) {   }
           System.out.println(i);
       }
   }

   public static void main(String [] args){
      TestMain4 test = new TestMain4();
      test.setDaemon(true);    //调试时可以设置为false，那么这个程序是个死循环，没有退出条件。设置为true，即可主线程结束，test线程也结束。
       test.start();
       System.out.println("isDaemon = " + test.isDaemon());
       try {
           System.in.read();   // 接受输入，使程序在此停顿，一旦接收到用户输入，main线程结束，守护线程自动结束
       } catch (IOException ex) {}
   }
}
```