package agentbuyer.auctiontasks;


import taskcontrol.basictasks.AuctionTask;

/**
 * Abstract class that each auction must extend
 * Represent basic auction and holds reference on
 * AuctionNegotiationTask class
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public abstract class Auction implements AuctionTask {

    AuctionNegotiationTask negTask;
    private long waitTime;

    public Auction(long waitTime){

        this.waitTime = waitTime;

    }

    public void setNegTask(AuctionNegotiationTask negTask){
        this.negTask = negTask;
    }
    public AuctionNegotiationTask getNegTask(){ return negTask;}

    public long getWaitTime(){return waitTime;}

}
