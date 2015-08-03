# 2.3.1发行日志 #

  * AviatorEvaluator.execute(exp, env)和AviatorEvaluator.execute(exp)默认将缓存编译结果，防止OOM。


# 2.3.0发行日志 #

  * 支持大整数计算，引入big int类型，以大写字母N为后缀或者超过long范围的整数将被解析为`java.math.BigInteger`。
  * 支持精度计算，引入decimal类型，以大写字母M为后缀的数字将被解析为`java.math.BigDecimal`。
  * `AviatorEvaluator`加入新方法`setMathContext(MathContext)`用于设置decimal的计算精度。
  * 支持科学计数法表示的数字，如2e-3,2E3等。
  * 更新math函数库支持大数和精度运算。

# 2.0版本特性 #
  * 支持完整的位运算操作
  * 添加新函数：str、double和long用于类型转换，string.indexOf(s1,s2)用于字符串位置查找
  * 性能优化，平均性能提升100%，方法调用提升200%
  * 修改AviatorFunction接口，如果你有自定义方法，需要适应新的接口。

# 2.1.1版本特性 #

  * 性能优化，方法调用和数值计算性能有较大提升，具体请看新的性能测试。整体性能达到甚至超过groovy 1.8。
  * 添加新函数： date\_to\_string和string\_to\_date用于字符串和日期对象的相互转化，string.split和string.join用于字符串的切分和连接，string.replace\_all和string.replace\_first用于字符串的替换。
  * 允许输出字节码生成信息，查看动态生成的类的字节码，设置trace为true即可：
```
AviatorEvaluator.setTrace(true);
```

默认输出在标准输出，可以重定向输出：
```
 AviatorEvaluator.setTraceOutputStream(new FileOutputStream(new File("aviator.log")));
```

生产环境不建议打开输出。

# 2.2.0 版本特性 #

  * 支持多维数组
  * DateFormat缓存无效的bug
  * 添加Expression#getVariableNames()用于返回表达式的变量列表
  * 添加AviatorEvaluator.exec 方法用于简化表达式的执行
  * 修复 [issue 2](https://code.google.com/p/aviator/issues/detail?id=2).

# 2.2.1 版本特性 #
  * 修复Expression#getVariableNames()无法正常返回数组或者map等集合类型名字的bug，感谢林韬。
  * 修复javadoc错误，默认Aviator的优化级别从COMPILE修改为EVAL。