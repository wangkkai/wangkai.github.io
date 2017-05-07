---
title: java值传递
date: 2017-02-19 21:12:21
tags: Java
categories: Java
---
#### java值传递
>对于java来说，只有值传递一种方式，并不存在引用传递，无论是传递的参数是基本数据类型还是对象类型。对于基本数据类型传递的是其值的副本，对于对象类型传递的是引用的副本。

下面我就来举例说明。  
##### 1.基本数据类型
```
public class Test {
	public static void main(String[] args) {
		int i=0;
		fermin(i);
		i=i++;
		System.out.println(i);//输出0
	}
	public static void fermin(int i){
		i++;
	}
}

```
之所以输出0，是因为` fermin(int` i)传入的参数i并不是地址，而是i的一个副本，所以在函数内无论进行什么样的计算都只是改变这个副本的值，而并不会改变实际的i值。i=i++就是先赋值在自增的原因。  
##### 2.对象类型  
```
public class TestDemo {
	int i = 10;

	public static void fermin(TestDemo t) {
		t.i = 5;
	}

	@Override
	public String toString() {
		return "TestDemo [i=" + i + "]";
	}

	public static void main(String[] args) {
		TestDemo t = new TestDemo();
		fermin(t);
		System.out.println(t);//输出TestDemo [i=5]
	}
}

 
对于参数为对象类型来说，就可以理解为传入原对象引用的一个副本，他们都指向同一个对象。当一个对象实例作为一个参数被传递到方法中时，参数的值就是该对象引用的一个副本。他们指向同一个对象,对象的内容可以在被调用的方法中改变，但对象的原引用(不是引用的副本)是永远不会改变的。所以改变这个引用副本，也就是对原对象进行了改变，所以结果为5.
```
```
public class Test {
	int i = 5;
    public Test(){
    
    }
    public Test(int i){
    	this.i=i;
    }

	public static void fermin(Test t) {
		t = new Test(10);
	}

	@Override
	public String toString() {
		return "Test [i=" + i + "]";
	}

	public static void main(String[] args) {
		Test t = new Test();
		fermin(t);
		System.out.println(t);  // Test [i=5]
	}
}
```
上面的例子证明了，传递的其实并不是原引用，只是原引用的一个副本，当为这个引用指向了一个新对象的时候，对原引用并没有影响。
