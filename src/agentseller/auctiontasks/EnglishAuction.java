package agentseller.auctiontasks;

import agentseller.auction.AuctionSubtype;
import agents.AgentSeller;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class witch implement English auction communication protocol
 * for seller side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class EnglishAuction extends SellerAuction {


    private boolean roundInitFlag;

    private Timer timer;

    private long waitTime;

    public EnglishAuction(String buuid, String suuid, AuctionSubtype auctionSubtype, long waitTime){

        super(buuid,suuid, auctionSubtype);

        this.waitTime = waitTime;

        roundInitFlag = false;

        timer = new Timer();

    }



    @Override
    public String getAuctionOffer(String value) {

        Double highestUtility = Double.parseDouble(value);
        Double offer = auctionSubtype.getOffer(highestUtility);

        return offer.toString();


    }




    @Override
    public void onStart() {

        Agent.logger.info("HelperAuction on start ");

        // apply for auction sending auction_participation message
        String mess = new MessageBuilder()
                .addMark("S")
                .addHeader("auction_participation")
                .addSender(getSellerUUID())
                .addContexts("participation_flag")
                .addValuesForContexts("true")
                .build()
                .toString();

        MessageProducer.getInstance().sendMessage(getBuyerUUID(),mess);

        // start timer - if no messages are received in defined time auction is passed
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                Agent.logger.warn("Timer ended " + " didn't receive auction message " + " auction passed - didn't participated");
                Agent.logger.info( " AUCTION LOSE ");

                System.out.println("AUCTION_LOSE");

                auctionNegotiationTask.done(false);

            }
        }, waitTime);
    }

    @Override
    public void onEnd() {

        Agent.logger.info("HelperAuction on end");


    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        // if auction_round message - extract winning offer and place higher bid (if possible)
        if(auctionMessage.getHeader().equals("auction_round")) {


            String winningSeller = auctionMessage.getValueForContext("winning_seller");
            String winningOffer = auctionMessage.getValueForContext("winning_offer");

            int roundNumber = Integer.parseInt(auctionMessage.getValueForContext("round_number"));

            if (roundNumber == 1 && !roundInitFlag) {

                roundInitFlag = true;
                timer.cancel();

            } else if (!roundInitFlag) {

                timer.cancel();
                System.out.println("HelperAuction started " + "late participation " + "quiting");
                Agent.logger.info( " AUCTION LOSE " + "DIDNT'T PARTICIPATED");

                auctionNegotiationTask.done(false);
            }


            if(roundInitFlag){



                String offer = getAuctionOffer(winningOffer);
                String mess;


                if(!winningSeller.equals(getSellerUUID())) {


                    mess = new MessageBuilder()
                            .addMark("S")
                            .addHeader("round_response")
                            .addSender(getSellerUUID())
                            .addContexts("proposed_value")
                            .addValuesForContexts(offer)
                            .build()
                            .toString();

                    if (offer.equals("-1.0")) {

                        Agent.logger.info(" AUCTION LOSE ");
                        System.out.println("AUCTION LOSE");

                        MessageProducer.getInstance().sendMessage(getBuyerUUID(), mess);

                        auctionNegotiationTask.done(false);
                    }


                } else {

                    mess = new MessageBuilder()
                            .addMark("S")
                            .addHeader("round_response")
                            .addSender(getSellerUUID())
                            .addContexts("proposed_value")
                            .addValuesForContexts(winningOffer)
                            .build()
                            .toString();



                }

                MessageProducer.getInstance().sendMessage(getBuyerUUID(), mess);


            }

        }


        // if auction_end message - see if winner
        if(auctionMessage.getHeader().equals("auction_end")){

            Agent.logger.info("Received message "  + " " + "SENDER: " + auctionMessage.getSender() + " " + " HEADER: " + auctionMessage.getHeader()
                    + " " + " CONTEXT: " + auctionMessage.getContext() + " " + " VALUE: " + auctionMessage.getValue() );

            if(auctionMessage.getValueForContext("auction_winner").equals(getSellerUUID())) {

                Agent.logger.info( " AUCTION WINNER ");
                System.out.println("AUCTION_WIN");
                auctionNegotiationTask.done(true);

            }else{

                Agent.logger.info( " AUCTION LOSE ");
                System.out.println("AUCTION_LOSE");
                auctionNegotiationTask.done(false);
            }



        }


    }




}
