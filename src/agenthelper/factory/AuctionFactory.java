package agenthelper.factory;


import agenthelper.auctionsubtype.AuctionSubtype;
import agenthelper.tasks.Auction;
import agenthelper.tasks.DutchAuction;
import agenthelper.tasks.EnglishAuction;
import agenthelper.tasks.FirstPriceAuction;
import enums.AuctionTypes;

/**
 * Auction factory class (for agent helper)
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionFactory {

    public static Auction createAuction(AuctionTypes type, Object[] parameters){


        switch (type){

            case ENGLISH_AUCTION:

                return new EnglishAuction((String) parameters[0], (String) parameters[1], (AuctionSubtype) parameters[2], (long) parameters[3]);


            case FIRST_PRICE_AUCTION:

                return new FirstPriceAuction((String) parameters[0], (String) parameters[1], (AuctionSubtype) parameters[2], (long) parameters[3]);


            case DUTCH_AUCTION:

                return new DutchAuction((String) parameters[0], (String) parameters[1], (AuctionSubtype) parameters[2], (long) parameters[3]);



        }


        return null;


    }


}
