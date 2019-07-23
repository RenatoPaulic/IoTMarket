package agentbuyer.streamtasks;

import taskcontrol.basictasks.AuctionTask;

/**
 * Abstract class tha each auction stream type must extend
 * Represent basic auction stream and holds reference on
 * StreamNegotiationTask class
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public abstract class AuctionStream implements AuctionTask {

    StreamNegotiationTask negTask;
    private long activeTime;

    public AuctionStream(long activeTime){

        this.activeTime = activeTime;

    }

    public void setNegTask(StreamNegotiationTask negTask){
        this.negTask = negTask;
    }
    public StreamNegotiationTask getNegTask(){ return negTask;}

    public long getActiveTime(){return activeTime;}

}
