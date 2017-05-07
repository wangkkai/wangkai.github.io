---
title: Java中的Infinity和NAN
date: 2016-04-26 18:21:26
tags: Java
categories: Java
---
>昨天做完美世界的在线笔试题，有一道选择题是求 
` System.out.println(5.0/0.0+''-"+0.0/0.0);
`
的输出结果。当时确实不知道是什么结果，所以考完试就抓紧查了些资料。参考这篇博客 http://blog.csdn.net/naruto_ahu/article/details/8805808
正确的输出结果是`Infinity-NaN`   

下面就分析一下为什么会是这个结果。  
#### 1.为什么不是java.lang.ArithmeticException: / by zero？  
之所以没有报异常，是因为这是浮点的除法，也就是说0.0并不是真正意义上的0，它只不过是非常接近0而已，所以y一个数除以一个接近0的数，那么结果应为无穷大。而在java浮点范围内存在Infinity表示无穷大的概念。
例如：`System.out.println(3/0.0);` 结果为Infinity  
#### 2、NAN
 浮点算术保留了一个特殊的值用来表示一个不是数字的数量,这个值就是NaN（“不是一个数字（Not a Number）”的缩写）。对于所有没有良好的数字定义的浮点计算，**例如0.0/0.0，或者对负数求平方根**其值都是它。例如：
  `  
  System.out.println(0.0/0.0);
  System.out.println(Math.sqrt(-6));
  `
  结果都为NAN
规范中描述道，NaN 不等于任何浮点数值，包括它自身在内。
任何浮点操作，只要它的一个或多个操作数为NaN，那么其结果为NaN。这条规则是非常合理的，但是它却具有奇怪的结果。例如，下面的程序将打印false：
`
 double k=Math.sqrt(-6);
 System.out.println(k-k==0);
`
但是`System.out.println(k-k);`结果打印为NAN
总之，float 和double 类型都有一个特殊的NaN 值，用来表示不是数字的数量。
#### 3、NaN与任何数比较均返回false  
Double.NaN == Double.NaN，结果是false。但是，
Double a = new Double(Double.NaN);
Double b = new Double(Double.NaN);]
a.equals(b);  //true
#### 4、Float.compare()
 &#160; &#160; &#160; &#160;而当我们使用Float.compare()这个方法来比较两个NaN时，却会得到相等的结果。可以用下面的代码验证：
float nan=Float.NaN;
float anotherNan=Float.NaN;
System.out.println(Float.compare(nan,anotherNan));

compare()方法如果返回0，就说明两个数相等，返回-1，就说明第一个比第二个小，返回1则正好相反。
上面语句的返回结果是0。
一般来说，基本类型的compare()方法与直接使用"\=="的效果应该是一样的，但在NaN这个问题上不一致，是利是弊，取决于使用的人作何期望。当程序的语义要求两个NaN不应该被认为相等时（例如用NaN来代表两个无穷大，学过高等数学的朋友们都记得，两个无穷看上去符号是一样，但不应该认为是相等的两样东西），就使用==判断；如果NaN被看得无足轻重（毕竟，我只关心数字，两个不是数字的东西就划归同一类好了嘛）就使用Float.compare()。

另一个在==和compare()方法上表现不一致的浮点数就是正0和负0（当然这也是计算机表示有符号数字的老大难问题），我们（万能的）人类当然知道0.0f和-0.0f应该是相等的数字，但是试试下面的代码：
```
float negZero=-0.0f;
float zero=0.0f;
System.out.println(zero==negZero);
System.out.println(Float.compare(zero,negZero));
```

返回的结果是true和-1。看到了么，==认为正0和负0相等，而compare()方法认为正0比负0要大。所以对0的比较来说，==是更好的选择。
更有趣的事：
```
double i = 1.0 / 0;                  
System.out.println(i);             //Infinity  
System.out.println(i + 1);         //Infinity  
System.out.println(i == i + 1);    //true  
  
i = 0.0 / 0;  
System.out.println(i);             //NaN  
System.out.println(i + 1);         //NaN  
System.out.println(i == i + 1);    //false  
```