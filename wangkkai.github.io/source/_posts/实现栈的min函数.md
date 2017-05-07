---
title: 实现栈的min函数
date: 2017-02-28 20:00:00
tags: 栈
categories: 算法
---
>定义栈的数据结构，请在该类型中实现一个能够得到栈最小元素的min函数。

思路：本题可以用两个栈来实现该功能，也就是一个栈stack1用来实现正常的栈功能，另一个栈stack2用来保存最小值。stack2的容量可以与stack1的容量完全相同，只不过stack2存放的却是stack1对应位置的最小值，需要注意的点就是stack2存放时候需要看当前值v是不是比stack2之前的值小，是的话就存入,否则就存stack2.peek()值。
Stack1: 2 3 4 5 1
Stack2: 2 2 2 2 1

```
import java.util.Stack;

public class MyStack {
    public static void main(String[] args) {
        MyStack mystack=new MyStack(stack1,stack2);
        mystack.push(1);
        mystack.push(2);
        System.out.println(mystack.min());
    }
    static Stack<Integer> stack1=new Stack<Integer>();
    static Stack<Integer> stack2=new Stack<Integer>();
    public void push(int node) {
        stack1.push(node);
        if(stack2.size()==0){
            stack2.push(node);
        }else if(node<=stack2.peek()){
            stack2.push(node);
        }else{
             stack2.push(stack2.peek());
        }
    }
    
    public void pop() {
        stack2.pop();
        stack1.pop();
        
    }
    
    public int top() {
        return stack1.peek();
    }
    
    public int min() {
        return stack2.peek();
    }
    public MyStack(Stack<Integer> s1,Stack<Integer> s2) {
        this.stack1=s1;
        this.stack2=s2;
    }
}

```