package com.ge81.tool;

import java.util.*;

/**
 * 命令行参数解析器
 * @author Michael Ge
 * @date 2019-06-06
 */

public class ArgumentsParser {
    private HashSet<String> mOptions;
    private HashSet<String> mOptionsWithoutValue;
    private String[] mArgs;
    private String[] mValueSeparator;
    private ArgumentsParser(){}

    private List<Argument> mNonOptionArgs;
    private List<Argument> mArguments;
    private Map<String,Argument> mOptionMap;

    private Exception mIllegalArgumentException;

    private void parse() throws IllegalArgumentException{
        mArguments = new ArrayList<>(mArgs.length);
        mOptionMap = new HashMap<>();
        mNonOptionArgs = new ArrayList<>(mArgs.length);
        
        Argument lastArgument = null;

        int length = mArgs.length;
        for (int argIndex=0;argIndex<length;argIndex++){
            String arg = mArgs[argIndex].trim();

            //无参数值选项
            if (mOptionsWithoutValue != null && mOptionsWithoutValue.size() >0){
                if (mOptionsWithoutValue.remove(arg)) {
                    Argument argument = new Argument();
                    argument.option = arg;
                    argument.original = arg;
                    
                    lastArgument = null;
                    mArguments.add(argument);
                    mOptionMap.put(arg,argument);
                    continue;
                }
            }
            
            if (mOptions != null && mOptions.size() > 0){
                //1. 首先直接查找是否在参数名列表中存在。
                if (mOptions.contains(arg)){
                    Argument argument = new Argument();
                    argument.option = arg;
                    argument.original = arg;

                    //当前参数项是参数名（选项名），如果下一个参数项不是参数名则就认为它是参数值，需将它填入参数对中
                    //因此需要记录上一个参数项的信息。
                    lastArgument = argument;
                    mArguments.add(argument);
                    mOptionMap.put(arg,argument);
                    continue;
                }

                //2. 选项没有在选项列表中有可能是 参数名与参数值粘合在一起的情况，如：
                //   -p8080 或 -p=8080 等，这时查找是否是以指定的参数名开头
                if (mValueSeparator != null && mValueSeparator.length > 0){
                    boolean match = false;
                    for (String option : mOptions){
                        //找到以某个参数名称开头，如 -p8080，则是以 -p 参数名开头
                        if (arg.startsWith(option)){
                            int offset = option.length(); //参数值在字符串中的起始位置
                            
                            //查找是否含有参数值分隔符
                            for (String separator:mValueSeparator){
                                int ol = separator.length(); 
                                if (ol == 0 && Character.isDigit(arg.charAt(offset))) {
                                    //对于选项值与名称连在一起的情况仅支持数值值
                                    offset += ol;
                                    match = true;
                                    break;
                                }
                                else if (ol > 0 && arg.substring(offset,offset + ol).equalsIgnoreCase(separator)){
                                    offset += ol;
                                    match = true;
                                    break;
                                }
                            }

                            if (!match){
                                continue;
                            }
                            
                            //取出参数值
                            String value = arg.substring(offset);

                            Argument argument = new Argument();
                            argument.option = option;
                            argument.original = arg;
                            argument.value = value;

                            //当前参数已经有参数值，则需要为其添加参数值因此要将 lastArgument 置空。
                            lastArgument = null;
                            mArguments.add(argument);
                            mOptionMap.put(arg,argument);
                            break;
                        }
                    }

                    if (match){
                        continue;
                    }
                }
            }

            //不区配前面的任何规则，则检查是否是混合写法，因为多个无参数的选项，则可以合并写入，
            //如：分开写时 -a -p -l -k -m，混合写时 -aplkm，这种方式仅直接单个字符的选项（不含引导符 "-")
            if (arg.startsWith("-")){
                List<Argument> mix = new ArrayList<>();
                char[] opts = arg.substring(1).toCharArray();
                for (char opt : opts){
                    boolean match = false;
                    for (String optName : mOptionsWithoutValue){
                        if (optName.length() == 2 && optName.startsWith("-")){
                            if (opt == optName.charAt(1)){
                                Argument argument = new Argument();
                                argument.original = "-" + opt;
                                argument.option = argument.original;
                                mix.add(argument);
                                match = true;
                            }
                        }
                    }

                    if (!match){
                        //混合写法时只要有一个没有匹配则丢弃所解析结果
                        mArguments = Collections.emptyList();
                        mNonOptionArgs = Collections.emptyList();
                        mOptionMap = Collections.emptyMap();
                        throw new IllegalArgumentException("illegal argument: " + arg);
                    }
                }

                for (Argument argument:mix){
                    mOptionsWithoutValue.remove(argument.option);
                    mArguments.add(argument);
                }

                continue;
            }

            //当前参数项不是参数名（选项名），则当作值来处理
            if (lastArgument != null){
                lastArgument.value = arg;
                lastArgument.original = lastArgument.original.concat(" " + arg);
                lastArgument = null;
            }
            else{
                //没有参数名（选项名）则认为是缺省值
                Argument argument = new Argument();
                argument.original = arg;
                argument.value = arg;
                mNonOptionArgs.add(argument);
            }
        }
    }

    /**
     * 循环迭代所有参数，可以在这里检查参数是否正确，如果不正确可以抛出 {@link IllegalArgumentException} 异常
     *
     * @param handler 处理器
     */
    public ArgumentsParser check(ArgumentChecker handler){
        try{
            for (Argument argument : mArguments){
                handler.check(argument);
            }

            for (Argument argument : mNonOptionArgs){
                handler.check(argument);
            }
        } catch (IllegalArgumentException e){
            mIllegalArgumentException = e;
        }
        
        return this;
    }

    /**
     * 获取无效参数信息，如果参数正确则返回 null。
     * @return 无效参数信息，如果参数正确则返回 null。
     */
    public String getIllegalArgumentMessage(){
        if (mIllegalArgumentException != null){
            return mIllegalArgumentException.getMessage();
        }
        else{
            return null;
        }
    }
    
    /**
     * 参数是否无效
     * @return  true or false
     */
    public boolean isIllegalArgument(){
        return mIllegalArgumentException != null;
    }

    /**
     * 根据选项名称获取指定的参数对象
     * @param option 选项名称，如  -a -p --h 等。
     * @return Argument，或能为 null
     */
    public Argument getOptionArgs(String option){
        return mOptionMap.get(option);
    }

    /**
     * 获取缺省参数值列表，即那此没有参数名的参数。
     * 如：cmd -p 8080 a b c d，则缺省参数列表是 a,b,c,d
     *
     * @return List
     */
    public List<Argument> getNonOptionArgs(){
        return mNonOptionArgs;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private ArgumentsParser mArgsParser;
        private Builder(){
            mArgsParser = new ArgumentsParser();
        }

        /**
         * 设置无参数值参数名称（选项名）列表，区分大小写
         * @param argNames 参数名列表
         *                 例如："-a","start","--help" 等
         * @return this
         */
        public Builder setOptionsWithoutValue(Collection<String> argNames){
            mArgsParser.mOptionsWithoutValue = new HashSet<>(argNames);
            return this;
        }

        /**
         * 设置无参数值参数名称（选项名）列表，区分大小写
         * @param argNames 参数名列表，例如："-a","start","--help" 等
         * @return this
         */
        public Builder setOptionsWithoutValue(String... argNames){
            mArgsParser.mOptionsWithoutValue = new HashSet<>(Arrays.asList(argNames));
            return this;
        }

        /**
         * 设置有数值参数名称（选项名）列表，区分大小写
         * @param argNames 参数名列表，例如："-a","start","--help" 等
         * @return this
         */
        public Builder setOptions(Collection<String> argNames){
            mArgsParser.mOptions = new HashSet<>(argNames);
            return this;
        }

        /**
         * 设置有参数值参数名称（选项名）列表，区分大小写
         * @param argNames 参数名列表，例如："-a","start","--help" 等
         * @return this
         */
        public Builder setOptions(String... argNames){
            mArgsParser.mOptions = new HashSet<>(Arrays.asList(argNames));
            return this;
        }
        
        /**
         * 待解析的命令行参数
         * @param args 命令行参数
         * @return this
         */
        public Builder setArgs(String[] args){
            mArgsParser.mArgs = args;
            return this;
        }
        
        /**
         * 设置参数值分隔符，可以设置多个。默认为 null。
         * 例：-p=6039，= 就是参数值分隔符; -p6039，"" 是分隔符
         * @param separator 参数值分隔符
         * @return this
         */
        public Builder setValueSeparator(String... separator){
            mArgsParser.mValueSeparator = separator;
            return this;
        }

        public ArgumentsParser build(){
            try{
                mArgsParser.parse();
            }
            catch (IllegalArgumentException e){
                mArgsParser.mIllegalArgumentException = e;
            }
            
            return mArgsParser;
        }
    }

    /**
     * 参数选项类
     */
    public class Argument {
        private String original;
        private String option;
        private String value;

        private Argument(){}

        /**
         * 参数名（选项名）
         * 如: -p、-p8080、-p=8080 ，参数名（选项名）是 -p。
         *
         * 如果该值为 null，则表示是一个缺省参数项，例如：
         * cmd -p 8080 start
         * 这里 start 如果没有出现在参数名列有中（ArgNames）则它就是一个缺省参数（无参数名参数）
         *
         * @return 参数名
         */
        public String getOption() {
            return option;
        }

        /**
         * 参数值（选项名值）
         * 如: -p、-p8080、-p=8080 ，参数值（选项值）是 8080 或 null。
         * 
         * @return 参数名，可能为 null。
         */
        public String getValue() {
            return value;
        }

        /**
         * 获取该参数原始值
         * @return 参数原始值
         */
        public String getOriginal() {
            return original;
        }
        
        /**
         * 根据选项名称获取指定的参数对象
         * @param option 选项名称，如  -a -p --h 等。
         * @return Argument，或能为 null
         */
        public Argument getOptionArgs(String option) {
            return mOptionMap.get(option);
        }

        /**
         * 获取缺省参数值列表，即那此没有参数名的参数。
         * 如：cmd -p 8080 a b c d，则缺省参数列表是 a,b,c,d
         *
         * @return List
         */
        public List<Argument> getNonOptionArgs(){
            return mNonOptionArgs;
        }
    }
    
    public interface ArgumentChecker {
        /**
         * 查检参数是否正确，如果不正确可以抛出 {@link IllegalArgumentException} 异常
         * @param argument 参数
         * @throws IllegalArgumentException
         */
        void check(Argument argument) throws IllegalArgumentException;
    }
}
