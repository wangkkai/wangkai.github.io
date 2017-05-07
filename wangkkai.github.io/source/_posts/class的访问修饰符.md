---
title: class的访问修饰符
date: 2017-04-24 13:58:30
tags: java
categories: Java
---
我们知道Java中的outer class只能被public default修饰。如果一个类被default修饰，那么该类的作用域就被限定到包。并且我们也知道一个public class的类名必须和文件名相同，那么default修饰的class呢？答案是无所谓。
我们新建一个PublicTest.java的文件。  
```
  class Test{
       public static void main(String[] args){
          System.out.println("hello");
       }
   }

```
然后我们javac PublicTest.java编译之后，就只会有Test.class。接着java Test运行后结果当然就是正确的。