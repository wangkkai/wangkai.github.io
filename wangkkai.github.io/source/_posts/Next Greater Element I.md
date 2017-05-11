---
title: Next Greater Element I
date: 2017-05-011 14:46:10
tags: [leetcode,stack]
categories: 算法
---
>You are given two arrays (without duplicates) nums1 and nums2 where nums1’s elements are subset of nums2. Find all the next greater numbers for nums1's elements in the corresponding places of nums2.   

>The Next Greater Number of a number x in nums1 is the first greater number to its right in nums2. If it does not exist, output -1 for this number.  
<!--more-->
>Example 1:  
Input: nums1 = [4,1,2], nums2 = [1,3,4,2].  
Output: [-1,3,-1]  
Explanation:  

    For number 4 in the first array, you cannot find the next greater number for it in the second array, so output -1.
    For number 1 in the first array, the next greater number for it in the second array is 3.
    For number 2 in the first array, there is no next greater number for it in the second array, so output -1.
>Example 2: 

>Input: nums1 = [2,4], nums2 = [1,2,3,4].  
Output: [3,-1]  
Explanation:  

    For number 2 in the first array, the next greater number for it in the second array is 3.
    For number 4 in the first array, there is no next greater number for it in the second array, so output -1.
>Note:
All elements in nums1 and nums2 are unique.  
The length of both nums1 and nums2 would not exceed 1000.

解答：  

其实这道题主要的部分就是找到nums2中每个数字的后续最近比它大的数。通常的做法就是遍历寻找，存入map中。如下：
```
public int[] nextGreaterElement(int[] findNums, int[] nums) {
        HashMap<Integer,Integer> map=new HashMap<Integer,Integer>();
        //寻找nums中后续较大的数
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i],-1);
            int j=i+1;
            while (j<nums.length){
                if (nums[j]>nums[i]){
                    map.put(nums[i],nums[j]);
                    break;
                }else j++;
           }
        }
        int res[]=new int[findNums.length];
            for (int i = 0; i <findNums.length ; i++) {
            res[i]=map.get(findNums[i]);
        }
        return res;
    }
```
对nums中每一个字符我们都需要遍历nums去寻找，所以时间的复杂度是O(n*n).但是我们如果采用stack就可以将时间复杂度降为O(n).并且注意`map.getOrDefault(findNums[i],-1);`这个方法。该方法可以设置map的默认值。
```
public int[] nextGreaterElement1111(int[] findNums, int[] nums) {
        HashMap<Integer,Integer> map=new HashMap<Integer,Integer>();
        Stack<Integer> stack=new Stack<Integer>();
        //遍历nums一遍
        for (int i=0;i<nums.length;i++){
            //如果栈不空，并且栈顶元素小于nums[i],就存入map.然后继续pop
            while(!stack.isEmpty()&&stack.peek()<nums[i]){
                map.put(stack.pop(),nums[i]);
            }
            //压栈操作
            stack.push(nums[i]);
            //优点 假如nums={5,4,3,2，6} 那么stack={2,3,4,5} 当6到来的时候，挨个出栈存入map即可。  
            //此时map{<2,6>,<3,6>,<4,6>,<5,6>}
        }
        for (int i = 0; i <findNums.length ; i++) {
            findNums[i]=map.getOrDefault(findNums[i],-1);
        }
        return findNums;
    }
```