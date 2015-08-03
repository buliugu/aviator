Aviator是一个高性能、轻量级的基于java实现的表达式引擎，它动态地将String类型的表达式编译成Java ByteCode并交给JVM执行。

Aviator的名称来自我很喜欢的电影——迪卡普里奥主演的 [《飞行家》](http://movie.douban.com/subject/1309070/)


```
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("email", "killme2008@gmail.com");
        String username = (String) AviatorEvaluator.execute("email=~/([\\w0-8]+)@\\w+[\\.\\w+]+/ ? $1:'unknow'", env);
```

**2014年 9 月 15 号，2.3.3版本正式发布，请看[发布日志](ReleaseNotes.md)**

你可以直接使用maven引用：
```
	<dependency>
			<groupId>com.googlecode.aviator</groupId>
			<artifactId>aviator</artifactId>
                        <version>2.3.3</version>
	</dependency>
```

[详细使用指南](User_Guide_zh.md)