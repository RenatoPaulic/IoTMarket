package taskcontrol.basictasks;

/**
 * Interface that represent task
 * witch define communication with message system
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public interface ISubscribeTask {

    /**
     * Method for adding auction task to task group
     * processMessage method in AuctionTask will be called when message arrives
     * @param task auction task to be added
     */
    void addSubTask(AuctionTask task);

    /**
     * Method for removing auction task from task group
     * @param task auction task to be removed
     */
    void removeSubTask(AuctionTask task);

    /**
     * Method witch ends subscribe task
     */
    void endTask();

}