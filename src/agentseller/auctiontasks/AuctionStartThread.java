package agentseller.auctiontasks;

import agentseller.auction.AreaAuction;
import agentseller.auction.AuctionSubtype;
import agentseller.datacenter.DataCenter;
import agentseller.datacenter.DataGroup;
import agentseller.factory.AuctionFactory;
import agentseller.factory.AuctionSubtypeFactory;
import agents.AgentSeller;
import agentseller.streamtasks.ProcessHelperStreamTask;
import agentseller.streamtasks.ProcessStreamTask;
import agentseller.streamtasks.StreamListenerTask;
import enums.AuctionProperties;
import enums.AuctionSubtypes;
import enums.AuctionTypes;
import enums.OffsetStart;
import help.AreaDots;
import help.AuctionMessage;
import help.MessageBuilder;
import help.Operations;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.basictasks.KafkaSubscribeTask;
import taskcontrol.basictasks.MessageSendTask;
import taskcontrol.executors.SequentialTaskExecutor;
import taskcontrol.executors.TaskExecutor;


import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Thread that is active while auction negotiation process is active
 * Calculate utility and manage communication with other agents
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionStartThread extends Thread {

    private AuctionMessage auctionMessage;
    private List<Auction> auctions;
    private String kafkaServer;

    public AuctionStartThread(AuctionMessage auctionMessage, List<Auction> auctions, String kafkaServer){

        this.auctionMessage = auctionMessage;
        this.auctions = auctions;
        this.kafkaServer = kafkaServer;

    }


    @Override
    public void run() {

        // get default properties from message
        Properties properties = Operations.buildProperties(auctionMessage.getAllSubcontextsForContext("basic_parameters"), auctionMessage.getAllValuesForContext("basic_parameters").get(0));

        // get corresponding DataGroup for given topic
        DataGroup dataGroup = DataCenter.getInstance().getDataGroupByTopic(auctionMessage.getTopic());


        AgentSeller.logger.info("Creating Buyer agent");
        AgentSeller.logger.info("--------------------");
        AgentSeller.logger.info("Default parameters");
        AgentSeller.logger.info("Topic: " + properties.get(AuctionProperties.TOPIC));
        AgentSeller.logger.info("Auction type: " + properties.get(AuctionProperties.AUCTION_TYPE));
        AgentSeller.logger.info("Auction subtype: " + properties.get(AuctionProperties.AUCTION_SUBTYPE));
        AgentSeller.logger.info("Device number utility function: " + properties.get(AuctionProperties.DEVICE_NUM_FUNCTION));
        AgentSeller.logger.info("Quality utility function: " + properties.get(AuctionProperties.QUALITY_FUNCTION));
        AgentSeller.logger.info("Device number restriction: " + properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION));
        AgentSeller.logger.info("Quality restriction: " + properties.get(AuctionProperties.QUALITY_RESTRICTION));
        AgentSeller.logger.info("Helper flag: " + properties.get(AuctionProperties.HELPER_FLAG));
        AgentSeller.logger.info("--------------------");


        AuctionSubtype auctionSubtype = null;

        String sellerUUID = UUID.randomUUID().toString();

        // create subtype
        if (properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.AREA_AUCTION) {

            Integer minx = Integer.parseInt(auctionMessage.getValuesForSubcontext("specific_parameters", "min_x").get(0));
            Integer maxx = Integer.parseInt(auctionMessage.getValuesForSubcontext("specific_parameters", "max_x").get(0));
            Integer miny = Integer.parseInt(auctionMessage.getValuesForSubcontext("specific_parameters", "min_y").get(0));
            Integer maxy = Integer.parseInt(auctionMessage.getValuesForSubcontext("specific_parameters", "max_y").get(0));

            AreaDots areaDots = new AreaDots(minx, maxx, miny, maxy);

            Object parameterList[] = {properties, dataGroup, areaDots, DataCenter.getInstance().getAuctionStrategy()};

            auctionSubtype = AuctionSubtypeFactory.createAuction((AuctionSubtypes) properties.get(AuctionProperties.AUCTION_SUBTYPE), parameterList);



        } else if (properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.INFORMATION_AUCTION) {

            String description = auctionMessage.getValuesForSubcontext("specific_parameters", "description").get(0);

            Object parameterList[] = {properties, dataGroup, description, DataCenter.getInstance().getAuctionStrategy()};

            auctionSubtype = AuctionSubtypeFactory.createAuction((AuctionSubtypes) properties.get(AuctionProperties.AUCTION_SUBTYPE), parameterList);

        }


        // calculate utility
        auctionSubtype.calculateUtility();


        // create auction if participation is possible
        if (auctionSubtype.checkParticipation()) {

            long waitTime = Long.parseLong(auctionMessage.getValuesForSubcontext("auction_parameters", "time_waiting").get(0));

            Object parameterList[] = {auctionMessage.getSender(), sellerUUID, auctionSubtype, waitTime};

            Auction auction = AuctionFactory.createAuction((AuctionTypes) properties.get(AuctionProperties.AUCTION_TYPE), parameterList);

            if (auctions.add(auction)) {

                AgentSeller.logger.info("Seller agent " + " Auction: " + auction.getBuyerUUID() + " " + "added");
                System.out.println("Seller agent " + " Auction: " + auction.getBuyerUUID() + " " + "added");

                setUpAuctionNegotiationProtocol(auction);

            }


        // if not ask helper for help
        } else if (Boolean.parseBoolean((String) properties.get(AuctionProperties.HELPER_FLAG)) && (properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.AREA_AUCTION)) {

            AgentSeller.logger.info("Seller agent " + " Cant start agentseller.auction alone");

            String helperTopic = "H-" + auctionMessage.getSender();

            AreaAuction areaAuction = (AreaAuction) auctionSubtype;

            System.out.println("Ajmo " + areaAuction.getSensorsInArea());

            String mess = new MessageBuilder()
                    .addMark("S")
                    .addHeader("hello_helper")
                    .addSender(sellerUUID)
                    .addContexts("auction_uuid","num_of_sensors")
                    .addValuesForContexts(auctionMessage.getSender(), String.valueOf(areaAuction.getSensorsInArea().size()))
                    .build()
                    .toString();

            setUpHelperNegotiationProtocol(helperTopic, sellerUUID, areaAuction, mess);



        // else end auction making process
        } else {


            System.out.println("Task " + "TASK_NAME" + " agentseller.auction flag for agentseller.auction " + auctionMessage.getSender() + " is disabled - can't communicate with SHmutual.helper and can't participate in agentseller.auction");


        }

    }


    /**
     * Method witch sets up auction negotiation protocol
     * @param auction
     */
    private void setUpAuctionNegotiationProtocol(Auction auction){

        TaskExecutor taskExecutor = new SequentialTaskExecutor();


        ITask task0 = new StreamListenerTask(taskExecutor, auction.getTopic());
        ITask task1 = new KafkaSubscribeTask(taskExecutor, kafkaServer, auction.getBuyerUUID(), OffsetStart.LATEST, "B");
        ITask task2 = new AuctionNegotiationTask(taskExecutor, (ISubscribeTask) task1, auction);
        ITask task3 = new ProcessStreamTask(taskExecutor,(ISubscribeTask)task0, auction);

        taskExecutor.addTask(task0);
        taskExecutor.addTask(task1);
        taskExecutor.addTask(task2);
        taskExecutor.addTask(task3);

        taskExecutor.startTaskExecution();

    }


    /**
     * Method witch sets up helper negotiation protocol
     * @param helperTopic name of helper topic
     * @param sellerUUID seller UUID
     * @param areaAuction AreaAuction
     * @param mess init message
     */
    private void setUpHelperNegotiationProtocol(String helperTopic, String sellerUUID, AreaAuction areaAuction, String mess){


        TaskExecutor taskExecutor = new SequentialTaskExecutor();

        ITask task0 = new StreamListenerTask(taskExecutor, "Pollution");
        ITask task1 = new KafkaSubscribeTask(taskExecutor, kafkaServer,  helperTopic, OffsetStart.EARLIEST, "H");
        ITask task2 = new MessageSendTask(taskExecutor, helperTopic, mess);
        ITask task3 = new HelperNegotiationTask( taskExecutor, (ISubscribeTask) task1, sellerUUID, areaAuction.getSensorsInArea());
        ITask task4 = new ProcessHelperStreamTask(taskExecutor, (ISubscribeTask) task0, (HelperNegotiationTask) task3, auctionMessage.getSender(), 20000);

        taskExecutor.addTask(task0);
        taskExecutor.addTask(task1);
        taskExecutor.addTask(task2);
        taskExecutor.addTask(task3);
        taskExecutor.addTask(task4);

        taskExecutor.startTaskExecution();


    }


}
