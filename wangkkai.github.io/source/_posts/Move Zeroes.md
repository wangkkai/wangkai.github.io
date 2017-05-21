---
title: Move Zeroes
date: 2017-05-12 11:41:20
tags: [够贱]
categories: 算法
---
>Given an array nums, write a function to move all 0's to the end of it while maintaining the relative order of the non-zero elements.

>For example, given nums = [0, 1, 0, 3, 12], after calling your function, nums should be [1, 3, 12, 0, 0].

>Note:
You must do this in-place without making a copy of the array.
Minimize the total number of operations.  

<!--nore-->
我们看到条件是不准copy数组。其实可以采用比较移动，类似于冒泡排序。但是时间复杂度偏大。  
对于本类型题，可以另辟蹊径。  
就是用其他数据覆盖0的数，然后数组最后就会留有空白，这个时候进行填充就行。  

废话不多说，直接上代码。
```
    public void moveZeroes(int[] nums) {
        int point=0;//指示不为0的数填充到了哪个位置
        for (int i = 0; i < nums.length; i++) {
            if (nums[i]!=0){ //如果不为0 就从前到后依次填充
                nums[point]=nums[i];
                point++;
            }
        }
        //对于数组后面的留白进行补填
        while (point<nums.length){
            nums[point++]=0;
        }
    }
``` 

类似的将某些数移到哪里都可以采用此种办法。