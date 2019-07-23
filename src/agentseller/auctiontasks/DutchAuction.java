package agentseller.auctiontasks;

import agentseller.auction.AuctionSubtype;
import agents.AgentSeller;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;


/**
 * Class witch implement Dutch auction communication protocol
 * for seller side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class DutchAuction extends Auction {

    private long waitTime;

    private Double wantedOffer;

    public DutchAuction(String buyerUuid, String sellerUuid, AuctionSubtype auctionSubtype, long waitTime){

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

        // get offer that will seller wait to accept
        wantedOffer = Double.parseDouble(getAuctionOffer("0"));
    }

    @Override
    public void onEnd() {

        AgentSeller.logger.info("Auction on end");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {


        // if auction_round message - extract offer and if it is acceptable, accept it
        if(auctionMessage.getHeader().equals("auction_round")){

            Double winningOffer = Double.parseDouble(auctionMessage.getValueForContext("winning_offer"));

            String mess;

            if(wantedOffer >= winningOffer){

                mess = new MessageBuilder()
                        .addMark("S")
                        .addHeader("round_response")
                        .addSender(getSellerUUID())
                        .addContexts("proposed_value")
                        .addValuesForContexts("Accept")
                        .build()
                        .toString();

                MessageProducer.getInstance().sendMessage(getBuyerUUID(),mess);

            }




        }

        // if auction_end message - see if winner
        if(auctionMessage.getHeader().equals("auction_end")){


            if(auctionMessage.getValue().equals(getSellerUUID())) {

                AgentSeller.logger.info( " AUCTION WIN ");
                System.out.println(" AUCTION WIN ");

                auctionNegotiationTask.done(true);

            }else{

                AgentSeller.logger.info( " AUCTION LOSE ");
                System.out.println(" AUCTION LOSE ");

                auctionNegotiationTask.done(false);
            }



        }

    }


}
