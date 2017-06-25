
---
title: ThreadLocal分析
date: 2017-06-25 17:10:53
tags: [线程，ThreadLocal]
categories: Java
---
>看到一篇关于ThreadLocal写的比较好的文章，[解密ThreadLocal。](http://blog.csdn.net/winwill2012/article/details/71625570)在此基础上，我就加上点自己的理解。
#### ThreadLocal的作用

ThreadLocal的作用是提供线程内的局部变量，这种变量在线程的生命周期内起作用，减少同一个线程内多个函数或者组件之间一些公共变量的传递的复杂度。 举个例子，我出门需要先坐公交再做地铁，这里的坐公交和坐地铁就好比是同一个线程内的两个函数，我就是一个线程，我要完成这两个函数都需要同一个东西：公交卡（北京公交和地铁都使用公交卡），那么我为了不向这两个函数都传递公交卡这个变量（相当于不是一直带着公交卡上路），我可以这么做：将公交卡事先交给一个机构，当我需要刷卡的时候再向这个机构要公交卡（当然每次拿的都是同一张公交卡）。这样就能达到只要是我(同一个线程)需要公交卡，何时何地都能向这个机构要的目的。
<!--more-->
有人要说了：你可以将公交卡设置为全局变量啊，这样不是也能何时何地都能取公交卡吗？但是如果有很多个人（很多个线程）呢？大家可不能都使用同一张公交卡吧(我们假设公交卡是实名认证的)，这样不就乱套了嘛。现在明白了吧？这就是ThreadLocal设计的初衷：提供线程内部的局部变量，在本线程内随时随地可取，隔离其他线程。
#### 源码分析
```
//支持泛型
public class ThreadLocal<T> {
     //构造函数
     public ThreadLocal() {
    }


//get方法
public T get() {
        //获得当前的线程
        Thread t = Thread.currentThread();
        //获取该线程的ThreadLocalMap你可能会困惑，线程怎么会有ThreadLocalMap?下面我们就继续分析为什么会有。看一下getMap()方法
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }

//getMap方法 返回ThreadLocalMap
ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    }

//我们就看一下线程里面到底有没有这个ThreadLocalMap
public class Thread implements Runnable {
    private Runnable target;
    //在这里
    ThreadLocal.ThreadLocalMap threadLocals = null;
    }

//既然知道了这个ThreadLocalMap怎么来的了，那么接下来就继续看看这个ThreadLocalMap到底是什么？
static class ThreadLocalMap {
    //Entry包含ThreadLocal对象的弱引用的key,value为ThreadLocal要保存的对象
    static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;
            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
    private Entry[] table;

    }
    //构造函数
    ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
            table = new Entry[INITIAL_CAPACITY];
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPACITY);
        }

    //最后我们看一下setInitialValue()方法。用来初始化
    private T setInitialValue() {
        T value = initialValue();
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
        return value;
    }
    //createMap()
      void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
```

现在我们总结一下，ThreadLocal的get()方法

1. 首先获取当前线程
2. 根据当前线程获取一个
3. Map如果获取的Map不为空，则在Map中以ThreadLocal的引用作为key来在Map中获取对应的value e，否则转到5
4. 如果e不为null，则返回e.value，否则转到5
5 .Map为空或者e为空，则通过initialValue函数获取初始值value，然后用ThreadLocal的引用和value作为firstKey和firstValue创建一个新的Map。

所以，可以总结一下ThreadLocal的设计思路： 
每个Thread维护一个ThreadLocalMap映射表，这个映射表的key是ThreadLocal实例本身，value是真正需要存储的Object。 
查阅了一下资料，这样设计的主要有以下几点优势：
>这样设计之后每个Map的Entry数量变小了：之前是Thread的数量，现在是ThreadLocal的数量，能提高性能，据说性能的提升不是一点两点(没有亲测)
当Thread销毁之后对应的ThreadLocalMap也就随之销毁了，能减少内存使用量。

#### 内存泄露
上面我们看到了*ThreadLocalMap是使用ThreadLocal的弱引用作为Key的*我们用一个图来介绍上面一些对象之间的引用关系图，实线表示强引用，虚线表示弱引用：
![](http://oc6shen8h.bkt.clouddn.com/ThreadLocal.png) 
然后网上就传言，ThreadLocal会引发内存泄露，他们的理由是这样的：
>如上图，ThreadLocalMap使用ThreadLocal的弱引用作为key，如果一个ThreadLocal没有外部强引用引用他，那么系统gc的时候，这个ThreadLocal势必会被回收，这样一来，ThreadLocalMap中就会出现key为null的Entry，就没有办法访问这些key为null的Entry的value，如果当前线程再迟迟不结束的话，这些key为null的Entry的value就会一直存在一条强引用链： 
Thread Ref -> Thread -> ThreaLocalMap -> Entry -> value 
永远无法回收，造成内存泄露。

我们来看看到底会不会出现这种情况。 
其实，在JDK的ThreadLocalMap的设计中已经考虑到这种情况，也加上了一些防护措施，下面是ThreadLocalMap的getEntry方法的源码：
```
    private Entry getEntry(ThreadLocal<?> key) {
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            if (e != null && e.get() == key)
                return e;
            else
                return getEntryAfterMiss(key, i, e);
        }
```
getEntryAfterMiss函数的源码：
```
private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                ThreadLocal<?> k = e.get();
                if (k == key)
                    return e;
                if (k == null)
                    expungeStaleEntry(i);
                else
                    i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }
```
expungeStaleEntry函数的源码：
```
private int expungeStaleEntry(int staleSlot) {
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for (i = nextIndex(staleSlot, len);
                 (e = tab[i]) != null;
                 i = nextIndex(i, len)) {
                ThreadLocal<?> k = e.get();
                if (k == null) {
                    e.value = null;
                    tab[i] = null;
                    size--;
                } else {
                    int h = k.threadLocalHashCode & (len - 1);
                    if (h != i) {
                        tab[i] = null;

                        // Unlike Knuth 6.4 Algorithm R, we must scan until
                        // null because multiple entries could have been stale.
                        while (tab[h] != null)
                            h = nextIndex(h, len);
                        tab[h] = e;
                    }
                }
            }
            return i;
        }
```
整理一下ThreadLocalMap的getEntry函数的流程：

1. 首先从ThreadLocal的直接索引位置(通过ThreadLocal.threadLocalHashCode & (len-1)运算得到)获取Entry e，如果e不为null并且key相同则返回e；
2. 如果e为null或者key不一致则向下一个位置查询，如果下一个位置的key和当前需要查询的key相等，则返回对应的Entry，否则，如果key值为null，则擦除该位置的Entry，否则继续向下一个位置查询

在这个过程中遇到的key为null的Entry都会被擦除，那么Entry内的value也就没有强引用链，自然会被回收。仔细研究代码可以发现，set操作也有类似的思想，将key为null的这些Entry都删除，防止内存泄露。 
但是光这样还是不够的，上面的设计思路依赖一个前提条件：要调用ThreadLocalMap的getEntry函数或者set函数。这当然是不可能任何情况都成立的，所以很多情况下需要使用者手动调用ThreadLocal的remove函数，手动删除不再需要的ThreadLocal，防止内存泄露。所以JDK建议将ThreadLocal变量定义成private static的，这样的话ThreadLocal的生命周期就更长，由于一直存在ThreadLocal的强引用，所以ThreadLocal也就不会被回收，也就能保证任何时候都能根据ThreadLocal的弱引用访问到Entry的value值，然后remove它，防止内存泄露。