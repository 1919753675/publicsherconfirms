package com.lagou.rabbitmq.demo;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class PublicsherConfirmsProducer {

    public static void main(String[] args) throws Exception, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.111.133");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //向RabbitMQ服务器发送AMQP命令，将当前通道标记为发送方确认通道
        final AMQP.Confirm.SelectOk selectOk = channel.confirmSelect();

        channel.queueDeclare("queue.pc",true,false,false,null);
        channel.exchangeDeclare("ex.pc","direct",true,false,null);
        channel.queueBind("queue.pc","ex.pc","key.pc");

        //发送消息
        channel.basicPublish("ex.pc","queue.pc",null,"hello world".getBytes());

        //同步的方式等待RabbitMQ的确认消息
        try{
            channel.waitForConfirmsOrDie(5_000);
            System.out.println("发送的消息已经得到确认");
        }catch (IOException ex){
            System.out.println("消息被拒收");
        }catch (InterruptedException ex){
            System.out.println("发送消息的通道不是PublisherConfirems通道");
        }catch (TimeoutException ex){
            System.out.println("等待消息确认超时");
        }

        channel.close();
        connection.close();
    }
}
