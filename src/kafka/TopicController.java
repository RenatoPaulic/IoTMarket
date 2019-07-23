package kafka;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import program.Agent;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


/**
 * Singleton class that provides basic methods for
 * managing Apache Kafka topics
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class TopicController {

    private Properties props;
    private AdminClient admin = null;
    private ZkClient zkClient = null;
    private ZkUtils zkUtils = null;

    private String zookeeperServer = Agent.zookeeperServer;
    private String kafkaServer = Agent.kafkaServer;

    private static TopicController instance = new TopicController();

    private TopicController() {

        buildDefaultClientConfig();

    }

    /**
     * Method witch configure kafka parameters
     */
    private void buildDefaultClientConfig(){

        props = new Properties();

        props.setProperty("bootstrap.servers", kafkaServer);
        props.setProperty("client.id", UUID.randomUUID().toString());
        props.setProperty("metadata.max.age.ms", "3000");
        props.setProperty("group.id", UUID.randomUUID().toString());
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("session.timeout.ms", "30000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        admin = AdminClient.create(props);

    }



    /**
     * Method that create new topic on kafka server
     * @param topicName name of topic to be created
     * @param numOfPartitions number of partitions in topic
     * @param replicationFactor number of topic replications
     */
    public void createTopic(String topicName, int numOfPartitions, short replicationFactor){


        try {
            NewTopic newTopic = new NewTopic(topicName, numOfPartitions, replicationFactor);

            CreateTopicsResult createTopicsResult = admin.createTopics(Collections.singleton(newTopic));

            createTopicsResult.values().get(topicName).get();

        } catch (InterruptedException | ExecutionException e) {

            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }

    }

    /**
     * Method that deletes topic from kafka server
     * @param topic name of topic to be deleted
     */
    public void deleteTopic(String topic){

        String zookeeperHosts = zookeeperServer;
        int sessionTimeOutInMs = 15 * 1000;
        int connectionTimeOutInMs = 10 * 1000;

        zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);
        zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

        try {

            AdminUtils.deleteTopic(zkUtils, topic);

            zkClient.deleteRecursive(ZkUtils.getTopicPath(topic));

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if (zkClient != null) {
                zkClient.close();
                zkUtils.close();
            }
        }


    }

    /**
     * Method that list all topics on kafka server
     * @return Set of all topic on kafka server
     */
    public Set<String>  listTopics() {

        ListTopicsResult topics = admin.listTopics();
        try {
            Set<String> topicNames = topics.names().get();
            return topicNames;
        }catch (Exception e){}

        return null;
    }


    public void close(){

        if(admin != null) {
            admin.close();
        }
        if (zkClient != null) {
            zkClient.close();
            zkUtils.close();
        }


    }

    public static TopicController getInstance(){

        return instance;


    }






}
