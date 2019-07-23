package agenthelper.tasks;



import agenthelper.helper.AuctionContainer;
import agenthelper.helper.HelperProperties;
import agents.AgentHelper;
import enums.OffsetStart;
import help.AuctionMessage;
import help.Operations;
import program.Agent;
import taskcontrol.basictasks.*;
import taskcontrol.executors.SequentialTaskExecutor;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;

import java.util.List;
import java.util.Properties;


/**
 * Class that represent main listener task that listen for auction_start messages
 * and creates AuctionContainer class
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class MainLoopTask implements AuctionTask, ITask {


    protected List<AuctionContainer> auctionContainers;

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;



    public MainLoopTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask){

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;


        auctionContainers = new ArrayList<>();

        AgentHelper.logger.info("Creating task " + " Main Listener Task ");


    }


    @Override
    public void onStart() {

        AgentHelper.logger.info("Task " + " Main Listener Task " + " on start ");
    }

    @Override
    public void onEnd() {

        AgentHelper.logger.info("Task " + " Main Listener Task " + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        if (auctionMessage.getHeader().equals("auction_start")) {

            Agent.logger.info("Task " + " Main Listener Task " + " received auction_start message for auction " + auctionMessage.getSender() );

            System.out.println("auction start messa d");

            if(Boolean.parseBoolean(auctionMessage.getValuesForSubcontext("basic_parameters", "HELPER_FLAG").get(0))) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // get default properties from message
                        Properties properties = Operations.buildProperties(auctionMessage.getAllSubcontextsForContext("basic_parameters"), auctionMessage.getAllValuesForContext("basic_parameters").get(0));

                        // make AuctionContainer class to gather and save sellers
                        AuctionContainer auctionContainer = new AuctionContainer(auctionMessage.getAllSubcontextsForContext("basic_parameters"), properties, auctionMessage.getSender(), HelperProperties.getInstance().getMaxSensorNum());

                        // subscribe to helper topic and auction topic
                        String helperTopic = "H-" + auctionMessage.getSender();
                        String auctionTopic = auctionMessage.getSender();

                        TaskExecutor taskExecutor = new SequentialTaskExecutor();

                        ITask task1 = new TopicCreateTask(taskExecutor, helperTopic);
                        ITask task2 = new KafkaSubscribeTask(taskExecutor,  helperTopic, OffsetStart.EARLIEST, "S");
                        ITask task3 = new AuctionMakingTask(auctionContainer, taskExecutor, (ISubscribeTask) task2, 5000);
                        ITask task4 = new KafkaSubscribeTask(taskExecutor,  auctionTopic, OffsetStart.EARLIEST, "B");
                        ITask task5 = new RoundTask(taskExecutor, (ISubscribeTask) task4, auctionContainer);
                   //     ITask task6 = new TopicDeleteTask(taskExecutor, helperTopic);

                        taskExecutor.addTask(task1);
                        taskExecutor.addTask(task2);
                        taskExecutor.addTask(task3);
                        taskExecutor.addTask(task4);
                        taskExecutor.addTask(task5);
                     //   taskExecutor.addTask(task6);

                        taskExecutor.startTaskExecution();

                    }
                }).start();

            }else{

                Agent.logger.info("Task " + " Main Listener Task " + " auction flag for auction " + auctionMessage.getSender() + " is disabled - can't start helper auction");

            }

        }

    }


    @Override
    public void execute() {

        subscribeTask.addSubTask(this);

    }

}
