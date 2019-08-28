package agents;

import agentcontrol.AuctionAgent;
import agentseller.connection.DatabaseConnection;
import agentseller.connection.SqlLiteConnection;
import agentseller.datacenter.DataCenter;
import agentseller.factory.StrategyFactory;
import agentseller.auctiontasks.MainListenerTask;
import enums.AuctionStrategies;


import enums.OffsetStart;
import org.apache.log4j.PropertyConfigurator;
import program.Agent;
import strategies.AuctionStrategy;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.basictasks.KafkaSubscribeTask;
import taskcontrol.executors.SequentialTaskExecutor;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent agent seller
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AgentSeller implements AuctionAgent {

    private List<String> topicList;


    public AgentSeller(List<String> topicList){

        this.topicList = topicList;


    }


    @Override
    public void start(){

        Agent.logger.info("--------------------- STARTING SELLER AGENT ---------------------" );

        TaskExecutor taskExecutor = new SequentialTaskExecutor();

        ITask task1 = new KafkaSubscribeTask(taskExecutor, topicList, OffsetStart.LATEST, "B", "H" );
        ITask task2 = new MainListenerTask(taskExecutor,(ISubscribeTask) task1);

        taskExecutor.addTask(task1);
        taskExecutor.addTask(task2);


        taskExecutor.startTaskExecution();



    }

    /**
     * Method witch set up seller agent
     * @param args input parameters
     * @return
     */
    public static AgentSeller setUpSellerAgent(String[] args){


        int dataCenterId = Integer.parseInt(args[3]);
        DatabaseConnection databaseConnection = new SqlLiteConnection(args[4]);
        AuctionStrategy auctionStrategy = StrategyFactory.createStrategy(AuctionStrategies.valueOf(args[7].toUpperCase()));
        int payment = Integer.parseInt(args[8]);
        String deviceNumberFunction = args[5];
        String ratingFunction = args[6];

        System.setProperty("agent_name", "Seller" + args[3]);

        DataCenter.getInstance().init(dataCenterId, databaseConnection, auctionStrategy, payment, deviceNumberFunction, ratingFunction);


        List<String> topicList = new ArrayList<>();

        for(int i = 9 ; i < args.length ; i++) {

            DataCenter.getInstance().addDataGroupForTopic(args[i]);
            topicList.add(args[i]);

        }


        return new AgentSeller(topicList);




    }



}
