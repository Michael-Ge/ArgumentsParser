package com.ge81.tool;

/**
 * @author Michael Ge
 * @date 2019-06-10
 */

public class Main {
    public static void main(String[] args){

        String msg = ArgumentsParser.builder()
                //有值的参数项
                .setOptions("-a","-b","-c","-d")
                //无值的参数项
                .setOptionsWithoutValue("-k","-l","-m","-n")
                //参数项与参数值之间的分隔符
                .setValueSeparator("=","")
                .setArgs(args)
                .build()
                .check((argument)->{
                    //可以在这里检查每个参数项，如果不正确则可以抛出 IllegalArgumentException 异常
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

        if (!StringUtils.isEmpty(msg)){
            System.out.println(msg);
            System.exit(1);
        }
    }
}
