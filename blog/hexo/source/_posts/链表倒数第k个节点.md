---
title: 链表倒数k节点
date: 2017-02-26 17:44:23
tags: [双指针,链表]
categories: 算法
---
>输入一个链表，输出该链表中倒数第k个结点

思路：利用两个指针pre 和 after。pre指向第1个节点，after指向第k个节点。然后依次遍历，若after到链表尾，则pre就是指向倒数第k个节点。

```
public class Solution {
    public ListNode FindKthToTail(ListNode head,int k) {
        if(head==null){
            return null;
        }
        ListNode pre=head;
        ListNode after=head;
        int i=0;
        while(i<k){ //目的使after指向k节点
            if(after==null){ //防止k大于链表长度
                return null;
            }
            after=after.next;
            i++;
            
        }
        while(after!=null){
            pre=pre.next;
            after=after.next;
        }
        return pre;
    }
}

class ListNode {
    int val;
    ListNode next = null;

    ListNode(int val) {
        this.val = val;
    }
}
```