package agentseller.factory;


import agentseller.auctiontasks.Auction;
import agentseller.auctiontasks.DutchAuction;
import agentseller.auctiontasks.EnglishAuction;
import agentseller.auctiontasks.FirstPriceAuction;
import agentseller.auction.AuctionSubtype;
import enums.AuctionTypes;


/**
 * Auction factory class (for seller)
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
