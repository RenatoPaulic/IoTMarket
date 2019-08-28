package agentbuyer.auctiontasks;

import agentbuyer.auction.AuctionSubtype;
import agentbuyer.auction.Bid;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;

import java.util.*;


/**
 * Class witch implement Dutch auction communication protocol
 * for buyer side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class EnglishAuction extends BuyerAuction {

    private Set<String> sellerUUIDList;

    private Set<String> inGameSellers;
    private Set<String> lastRoundSellers;
    private Timer timer ;
    private int phaseFlag;

    private long roundTime;

    private int round;

    private String topic;


    private AuctionSubtype auctionSubtype;


    /**
     * @param auctionSubtype represent auction subtype
     * @param waitTime time to wait until sellers calculate their offers
     * @param roundTime time to wait offer in round - time between two rounds
     */
    public EnglishAuction(AuctionSubtype auctionSubtype, long waitTime, long roundTime){

        super(waitTime);

        this.roundTime = roundTime;

        sellerUUIDList = new HashSet<>();
        timer = new Timer();

        phaseFlag = 1;
        round = 0;

        this.topic = auctionSubtype.getAuctionUuid();
        this.auctionSubtype = auctionSubtype;


        Agent.logger.info("Creating task: " + " English auction ");

    }



    public void makeRoundResponse(AuctionMessage auctionMessage){

        if(sellerUUIDList.contains(auctionMessage.getSender())) {

            inGameSellers.add(auctionMessage.getSender());
            auctionSubtype.putBid(new Bid(auctionMessage.getSender(), Double.parseDouble(auctionMessage.getValue())));

            // all participants applied their offers before round time has ended - start new round
            if (lastRoundSellers.size() == inGameSellers.size()) {

                if (round > 1) {
                    timer.cancel();
                }

                Agent.logger.info("Task " + " English auction " + " Round ended when all participants applied");

                pronaunceRoundWinner();


            }

        }

    }






    public void makeNewRound(){

        round ++;

        if(round == 1){
            lastRoundSellers = sellerUUIDList;
        }else{
            lastRoundSellers = inGameSellers;
        }

        inGameSellers = new HashSet<>();

        Agent.logger.info("Buyer Agent "  + " -------------------------------------------------------- Starting Round " + round + " ----------------------------------------------------------------------");
        System.out.println("Buyer agent "  + " -------------------------------------------------------- Starting Round " + round + " ----------------------------------------------------------------------");

        timer = new Timer();

        // start round timer
        timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Agent.logger.info("Task " + " English auction " + " Round ended form timer ");
                    pronaunceRoundWinner();
                }


                }, roundTime);



    }



    public void pronaunceRoundWinner(){


        // if there is only one seller in auction pronounce winner
        if(inGameSellers.size() <= 1){

            pronaunceAuctionWinner();

        // else get best bid and start new round with highest bid
        }else{

            Bid currentWinnerBid = auctionSubtype.getBestBid();

            makeNewRound();

            if(currentWinnerBid != null) {

               String mess = new MessageBuilder()
                                    .addMark("B")
                                    .addHeader("auction_round")
                                    .addSender(topic)
                                    .addContexts("round_number", "winning_seller", "winning_offer")
                                    .addValuesForContexts(String.valueOf(round),currentWinnerBid.getSellerUUID(), currentWinnerBid.getUtility().toString())
                                    .build()
                                    .toString();

                MessageProducer.getInstance().sendMessage(topic, mess);

            }


        }

    }





    private void pronaunceAuctionWinner(){

        // cancel all round timers - auction is over
        timer.cancel();

        try {


            // get best bid and send auction_end message
            Bid winnerBid = auctionSubtype.getBestBid();

            System.out.println("Buyer agent "+  " --- WINNER --- " + winnerBid.getSellerUUID() + " " + winnerBid.getUtility());
            Agent.logger.info("AUCTION WINNER " + winnerBid.getSellerUUID() + " with utility " + winnerBid.getUtility());


            String mess = new MessageBuilder()
                    .addMark("B")
                    .addHeader("auction_end")
                    .addSender(topic)
                    .addContexts("auction_winner")
                    .addValuesForContexts(winnerBid.getSellerUUID())
                    .build()
                    .toString();


            MessageProducer.getInstance().sendMessage(topic,mess);

            negTask.done(true);


        }catch (NoSuchElementException e){

            System.out.println("Buyer agent " + " --- WINNER --- " + " DON'T HAVE WINNER "  );
            Agent.logger.info("AUCTION WINNER " + " DON'T HAVE WINNER " );

            negTask.done(false);
        }


    }

    @Override
    public void onStart() {

        Agent.logger.info("Task " + " English auction " + " on start ");

        Timer timer = new Timer();


        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                if(sellerUUIDList.size() == 0){
                    negTask.done(false);
                }else {

                    phaseFlag = 2;

                    makeNewRound();

                    String roundInitMessage = new MessageBuilder()
                            .addMark("B")
                            .addHeader("auction_round")
                            .addSender(topic)
                            .addContexts("round_number", "winning_seller", "winning_offer")
                            .addValuesForContexts(String.valueOf(round), "None", "0.0")
                            .build()
                            .toString();

                    MessageProducer.getInstance().sendMessage(topic, roundInitMessage);
                }

            }
        }, super.getWaitTime());






    }

    @Override
    public void onEnd() {

        Agent.logger.info("Task " + " English auction " + " on end ");


    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {


        // if round_response message - save bid and if all round participants placed their bids can start new round
        if (auctionMessage.getHeader().equals("round_response") && phaseFlag == 2) {
            makeRoundResponse(auctionMessage);
        }

        // if auction_participation message - add seller to seller participation list
        if (auctionMessage.getHeader().equals("auction_participation") && phaseFlag == 1) {

            sellerUUIDList.add(auctionMessage.getSender());
            Agent.logger.info("Task " + " English auction "  + " added seller " + auctionMessage.getSender() + " to auction " );
        }
    }





    public Set<String> getSellerUUIDList() {
        return sellerUUIDList;
    }

    public int getPhaseFlag() {
        return phaseFlag;
    }
    public void setPhaseFlag(int phaseFlag) {
        this.phaseFlag = phaseFlag;
    }
}





