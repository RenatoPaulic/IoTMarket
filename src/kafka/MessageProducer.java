package kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import program.Agent;

import java.util.Properties;

/**
 * Singleton class that represent
 * kafka producer
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class MessageProducer {

    private static final MessageProducer instance = new MessageProducer();
    private KafkaProducer producer = null;
    private String key = "key";

    private String kafkaServer = Agent.kafkaServer;

    private MessageProducer(){  }


    /**
     * Method witch configure kafka producer
     */
    private void makeProducer(){

        Properties props = new Properties();

        props.put("bootstrap.servers", kafkaServer);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer(props);

    }

    /**
     * Method for sending message to kafka server
     * @param topic topic name
     * @param message message that will be send to topic
     */
    public void sendMessage(String topic, String message){

        Agent.logger.info("Message Produces " + "sending message " + message + " to topic " + topic) ;

        System.out.println("sendim mess " + message + " to topic " + topic);

        ProducerRecord<String,String> record = new ProducerRecord<>(topic,key,message);

        producer.send(record);


    }

    public void initMessageProducer(String kafkaServer){

        this.kafkaServer = kafkaServer;
        makeProducer();

    }

    public void shutDown(){

        if(producer != null) {
            producer.close();
        }
    }


    public static MessageProducer getInstance(){
        return instance;
    }






}
