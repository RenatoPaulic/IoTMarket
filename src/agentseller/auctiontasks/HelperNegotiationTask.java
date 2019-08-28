package agentseller.auctiontasks;


import agentseller.datacenter.SensorSchema;
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
 * Wrapper class for HelperAuction task for negotiation with helper
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class HelperNegotiationTask implements AuctionTask, ITask {


    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private List<SensorSchema> areaDots;
    private String uuid;
    private List<String> winningSensorsIDs;
    private long auctionTime;
    private Timer timer;

    public HelperNegotiationTask( TaskExecutor taskExecutor, ISubscribeTask subscribeTask, String uuid, List<SensorSchema> areaDots, long auctionTime){

        Agent.logger.info("Creating task " + " Helper Negotiation Task ");
        this.uuid = uuid;
        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.areaDots = areaDots;
        this.auctionTime = auctionTime;
        winningSensorsIDs = new ArrayList<>();

    }

    @Override
    public void onStart() {

        Agent.logger.info("Task " + " Helper Negotiation Task " + " on start ");

        timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                subscribeTask.removeSubTask(HelperNegotiationTask.this);
                taskExecutor.notifyTaskResult(false);

            }
        }, auctionTime);


    }

    @Override
    public void onEnd(){

        Agent.logger.info("Task " + " Helper Negotiation Task " + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if data_request message - send first "data_num" sensors
        if(auctionMessage.getHeader().equals("data_request")){

            timer.cancel();

            System.out.println(" uuid " + uuid + " mn " + auctionMessage.getAllValuesForContext("seller_uuid"));
            if(auctionMessage.getValueForContext("seller_uuid").equals(uuid)) {

                int numOfSensorsToSend = Integer.parseInt(auctionMessage.getValueForContext("num_of_sensors"));

                MessageBuilder messageBuilder = new MessageBuilder()
                        .addMark("S")
                        .addHeader("data_response")
                        .addSender(uuid)
                        .addSubcontexts("data", "sensor_id", "sensor_topic", "sensor_price");

                for (int i = 0; i < numOfSensorsToSend; i++) {

                    messageBuilder.addValuesForSubcontexts("data", areaDots.get(i).getSensorId(), String.valueOf(areaDots.get(i).getQuality()), String.valueOf(areaDots.get(i).getPrice()));

                }

                String mess = messageBuilder.build().toString();

                MessageProducer.getInstance().sendMessage(auctionMessage.getTopic(), mess);
            }

        }

        // if request_cancel message - can't participate in group bids, auction passed
        if(auctionMessage.getHeader().equals("request_cancel")){

            timer.cancel();

            if(auctionMessage.getValueForContext("cancel_type").equals("1")) {

                if(auctionMessage.getValueForContext("cancel_uuid").equals(uuid)) {

                    Agent.logger.info("Can't participate in cooperative auction");
                    System.out.println("Can't participate in cooperative auction");

                    subscribeTask.removeSubTask(this);
                    taskExecutor.notifyTaskResult(false);

                }

            }else if(auctionMessage.getValueForContext("cancel_type").equals("1")){

                Agent.logger.info("Can't participate in cooperative auction");
                System.out.println("Can't participate in cooperative auction");

                subscribeTask.removeSubTask(this);
                taskExecutor.notifyTaskResult(false);

            }
        }


        // if auction_result message - if win get wining sensors from message
        if(auctionMessage.getHeader().equals("auction_result")){

            String auctionResult = auctionMessage.getValueForContext("result");

            if(auctionResult.equals("win")){

                Agent.logger.info("Task " + " Helper Negotiation Task " + " AUCTION WIN");

                // get all values for context in list [ winning_uuid , winning_sensor_id ]
              for(List<String> context : auctionMessage.getAllValuesForContext( "winning_data")){

                 System.out.println("S " + context);

                  if(context.get(0).equals(uuid)){

                      winningSensorsIDs.add(context.get(1));
                   }

                 }

                subscribeTask.removeSubTask(this);
                taskExecutor.notifyTaskResult(true);


            }else if(auctionResult.equals("lose")){

                Agent.logger.info("Task " + " Helper Negotiation Task " + " AUCTION LOSE");

                subscribeTask.removeSubTask(this);
                taskExecutor.notifyTaskResult(false);
            }




        }


    }


    @Override
    public void execute() {

        subscribeTask.addSubTask(this);

    }



    public List<String> getWinningSensorsIDs(){
        return winningSensorsIDs;
    }
}
