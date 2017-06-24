---
title: 线程安全之ReentrantLock
date: 2017-05-04 19:31:20
tags: [多线程,线程安全，线程同步]
categories: 多线程
---
>前面我们提到了在协调对共享对象的访问时可以使用的机制有synchronized。但是在jdk5.0内又增加了一种新的机制：ReentrantLock.与之前提到的机制相反，ReentrantLock并不是一种替代内置锁的方法，而是当内置锁机制不适用时，作为一种可选择择的高级功能。
<!--more-->
#### 1.Lock概述
与synchronized不同的是，Lock接口定义了一组抽象的加锁操作。Lock提供了一种无条件的、可轮询的、定时的以及可中断的锁获取操作。所有加锁和解锁的操作都是显示的。我们看一下Lcok接口中的方法。
```
public interface Lock {
    //获取加锁操作，如果没有获取到锁，一直阻塞，直到获取
    void lock();
    //调用后一直阻塞到获得锁 但是接受中断信号
    void lockInterruptibly() throws InterruptedException;
    //判断是否获得锁。获得返回true否则返回false
    boolean tryLock();
    //判断在指定时间内是否获得锁
    boolean tryLock(long time, TimeUnit unit) throws Exception;
    //解锁
    void unlock();
    //类似于wait、notify操作
    Condition newCondition();
}
``` 
synchronized属于jdk层面的，而lock属于语言层面的，所以在一些情况下显得更加灵活。比如synchronized无法中断一个正在等待锁的线程。
#### 2.Lcok的使用
因为lock是显示的，所以我们必须手动释放锁，并且必须在finally块里面释放，因为有些情况产生了异常，那么你的锁可能就没有被释放。我们仍以售票为例。
```
class Windows implements Runnable {
    private int tickets = 10;
    public void run() {
        saleTicket();
    }
    final Lock l = new ReentrantLock(); //产生一个可重入锁锁
    public void saleTicket() {
        l.lock(); //获取锁
        try {
            while (tickets > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is sale " + tickets--);
            }

        } finally {
            l.unlock(); //在finally里释放锁
        }
    }
}

```
##### 2.1锁的公平性
值得注意的是， ReentrantLock（boolean fair）有一个带参数的构造器,表示是否是一个公平锁。所谓的公平锁，就是按照时间先后的顺序获取锁。比如去厕所排队进去。而非公平锁就是看谁能抢，谁将抢到锁谁进去。大部分情况下都是采用非公平锁，因为这样效率会高一点。之所以说效率高，是因为公平锁需要在挂起的线程中选择一个挂起时间最长的使其运行，通常这个状态转换是相对耗时间的。而非公平锁就是恰好锁被释放并且被其获得，免去了状态的切换。
##### 2.2 定时锁
在具有时间限制的操作中，定时锁非常有用，当在带有时间限制的操作中调用了一个阻塞方法时，她能根据剩余的时间提供一个时限，若果操作不能再指定的时间内完成，就会使程序提前结束。而synchronized一旦请求就无法取消。
```
public boolean tryLock(long timeout) throws InterruptedException{
        if(!l.tryLock(timeout, TimeUnit.NANOSECONDS)){
            return false;
        }
        try{
            return doSomething();
        }finally{
            l.unlock();
        }
    }
```
##### 2.2 可中断锁
可中断锁就是在锁获取的过程中，仍接受中断的响应。
lockInterruptibly()稍微难理解一些。
先说说线程的打扰机制，每个线程都有一个 打扰 标志。这里分两种情况，
1.  线程在sleep或wait,join， 此时如果别的进程调用此进程的 interrupt（）方法，此线程会被唤醒并被要求处理InterruptedException；(thread在做IO操作时也可能有类似行为，见java thread api)
2.  此线程在运行中， 则不会收到提醒。但是 此线程的 “打扰标志”会被设置， 可以通过isInterrupted()查看并 作出处理。lockInterruptibly()和上面的第一种情况是一样的， 线程在请求lock并被阻塞时，如果被interrupt，则“此线程会被唤醒并被要求处理InterruptedException”。并且如果线程已经被interrupt，再使用lockInterruptibly的时候，此线程也会被要求处理interruptedException.
```
作者：郭无心
链接：https://www.zhihu.com/question/36771163/answer/68974735
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public class TestLock
{
    public void test() throws Exception
    {
        final Lock lock = new ReentrantLock();
        lock.lock();
       Thread t1 = new Thread(new Runnable()
        {
            public void run()
            {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " interrupted.");
            }
        },"child thread -1");
        
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
        Thread.sleep(1000000);
    }    
    public static void main(String[] args) throws Exception
    {
        new TestLock().test();
    }
}
```
debug发现，即使子线程被打断，但是子线程仍然在run,可见lock()方法并不关心线程是否被打断，甚至说主线程已经运行完毕，子线程仍然在block().而使用LockInterupptibly，则会响应中断.
```
package com.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test1 {
    public void test() throws Exception {
        final Lock lock = new ReentrantLock();
        lock.lock();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                // lock.lock();
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " interrupted.");
                    e.printStackTrace();
                }
            }
        }, "child thread -1");

        t1.start();
        Thread.sleep(1000);

        t1.interrupt();

        Thread.sleep(10000);
    }

    public static void main(String[] args) throws Exception {
        new Test1().test();
    }
}

打印：child thread -1 interrupted.
    java.lang.InterruptedException
    at  java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireInterruptibly(Unknown Source)

```


#### 3.Condition
```
//通过锁new一个Condition对象
Condition condition=l.newCondition();
//等价于notify
condition.signal();
//等价于wait()
condition.await();
//等价于notifyAll()
condition.signalAll();
```