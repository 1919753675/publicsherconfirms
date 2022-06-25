package com.lagou.rabbitmq.Qos;

import com.rabbitmq.client.*;

import java.io.IOException;

public class MyConsumer {

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("192.168.111.133");
        factory.setPort(5672);
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

//        //使用basic做限流，仅对消息推送模式生效
//        //表示Qos是10个消息，最多10个消息等待确认
//        channel.basicQos(10);
//        //表示最多10个消息等待，如果global设置为true，则表示只要是使用当前的channel的Consumer,该设置都生效
//        //false表示几限于当前Consumer
//        channel.basicQos(10,false);
//        //第一个参数表示未确认消息的大小，Rabbit没有实现，不用管
//        channel.basicQos(1000,10,true);


//        channel.queueDeclare("queue.ca",false,false,false,null);
        channel.basicConsume("queue.ca",false,"myConsumer",new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(new String(body));

                //可以批量确认消息，减少每个消息都发送确认带来的网络流量负载
                channel.basicAck(envelope.getDeliveryTag(),false);

                //可以对多条消息拒收
                //第一个参数是消息的标签，第二个参数表示不确认多个消息还是一个消息
                //第三个参数表示不确认的消息是否需要重新入列，然后重发
//                channel.basicNack(envelope.getDeliveryTag(),false,true);

                //可以拒收一条消息
                //对于不确认的消息，是否重新入列，然后重发
//                channel.basicReject(envelope.getDeliveryTag(),true);
            }
        });


//        channel.close();
//        connection.close();
    }
}
