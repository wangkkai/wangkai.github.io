---
title: java序列化及反序列化
date: 2017-02-22 14:17:10
tags: 序列化
categories: Java
---
#### java序列化、反序列化
##### 一、概念
>把对象转换为字节序列的过程称为对象的序列化。把字节序列恢复为对象的过程称为对象的反序列化。

对象的序列化主要有两种用途：  
1. 把对象的字节序列永久地保存到硬盘上，通常存放在一个文件中；  
2. 在网络上传送对象的字节序列。

　　在很多应用中，需要对某些对象进行序列化，让它们离开内存空间，入住物理硬盘，以便长期保存。比如最常见的是Web服务器中的Session对象，当有 10万用户并发访问，就有可能出现10万个Session对象，内存可能吃不消，于是Web容器就会把一些seesion先序列化到硬盘中，等要用了，再把保存在硬盘中的对象还原到内存中。  
当两个进程在进行远程通信时，彼此可以发送各种类型的数据。无论是何种类型的数据，都会以二进制序列的形式在网络上传送。发送方需要把这个Java对象转换为字节序列，才能在网络上传送；接收方则需要把字节序列再恢复为Java对象。   
序列化反序列化需要注意的是：
* 反序列化时候，必须提供class文件。否则会报`ClassNotFoundException`因为反序列化读取的是java对象的数据而不是java类。
* 如果一个对象包含引用类型，那么引用的类也得是可序列化的。程序会默认进行序列化的。
##### 二、序列化的操作--实现seriallizable接口
若想将一个对象的字节序列保存起来，那么该对象就必须得实现序列化的接口。
```
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TestTransient  {

	public static void main(String[] args) {
		People p = new People();
		p.setAge(25);
		p.setName("小A");
		// 将对象p写入文件中
		try {
			ObjectOutputStream os = new ObjectOutputStream(
					new FileOutputStream("D:/p.text"));
			os.writeObject(p);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将对象读取出来
		try {

			ObjectInputStream is = new ObjectInputStream(new FileInputStream(
					"D:/p.text"));
			People p1 = (People) is.readObject();
			System.out.println(p1.getName());
			System.out.println(p1.getAge());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}

class People implements Serializable{
        private static final long serialVersionUID = -1447110602118809700L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	private int age;
}
```
如上，我们将People对象序列化和反序列化，那么我就得让People类实现Serializable接口或者Externalizable接口。Externalizable接口继承自 Serializable接口，实现Externalizable接口的类完全由自身来控制序列化的行为，而仅实现Serializable接口的类可以采用默认的序列化方式 。  
在上面的代码中我们使用java.io.ObjectOutputStream代表对象输出流，它的writeObject(Object obj)方法可对参数指定的obj对象进行序列化，把得到的字节序列写到一个目标输出流中。  
java.io.ObjectInputStream代表对象输入流，它的readObject()方法从一个源输入流中读取字节序列，再把它们反序列化为一个对象，并将其返回。运行结果如下：  
>小A  
25  
##### 二、实现Externalizable接口  
实现该接口的类需要通过实现writeExternal() 和 Readexternal()方法来实现实现序列化和反序列化。除此之外，实现该接口的类必须要有非空的构造函数，因为通过Externalizable是通过构造函数来实现序列化的。性能略好，但是编程复杂。
##### 三、serialVersionUID
serialVersionUID字面的意思是序列化的版本号，凡是实现Serializable接口的类都有一个表示序列化版本标识符的静态变量。如果没有写serialVersionUID ，eclipse会有提示。其中serialVersionUID有两种方式，一种是default serialVersionUID=1L.另一种是generated serialVersionUID,也就是根绝类的信息等生成的=-1447110602118809700L，这个值不是固定的。下面我们看一下这个serialVersionUID有什么作用，所以我们去掉serialVersionUID。
```
class People implements Serializable {
	private String name;
	private int age;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
```
我们在People类中去掉serialVersionUID，然后执行上面的序列化操作后，我们发现结果是正确的，看起来serialVersionUID也没起什么作用呀。那serialVersionUID到底有什么用呢？我们这次将People类增加一个sex属性。
```
class People implements Serializable {
	private String name;
	private int age;
	private String sex;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
```
然后我们执行反序列化操作，也就是在硬盘内我们读入对象，会发现抛出了如下的异常信息。
>` java.io.InvalidClassException: com.java.People; local class incompatible: stream classdesc serialVersionUID = -1447110602118809700, local class serialVersionUID = -7441962391401350150`

意思就是说，文件流中的class和classpath中的class，也就是修改过后的class，不兼容了，处于安全机制考虑，程序抛出了错误，并且拒绝载入。那么如果我们真的有需求要在序列化后添加一个字段或者方法呢？应该怎么办？那就是自己去指定serialVersionUID。在TestSerialversionUID例子中，没有指定People类的serialVersionUID的，那么java编译器会自动给这个class进行一个摘要算法，类似于指纹算法，只要这个文件 多一个空格，得到的UID就会截然不同的，可以保证在这么多类中，这个编号是唯一的。所以，添加了一个字段后，由于没有显示指定 serialVersionUID，编译器又为我们生成了一个UID，当然和前面保存在文件中的那个不会一样了，于是就出现了2个序列化版本号不一致的错误。因此，只要我们自己指定了serialVersionUID，就可以在序列化后，去添加一个字段，或者方法，而不会影响到后期的还原，还原后的对象照样可以使用，而且还多了方法或者属性可以用。
下面我们为People类加上SerialversionUID，也就是之前的People类中的serialVersionUID，看一下效果。
```
public class TestTransient {

	public static void main(String[] args) {
		// 将对象读取出来
		try {

			ObjectInputStream is = new ObjectInputStream(new FileInputStream(
					"D:/p.text"));
			People p1 = (People) is.readObject();
			System.out.println(p1.getName());
			System.out.println(p1.getAge());
			System.out.println(p1.getSex());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}

class People implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1447110602118809700L;
	private String name;
	private int age;
	private String sex;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
```
运行正确。综上，我们只要指定了serialVersionUID，那么这个serialVersionUID就固定了，不会随着你对类的修改而变化。所以在你修改类结构后还是可以正确的进行序列化和反序列化。

如果一个Teacher类有一个Student属性。那么
```
Srudent s=new Student();
Teacher t1=new Teacher(s);  
Teacher t2=new Teacher(s);  
```
可能会创建出3个Student对象。因为假如首先创建t1,会自动将s序列化，然后创建t2又会序列化一个s.最后再次序列化一个s。但是如果是这样的我们执行反序列化会得到不一样的对象，与我们的目的是相违背的。那么序列化是采用什么样的方式呢？采用的是序列化到磁盘的对象都有一个唯一的编号。当程序试图序列化一个对象时，会看该对象是否已经序列化了，若没有就进行序列化。否则直接返回已经序列化的编号。
##### 四、Transient关键字
今天看ArrayList源码时，看到了`  private transient Object[] elementData;` 想起之前多次看到transient关键字，但是并没有深究有什么作用，所以今天就打算简单的研究一下。
>变量修饰符，如果用transient声明一个实例变量(只能修饰实例变量)，当对象存储时，它的值不需要维持。换句话来说就是，用transient关键字标记的成员变量不参与序列化过程。
```
package com.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TestTransient {

	public static void main(String[] args) {
		People p=new People();
		p.setName("小A");
		p.setAge(20);
		p.setSex("男");
		//将对象存储
		try {
			ObjectOutputStream oo=new ObjectOutputStream(new FileOutputStream("D:/p.text"));
			oo.writeObject(p);
			oo.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 将对象读取出来
		try {

			ObjectInputStream is = new ObjectInputStream(new FileInputStream(
					"D:/p.text"));
			People p1 = (People) is.readObject();
			System.out.println(p1.getName());
			System.out.println(p1.getAge());
			System.out.println(p1.getSex());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}

class People implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1447110602118809700L;
	private transient String name;
	private int age;
	private String sex;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
```
我们将name属性 设置为transient类型的，然后进行序列化和反序列化操作。得到的结果如下：
>null  
20  
男

由此我们可以看到，得到的name为null，说明name并没有被序列化。
transient只能修饰成员变量。序列化事针对对象的，所以静态变量是永远不会被序列化的。
##### 五、单例的序列化
对单例对象的序列化可能就破坏了其单例模式。测试如下：
```
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TestResolve {
	public static void main(String[] args) throws IOException {
		Singleton instance = Singleton.getInstance();
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(new File("instance.txt")));
			out.writeObject(instance);
			in = new ObjectInputStream(new FileInputStream(new File("instance.txt")));
			Singleton instance1 = (Singleton) in.readObject();
			System.out.println(instance == instance1);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			out.close();
			in.close();
		}

	}

	public static void name() {

	}
}

class Singleton implements Serializable {
	private static final long serialVersionUID = 1L;

	private Singleton() {
	}

	private static Singleton instance = new Singleton();

	public static Singleton getInstance() {
		return instance;
	}

	/*private Object readResolve() {
		return instance;
	}*/

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "singleton";
	}
}

```
>结果：false  

说明了序列化后的对象跟之前的并不是同一个。那么这么做就不满足单例了。但是，我们只要去掉注释，就会得到true的结果。原因就是我们实现了一个readReslove方法，该方法就会保证你反序列化得到的结果就是单例的。