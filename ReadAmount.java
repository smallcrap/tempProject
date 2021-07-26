package test.HSBC;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 持续输入金额
 * 定时账户余额
 */
public class ReadAmount {
    //记录当前账户余额
    private static HashMap<String , BigDecimal> balanceHash = new HashMap<String , BigDecimal>();
    //线程输出结束标志
    private static boolean STOP =false;
    //输出固定时间
    private static final long sleepTime =60*1000;

    //入口函数
    public static void main(String[] args) {
    //数组缓冲
    byte[] b = new byte[1024];
    //有效数据个数
    int n = 0;
    //开始输出线程
   startThread();
    try{
        while(true){
            //提示信息
            System.out.println("Please input the money[RMB 100]：");
            //读取数据
            n = System.in.read(b);
            //转换为字符串
            String s = new String(b,0,n - 1);
            //判断是否是quit
            if(s.equalsIgnoreCase("quit")){
                STOP = true;
                System.out.println("Exit：" + s);
                break;
            }
            //检查输入
            checkInput(s);
        }

    }catch(Exception e){}

    }

    //检查输入是否合法
    private static void checkInput(String str){
        String UperStr = str.toUpperCase();
        //进行有效拆分
        String[]  amountInputs =  UperStr.split(" ");
        //格式必须满足币种  金额
        if (amountInputs.length==2){
           String currency=  amountInputs[0];
           String amount = amountInputs[1];
          //  System.out.println("输入内容为：" +currency +"'"+amount);
            if (isAmount(amount) && isCurrency(currency) ){
                updateBalance(currency,amount);
            }
        }else{
            System.out.println("输入格式不合法");
        }
    }

    //检查金额是否合法
    private static boolean isAmount(String str){
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([-,1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
            return false;
        }
        else {
            return true;
        }
    }

    //检查币种是否合法
    private static boolean isCurrency(String currency){
        //TODO
        return true;
    }


    //更新金额
    private static void  updateBalance(String key,String value){
        if (balanceHash.get(key)==null){
            BigDecimal bdAmount =new BigDecimal(value);
            balanceHash.put(key,bdAmount);
        }else{
            BigDecimal bdBalance = balanceHash.get(key);
            bdBalance = bdBalance.add(new BigDecimal(value));
            if(bdBalance.equals(0.00)){
                balanceHash.remove(key);
            }else{
                //更新金额
                balanceHash.put(key,bdBalance );
            }

        }
    }

    //开启输出线程
    private static void startThread(){
        RunnableThreadShow rtt = new RunnableThreadShow();
        new Thread(rtt,"输出线程").start();
    }

    //持续输出线程
    private static class RunnableThreadShow implements Runnable {
       public void run() {
            while (true){
                if (STOP){
                    break;
                }
                //判断是否为空
                if (!balanceHash.isEmpty()){
                    Iterator<String> set=  balanceHash.keySet().iterator();
                    String keyCurrency;
                    String valueAmount;
                    System.out.println("截止到["+getCurrenctDate()+ "]，账户当前余额：");
                    while (set.hasNext()){
                        keyCurrency = set.next();
                        valueAmount=  balanceHash.get(keyCurrency).toString();
                        System.out.println(keyCurrency +" "+valueAmount);
                    }
                }

                try {
                    //暂停60秒
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        }
    }

    //获取当前时间
    private static String getCurrenctDate(){
        Date d =new Date();
        SimpleDateFormat sbf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sbf.format(d);
    }


}
