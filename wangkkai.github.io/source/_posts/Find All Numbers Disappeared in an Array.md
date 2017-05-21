---
title: Find All Numbers Disappeared in an Array
date: 2017-05-12 09:37:26
tags: [leetcode]
categories: 算法
---
>Given an array of integers where 1 ≤ a[i] ≤ n (n = size of array), some elements appear twice and others appear once.

>Find all the elements of [1, n] inclusive that do not appear in this array.

>Could you do it without extra space and in O(n) runtime? You may assume the returned list does not count as extra space.

>Example:

>Input:
[4,3,2,7,8,2,3,1]

>Output:
[5,6]  
<!--more-->

这题要求找出数组中没有出现的所有的数字。但是要求时间复杂度O(n),不使用额外的空间复杂度。  
如果可以使用O(n)的空间复杂度，我们就可以使用一个额外的辅助数组比如arr[ ]用来标记对应的nums[i]。  
比如nums[i]=3,我们就将辅助数组的第三个数赋值为-1，也就是arr[2]=-1.  
最后只要遍历arr，找出其中不为-1的位置即可，该位置+1就是未出现的数。  
例如：  
nums=[4,3,2,7,8,2,3,1]  
arr =[-1,-1,-1,-1,0,0,-1,-1]   
不为-1的位置是4,5.加1后为5,6.    

但是如果不允许使用额外的空间复杂度，我们就需要在原有的数组基础上想办法。  
方法就是，利用nums充当arr。相当于arr不存-1,而是存放nums[i]的原有的正数取反。  
同样是上面的例子，为了清楚地说明，我们还使用o(n)的空间复杂度。
nums=[4,3,2,7,8,2,3,1]  
arr =[-4,-3,-2,-7,8,2,-3,-1]   

如上，我们只需要找出arr>0的数的位置即可。位置为5,6.
到这里，我们就可以消去我们的空间复杂度了。将nums替换为arr。这样即使替换后也不会造成任何的影响。我们只需要在取的时候，nums[i]取绝对值就可以了。  
```
 public ArrayList<Integer> findDisappearedNumbers(int[] nums) {
        ArrayList<Integer> list=new ArrayList<Integer>();
        for (int i = 0; i < nums.length; i++) {
            int value=Math.abs(nums[i])-1;
            if (nums[value]>0){
                nums[value]=-nums[value];
            }
        }

        for (int i = 0; i <nums.length ; i++) {
            if (nums[i]>0){
                list.add(i+1);
            }
        }
        return list;
    }
```