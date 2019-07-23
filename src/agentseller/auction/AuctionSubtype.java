package agentseller.auction;

import agentseller.datacenter.DataGroup;
import agentseller.datacenter.SensorSchema;
import strategies.AuctionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class witch represent auction subtype and holds
 * basic informations for each auction
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public abstract class AuctionSubtype {

    Properties properties;
    DataGroup dataGroup;

    AuctionStrategy auctionStrategy;

    List<SensorSchema> tmpOfferData;
    Double tmpBuyerUtility = - 1.0;
    Double tmpSellerUtility = - 1.0;


    public AuctionSubtype(Properties properties, DataGroup dataGroup, AuctionStrategy auctionStrategy){

        this.properties = properties;
        this.dataGroup = dataGroup;
        this.auctionStrategy = auctionStrategy;

        tmpOfferData = new ArrayList<>();
    }


    /**
     * Method witch check if seller can participate in auction
     * @return true if can, false if not
     */
    public abstract boolean checkParticipation();

    /**
     * Method witch calculates utilities for auction subtype
     */
    public abstract void calculateUtility();

    /**
     * Method witch calculate auction offer, new offer must be greater then one passed in argument
     * @param highestUtility utility that is currently highest on auction
     * @return new auction offer
     */
    public abstract Double getOffer(Double highestUtility);


    public Properties getProperties() { return properties; }
    public void setProperties(Properties properties) { this.properties = properties; }
    public DataGroup getDataGroup() { return dataGroup; }
    public void setDataGroup(DataGroup dataGroup) { this.dataGroup = dataGroup; }
    public AuctionStrategy getAuctionStrategy() { return auctionStrategy; }
    public void setAuctionStrategy(AuctionStrategy auctionStrategy) { this.auctionStrategy = auctionStrategy; }
    public List<SensorSchema> getTmpOfferData() { return tmpOfferData; }
    public void setTmpOfferData(List<SensorSchema> tmpOfferData) { this.tmpOfferData = tmpOfferData; }
    public Double getTmpBuyerUtility() { return tmpBuyerUtility; }
    public void setTmpBuyerUtility(Double tmpBuyerUtility) { this.tmpBuyerUtility = tmpBuyerUtility; }
    public Double getTmpSellerUtility() { return tmpSellerUtility; }
    public void setTmpSellerUtility(Double tmpSellerUtility) { this.tmpSellerUtility = tmpSellerUtility; }
}
