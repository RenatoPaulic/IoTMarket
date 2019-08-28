package agenthelper.tasks;



import agenthelper.auctionsubtype.AuctionSubtype;
import agenthelper.factory.AuctionFactory;
import agenthelper.factory.AuctionSubtypeFactory;
import agenthelper.helper.AuctionContainer;
import agenthelper.helper.HelperProperties;
import agenthelper.helper.HelperSensorSchema;
import agents.AgentHelper;
import enums.AuctionProperties;
import enums.AuctionSubtypes;
import enums.AuctionTypes;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;

import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * Wrapper task class for HelperAuction
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class RoundTask implements ITask {


    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private AuctionContainer auctionContainer;
    private HelperAuction auction;

    // bilo bi mozda bolje da ga prima kroz konstruktor aukciju koju sam vec napravio
    public RoundTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, AuctionContainer auctionContainer ) {

        Agent.logger.info("Creating task " + "Round Task");


        this.auctionContainer = auctionContainer;
        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;





    }


    public void makeAuction() {

        Agent.logger.info("Task " + "Round Task - " + " on start ");

        Properties properties = auctionContainer.getProperties();
        Set<HelperSensorSchema> dots = auctionContainer.getAllData();

        Object[] objectList = new Object[3];

        List<HelperSensorSchema> dots2 = new ArrayList<>();
        dots2.addAll(dots);

        objectList[0] = properties;
        objectList[1] = dots2;
        objectList[2] = HelperProperties.getInstance().getAuctionStrategy();

        AuctionSubtype auctionSubtype = AuctionSubtypeFactory.createAuction((AuctionSubtypes) properties.get(AuctionProperties.AUCTION_SUBTYPE), objectList);

        auctionSubtype.calculateUtility();

        if (auctionSubtype.checkParticipation()) {


            Object parameterList[] = {auctionContainer.getBuyerUuid(), auctionContainer.getHelperUuid(), auctionSubtype, (long)10000 };

            auction = AuctionFactory.createAuction((AuctionTypes) properties.get(AuctionProperties.AUCTION_TYPE), parameterList);
            auction.setRoundTask(this);

            subscribeTask.addSubTask(auction);


        }else{


            String mess = new MessageBuilder()
                    .addMark("H")
                    .addHeader("request_cancel")
                    .addSender(auctionContainer.getHelperUuid())
                    .addContexts("cancel_type")
                    .addValuesForContexts("2")
                    .build()
                    .toString();

            MessageProducer.getInstance().sendMessage("H-" + auctionContainer.getBuyerUuid(), mess);

            taskExecutor.notifyTaskResult(false);


        }

    }




    public HelperAuction getAuction() {


        return auction;
    }


    @Override
    public void execute() {

        makeAuction();

    }

    public void done(Boolean flag){

        subscribeTask.removeSubTask(auction);
        taskExecutor.notifyTaskResult(flag);

    }


}


