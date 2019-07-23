package agentseller.factory;

import agentseller.auction.AreaAuction;
import agentseller.auction.AuctionSubtype;
import agentseller.auction.InformationAuction;
import agentseller.datacenter.DataGroup;
import enums.AuctionSubtypes;
import help.AreaDots;
import strategies.AuctionStrategy;

import java.util.Properties;

/**
 * Auction subtype factory class (for seller)
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionSubtypeFactory {

    public static AuctionSubtype createAuction(AuctionSubtypes type, Object[] parameters){


        switch (type) {

            case AREA_AUCTION:

                return new AreaAuction((Properties) parameters[0], (DataGroup)parameters[1], (AreaDots) parameters[2], (AuctionStrategy) parameters[3]);

            case INFORMATION_AUCTION:

                return new InformationAuction((Properties) parameters[0], (DataGroup)parameters[1], (String) parameters[2], (AuctionStrategy) parameters[3]);

        }


        return null;


    }

}
