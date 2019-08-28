package agentseller.auctiontasks;

import help.AuctionMessage;
import program.Agent;
import taskcontrol.basictasks.AuctionTask;
import taskcontrol.basictasks.ISubscribeTask;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Class that represent main listener task that listen for auction_start messages
 * and creates auction making thread
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class MainListenerTask implements AuctionTask, ITask {


    protected List<SellerAuction> auctions;

    private TaskExecutor taskExecutor;
    private ISubscribeTask subscribeTask;


    public MainListenerTask(TaskExecutor taskExecutor, ISubscribeTask subscribeTask){

        this.taskExecutor = taskExecutor;
        this.subscribeTask = subscribeTask;


        auctions = new ArrayList<>();

        Agent.logger.info("Creating task " + " Main Listener Task " );

    }




    @Override
    public void onStart() {

        Agent.logger.info("Task " + " Main Listener Task "  + " on start ");
    }

    @Override
    public void onEnd() {

        Agent.logger.info("Task " + " Main Listener Task "  + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if auction_start message - calculate utilities and check participation (in  new thread)
        if (auctionMessage.getHeader().equals("auction_start")) {

            System.setProperty("auction_uuid", auctionMessage.getSender());
            new AuctionStartThread(auctionMessage, auctions).start();

        }


        // if auction_end message - remove auction from auctions list
        if(auctionMessage.getHeader().equals("auction_end")) {

                for(SellerAuction auction : auctions){

                    if (auction.getBuyerUUID().equals(auctionMessage.getSender())){

                        // update auction parameters

                        removeAuction(auction);

                    }
                }

            }



        }


    public void removeAuction(SellerAuction auction){

        auctions.remove(auction);

    }


    @Override
    public void execute() {

        subscribeTask.addSubTask(this);

    }


}
