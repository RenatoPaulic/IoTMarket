package agentseller.auctiontasks;

import agentseller.auction.AuctionSubtype;
import agents.AgentSeller;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;


/**
 * Class witch implement English auction communication protocol
 * for seller side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class FirstPriceAuction extends Auction {

    private long waitTime;

    public FirstPriceAuction(String buyerUuid, String sellerUuid, AuctionSubtype auctionSubtype, long waitTime){

        super(buyerUuid,sellerUuid, auctionSubtype);

        this.waitTime = waitTime;

    }

    @Override
    public String getAuctionOffer(String value) {

        Double highestUtility = Double.parseDouble(value);
        Double offer = auctionSubtype.getOffer(highestUtility);

        return offer.toString();

    }



    @Override
    public void onStart() {

        AgentSeller.logger.info("Auction on start");


    }

    @Override
    public void onEnd() {

        AgentSeller.logger.info("Auction on end");


    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {


        // if auction_round message - extract winning offer and place higher bid
        if(auctionMessage.getHeader().equals("auction_round")){


            String winningOffer = auctionMessage.getValueForContext("winning_offer");

            String offer = getAuctionOffer(winningOffer);

            String mess = new MessageBuilder()
                    .addMark("S")
                    .addHeader("round_response")
                    .addSender(getSellerUUID())
                    .addContexts("proposed_value")
                    .addValuesForContexts(offer)
                    .build()
                    .toString();


            MessageProducer.getInstance().sendMessage(getBuyerUUID(),mess);

        }

        // if auction_end message - see if winner
        if(auctionMessage.getHeader().equals("auction_end")){


            AgentSeller.logger.info("Received message "  + " " + "SENDER: " + auctionMessage.getSender() + " " + " HEADER: " + auctionMessage.getHeader()
                    + " " + " CONTEXT: " + auctionMessage.getContext() + " " + " VALUE: " + auctionMessage.getValue() );

            if(auctionMessage.getValue().equals(getSellerUUID())) {

                AgentSeller.logger.info( " AUCTION WINNER ");
                System.out.println( " AUCTION WINNER ");
                auctionNegotiationTask.done(true);

            }else{

                AgentSeller.logger.info( " AUCTION LOSE ");
                System.out.println( " AUCTION LOSE ");
                auctionNegotiationTask.done(false);

            }





        }

    }
}
