package strategies;


import java.util.List;

/**
 * Interface for auction strategies with method that
 * pick utility based on index defined by strategy
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public interface AuctionStrategy {
    Double pickUtility(Double highestUtility, List<Double> utilityList, List<Double> sellerList);
}
