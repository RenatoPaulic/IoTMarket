package agents;

import agentcontrol.AuctionAgent;
import agentseller.connection.DatabaseConnection;
import agentseller.connection.SqlLiteConnection;
import agentseller.datacenter.DataCenter;
import agentseller.factory.StrategyFactory;
import agentseller.auctiontasks.MainListenerTask;
import enums.AuctionStrategies;


import enums.OffsetStart;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import strategies.AuctionStrategy;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.basictasks.KafkaSubscribeTask;
import taskcontrol.executors.SequentialTaskExecutor;
import taskcontrol.executors.TaskExecutor;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that represent agent seller
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AgentSeller implements AuctionAgent {

    public static final Logger logger = Logger.getLogger(Class.class.getName());

    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss");
        System.setProperty("current_date", dateFormat.format(new Date()));

    }


    private List<String> topicList;


    public AgentSeller(List<String> topicList){

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);

        this.topicList = topicList;

        logger.info( "Starting AGENT SELLER" );
    }


    @Override
    public void start(){

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

        System.setProperty("data_center_id", args[3]);


        DataCenter.getInstance().init(dataCenterId, databaseConnection, auctionStrategy, payment, deviceNumberFunction, ratingFunction);


        List<String> topicList = new ArrayList<>();

        for(int i = 9 ; i < args.length ; i++) {

            DataCenter.getInstance().addDataGroupForTopic(args[i]);
            topicList.add(args[i]);

        }


        return new AgentSeller(topicList);




    }



}
