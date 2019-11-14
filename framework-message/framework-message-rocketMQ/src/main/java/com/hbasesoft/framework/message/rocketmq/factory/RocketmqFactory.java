package com.hbasesoft.framework.message.rocketmq.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 *
 * @author 大刘杰<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月25日 <br>
 * @see com.hbasesoft.framework.message.rocketmq <br>
 * @since V1.0<br>
 */
public final class RocketmqFactory {

    private static final Logger log = new Logger(RocketmqFactory.class);

    private static final Long[] delayTimeArray = new Long[] {
        1L, 5L, 10L, 30L, 60L, 5 * 60L, 10 * 60L, 15 * 60L, 30 * 60L, 3600L, 2 * 3600L, 3 * 3600L, 4 * 3600L, 5 * 3600L,
        6 * 3600L, 8 * 3600L, 12 * 3600L, 24 * 3600L
    };

    public static final String ROCKET_MQ_NAME = "ROCKET_MQ";

    public static final String CONSUME_TYPE = "CONSUME_TYPE";

    // 普通消费
    public static final String ROCKET_MQ_DEFAULT_PUBLISH_TYPE = "NORMAL";

    // 顺序消费
    public static final String ROCKET_MQ_PUBLISH_TYPE_ORDERLY = "ORDERLY";

    // 事务消费
    public static final String ROCKET_MQ_PUBLISH_TYPE_TRANSACTION = "TRANSACTION";

    private static ThreadLocal<Map<String, DefaultMQPushConsumer>> threadLocalHolder = new ThreadLocal<Map<String, DefaultMQPushConsumer>>();

    private static DefaultMQProducer defaultMQProducer;

    /**
     * 初始化向rocketmq发送普通消息的生产者
     */
    public static DefaultMQProducer getDefaultProducer(String producerGroup) {
        /**
         * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ProducerGroupName需要由应用来保证唯一<br>
         * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键， 因为服务器会回查这个Group下的任意一个Producer
         */
        if (defaultMQProducer != null) {
            log.debug("producerGroup has exist");
            return defaultMQProducer;
        }

        // Producer Group Name
        defaultMQProducer = new DefaultMQProducer(producerGroup);

        // Name service address
        defaultMQProducer.setNamesrvAddr(PropertyHolder.getProperty("message.rocketmq.namesrvAddr"));

        // Defalut value ip@pid when not set , this key used for cluster
        // producer.setInstanceName(properties.getProducerInstanceName());

        // vip netty channel
        // defaultMQProducer.setVipChannelEnabled(false);

        // Producer retry times
        defaultMQProducer.setRetryTimesWhenSendAsyncFailed(
            PropertyHolder.getIntProperty("message.rocketmq.producer.retrytimes", 10));

        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        try {
            defaultMQProducer.start();
        }
        catch (MQClientException e) {
            log.error("RocketMq defaultProducer faile.");
            defaultMQProducer.shutdown();
            throw new UtilException(ErrorCodeDef.MESSAGE_MODEL_P_CREATE_ERROR, e);
        }

        log.debug("RocketMq defaultProducer Started.");
        return defaultMQProducer;
    }

    /**
     * 初始化向rocketmq发送事务消息的生产者
     */
    public static TransactionMQProducer getTransactionProducer() throws MQClientException {
        /**
         * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ProducerGroupName需要由应用来保证唯一<br>
         * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键， 因为服务器会回查这个Group下的任意一个Producer
         */
        // Producer Group Name
        TransactionMQProducer producer = new TransactionMQProducer(
            PropertyHolder.getProperty("message.rocketmq.producer.transactionProducerGroupName"));

        // Name service address
        producer.setNamesrvAddr(PropertyHolder.getProperty("message.rocketmq.namesrvAddr"));

        // Defalut value ip@pid when not set , this key used for cluster
        // producer.setInstanceName(properties.getProducerTranInstanceName());

        // Retry times
        producer.setRetryTimesWhenSendAsyncFailed(10);

        // 事务回查最小并发数
        // producer.setCheckThreadPoolMinSize(2);
        // 事务回查最大并发数
        // producer.setCheckThreadPoolMaxSize(2);
        // 队列数
        // producer.setCheckRequestHoldMax(2000);

        // TODO 由于社区版本的服务器阉割调了消息回查的功能，所以这个地方没有意义
        // TransactionCheckListener transactionCheckListener = new
        // TransactionCheckListenerImpl();
        // producer.setTransactionCheckListener(transactionCheckListener);

        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        producer.start();

        log.debug("RocketMq TransactionMQProducer Started.");
        return producer;
    }

    /**
     * 初始化rocketmq消息监听方式的消费者
     *
     * @param messageListenerConcurrently
     * @param datas
     * @param consumerGroup2
     */
    public static DefaultMQPushConsumer getPushConsumer(String channel, String consumerGroup,
        Boolean isConsumerBroadcasting, MessageListener messageListener) {

        log.debug("getPushConsumer start topic : " + channel);

        DefaultMQPushConsumer consumer = null;

        // Get consumer from threadlocal
        Map<String, DefaultMQPushConsumer> defaultMQPushConsumerMap = getDefaultMQPushConsumerHolder();
        consumer = defaultMQPushConsumerMap.get(consumerGroup);
        if (consumer != null) {
            log.debug("consumerGroup has exist!");
            return consumer;
        }

        // Consumer Group Name
        consumer = new DefaultMQPushConsumer(consumerGroup);

        // Name service address
        consumer.setNamesrvAddr(PropertyHolder.getProperty("message.rocketmq.namesrvAddr"));

        // Defalut value ip@pid when not set , this key used for cluster
        // consumer.setInstanceName(properties.getConsumeCrInstanceName());

        // Set Consume Thread
        consumer.setConsumeThreadMin(PropertyHolder.getIntProperty("message.executor.coreSize", 20));
        consumer.setConsumeThreadMax(PropertyHolder.getIntProperty("message.executor.maxPoolSize", 64));

        // Message Model
        if (isConsumerBroadcasting) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
        }

        // One time consume max size
        consumer.setConsumeMessageBatchMaxSize(
            PropertyHolder.getIntProperty("message.rocketmq.consumer.consumerBatchMaxSize", 0) == 0 ? 1
                : PropertyHolder.getIntProperty("message.rocketmq.consumer.consumerBatchMaxSize", 0));// 设置批量消费，以提升消费吞吐量，默认是1

        try {
            consumer.subscribe(channel, "*");
        }
        catch (MQClientException e) {
            log.error("RocketMq pushConsumer Start failure!!!.");
            throw new UtilException(ErrorCodeDef.MESSAGE_MODEL_C_CREATE_ERROR, e);
        }

        if (messageListener instanceof MessageListenerConcurrently) {
            consumer.registerMessageListener((MessageListenerConcurrently) messageListener);
        }
        else if (messageListener instanceof MessageListenerOrderly) {
            consumer.registerMessageListener((MessageListenerOrderly) messageListener);
        }

        // 延迟5秒再启动，主要是等待spring事件监听相关程序初始化完成，否则，回出现对RocketMQ的消息进行消费后立即发布消息到达的事件，
        // 然而此事件的监听程序还未初始化，从而造成消息的丢失
        // Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
        try {
            consumer.start();
        }
        catch (Exception e) {
            log.error("RocketMq pushConsumer Start failure!!!.");
            consumer.shutdown();
            throw new UtilException(ErrorCodeDef.MESSAGE_MODEL_C_CREATE_ERROR, e);
        }

        log.debug("RocketMq pushConsumer Started.");

        // Keep customer
        defaultMQPushConsumerMap.put(consumerGroup, consumer);
        log.debug(consumer.toString());
        return consumer;
    }

    private static Map<String, DefaultMQPushConsumer> getDefaultMQPushConsumerHolder() {
        Map<String, DefaultMQPushConsumer> defaultMQPushConsumerHolder = threadLocalHolder.get();
        if (defaultMQPushConsumerHolder == null) {
            defaultMQPushConsumerHolder = new HashMap<String, DefaultMQPushConsumer>();
            threadLocalHolder.set(defaultMQPushConsumerHolder);
        }
        return defaultMQPushConsumerHolder;
    }

    public static int calculationLevel(long key) {
        Long[] longArray = delayTimeArray;
        List<Long> longList = Arrays.asList(longArray);
        longList = new ArrayList<Long>(longList);
        if (longList.indexOf(key) >= 0) {
            return longList.indexOf(key) + 1;
        }
        longList.add(key);
        longArray = longList.toArray(new Long[longList.size()]);
        Arrays.sort(longArray);
        longList = Arrays.asList(longArray);
        int index = longList.indexOf(key) - 1;
        return index == -1 ? 1 : index + 1;
    }

}
