package agentcontrol;

/**
 * Interface that define method for
 * initializing whole data stream purchase process
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public interface AuctionProtocol {

    /**
     * Method witch define protocol, defining behaviours and tasks that
     * will be executing
     * @param topic head topic name
     */
    void initAuctionBehaviors(String topic);
}
