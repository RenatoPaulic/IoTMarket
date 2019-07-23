package agentbuyer.auctiontasks;


import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

/**
 * Wrapper task class for Auction
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionNegotiationTask implements ITask {

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;
    private Auction auction;

    public AuctionNegotiationTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, Auction auction){

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.auction = auction;
        auction.setNegTask( this);

    }


    public void done(Boolean flag){

        subscribeTask.removeSubTask(auction);

        taskExecutor.notifyTaskResult(flag);

    }

    @Override
    public void execute() {
        subscribeTask.addSubTask(auction);
    }
}
