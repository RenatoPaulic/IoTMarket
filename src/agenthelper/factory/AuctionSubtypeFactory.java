package agenthelper.factory;


import agenthelper.auctionsubtype.AreaAuction;
import agenthelper.auctionsubtype.AuctionSubtype;
import agenthelper.helper.HelperSensorSchema;
import enums.AuctionSubtypes;
import strategies.AuctionStrategy;

import java.util.List;
import java.util.Properties;


/**
 * Auction subtype factory class (for agent helper)
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionSubtypeFactory {

    public static AuctionSubtype createAuction(AuctionSubtypes type, Object[] parameters){


        switch (type) {

            case AREA_AUCTION:

                return new AreaAuction((Properties) parameters[0], (List<HelperSensorSchema>) parameters[1], (AuctionStrategy) parameters[2]);


        }


        return null;


    }

}
