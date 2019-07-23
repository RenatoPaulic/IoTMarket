package agentbuyer.streamtasks;


import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

/**
 * Wrapper class for Auction Stream task
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class StreamNegotiationTask implements ITask {

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private AuctionStream auctionStream;

    public StreamNegotiationTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, AuctionStream auctionStream){

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.auctionStream = auctionStream;
        auctionStream.setNegTask(this);

    }


    public void done(Boolean flag){

        subscribeTask.removeSubTask(auctionStream);

        taskExecutor.notifyTaskResult(flag);

    }

    @Override
    public void execute() {
        subscribeTask.addSubTask(auctionStream);
    }
}
