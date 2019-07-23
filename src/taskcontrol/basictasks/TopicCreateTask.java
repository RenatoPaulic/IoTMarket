package taskcontrol.basictasks;

import kafka.TopicController;
import program.Agent;
import taskcontrol.executors.TaskExecutor;

/**
 * Class that represent task for creating new topic
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class TopicCreateTask implements ITask {


    private String topic;
    private TaskExecutor taskExecutor;

    /**
     * @param taskExecutor  reference to TaskExecutor class, for task result notification
     * @param topic topic to be created
     */
    public TopicCreateTask(TaskExecutor taskExecutor, String topic){

        Agent.logger.info("Creating task: " + "Topic Create Task");

        this.topic = topic;
        this.taskExecutor = taskExecutor;

    }

    @Override
    public void execute() {

        System.out.println("Task: Topic Create Task - "  + " Creating topic: " + topic);

        TopicController.getInstance().createTopic(topic,1, (short)1);
        taskExecutor.notifyTaskResult(true);
    }



}
