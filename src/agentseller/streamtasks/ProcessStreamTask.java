package agentseller.streamtasks;

import agentseller.auctiontasks.SellerAuction;
import agentseller.datacenter.SensorSchema;
import agents.AgentSeller;
import enums.AuctionProperties;
import help.AuctionMessage;
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
 * HelperAuction task that forwards stream data to input stream topic
 * as they arrive to system, for normal auction process
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class ProcessStreamTask implements ITask, AuctionTask {

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private String topic;

    private List<String> winingSensorsIDS;

    private long time;

    private SellerAuction auction;

    public ProcessStreamTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, SellerAuction auction) {

        Agent.logger.info("Creating task " + " Process Stream Task ");

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.topic = auction.getBuyerUUID();
        this.auction = auction;

        winingSensorsIDS = new ArrayList<>();

        this.time = Long.parseLong(auction.getAuctionSubtype().getProperties().get(AuctionProperties.STREAM_TIME).toString());
    }

    @Override
    public void onStart() {

        Agent.logger.info("Task " + " Process Stream Task " + " on start ");


         List<SensorSchema> sensorSchemas = auction.getAuctionSubtype().getTmpOfferData();

         for(SensorSchema schema : sensorSchemas){

             winingSensorsIDS.add(schema.getSensorId());

        }

        Timer timer = new Timer();

            timer.schedule(new TimerTask() {

                @Override
                public void run() {

                    subscribeTask.removeSubTask(ProcessStreamTask.this);
                    taskExecutor.notifyTaskResult(true);


                }
            }, time);



    }

    @Override
    public void onEnd() {

        Agent.logger.info("Task " + " Process Stream Task " + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        if(auctionMessage.getHeader().equals("data")) {

            Agent.logger.info("Processing message " + auctionMessage.getValue() + " to stream topic " + topic);

            String sensorId = auctionMessage.getValuesForSubcontext("sensor_data", "id").get(0);

            if (winingSensorsIDS.contains(sensorId)) {

                MessageProducer.getInstance().sendMessage("input-" + topic, auctionMessage.toString());

            }

        }
    }

    @Override
    public void execute(){

        subscribeTask.addSubTask(this);

    }


}
