package agentbuyer.auctiontasks;

import agentbuyer.auction.AuctionSubtype;
import agentbuyer.auction.Bid;
import agents.AgentBuyer;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class witch implement Fist price sealed bid auction communication protocol
 * for buyer side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class FirstPriceAuction extends Auction {

    private String topic;

    private long roundTime;

    private AuctionSubtype auctionSubtype;

    /**
     * @param auctionSubtype represent auction subtype
     * @param waitTime time to wait until sellers calculate their offers
     * @param roundTime time to wait between publishing first auction round and pronouncing winner
     */
    public FirstPriceAuction(AuctionSubtype auctionSubtype, long waitTime, long roundTime){

        super(waitTime);

        this.roundTime = roundTime;

        this.auctionSubtype = auctionSubtype;

        AgentBuyer.logger.info("Creating task " + " First price auction ");

        this.topic = auctionSubtype.getAuctionUuid();


    }

    @Override
    public void onStart() {

        AgentBuyer.logger.info("Task " + " First price auction "  + " on start ");

        Timer timer = new Timer();


        // start first round after time for seller to calculate utility is done
        timer.schedule(new TimerTask() {

            @Override
            public void run() {


                // start first round
                startaAuctionRound();



            }
        }, super.getWaitTime());




    }

    private void startaAuctionRound(){



        Timer timer = new Timer();

        // wait round time to get offers
        timer.schedule(new TimerTask() {
            @Override
            public void run() {


                // if there are any bids
                if(auctionSubtype.getBids().size() != 0) {


                 //   Bid currentWinnerBid = Collections.max(auctionSubtype.getBids(), Comparator.comparing(Bid :: getUtility));


                    // get best bid and pronounce winner
                    Bid winningBid = auctionSubtype.getBestBid();

                    System.out.println("Buyer agent "+  " --- WINNER --- " + winningBid.getSellerUUID() + " " + winningBid.getUtility());
                    AgentBuyer.logger.info("AUCTION WINNER " + winningBid.getSellerUUID() + " with utility " + winningBid.getUtility());


                    String mess = new MessageBuilder()
                            .addMark("B")
                            .addHeader("auction_end")
                            .addSender(topic)
                            .addContexts("auction_winner")
                            .addValuesForContexts(winningBid.getSellerUUID())
                            .build()
                            .toString();

                    MessageProducer.getInstance().sendMessage(topic, mess);


                    negTask.done(true);


                 // if not auction doesn't have winner
                }else{

                    System.out.println("Buyer agent " + " --- WINNER --- " + " DON'T HAVE WINNER "  );
                    AgentBuyer.logger.info("AUCTION WINNER " + " DON'T HAVE WINNER " );


                    negTask.done(false);


                }


            }
        }, roundTime);


        // send first round - start auction
        String mess = new MessageBuilder()
                .addMark("B")
                .addHeader("auction_round")
                .addSender(topic)
                .addContexts("winning_seller", "winning_offer")
                .addValuesForContexts("None", "0.0")
                .build()
                .toString();

        MessageProducer.getInstance().sendMessage(topic, mess);
    }




    @Override
    public void onEnd() {

        AgentBuyer.logger.info("Task " + " First price auction "  + " on end ");


    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if round_response message - save bid
        if(auctionMessage.getHeader().equals("round_response")) {

            auctionSubtype.putBid(new Bid(auctionMessage.getSender(), Double.parseDouble(auctionMessage.getValueForContext("proposed_value"))));

            AgentBuyer.logger.info("Task " + " First price auction "  + " received offer from " + auctionMessage.getSender() + " value " + auctionMessage.getValue());

        }
    }


}
