package agentbuyer.protocol;

import agentbuyer.auction.AuctionSubtype;
import agentbuyer.auctiontasks.Auction;
import agentbuyer.auctiontasks.AuctionNegotiationTask;
import agentbuyer.streamtasks.AuctionStream;
import agentbuyer.streamtasks.CreateBasicStreamTask;
import agentbuyer.streamtasks.StdoutPrintStreamTask;
import agentbuyer.streamtasks.StreamNegotiationTask;
import agentcontrol.AuctionProtocol;
import enums.AuctionProperties;
import enums.AuctionSubtypes;
import enums.OffsetStart;
import help.MessageBuilder;
import taskcontrol.basictasks.*;
import taskcontrol.executors.SequentialTaskExecutor;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Class that represent basic communication protocol
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class BasicAuctionProtocol implements AuctionProtocol {


    private String auctionStartMessage;
    private String buyerTopic;
    private Auction auction;
    private long streamTime;
    private AuctionStream auctionStream;

    /**
     * Constructor that builds auction start message used in protocol
     * @param properties default auction properties
     * @param auctionSubtype auction subtype class
     * @param auction auction class
     */
    public BasicAuctionProtocol(Properties properties, AuctionSubtype auctionSubtype, Auction auction) {

        this.auction = auction;
        streamTime = Long.parseLong(properties.get(AuctionProperties.STREAM_TIME).toString());

        List<String> contexList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        contexList.add(AuctionProperties.TOPIC.toString());
        contexList.add(AuctionProperties.AUCTION_TYPE.toString());
        contexList.add(AuctionProperties.AUCTION_SUBTYPE.toString());
        contexList.add(AuctionProperties.DEVICE_NUM_FUNCTION.toString());
        contexList.add(AuctionProperties.QUALITY_FUNCTION.toString());
        contexList.add(AuctionProperties.DEVICE_NUM_RESTRICTION.toString());
        contexList.add(AuctionProperties.QUALITY_RESTRICTION.toString());
        contexList.add(AuctionProperties.HELPER_FLAG.toString());
        contexList.add(AuctionProperties.STREAM_TIME.toString());

        valueList.add(properties.get(AuctionProperties.TOPIC).toString());
        valueList.add(properties.get(AuctionProperties.AUCTION_TYPE).toString());
        valueList.add(properties.get(AuctionProperties.AUCTION_SUBTYPE).toString());
        valueList.add(properties.get(AuctionProperties.DEVICE_NUM_FUNCTION).toString());
        valueList.add(properties.get(AuctionProperties.QUALITY_FUNCTION).toString());
        valueList.add(properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION).toString());
        valueList.add(properties.get(AuctionProperties.QUALITY_RESTRICTION).toString());
        valueList.add(properties.get(AuctionProperties.HELPER_FLAG).toString());
        valueList.add(properties.get(AuctionProperties.STREAM_TIME).toString());


        MessageBuilder messageBuilder = new MessageBuilder()
                .addMark("B")
                .addHeader("auction_start")
                .addSender(auctionSubtype.getAuctionUuid())
                .addSubcontexts("basic_parameters",  contexList.toArray(new String[contexList.size()]))
                .addValuesForSubcontexts("basic_parameters",  valueList.toArray(new String[contexList.size()]) )
                .addSubcontexts("auction_parameters", "time_waiting")
                .addValuesForSubcontexts("auction_parameters", String.valueOf(auction.getWaitTime()));

        List<Object> specificParameters = auctionSubtype.getSpecificParametersForItem();

        if(properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.AREA_AUCTION){

            messageBuilder.addSubcontexts("specific_parameters", "min_x","max_x","min_y","max_y")
                          .addValuesForSubcontexts("specific_parameters",(String) specificParameters.get(0),(String) specificParameters.get(1),(String) specificParameters.get(2),(String) specificParameters.get(3));

        }

        if(properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.INFORMATION_AUCTION){


            messageBuilder.addSubcontexts("specific_parameters", "description")
                          .addValuesForSubcontexts("specific_parameters",(String) specificParameters.get(0));

        }

        auctionStartMessage = messageBuilder.build().toString();

        buyerTopic = auctionSubtype.getAuctionUuid();

        auctionStream = new StdoutPrintStreamTask(streamTime);

        System.setProperty("auction_uuid", UUID.randomUUID().toString());




    }

    @Override
    public void initAuctionBehaviors(String topic, String kafkaServer) {

        TaskExecutor taskExecutor = new SequentialTaskExecutor();

        ITask task1 = new TopicCreateTask(taskExecutor, buyerTopic);
        ITask task2 = new KafkaSubscribeTask(taskExecutor,kafkaServer, buyerTopic, OffsetStart.LATEST, "S");
        ITask task3 = new MessageSendTask(taskExecutor,topic,auctionStartMessage);
        ITask task4 = new AuctionNegotiationTask(taskExecutor, (ISubscribeTask) task2, auction);
        ITask task5 = new TopicCreateTask(taskExecutor, "input-" + buyerTopic);
        ITask task6 = new TopicCreateTask(taskExecutor, "output-" + buyerTopic);
        ITask task7 = new KafkaSubscribeTask(taskExecutor, kafkaServer, "output-" + buyerTopic, OffsetStart.EARLIEST, "S");
        ITask task8 = new CreateBasicStreamTask(taskExecutor, kafkaServer, buyerTopic );
        ITask task9 = new StreamNegotiationTask(taskExecutor, (ISubscribeTask) task2, auctionStream);
    //    ITask task10 = new TopicDeleteTask(taskExecutor,buyerTopic);
    //    ITask task11 = new TopicDeleteTask(taskExecutor, "input-" + buyerTopic);
    //    ITask task12 = new TopicDeleteTask(taskExecutor,"output-" + buyerTopic);


        taskExecutor.addTask(task1);
        taskExecutor.addTask(task2);
        taskExecutor.addTask(task3);
        taskExecutor.addTask(task4);
        taskExecutor.addTask(task5);
        taskExecutor.addTask(task6);
        taskExecutor.addTask(task7);
        taskExecutor.addTask(task8);
        taskExecutor.addTask(task9);
   //     taskExecutor.addTask(task10);
   //     taskExecutor.addTask(task11);
   //     taskExecutor.addTask(task12);


        taskExecutor.startTaskExecution();




    }





}
