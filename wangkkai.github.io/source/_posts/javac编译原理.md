---
title: 括号匹配
date: 2016-09-19 10:57:53
tags: 栈
categories: 算法
---
左右括号的匹配问题，可以用栈来很好的解决。  
1. 遇到左括号，就入栈。
2.  遇到右括号，判断此时栈是否为空。 
* 栈为空，说明右括号多。
* 不为空，判断栈顶元素与该右括号是否对应，不对应，说明不匹配。如{(};若匹配则出栈。
3. 最后，遍历整个Str后,若栈为空，说明完全匹配，若栈不空，说明左括号多。
```
import java.util.Stack;
public class BracketMatch{
	public static void main(String[] args) {
		String s="{[(2+4)+(3-5)/9]*4+1}*{[(2-4)+(3-5)*9]*(4+1)} ";
		System.out.println(match(s));
	}

	public static boolean match(String s){
		Stack stack=new Stack();
		char c[]=s.toCharArray();
		for (int i=0;i<c.length ;i++ ) {
			if(isLeftBracket(c[i])){
				stack.push(c[i]);
			}else if(isRightBracket(c[i])){
				//如果栈为空，说明右括号多
				if(stack.isEmpty()){
					return false;
				}else {
					//判断栈顶的元素是否和右括号是匹配的
					if(!stack.peek().equals(resverse(c[i]))){
						return false;
					}else{
						//若匹配则将对应的左括号出栈
						stack.pop();
					}
				}
			}
		}
		//如果所有的字符都匹配结束，栈也为空，那么就表示匹配，否则就是左括号多
		if (stack.isEmpty()) {
			return true;
		}
		return false;
	}
	//判断是否为左括号
	public static boolean isLeftBracket(char c){
		if(c=='{'||c=='['||c=='('){
			return true;
		}
		return false;
	}
	//判断是否为右括号
	public static boolean isRightBracket(char c){
		if(c=='}'||c==']'||c==')'){
			return true;
		}
		return false;
	}
	//找出与右括号对应的左括号
	public static char resverse(char c){
		char res=' ';
		if(c=='}'){
			res= '{';
		}else if(c==']'){
			res= '[';
		}else if(c==')'){
			res= '(';
		}
		return res;
	}
}
```