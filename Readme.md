# 命令行参数解析类
## 1. 用法
```java

    String msg = ArgumentsParser.builder()
            //有值的参数项
            .setOptions("-a","-b","-c","-d")
            //无值的参数项
            .setOptionsWithoutValue("-k","-l","-m","-n")
            //参数项与参数值之间的分隔符
            .setValueSeparator("=","")
            .setArgs(args)
            .build()
            .arguments((argument)->{
                //可以在这里处理每个参数项，也可以调用 ArgumentsParser.getArguments*** 方法处理参数。
                if (StringUtils.isEmpty(argument.getOption())){
                    System.out.println("argument：" + argument.getValue());
                    System.out.println("--------------------");
                }
                else{
                    System.out.println("option：" + argument.getOption());
                    System.out.println("value ：" + argument.getValue());
                    System.out.println("--------------------");
                }
            })
            .getIllegalArgumentMessage();
```

```bash
   java -jar ArgumentsParser.jar -a=A -k -b1234 -lmn -c C -d 102 demo.txt
```