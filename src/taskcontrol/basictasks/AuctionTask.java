package taskcontrol.basictasks;

import help.AuctionMessage;

/**
 * Interface that represent task
 * witch define auction behaviour
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public interface AuctionTask {

    /**
     * Method called before auction task execution
     */
    void onStart();

    /**
     * Method called after auction task execution
     */
    void onEnd();

    /**
     * Method called when message arrives on topic
     * @param auctionMessage received auction message
     */
    void processMessage(AuctionMessage auctionMessage);

}
