package agentseller.streamtasks;


import agentseller.auctiontasks.HelperNegotiationTask;
import agents.AgentSeller;
import help.AuctionMessage;
import kafka.MessageProducer;
import taskcontrol.basictasks.AuctionTask;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Auction task that forwards stream data to input stream topic
 * as they arrive to system, for cooperating auction process
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class ProcessHelperStreamTask implements ITask, AuctionTask {


    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private String topic;


    private List<String> winingSensorsIDS;

    private HelperNegotiationTask helperTask;

    private long time;

    public ProcessHelperStreamTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, HelperNegotiationTask helperTask, String topic, long time) {

        AgentSeller.logger.info("Creating task " + " Process Helper Stream Task");

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.topic = topic;
        this.helperTask = helperTask;

        this.time = time;
    }

    @Override
    public void onStart() {

        AgentSeller.logger.info("Task " + " Process Helper Stream Task " + " on start ");

        winingSensorsIDS = helperTask.getWinningSensorsIDs();



            Timer timer = new Timer();

            timer.schedule(new TimerTask() {

                @Override
                public void run() {

                    System.out.println("Timer done " + "done");

                    subscribeTask.removeSubTask(ProcessHelperStreamTask.this);
                    taskExecutor.notifyTaskResult(true);


                }
            }, time);

    }

    @Override
    public void onEnd() {

        AgentSeller.logger.info("Task " + " Process Helper Stream Task " + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if data message - if data is from sold stream, forward it to topic
        if (auctionMessage.getHeader().equals("data")) {

            String sensorId = auctionMessage.getValuesForSubcontext("sensor_data", "id").get(0);

            System.out.println("WINING SENSORS ID " + winingSensorsIDS);
            System.out.println("Sensor " + sensorId);

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
