package strategies;

import java.util.List;

/**
 * Auction strategy that pick offer with highest utility for buyer
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class RationalStrategy implements AuctionStrategy {
    @Override
    public Double pickUtility(Double highestUtility, List<Double> utilityList, List<Double> sellerList ) {

        return utilityList.get(0);
    }
}
