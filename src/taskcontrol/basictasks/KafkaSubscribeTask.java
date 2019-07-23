package taskcontrol.basictasks;

import enums.OffsetStart;
import help.AuctionMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import program.Agent;
import taskcontrol.executors.TaskExecutor;

import java.util.*;

/**
 * Subscription task that act as kafka consumer
 * and process income messages to all auction tasks it owns
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class KafkaSubscribeTask extends Thread implements ITask, ISubscribeTask {

    private KafkaConsumer<String, String> consumer;
    private TaskExecutor taskExecutor;
    private List<String> topicList;

    private List<AuctionTask> tasks;

    private String kafkaServer;

    private String[] marks;

    private String topic;
    private OffsetStart startPoint;


    /**
     * KafkaSubscribeTask constructor that provides subscription to multiple topics
     * @param taskExecutor  reference to TaskExecutor class, for task result notification
     * @param kafkaServer connection string for kafka server
     * @param topicList  topics list to subscribe
     * @param startPoint  offset start point for reading message from topic
     * @param marks  represent auction messages mark that will be filtered while receiving message
     */
    public KafkaSubscribeTask(TaskExecutor taskExecutor, String kafkaServer, List<String> topicList, OffsetStart startPoint, String ... marks){

        this.topicList = topicList;
        this.taskExecutor = taskExecutor;
        this.startPoint = startPoint;

        this.marks = marks;

        this.kafkaServer = kafkaServer;

        tasks = new ArrayList<>();


    }

    /**
     * KafkaSubscribeTask constructor that provides subscription to single topic
     * @param taskExecutor  reference to TaskExecutor class, for task result notification
     * @param kafkaServer connection string for kafka server
     * @param topic  topic to subscribe
     * @param startPoint  offset start point for reading message from topic
     * @param marks  represent auction messages mark that will be filtered while receiving message
     */
    public KafkaSubscribeTask(TaskExecutor taskExecutor, String kafkaServer, String topic, OffsetStart startPoint, String ... marks){

        this.topic = topic;
        this.taskExecutor = taskExecutor;
        this.startPoint = startPoint;

        this.marks = marks;


        this.kafkaServer = kafkaServer;

        tasks = new ArrayList<>();

    }


    @Override
    public void endTask(){

        this.stop();

    }

    @Override
    public void run(){


            // process message while thread is active
            while (true) {

                // poll messages every 100 ms
                ConsumerRecords<String, String> records = consumer.poll(100);

                for (ConsumerRecord<String, String> record : records) {

                    AuctionMessage auctionMessage = new AuctionMessage(record.value());

                    // process only message that are important for conversation (only with right message mark)
                    if(Arrays.asList(marks).contains(auctionMessage.getMessageMark())) {

                        Agent.logger.info(" RECEIVED MESSAGE: " + " HEADER: " + auctionMessage.getHeader() + " SENDER: " +
                                auctionMessage.getSender() + " CONTEXT: " + auctionMessage.getContext() + " VALUE: " + auctionMessage.getValue());

                        System.out.println("Received message " + auctionMessage.toString());

                        // forward message to all listening tasks
                        for(int i = 0 ; i < tasks.size(); i ++){

                            tasks.get(i).processMessage(auctionMessage);

                        }
                    }

                }

            }


    }




    @Override
    public void execute() {

        // set up consumer
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServer);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, startPoint.toString().toLowerCase());

        consumer = new KafkaConsumer<>(props);

        if(topicList == null){
            consumer.subscribe(Arrays.asList(topic));
        }else {
            consumer.subscribe(topicList);
        }


        taskExecutor.notifyTaskResult(true);
        start();



    }

    @Override
    public void addSubTask(AuctionTask task){

        Agent.logger.info("Kafka subscribe task: " + "added subtask " + task.toString());
        System.out.println("Kafka subscribe task: " + "added subtask " + task.toString());

        tasks.add(task);
        task.onStart();



    }

    @Override
    public void removeSubTask(AuctionTask task) {

        task.onEnd();
        tasks.remove(task);

        Agent.logger.info("Kafka subscribe task: "  + "removed subtask " + task.toString());
        System.out.println("Kafka subscribe task: "  + "removed subtask " + task.toString());
    }




}






