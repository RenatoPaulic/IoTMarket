package agentbuyer.auctiontasks;


import agentbuyer.auction.AuctionSubtype;
import agents.AgentBuyer;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class witch implement Dutch auction communication protocol
 * for buyer side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class DutchAuction extends BuyerAuction {


    private Timer timer;

    private double offer;
    private double step;

    private double minOffer;

    private long roundTime;

    private int round;

    private String topic;


    /**
     * @param auctionSubtype represent auction subtype
     * @param waitTime       time to wait until sellers calculate their offers
     * @param roundTime      time to wait offer in round - time between two rounds
     * @param startOffer     offer in first round of auction
     * @param step           step in witch offer is decreased each auction round
     */
    public DutchAuction(AuctionSubtype auctionSubtype, long waitTime, long roundTime, double startOffer, double step) {

        super(waitTime);

        this.roundTime = roundTime;

        Agent.logger.info("Creating task: " + "Dutch auction");


        this.minOffer = 0;
        this.offer = startOffer;
        this.step = step;


        round = 0;

        this.topic = auctionSubtype.getAuctionUuid();


    }


    public void makeRoundResponse(AuctionMessage auctionMessage) {


        // if seller accepted offer - it is winning seller, pronounce winner
        if (auctionMessage.getValue().equals("Accept")) {

            System.out.println("HelperAuction ended in round " + round + " WINNER " + auctionMessage.getSender() + " with utility accepted " + (offer + step));

            System.out.println("Buyer agent " + " --- WINNER --- " + auctionMessage.getSender() + " " + (offer + step));
            Agent.logger.info("AUCTION WINNER " + auctionMessage.getSender() + " with utility " + (offer + step));

            String mess = new MessageBuilder()
                    .addMark("B")
                    .addHeader("auction_end")
                    .addSender(topic)
                    .addContexts("auction_winner")
                    .addValuesForContexts(auctionMessage.getSender())
                    .build()
                    .toString();

            MessageProducer.getInstance().sendMessage(topic, mess);

            // end round timer - auction is over
            timer.cancel();

            negTask.done(true);


        }


    }


    public void makeNewRound() {

        Agent.logger.warn("Buyer agent " + " -------------------------------------------------------- Starting Round " + round + " ----------------------------------------------------------------------");

        // add new round
        round++;

        timer = new Timer();

        // wait for response till round time expires
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                Agent.logger.info("Task " + " Dutch auction " + " Round ended form timer ");

                Agent.logger.info("Starting new Round " + "with offer " + offer);
                System.out.println("Starting new Round" + "with offer " + offer);

                // if offer is smaller then minimum offer - end auction
                if (offer < minOffer) {

                    System.out.println("Buyer agent " + " --- WINNER --- " + " don't have winner ");
                    Agent.logger.info("AUCTION WINNER " + " DON'T HAVE WINNER ");


                    negTask.done(false);

                    // else - start new round with smaller offer
                } else {

                    makeNewRound();
                }

            }
        }, roundTime);


        // start first round
        String mess = new MessageBuilder()
                .addMark("B")
                .addHeader("auction_round")
                .addSender(topic)
                .addContexts("round_number", "winning_offer")
                .addValuesForContexts(String.valueOf(round), String.valueOf(offer))
                .build()
                .toString();


        offer = offer - step;


        MessageProducer.getInstance().sendMessage(topic, mess);

    }


    @Override
    public void onStart() {

        Agent.logger.info("Task " + " Dutch auction " + " on start ");


        Timer timer = new Timer();

        // start first round after time for seller to calculate utility is done
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                makeNewRound();

            }
        }, super.getWaitTime());


    }


    @Override
    public void onEnd() {

        Agent.logger.info("Task " + " Dutch auction " + " on end ");


    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if round_response message - save bid and pronounce winner
        if (auctionMessage.getHeader().equals("round_response")) {
            makeRoundResponse(auctionMessage);
        }


    }

}
