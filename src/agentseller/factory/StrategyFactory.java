package agentseller.factory;

import enums.AuctionStrategies;
import strategies.*;

/**
 * Strategy factory class
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class StrategyFactory {

    public static AuctionStrategy createStrategy(AuctionStrategies type){


        switch (type){

            case RATIONAL:

                return new RationalStrategy();


            case AGGRESSIVE:

                return new AggressiveStrategy();


            case OPTIMAL:

                return new OptimalStrategy();

            case RANDOM:

                return new RandomStrategy();


            default: return null;
        }





    }




}
