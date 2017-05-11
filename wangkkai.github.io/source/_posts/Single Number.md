---
title: Single Number
date: 2017-05-11 20:42:01
tags: [leetcode,XOR]
categories: 算法
---
>Given an array of integers, every element appears twice except for one. Find that single one.

>Note:
Your algorithm should have a linear runtime complexity. Could you implement it without using extra memory?

原问题如果不限制时间复杂度和空间度应该很容易解决。但是要求线性的时间复杂度和不准用额外的空间，这就有点难度。
<!--more-->  
用XOR异或来解就非常适合。  
0^X=X;  
X^X=0;  
那么因为其他数都为偶数个，那么它们之间的异或就是0，只有一个数是奇数个，那么0^该数就是答案。
A^B^C^D^C^B^A^D^X=A^A^B^B^C^C^D^D^X=0^X=X
```
public int singleNumber(int[] nums) {
        int res=0;
        for (int i = 0; i <nums.length ; i++) {
            res=res^nums[i];
        }
        return res;
    }
```

类似的题目有一个给定的连续的整数数字，其中间缺少一个数。求缺少的这个数。
一种做法是将这个数组的所有数字进行相加，然后与N/2*(开始+结尾)相减，差值就是缺少的数。  
另一种方式就是采用异或。当前数组的所有值异或，再异或完整的数组。  
比如数组是[1,2,3,5]  
res=(1^2^3^5)^(1^2^3^4^5);

note:就在刚刚又做了一题，找出两个字符串中的不同。其中一个字符串是另一个字符串的乱序+1个随机生成的字符。  
>Input:  
s = "abcd"  
t = "abcde"  

>Output:  
e  

我还是按照固有的思路进行的，采用map存取判断。
```
    public char findTheDifference(String s, String t){
        HashMap<Character,Integer> map=new HashMap<Character,Integer>();
        for (int i = 0; i <s.length() ; i++) {
            if (map.containsKey(s.charAt(i))){
                int value=map.get(s.charAt(i));
                value++;
                map.put(s.charAt(i),value);

            }else map.put(s.charAt(i),1);
        }
        for (int i = 0; i < t.length(); i++) {
            if (map.containsKey(t.charAt(i))){
                int value=map.get(t.charAt(i));
                value--;
                map.put(t.charAt(i),value);
            }else{
                return t.charAt(i);
            }

        }
        for (Map.Entry<Character,Integer> entry:map.entrySet()
                ) {
            if (entry.getValue()!=0){
                return  entry.getKey();
            }
        }
        return 0;
    }
```

后来看到了讨论区才发现这种方法如此的幼稚。异或简单优雅。如下：
```
    public char findTheDifference1(String s, String t) {
        char res=t.charAt(t.length()-1);//t的最后一个字符
        for (int i = 0; i < s.length(); i++) {
            res^=s.charAt(i);
            res^=t.charAt(i);
        }
        return  res;
    }
```