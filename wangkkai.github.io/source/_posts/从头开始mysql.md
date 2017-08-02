---
title: 从头开始mysql
date: 2016-09-19 10:57:53
tags: MySql
categories: 数据库
---
```

```
//将自增的列变为不自增
alter table  tablename modify columnname int not null;
```
alter table的操作
![](http://oc6shen8h.bkt.clouddn.com/index.jpg)
//mysql的自定义变量
SELECT @min_price:=MIN(price),@max_price:=MAX(price) FROM shop;    
SELECT * FROM shop WHERE price=@min_price OR price=@max_price;
```
![](http://oc6shen8h.bkt.clouddn.com/userdefinepara.jpg)

```
对于自增字段插入时候可以选择null

select LAST_INSERT_ID();//最后插入的id

mysql < mysql-batch-file //mysql的批处理

alter table stu drop column sex;//删除列

alter table stu add column sex varchar(20) not null after name;//指定位置添加列

alter table stu change sex xingbie varchar(20);//改变列名

select database();//显示当前使用的mysql库

mysql -uroot -p

mysql -h -uroot -p //远程 

show databases;

use mysql;

show tables;

show columns from stu;//与下面这条语句相等

describe stu;

show create database mysql;//显示创建该库的语句（在该库已经存在的情况下）

show create table stu;//显示创建该表时候的语句（在该表已经存在的情况下使用）

表名区分大小写 列名不区分大小写  列里面的值也不区分大小写

select distinct name from stu;//distinct应用于所有列,即所有列的内容完全一样才返回一条


select distinct name,mail from stu order by name limit 0,1;//等价于下面

select distinct name,mail from stu order by name limit 1 offset 0;

 select distinct name,mail from stu where name like 'zhangsan';//like在没有通配符的情况下就是=
 
 select * from stu where name <> 'zhangsan';
 
 select * from stu where id between 2 and 5;
 
 is null 
 
 select name from stu where id in (1,2);
 
 select * from stu where name like 'zhangsan%';// %匹配0个1个多个
 
 select * from stu where name like 'zhangsan_';// %匹配1个字符
 
 select * from stu where mail like '%/_%' escape '/';  //转义采用 escape '/'
 
 select concat(name,' ',mail,' ',sex) as stuinfo from stu;//concat()函数 
 
 select now(); //2017-07-07 20:38:56

date(now())  //2017-07-07
Year(now())  //2017
month(now()) //07

count(*) //计算所有的行包括null行
count(列) //计算非空行

select max(name) from stu;//对于字符串的max返回排序后的最后一条

select name from stu group by name having name='zhangsan' ;

 select id,stu.name,mail,course,score from stu join grades on stu.name=grades.name;

 
 select id,s.name,mail,course,score from stu as s, grades as g where s.name=g.name;
 
 
 union 要有两个或两个以上的select语句  并且选择的列必须一致 并且自动去重 如果不想去重选择union all
 
 order by 只能用在最后一个select后面
 
 全文本索引：fulltext   select note from textnote where match(note) against('hello');
 
 varchar类型不给长度会报错

insert into table2(name) select name from table1;//将一张表的数据插入另一张表 mysql不支持select into 

update table set column ='' where column1='';//若更新过程中出现错误回退，若想忽略继续执行update ignore

//存储过程
delimiter //
mysql> create procedure pro()
    -> begin 
    -> select * from stu;
    -> end //
delimiter ;

//调用
call pro();

//删除
drop procedure pro ;
```
```

//带参数的存储过程
delimiter //
create procedure pro(
out count int
)
begin
select count(*) from stu into count;
end //
delimiter ;
//调用
call pro(@count);
select @count;  
```
![image](http://oc6shen8h.bkt.clouddn.com/delimiter.jpg)
```



delimiter //
create procedure pro1(
in idnum int,
out idcount int
)
begin 
select count(*) from stu where id <idnum into idcount;
end //
delimiter ;

call pro1(5,@idcount);

select @idcount;

```
![image](http://oc6shen8h.bkt.clouddn.com/delimiter2.jpg)
`create trigger newtrigger after insert on stu for each row select 'insert complete' `