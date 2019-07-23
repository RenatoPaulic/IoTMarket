package agenthelper.tasks;

import agenthelper.auctionsubtype.AuctionSubtype;
import taskcontrol.basictasks.AuctionTask;

import java.util.Properties;

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

    private Properties properties;
    RoundTask roundTask;

    AuctionSubtype auctionSubtype;


    public Auction(String buuid, String suuid, AuctionSubtype auctionSubtype){

        this.buyerUUID = buuid;
        this.sellerUUID = suuid;
        this.auctionSubtype = auctionSubtype;
        properties = new Properties();

    }

    public String getBuyerUUID() { return buyerUUID; }
    public void setBuyerUUID(String buyerUUID) { this.buyerUUID = buyerUUID; }
    public String getSellerUUID() { return sellerUUID; }
    public void setSellerUUID(String sellerUUID) { this.sellerUUID = sellerUUID; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public Properties getProperties(){ return  properties; }
    public void setProperties(Properties properties){ this.properties = properties;}


    public abstract String getAuctionOffer(String value);

    public void setRoundTask(RoundTask roundTask){
        this.roundTask = roundTask;
    }

    public RoundTask getRoundTask(){ return roundTask; }

    public AuctionSubtype getAuctionSubtype(){
        return auctionSubtype;
    }

}
