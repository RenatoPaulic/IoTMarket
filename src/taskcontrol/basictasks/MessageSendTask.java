package taskcontrol.basictasks;

import kafka.MessageProducer;
import taskcontrol.executors.TaskExecutor;


/**
 * Class that represent task for sending message to topic
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class MessageSendTask implements ITask {

    private String topic;
    private String mess;
    private TaskExecutor taskExecutor;

    /**
     * @param taskExecutor reference to TaskExecutor class, for task result notification
     * @param topic topic name
     * @param mess message to be send on topic
     */
    public MessageSendTask(TaskExecutor taskExecutor, String topic, String mess){

        this.topic = topic;
        this.taskExecutor = taskExecutor;
        this.mess = mess;
    }

    @Override
    public void execute() {

        MessageProducer.getInstance().sendMessage(topic,mess);
        taskExecutor.notifyTaskResult(true);
    }



}
