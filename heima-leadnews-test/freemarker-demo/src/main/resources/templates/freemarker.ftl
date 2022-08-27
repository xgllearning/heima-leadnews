<!--该注释输入html注释，可以在源代码看到，属于freemarker的文本-->
<#--该注释在在源代码看不到-->
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
<b>普通文本 String 展示：</b><br><br>
Hello ${name!""} <br>
<#--${name!''}表示如果name为空显示空字符串-->
<hr>
<b>对象Student中的数据展示：</b><br/>
姓名：${stu.name}<br/>
年龄：${stu.age}
<hr>
</body>
</html>