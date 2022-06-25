package com.lagou.rabbitmq.Excutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.*;

public class SchduleExecutor {
    /**
     * 有点：可以多线程执行，一定程度上避免任何互相影响，单个任务移除不影响其它任务
     * 在高并发的情况下，不建议使用定时任务去做，因为太浪费服务器性能，不建议
     *
     * @param args
     */
    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Timer timer = new Timer();
        //线程工厂
        ThreadFactory factory = Executors.defaultThreadFactory();
        //使用线程池
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(10,factory);
        System.out.println("开始等待用户付款10秒： "+sdf.format(new Date()));
        service.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("用户未付款，交易取消："+ sdf.format(new Date()));
            }
        },10,TimeUnit.SECONDS);
    }
}
