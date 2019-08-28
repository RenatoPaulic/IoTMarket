package strategies;

import java.util.List;
import java.util.Random;

/**
 * HelperAuction strategy that randomly pick offer
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class RandomStrategy implements AuctionStrategy {
    @Override
    public Double pickUtility(Double highestUtility, List<Double> utilityList, List<Double> sellerList ) {


        int index = Math.abs(new Random().nextInt()) % utilityList.size();

        return utilityList.get(index);
    }
}
