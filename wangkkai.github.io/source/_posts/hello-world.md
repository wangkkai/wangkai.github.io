---
title: java中comparable 和comparator
date: 2017-02-18 19:25:10
tags: Java
categories: Java
---
>在java中,如果想实现两个对象之间的比较，那么我们有两种选择。一是实现`comparable<T>`接口中的`int compareTo(T o)`方法。二是实现`comparator`接口中的`int compare<T>（T o1,T o2）`方法。上面的两种方法都返回一个int值,若大于0，说明第一个对象大，等于0表示相等，小于0，说明对象小.下面介绍一下两者的区别。
#### 1.comparable --自己与其他对象比较
我们看到comparable接口中的用于比较的`compareTo<T>(T o)`方法,参数只有一个。并且通过这个方法名`compareTo()`也可以推断是自己与其他对象比较。
```

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TestComparable implements Comparable<TestComparable> {
	private int version;

	@Override
	public int compareTo(TestComparable o) {// 重写compareTo方法，只有一个参数
		return this.version - o.version;
	}

	public TestComparable(int version) {
		super();
		this.version = version;
	}

	@Override
	public String toString() {
		return "TestComparable [version=" + version + "]";
	}

	public static void main(String[] args) {
		TestComparable t1 = new TestComparable(1);
		TestComparable t2 = new TestComparable(3);
		ArrayList<TestComparable> list = new ArrayList<TestComparable>();
		list.add(t2);
		list.add(t1);
		Collections.sort(list);//用collections排序
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			TestComparable testComparable = (TestComparable) iterator.next();
			System.out.println(testComparable);
		}
	}
}
结果：
TestComparable [version=1]
TestComparable [version=3]

我们看到 collections.sort 如下
public static <T extends Comparable<? super T>> void sort(List<T> list) {
        list.sort(null);
    }
```
#### 2.comparator --第三方的比较机构
comparator类似于一个三方比较机构，专门用来处理那些比较双方不能处理的问题。也就是说比较双方可能没有实现`comparable`接口,或者是比较的方式不是我们想要的。举个例子，比如Integer类是按照大小进行比较，但是我们想按照绝对值进行比较等等这种情况。
```
package com.test.oj;

import java.util.Arrays;
import java.util.Comparator;

public class TestComparator {
	int version;

	public TestComparator(int version) {
		super();
		this.version = version;
	}

	@Override
	public String toString() {
		return "TestComparator [version=" + version + "]";
	}

	public static void main(String[] args) {
		TestComparator t1 = new TestComparator(1);
		TestComparator t2 = new TestComparator(3);
		TestComparator[] t = { t2, t1 };
		Arrays.sort(t, new cm());
		System.out.println(t[0]);
	}
}

// 这就是所谓的地方比较机构 ，并且该机构定义了比较的规则
class cm implements Comparator<TestComparator> {
	@Override
	public int compare(TestComparator o1, TestComparator o2) {
		return o1.version - o2.version;
	}

}
结果：
TestComparator [version=1]
```
#### 3.常用数据类型的比较
* Integer  等数字类型
```
public final class Integer extends Number implements Comparable<Integer> //实现Comparable
public int compareTo(Integer anotherInteger) {
        return compare(this.value, anotherInteger.value);
    }
public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }//就是按照大小进行比较的

```
* String 类型  
```
//实现comparable接口
public final class String 
    implements java.io.Serializable, Comparable<String>, CharSequence
    //重写compareTo方法
    public int compareTo(String anotherString) {
        int len1 = value.length;
        int len2 = anotherString.value.length;
        int lim = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = anotherString.value;

        int k = 0;
        while (k < lim) {//先从首字母开始判断
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;//若前面的字母都相等，则比较长度
    }
```