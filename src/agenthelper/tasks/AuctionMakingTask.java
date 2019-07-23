package agenthelper.tasks;

import agenthelper.helper.AuctionContainer;
import agenthelper.helper.HelperSensorSchema;
import agents.AgentHelper;
import agentseller.datacenter.SensorSchema;
import enums.AuctionProperties;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;
import taskcontrol.basictasks.AuctionTask;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AuctionTask that represent auction making process for helper agent
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionMakingTask implements AuctionTask, ITask {

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private long time;
    private AuctionContainer auctionContainer;

    public AuctionMakingTask(AuctionContainer auctionContainer, TaskExecutor taskExecutor, ISubscribeTask subscribeTask, long time){

        AgentHelper.logger.info("Creating task " + " Auction Making Task ");

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.time = time;
        this.auctionContainer = auctionContainer;


    }

    @Override
    public void onStart() {

        AgentHelper.logger.info("Task " + " Auction Making Task " + " on start ");


    }

    @Override
    public void onEnd() {

        AgentHelper.logger.info("Task " + " Auction Making Task " + " on end ");



    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {


        // if hello_helper message and there is space for more sensors in cooperation
        if(auctionMessage.getHeader().equals("hello_helper") && !auctionContainer.isFull()){

            Agent.logger.info("Task " + " Auction Making Task " + " received help message from seller " + auctionMessage.getSender());

            int numOfSensorsToAdd = Integer.parseInt(auctionMessage.getValueForContext("num_of_sensors"));

            Integer numOfSensorsPossibleToAdd;

            // calculate how much more space was left
            if(auctionContainer.getReservedSpace() + numOfSensorsToAdd > auctionContainer.getMaxSetSize()){

                numOfSensorsPossibleToAdd = auctionContainer.getMaxSetSize() - auctionContainer.getReservedSpace();

            }else{

                numOfSensorsPossibleToAdd = numOfSensorsToAdd;

            }

            auctionContainer.reserveSetSpace(numOfSensorsPossibleToAdd);


            // send respond message
            String mess = new MessageBuilder()
                            .addMark("H")
                            .addHeader("data_request")
                            .addSender(auctionContainer.getHelperUuid())
                            .addContexts("seller_uuid", "num_of_sensors")
                            .addValuesForContexts(auctionMessage.getSender(),String.valueOf(numOfSensorsPossibleToAdd))
                            .build()
                            .toString();

            MessageProducer.getInstance().sendMessage(auctionMessage.getTopic(), mess);


         // if no space left send request_cancel message to specific seller
        }else if(auctionMessage.getHeader().equals("hello_helper") && auctionContainer.isFull()){

            String mess = new MessageBuilder()
                    .addMark("H")
                    .addHeader("request_cancel")
                    .addSender(auctionContainer.getHelperUuid())
                    .addContexts("cancel_type", "cancel_uuid")
                    .addValuesForContexts("1", auctionMessage.getSender())
                    .build()
                    .toString();

            MessageProducer.getInstance().sendMessage(auctionMessage.getTopic(), mess);

        }


        // if data_request message - add new data to cooperation
        if(auctionMessage.getHeader().equals("data_response")){

             List<HelperSensorSchema> receivedSensors = new ArrayList<>();

             List<List<String>> allData = auctionMessage.getAllValuesForContext("data");

             for(List<String> data : allData){

                 HelperSensorSchema sensorSchema = new HelperSensorSchema(data.get(0), Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), auctionMessage.getSender());

                 receivedSensors.add(sensorSchema);
             }


            auctionContainer.addData(receivedSensors);

             Agent.logger.info("Task " + " Auction Making Task " + " received data from seller " + auctionMessage.getSender() + " " + auctionContainer.getAllData());

             // jos za testiranje
             for(SensorSchema sensorSchema : receivedSensors){

                System.out.println("AJDMO SAD " + sensorSchema.toString());
            }


        }

    }


    @Override
    public void execute() {

        Timer timer = new Timer();

        // make cooperation only for given time
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                subscribeTask.removeSubTask(AuctionMakingTask.this);

                if(auctionContainer.getAllData().size() < Integer.valueOf((String)auctionContainer.getProperties().get(AuctionProperties.DEVICE_NUM_RESTRICTION))){
                    taskExecutor.notifyTaskResult(true);
                }else {
                    taskExecutor.notifyTaskResult(true);
                }

            }
        }, time);


        subscribeTask.addSubTask(this);
    }





}
