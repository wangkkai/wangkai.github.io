---
title: KMP
date: 2017-8-06 19:46:20
tags: [KMP]
categories: 算法
---
>给定两个字符串a和b。要求返回b在a中出现的位置。
#### 1.暴力方法
就是从头开始遍历a,如果相同就向后移动。否则，从下个位置开始。时间复杂度O(mn)
```
public static int bfPattern(String a, String b) {
        if (a==null||b==null||a.length()>1||b.length()<1||a.length()<b.length()){
            return -1;
        }
        int i = 0;
        int j = 0;
        while (i < a.length() && j < b.length()) {
            if (a.charAt(i) == b.charAt(j)) {
                i++;
                j++;
            } else {
                i = i - j + 1;
                j = 0;
            }
        }
        if (j == b.length()) {
            return i - j;
        } else return -1;
    }
```

#### 2.KMP算法
该算法的时间复杂度为O(m).在该算法中，a的位置不会后退，只会前进。我们首先对于B计算出它的next[]数组，表示前缀后缀最大的长度。具体的可以参考这篇博文。[【经典算法】——KMP，深入讲解next数组的求解](http://www.cnblogs.com/c-cloud/p/3224788.html)
```
public static int KMP(String a, String b) {
        int i = 0;
        int j = 0;
        int[] next = calNext(b);
        while (i < a.length() && j < b.length()) {
            if (a.charAt(i) == b.charAt(j)) {
                i++;
                j++;
            } else if (next[j] == -1) {
                i++;
            } else j = next[j];
        }
        return j == b.length() ? i - j : -1;
    }
    //求解next数组
    public static int[] calNext(String b) {
        int[] next = new int[b.length()];
        next[0] = -1;
        next[1] = 0;
        int pos = 2;
        int cnt = 0;
        while (pos < next.length) {
            if (b.charAt(pos - 1) == b.charAt(cnt)) {
                cnt++;
                next[pos] = cnt;
                pos++;
            } else if (cnt > 0) {
                cnt = next[cnt];
            } else next[pos++] = 0;
        }
        return next;
    }

```