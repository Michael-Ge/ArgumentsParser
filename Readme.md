# 命令行参数解析类
## 1. 用法
```java

    public static void main(String[] args){

        String msg = ArgumentsParser.builder()
                //Option with parameter values
                .setOptions("-a","-b","-c","-d")
                //Option without parameter values
                .setOptionsWithoutValue("-k","-l","-m","-n")
                //Separator between option and value
                .setValueSeparator("=","") 
                //Command line arguments
                .setArgs(args)
                .build()
                .check((argument)->{
                    //You can check each parameter item here, if it is not correct, you can throw an IllegalArgumentException
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
        }
```

```bash
   java -jar ArgumentsParser.jar -a=A -k -b1234 -lmn -c C -d 102 demo.txt
```