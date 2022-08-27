<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>

<#-- list 数据的展示 -->
<b>展示list中的stu数据:</b>
<br>
<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#if stus??><#--空值处理，对集合是否为空进行判断处理 -->
    <#list stus as stu><#--#list 集合名 as 对象名-->
        <#if stu.name=='小红'>
            <tr style="color: blueviolet">
                <td>${stu_index+1}</td><#--_index默认从0开始-->
                <td>${stu.name}</td>
                <td>${stu.age}</td>
                <td>${stu.money}</td>
            </tr>
        <#else >
            <tr>
                <td>${stu_index+1}</td><#--_index默认从0开始-->
                <td>${stu.name}</td>
                <td>${stu.age}</td>
                <td>${stu.money}</td>
            </tr>
        </#if>
    </#list>
    </#if>
</table>
<#--内建函数-->
1.获取某个集合的大小:集合大小为:${stus?size}
2.当前时间(日期加时间)：${today?datetime}
3.自定义时间：${today?string("yyyy年MM月")}
4.长数据类型的数字以字符串显示：${point?c}
5.将json字符串转为对象1:
<#assign text="{'bank':'工商银行','account':'123456'}"/>
<#assign data=text?eval/>
开户行:${data.bank}
账户:${data.account}
json转对象方式2
6.<#assign object=json?eval/>
姓名：${object.name}
性别：${object.age}
<hr>

<#-- Map 数据的展示 -->
<b>map数据的展示：</b>
<br/><br/>
<a href="###">方式一：通过map['keyname'].property</a><br/>
输出stu1的学生信息：<br/>
姓名：${stuMap['stu1'].name}<br/>
年龄：${stuMap['stu1'].age}<br/>
<br/>
<a href="###">方式二：通过map.keyname.property</a><br/>
输出stu2的学生信息：<br/>
姓名：${stuMap.stu2.name}<br/>
年龄：${stuMap.stu2.age}<br/>
<#--方式一和方式二的区别，方式一是根据字符串找对象，方式二传入的直接就是key，故方式二不能用于遍历-->
<br/>
<a href="###">遍历map中两个学生信息：</a><br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stuMap?keys as key>
        <tr>
        <td>${key_index+1}</td>
        <td>${stuMap[key].name}</td>
        <td>${stuMap[key].age}</td>
        <td>${stuMap[key].money}</td>
    </tr>
    </#list>
</table>
<hr>

</body>
</html>