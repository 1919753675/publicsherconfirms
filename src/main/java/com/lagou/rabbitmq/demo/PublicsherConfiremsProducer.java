package com.lagou.rabbitmq.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class PublicsherConfiremsProducer {

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.111.133");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare("queue.pc",true,false,false,null);
        channel.exchangeDeclare("ex.pc","direct",true,false,null);
        channel.queueBind("queue.pc","ex.pc","key.pc");

        String message = "hello-";
        //批处理大小
        int batchSize = 10;
        //用于对需要等待确认消息的计数
        int outstandConfirms = 0;
        for(int i=0;i<103;i++){
            channel.basicPublish("ex.pc","key.pc",null,(message+i).getBytes());
            if(outstandConfirms == batchSize){
                //此时已经有一个批次的消息需要同步等待broker的消息确认
                //同步等待
                channel.waitForConfirmsOrDie(5_000);
                System.out.println("消息已经被确认");
                outstandConfirms=0;
            }
        }

        if(outstandConfirms > 0){
            channel.waitForConfirmsOrDie(5_000);
            System.out.println("剩余消息已经被确认了");
        }
        channel.close();
        connection.close();
    }
}
