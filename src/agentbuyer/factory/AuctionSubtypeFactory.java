package agentbuyer.factory;

import agentbuyer.auction.AreaAuction;
import agentbuyer.auction.AuctionSubtype;
import agentbuyer.auction.InformationAuction;
import enums.AuctionSubtypes;
import help.AreaDots;

/**
 * HelperAuction subtype factory class (for buyer)
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionSubtypeFactory {

    public static AuctionSubtype createAuctionSubtype(AuctionSubtypes type, Object[] parameters)  {


        switch (type) {

            case AREA_AUCTION:

                return new AreaAuction((AreaDots) parameters[0]);

            case INFORMATION_AUCTION:

                return new InformationAuction((String) parameters[0]);

            default: return null;


        }


    }
}