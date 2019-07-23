package agentseller.auctiontasks;


import agents.AgentSeller;
import help.AuctionMessage;
import taskcontrol.basictasks.AuctionTask;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that represent main listener task that listen for auction_start messages
 * and creates auction making thread
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class MainListenerTask implements AuctionTask, ITask {


    protected List<Auction> auctions;

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;

    private String kafkaServer;

    public MainListenerTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask, String kafkaServer){

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;
        this.kafkaServer = kafkaServer;

        auctions = new ArrayList<>();

        AgentSeller.logger.info("Creating task " + " Main Listener Task " );

    }




    @Override
    public void onStart() {

        AgentSeller.logger.info("Task " + " Main Listener Task "  + " on start ");
    }

    @Override
    public void onEnd() {

        AgentSeller.logger.info("Task " + " Main Listener Task "  + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if auction_start message - calculate utilities and check participation (in  new thread)
        if (auctionMessage.getHeader().equals("auction_start")) {

            new AuctionStartThread(auctionMessage, auctions, kafkaServer).start();

        }


        // if auction_end message - remove auction from auctions list
        if(auctionMessage.getHeader().equals("auction_end")) {

                for(Auction auction : auctions){

                    if (auction.getBuyerUUID().equals(auctionMessage.getSender())){

                        // update auction parameters

                        removeAuction(auction);

                    }
                }

            }



        }


    public void removeAuction(Auction auction){

        auctions.remove(auction);

    }


    @Override
    public void execute() {

        subscribeTask.addSubTask(this);

    }


}
