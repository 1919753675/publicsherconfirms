package com.lagou.rabbitmq.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PublicsherConfiremsProducer2 {

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

//        ConfirmCallback confirmCallback = new ConfirmCallback() {
//            @Override
//            public void handle(long deliveryTag, boolean multiple) throws IOException {
//                if(multiple){
//                    System.out.println("编号小于等于 "+deliveryTag+" 的消息都已经被确认了");
//                }else{
//                    System.out.println("编号为："+deliveryTag+" 的消息被确认");
//                }
//            }
//        };

        ConcurrentNavigableMap<Long,String> map = new ConcurrentSkipListMap<>();
        
        ConfirmCallback confirmCallback = (deliveryTag,multiple) ->{
          if(multiple){
              System.out.println("编号小于等于 "+deliveryTag+" 的消息都已经被确认了");
              final ConcurrentNavigableMap<Long,String> headMap = map.headMap(deliveryTag,true);

              //清空map中已经被确认的消息信息
              headMap.clear();
          }else{
              //移除已经被确认的消息
              map.remove(deliveryTag);
                System.out.println("编号为："+deliveryTag+" 的消息被确认");
          }
        };
        
        //设置channel的监听器，处理确认的消息和不确认的消息
        channel.addConfirmListener(confirmCallback,(deliveryTag,multiple)->{ 
                if(multiple){
                    //将没有确认的消息记录到一个集合中
                    //此处省略实现 DDTO
                    System.out.println("编号小于等于 "+deliveryTag+" 的消息不确认");
                }else{
                    System.out.println("编号为："+deliveryTag+" 的消息不确认");
                }
        });
        
        String message = "hello-";
        for (int i = 0; i < 1000; i++) {
            //获取下一条即将发送的消息的消息ID
            final long nextPublishSeqNo = channel.getNextPublishSeqNo();
            channel.basicPublish("ex.pc","key.pc",null,(message+i).getBytes());
            System.out.println("编号为："+nextPublishSeqNo+ "的消息已经发送成功，尚未确认");
            map.put(nextPublishSeqNo,(message+i));
        }

        Thread.sleep(10000);
        channel.close();
        connection.close();
    }
}
