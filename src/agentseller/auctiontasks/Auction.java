package agentseller.auctiontasks;

import agentseller.auction.AuctionSubtype;
import enums.AuctionProperties;
import taskcontrol.basictasks.AuctionTask;


/**
 * Abstract class that each auction must extend
 * Represent basic auction and has reference on
 * AuctionNegotiationTask class
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public abstract class Auction implements AuctionTask {

    private String buyerUUID;
    private String sellerUUID;
    private String topic;

    AuctionNegotiationTask auctionNegotiationTask;

    AuctionSubtype auctionSubtype;


    public Auction(String buuid, String suuid, AuctionSubtype auctionSubtype){

        this.buyerUUID = buuid;
        this.sellerUUID = suuid;
        this.auctionSubtype = auctionSubtype;

        topic = auctionSubtype.getProperties().get(AuctionProperties.TOPIC).toString();


    }

    public String getBuyerUUID() { return buyerUUID; }
    public void setBuyerUUID(String buyerUUID) { this.buyerUUID = buyerUUID; }
    public String getSellerUUID() { return sellerUUID; }
    public void setSellerUUID(String sellerUUID) { this.sellerUUID = sellerUUID; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }


    public abstract String getAuctionOffer(String value);

    public void setAuctionNegotiationTask(AuctionNegotiationTask auctionNegotiationTask){
        this.auctionNegotiationTask = auctionNegotiationTask;
    }

    public AuctionNegotiationTask getAuctionNegotiationTask(){ return auctionNegotiationTask; }

    public AuctionSubtype getAuctionSubtype(){
        return auctionSubtype;
    }

}
