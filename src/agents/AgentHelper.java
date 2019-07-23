package agents;

import agentcontrol.AuctionAgent;
import agenthelper.factory.StrategyFactory;
import agenthelper.helper.HelperProperties;
import agenthelper.tasks.MainLoopTask;
import enums.AuctionStrategies;
import enums.OffsetStart;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import program.Agent;
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
 * Class that represent agent helper
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AgentHelper implements AuctionAgent {

    public static final Logger logger = Logger.getLogger(Class.class.getName());

    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss");
        System.setProperty("current_date", dateFormat.format(new Date()));

    }


    private List<String> topicList;



    public AgentHelper( List<String> topicList ){

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);

        this.topicList = topicList;


        Agent.logger.info("Helper agent " + "starting");

    }


    @Override
    public void start(){

        TaskExecutor taskExecutor = new SequentialTaskExecutor();

        ITask task1 = new KafkaSubscribeTask(taskExecutor, topicList,OffsetStart.LATEST, "B");
        ITask task2 = new MainLoopTask(taskExecutor,(ISubscribeTask) task1);

        taskExecutor.addTask(task1);
        taskExecutor.addTask(task2);

        taskExecutor.startTaskExecution();

    }


    /**
     * Method witch set up helper agent
     * @param args input parameters
     * @return AgentHelper class
     */
    public static AgentHelper setUpHelperAgent(String[] args){

        List<String> topicList = new ArrayList<>();


        for(int i = 6 ; i < args.length; i++) {
            topicList.add(args[i]);

        }

        AuctionStrategy auctionStrategy = StrategyFactory.createStrategy(AuctionStrategies.valueOf(args[4]));

        HelperProperties.getInstance().init(auctionStrategy, args[3], Integer.parseInt(args[5]));

        return new AgentHelper( topicList);


    }



}
