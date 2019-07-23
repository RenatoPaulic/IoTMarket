package agenthelper.factory;


import enums.AuctionStrategies;
import strategies.AggressiveStrategy;
import strategies.AuctionStrategy;
import strategies.RandomStrategy;
import strategies.RationalStrategy;

/**
 * Strategy factory class (for agent helper)
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



            case RANDOM:

                return new RandomStrategy();


            default: return null;
        }





    }




}
