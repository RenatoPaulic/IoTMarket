package strategies;

import agentseller.datacenter.DataCenter;

import java.util.List;


/**
 * HelperAuction strategy that pick offer based on data center rating
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class OptimalStrategy implements AuctionStrategy{


        @Override
        public Double pickUtility(Double highestUtility, List<Double> utilityList, List<Double> sellerList) {


            int wins = DataCenter.getInstance().getDatabaseConnection().getNumOfAuctionWins();
            int auctionParticipated = DataCenter.getInstance().getDatabaseConnection().getNumOfAuctionParticipated();

            double winRating ;

            if(auctionParticipated == 0) {
                winRating = 1;
            }else {

                winRating= (double) wins /  auctionParticipated;
            }

            int numOfPossibleOffers = utilityList.size();

            int index = (int)((winRating) * (numOfPossibleOffers - 1));

            return  utilityList.get(index);


        }


}