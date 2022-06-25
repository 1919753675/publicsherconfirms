package com.lagou.rabbitmq.Qos;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MyProducer {

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.111.133");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare("queue.ca",true,false,false,null);
        channel.exchangeDeclare("ex.ca","direct",false,false,null);
        channel.queueBind("queue.ca","ex.ca","key.ca");

        for (int i = 0; i < 5; i++) {
            channel.basicPublish("ex.ca","key.ca",null,("hello-"+i).getBytes());
        }



        channel.close();
        connection.close();
    }
}
