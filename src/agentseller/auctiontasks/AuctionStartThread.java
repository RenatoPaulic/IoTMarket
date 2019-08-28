package agentseller.auctiontasks;

import agentseller.auction.AreaAuction;
import agentseller.auction.AuctionSubtype;
import agentseller.datacenter.DataCenter;
import agentseller.datacenter.DataGroup;
import agentseller.factory.AuctionFactory;
import agentseller.factory.AuctionSubtypeFactory;
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
import program.Agent;
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
    private List<SellerAuction> auctions;


    public AuctionStartThread(AuctionMessage auctionMessage, List<SellerAuction> auctions){

        this.auctionMessage = auctionMessage;
        this.auctions = auctions;


    }


    @Override
    public void run() {

        // get default properties from message
        Properties properties = Operations.buildProperties(auctionMessage.getAllSubcontextsForContext("basic_parameters"), auctionMessage.getAllValuesForContext("basic_parameters").get(0));

        // get corresponding DataGroup for given topic
        DataGroup dataGroup = DataCenter.getInstance().getDataGroupByTopic(auctionMessage.getTopic());

        System.out.println("Data group: " + dataGroup.getTopic());

        System.out.println("Topic: " + auctionMessage.getTopic());


        Agent.logger.info("Auction parameters");
        Agent.logger.info("--------------------");
        Agent.logger.info("Default parameters");
        Agent.logger.info("Topic: " + properties.get(AuctionProperties.TOPIC));
        Agent.logger.info("HelperAuction type: " + properties.get(AuctionProperties.AUCTION_TYPE));
        Agent.logger.info("HelperAuction subtype: " + properties.get(AuctionProperties.AUCTION_SUBTYPE));
        Agent.logger.info("Device number utility function: " + properties.get(AuctionProperties.DEVICE_NUM_FUNCTION));
        Agent.logger.info("Quality utility function: " + properties.get(AuctionProperties.QUALITY_FUNCTION));
        Agent.logger.info("Device number restriction: " + properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION));
        Agent.logger.info("Quality restriction: " + properties.get(AuctionProperties.QUALITY_RESTRICTION));
        Agent.logger.info("Helper flag: " + properties.get(AuctionProperties.HELPER_FLAG));
        Agent.logger.info("--------------------");


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

            SellerAuction auction = AuctionFactory.createAuction((AuctionTypes) properties.get(AuctionProperties.AUCTION_TYPE), parameterList);

            if (auctions.add(auction)) {

                Agent.logger.info("Seller agent " + " Auction: " + auction.getBuyerUUID() + " " + "added");
                System.out.println("Seller agent " + " Auction: " + auction.getBuyerUUID() + " " + "added");

                setUpAuctionNegotiationProtocol(auction);

            }



        // if not ask helper for help
        } else if (Boolean.parseBoolean((String) properties.get(AuctionProperties.HELPER_FLAG)) && (properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.AREA_AUCTION)) {

            Agent.logger.info("Seller agent " + " Cant start auction alone");

            String helperTopic = "H-" + auctionMessage.getSender();

            AreaAuction areaAuction = (AreaAuction) auctionSubtype;

            System.out.println("Sensors in area " + areaAuction.getSensorsInArea());

            String mess = new MessageBuilder()
                    .addMark("S")
                    .addHeader("hello_helper")
                    .addSender(sellerUUID)
                    .addContexts("auction_uuid","num_of_sensors")
                    .addValuesForContexts(auctionMessage.getSender(), String.valueOf(areaAuction.getSensorsInArea().size()))
                    .build()
                    .toString();

            setUpHelperNegotiationProtocol(helperTopic, sellerUUID, areaAuction, mess, auctionMessage.getValuesForSubcontext("auction_parameters", "wait_time").get(0));



        // else end auction making process
        } else {


            System.out.println("Task " + "TASK_NAME" + " auction flag for auction " + auctionMessage.getSender() + " is disabled - can't communicate with helper and can't participate in auction");



        }

    }


    /**
     * Method witch sets up auction negotiation protocol
     * @param auction
     */
    private void setUpAuctionNegotiationProtocol(SellerAuction auction){

        TaskExecutor taskExecutor = new SequentialTaskExecutor();


        ITask task0 = new StreamListenerTask(taskExecutor, auction.getTopic());
        ITask task1 = new KafkaSubscribeTask(taskExecutor, auction.getBuyerUUID(), OffsetStart.LATEST, "B");
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
    private void setUpHelperNegotiationProtocol(String helperTopic, String sellerUUID, AreaAuction areaAuction, String mess, String waitTime){


        TaskExecutor taskExecutor = new SequentialTaskExecutor();

        ITask task0 = new StreamListenerTask(taskExecutor, auctionMessage.getTopic());
        ITask task1 = new KafkaSubscribeTask(taskExecutor,  helperTopic, OffsetStart.EARLIEST, "H");
        ITask task2 = new MessageSendTask(taskExecutor, helperTopic, mess);
        ITask task3 = new HelperNegotiationTask( taskExecutor, (ISubscribeTask) task1, sellerUUID, areaAuction.getSensorsInArea(), Long.parseLong(waitTime));
        ITask task4 = new ProcessHelperStreamTask(taskExecutor, (ISubscribeTask) task0, (HelperNegotiationTask) task3, auctionMessage.getSender(), 20000);

        taskExecutor.addTask(task0);
        taskExecutor.addTask(task1);
        taskExecutor.addTask(task2);
        taskExecutor.addTask(task3);
        taskExecutor.addTask(task4);

        taskExecutor.startTaskExecution();


    }


}
