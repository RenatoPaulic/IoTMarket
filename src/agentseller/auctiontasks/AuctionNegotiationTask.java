package agentseller.auctiontasks;

import agentseller.datacenter.DataCenter;
import agentseller.datacenter.SensorSchema;
import agents.AgentSeller;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.List;

/**
 * Wrapper class for Auction task
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionNegotiationTask implements ITask {

        private Auction auction;
        private TaskExecutor taskExecutor;
        private ISubscribeTask subscribeTask;

        public AuctionNegotiationTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, Auction auction){

            AgentSeller.logger.info("Creating task " + " Auction Negotiation Task");

            this.auction = auction;

            this.taskExecutor = taskExecutor;
            this.subscribeTask = subscribeTask;

            auction.setAuctionNegotiationTask(this);


         }


    public void done(Boolean flag){



            // update database properties table to wining/losing data center
            List<SensorSchema> winningSensor = auction.getAuctionSubtype().getTmpOfferData();
            DataCenter.getInstance().getDatabaseConnection().updateDataCenterProperties(flag, winningSensor);


            // update database auction parameters table
            DataCenter.getInstance().getDatabaseConnection().updateAuctionProperties(auction.getBuyerUUID(),auction.getSellerUUID(),auction.getAuctionSubtype().getProperties());

            // update specific parameters


            subscribeTask.removeSubTask(auction);
            taskExecutor.notifyTaskResult(flag);

    }



    public Auction getAuction(){

            return auction;

    }


    @Override
    public void execute() {

            subscribeTask.addSubTask(auction);

    }

}
