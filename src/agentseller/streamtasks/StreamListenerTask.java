package agentseller.streamtasks;


import agentseller.datacenter.DataCenter;
import agentseller.datacenter.SensorSchema;
import agents.AgentSeller;
import help.AuctionMessage;
import help.MessageBuilder;
import taskcontrol.basictasks.AuctionTask;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

/**
 * Subscribe task that generates dummy stream - random data
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class StreamListenerTask extends Thread implements ITask, ISubscribeTask {

    private TaskExecutor taskExecutor;
    private List<AuctionTask> auctionTaskList;
    private List<SensorSchema> sensorSchemaList;
    private String topic;

    private Random random;

    public StreamListenerTask(TaskExecutor taskExecutor, String topic){

        AgentSeller.logger.info("Creating task " + " Stream Listener Task ");

        random = new Random();

        this.topic = topic;
        this.taskExecutor = taskExecutor;
        auctionTaskList = new ArrayList<>();
        sensorSchemaList = DataCenter.getInstance().getDataGroupByTopic(topic).getSensorSchemaList();


    }

    @Override
    public void run(){

        while (true){

            // every 2 second generate random data for each sensor in group
            try{sleep(2000);} catch (Exception e){e.printStackTrace();}

            for(SensorSchema sensorSchema : sensorSchemaList){

                int randomNum = random.nextInt() % 10 + 1;

                String mess = new MessageBuilder()
                                .addMark("S")
                                .addHeader("data")
                                .addSender("None")
                                .addSubcontexts("sensor_data", "id", "value")
                                .addValuesForSubcontexts("sensor_data", sensorSchema.getSensorId(), String.valueOf(randomNum))
                                .build()
                                .toString();

                AuctionMessage auctionMessage = new AuctionMessage(mess, topic);

                for (AuctionTask task : auctionTaskList) {
                    task.processMessage(auctionMessage);
                }

            }



        }


    }

    @Override
    public void addSubTask(AuctionTask task) {

        System.out.println("Task " + " Stream Listener Task "  + " added subtask.");


        task.onStart();
        auctionTaskList.add(task);

    }

    @Override
    public void removeSubTask(AuctionTask auctionTask) {

        auctionTask.onEnd();
        auctionTaskList.remove(auctionTask);

    }

    @Override
    public void endTask() {

        stop();
    }

    @Override
    public void execute() {


        taskExecutor.notifyTaskResult(true);
        run();

    }
}
