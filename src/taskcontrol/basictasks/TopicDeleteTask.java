package taskcontrol.basictasks;

import kafka.TopicController;
import program.Agent;
import taskcontrol.executors.TaskExecutor;

/**
 * Class that represent task for deleting topic
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class TopicDeleteTask implements ITask {


    private String topic;
    private TaskExecutor taskExecutor;

    /**
     * @param taskExecutor  reference to TaskExecutor class, for task result notification
     * @param topic  topic to be deleted
     */
    public TopicDeleteTask(TaskExecutor taskExecutor, String topic){

        Agent.logger.info("Creating task: " + "Topic Delete Task");

        this.topic = topic;
        this.taskExecutor = taskExecutor;

    }



    @Override
    public void execute() {

        System.out.println("Task: Topic Delete Task - " +  " Deleting topic: " + topic);

        TopicController.getInstance().deleteTopic(topic);
        taskExecutor.notifyTaskResult(true);
    }


}
