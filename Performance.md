
# Introduction #
这个测试本质上是不公平的，因为实现的机制完全不同，这里只是为了做一个直观的比较，让用户对Aviator的性能有个直观的感受。参与测试的包括Aviator、groovy和[JEXL](http://commons.apache.org/jexl/)。


# 硬件和软件 #

  * OS:             Mac OS X Lion 10.7.1 (11B26)
  * MemTotal:       4 GB 1333 MHz DDR3

  * CPU:              2.3 GHz Intel Core i5

  * JDK:             java version "1.6.0\_26"

  * Aviator :  2.1.1

  * Groovy:    1.8.0

  * JEXL :  2.0.1

# JVM启动参数 #

```
   -Xmx1024m -XX:CompileThreshold=10000 -XX:MaxPermSize=256m
```


# 测试方法 #

三个引擎都采用预先编译再进行测试的方式，循环1000万次执行编译后的结果，测量耗时。

# test by yourself #

源码在 https://github.com/killme2008/EL-benchmark

你可以自己构建并测试。

# 场景1 #

计算算术表达式：
```
1000+100.0*99-(600-3*15)/(((68-9)-3)*2-100)+10000%7*71
```

测试结果：
| 测试 | 耗时（单位: 毫秒）|
|:---|:----------|
| Aviator | 258        |
| Groovy  | 27031       |
| JEXL |  43790    |

结论： Aviator在计算常量算术表达式的时候是非常快速的，跟Groovy和JEXL差距极大。这是因为aviator会在编译的时候直接计算出常量表达式的结果。

# 场景2 #

计算逻辑表达式和三元表达式混合：
```
6.7-100>39.6 ? 5==5? 4+5:6-1 : !(100%3-39.0<27) ? 8*2-199: 100%3
```

测试结果：
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 0        |
| Groovy  | 8103      |
| JEXL |  22180   |

结论： 跟上面一样，aviator仍然有极大优势，得益于编译时做的优化。

# 场景3 #

计算算术表达式和逻辑表达式的混合，带有5个变量的表达式：
```
i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99
```

变量设定为：
```
        int i = 100;
        float pi = 3.14f;
        double d = -3.9;
        byte b = (byte) 4;
        boolean bool=false;
```

每次执行前都重新设置这些变量的值。

结果：
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 11359        |
| Groovy  | 10404     |
| JEXL | 53654    |

结论： Aviator接近Groovy，JEXL最差。

# 场景4 #

表达式为：
```
pi*d+b-(1000-d*b/pi)/(pi+99-i*d)-i*pi*d/b
```
变量的设定值与场景3一致。

结果：
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 7400        |
| Groovy  | 7725     |
| JEXL | 26737    |

结论：Aviator最佳，比Groovy稍快一点点，JEXL仍然最差。

# 场景5:日期函数调用 #

  * Aviator执行 sysdate()
  * groovy执行   new java.util.Date()
  * JEXL执行 new("java.util.Date")

结果:
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 654        |
| Groovy  | 2708     |
| JEXL |  1563    |

结论：Aviator最快，JEXL第二，而Groovy最差。

# 场景6:字符串函数调用 #

  * Aviator执行 string.substring(s,b.d)
  * groovy执行  s.substring(b.d)
  * JEXL执行  s.substring(b.d)

其中变量设定为：
```
   s="hello world";
   b为map
   b.d=5;
```

结果:
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 2422        |
| Groovy  | 2611     |
| JEXL |  8344    |

结论：Aviator比Groovy稍占优势，JEXL最差。

# 场景6:字符串函数嵌套调用 #

  * Aviator执行 string.substring(string.substring(s,b.d),a,b.c.e)
  * groovy执行  s.substring(b.d).substring(a,b.c.e);
  * JEXL执行  s.substring(b.d).substring(a,b.c.e);

其中变量设定为：
```
   s="hello world";
   a=1
   b为map
   b.d=5;
   b.c为map
   b.c.e=3;
```

结果:
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 5755        |
| Groovy  | 3468     |
| JEXL |  24598   |

结论：Groovy最佳，aviator其次，JEXL与前两者差距较大。


# 场景7:测试编译性能 #

测试编译性能，编译表达式
```
i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99
```

循环编译1000次，因为groovy编译非常慢加上perm区大小限制，只能将次数限制。

结果：
| 测试 | 耗时（单位:毫秒）|
|:---|:---------|
| Aviator | 1182       |
| Groovy  | 9971     |
| JEXL |  452     |

JEXL编译速度最快，aviator其次，而groovy反而最差。我估计是groovy的语法解释，以及编译的时候要做很多优化导致。

# 小结 #

  * Aviator在执行常量表达式的时候，性能最佳。常量越多的表达式，编译优化效果越好。
  * 带有变量表达式和函数调用上，Aviator和Groovy在一个数量级上，并且有些场景Aviator稍占优势，JEXL最差。
  * 编译性能上，JEXL最佳，aviator其次，而groovy最差。