package agentbuyer.factory;

import agentbuyer.auction.AuctionSubtype;
import agentbuyer.auctiontasks.BuyerAuction;
import agentbuyer.auctiontasks.DutchAuction;
import agentbuyer.auctiontasks.EnglishAuction;
import agentbuyer.auctiontasks.FirstPriceAuction;
import enums.AuctionTypes;

/**
 * HelperAuction factory class (for buyer)
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionFactory {

    public static BuyerAuction createAuction(AuctionTypes type, Object[] parameters)  {


        switch (type) {

            case ENGLISH_AUCTION:

                return new EnglishAuction((AuctionSubtype) parameters[0], (Long) parameters[1], (Long) parameters[2]);

            case DUTCH_AUCTION:

                return new DutchAuction((AuctionSubtype) parameters[0],  (Long) parameters[1], (Long) parameters[2], (Double) parameters[3], (Double) parameters[4]);


            case FIRST_PRICE_AUCTION:

                return new FirstPriceAuction( (AuctionSubtype) parameters[0],   (Long) parameters[1], (Long) parameters[2]);

            default: return null;
        }


    }


}