---
title: 线程安全之Synchronized
date: 2017-05-04 11:25:10
tags: [多线程,线程安全，线程同步]
categories: 多线程
---
>我们采用多线程来处理一些问题，知道是会提升效率的，但是多线程的并发也会带来线程安全的问题。

#### 1、前言
我们以售票为例，假如总共有10张票，我们通过三个窗口来卖。我们就会发现其中的问题了。
<!--more-->
```
public class SaleTicket {
    public static void main(String[] args) {
        Window w = new Window();
        new Thread(w, "win1").start();
        new Thread(w, "win2").start();
        new Thread(w, "win3").start();
    }
}
class Window implements Runnable {
    private int tickets = 10;
    public void run() {
        while (tickets > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " is sale " + tickets--);
        }

    }
}
```
运行结果：  
win3 is sale 4   
win1 is sale 2   
win2 is sale 3  
win2 is sale 1  
win1 is sale -1  
win3 is sale 0  
我们看到票数竟然变成了负数，这是完全不合理的。所以说明多线程环境下，有些时候操作室不安全的。那么怎么保证安全呢？下面我们就介绍Synchronized。
#### 2. Synchronized同步操作
Synchronized就是同步的意思，能保证多个线程能够安全的并发访问共享资源。关于同步，有同步方法和同步代码块两种方式。
##### 2.1 同步代码块
同步代码块，需要有一个类的对象作为同步监视器，也就是通常我们所说的锁，该**对象只要是任意一个类的对象**即可。只要一个线程获取到这个锁，它就会阻塞其他线程，直到该线程释放锁后，其他的线程才能竞争这把锁。就像是有几个人同时去上厕所，但是只有一个人抢到了，进去后立马就将门锁上了。这时候剩下的人只能在外面交际的等着。当这个人解决了生理需要，把门打开后，后面的人立马往里面挤，挤进去的又立刻把门锁上。
```
class Window implements Runnable {
    private int tickets = 10;
    Object o=new Object(); //构造了一个Object对象
    public void run() {
        synchronized (o) { //用构造的object对象作为同步监视器
            while (tickets > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is sale " + tickets--);
            }
        }
    }
}
```
如上，我们就随意构造了一个Object对象，就相当于厕所门上的锁。这样就能保证同一个时间只有一个线程能够对tickets操作，也就保证结果的正确性。但是，该对象要保证各线程访问的是同一个。否则还是不能保证线程的同步。比如：
```
class Window implements Runnable {
    private int tickets = 10;
    public void run() {
        Object o=new Object(); //将构造的时间放在了方法内
        synchronized (o) { //用构造的object对象作为同步监视器
            while (tickets > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is sale " + tickets--);
            }
        }

    }
}
```
上面的这种操作就不能保证线程安全。因为这个Object对象不是Window的成员变量了，在方法内部成了局部的变量，每一个线程调用的时候都会new一个。也就是锁并不是唯一的了。
但是我们每次都new一个其他的对象，这样做真的好吗？？当然不好。所以我们就用this当做锁。this指的永远是调用该方法的对象。
```
class Window implements Runnable {
    private int tickets = 10;
    public void run() {
        synchronized (this) { // 用this指代的对象作为同步监视器
            while (tickets > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is sale " + tickets--);
            }
        }

    }
}
```
还有一种情况就是我们在静态方法中无法使用this的问题。无法用this做为对象锁，那我们用什么来同步呢？难道还需要new一个其他的对象吗？当然也是不需要的。我们看一下著名的懒汉模式是怎么解决的。
```
public class Singleton {
    public static void main(String[] args) {
        Singleton.getSingleton();
    }

    private Singleton() {

    }

    private static volatile Singleton singleton = null;

    public static Singleton getSingleton() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                // synchronized (singleton) { 为什么不这么写？
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}
```
这里因为是静态方法所以不能用this,那么为什么不能用注释的写法呢？因为会有空指针错误。
##### 2.2 同步方法
顾名思义，就是将整个方法给同步。只需要在方法的前面加上synchronized修饰符。
```
class Window implements Runnable {
    private int tickets = 10;
    public void run() {
        saleTicket();
    }
    public synchronized void saleTicket() { //同步方法
        while (tickets > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " is sale " + tickets--);
        }
    }
}
```