package strategies;

import java.util.List;

/**
 * HelperAuction strategy that pick offer with lowest utility for buyer
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AggressiveStrategy implements AuctionStrategy{

    @Override
    public Double pickUtility(Double highestUtility, List<Double> utilityList, List<Double> sellerList ) {

        return utilityList.get(utilityList.size() - 1);
    }
}
