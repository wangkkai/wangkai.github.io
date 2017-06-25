---
title: Java中的null
date: 2017-06-24 15:03:01
tags: [null]
categories: Java
---
>今天在论坛看到一个人提问关于null的问题。问题见下面。但是觉得回答的不是很在点子上，所以自己测试摸索一下。虽然在网上并没有找到答案，但是自己通过实验证实了自己的想法。
```
下面这段代码输出什么？
public class Buffer {

    public Buffer(double a[][]) {
        System.out.println("111111111");
    }

    public Buffer(Object o) {
        System.out.println("OOOOOOOOO");
    }

    public static void main(String[] args) {
        new Buffer(null);
        System.out.println(null + "aaaaa");
    }
}
<!--more-->
```
答案是111111111。
你一定会问为什么对吧？  
首先，明确一下null是java的关键字，但不是类型，也不是对象，只是一个值，并且是所有引用类型的默认值。  
那么，既然是所有引用类型的默认值Object也是引用类型啊，为什么不调用obj的呢？  
我认为是null匹配的最底层的子类。因为数组类型是Object的子类，所以会调用arr。当然我不是瞎说的，会有实验的支持。看代码！！！
```
public class B {
    public B(C c) {
        System.out.println("ccccc");
    }

    public B(D d) {
        System.out.println("ddddd");
    }

    public static void main(String[] args) {
        new B(null);
    }
}

class C {

}

class D extends C {

}
```
答案输出dddd  

所以就证明了上面为什么会输出arr了。