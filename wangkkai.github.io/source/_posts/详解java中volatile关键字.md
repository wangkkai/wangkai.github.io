---
title: 详解java中volatile关键字
date: 2017-02-19 20:35:20
tags: [Java,volatile]
categories: Java
---
#### volatile的内存可见性
在多线程环境下，经常看到volatile关键字。用volatile修饰的变量，能够保持内存的可见性。而什么是内存的可见性呢？  
所谓的内存可见性就是一个线程对共享变量进行修改，其他线程能够立即得到该共享变量的最新值。  
为什么会出现内存的不可见性呢？这是因为每个线程都有自己的缓存。一个线程对共享变量的修改会先修改缓存，然后再写入到主存中。在这个过程中就会出现线程读取的不一致。也就是所谓的内存不可见性。
下面的代码说明内存的不可见性。  
```
public class TestVolite {
	public static void main(String[] args) {
		ThreadDemo td = new ThreadDemo();
		new Thread(td).start();
		while(true){
			//System.out.println("!!");
			if(td.isFlag()){
				System.out.println("------------------");
				break;
			}
		}
	}
}
class ThreadDemo implements Runnable {
	private  boolean flag = false;
	@Override
	public void run() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		flag = true;
		System.out.println("flag=" + isFlag());
	}
	public boolean isFlag() {
		return flag;
	}
}
```
运行结果为：  
flag=true.并一直运行没有结束。  
说明一个线程已经修改了flag为true。另一个线程读取到的仍然是false. 之所以产生这样的情况据说是因为，jvm对程序进行了优化，产生了指令的重排序。排序后的代码类似于这样。具体我没有去看变异后的程序，留待以后验证。
```
if(td.isFlag()){
	while(true)
		{
			System.out.println("------------------");
			break;
		}
	}
```
若对flag前面加上 volatile 会输出flag=true 和 -----
#### volatile 不保证原子性和互斥性
虽然volatile能够保证内存的可见性，但是不能保证操作的原子性和互斥性，也就说不能替代synchronazied.
```

public class TestThread {
	public static void main(String[] args) {
		ThreadDemon td = new ThreadDemon();
		for (int i = 0; i < 10; i++) {
			new Thread(td).start();
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(td.num);
	}
}

class ThreadDemon implements Runnable {
	public volatile int num = 0;

	@Override
	public  void run() {
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		num++;
	}

}
```
运行结果<=10.并不是我们所期待的10.原因就是num++操作并不是原子操作。该操作分为读取、修改、写入。在修改并没写入这个过程中，就可能有其他线程来进行读取。所以会造成结果偏小。也说明volatile并不能代替synchronized来进行同步操作。而通过synchronized加锁就能保证得到正确结果。
```

public class TestThread {
	public static void main(String[] args) {
		ThreadDemon td = new ThreadDemon();
		for (int i = 0; i < 10; i++) {
			new Thread(td).start();
		}
		try {
			Thread.sleep(100);  //目的是让主线程最后结束  才能得到最后结果。
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(td.num);
	}
}

class ThreadDemon implements Runnable {
	public  int num = 0;

	@Override
	public synchronized void run() {
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		num++;
	}

}
```
运行结果为10.
#### CAS算法
那么有没有一种既不加锁又能保证结果正确的方法呢？答案是有的,那就是CAS（compare and swap）算法。CAS算法，有3个操作数，内存值V,旧的预期值A,要修改的新值B.当且仅当预期值A和内存值v相同时，将内存值V修改为B,否则什么都不做。现在的cpu提供了特殊的指令，可以自动更新共享数居，而且能够检测到其他线程的干扰。java.util.concurrent包提供了并发的操作。如` AtomicInteger`的 `getAndIncrement()`方法就保证了i++的原子操作。java8中concurrenthashma---
