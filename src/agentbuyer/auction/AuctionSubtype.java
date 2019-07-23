package agentbuyer.auction;

import java.util.*;


/**
 * Abstract class that represent auction subtype
 * Holds UUID used as buyer ID and auction ID
 * Contains methods for managing auction bids
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public abstract class AuctionSubtype {

    private List<Bid> auctionBidList;
    private String auctionUuid;

    public AuctionSubtype(){

        this.auctionUuid = UUID.randomUUID().toString();

        // list that holds all bids in auction
        auctionBidList = new ArrayList<>();
    }


    public void putBid(Bid bid){

        auctionBidList.add(bid);

    }


    /**
     * @return highest bid in auction
     */
    public Bid getBestBid(){

        return Collections.max(auctionBidList, Comparator.comparing(Bid :: getUtility));
    }


    /**
     * @param inGameSellersList set of sellers UUIDs
     * @return highest bid for given sellers
     */
    public Bid getBestBidForSellers(Set<String> inGameSellersList){


        List<Bid> sellerBidList = new ArrayList<>();

        for(Bid bid : auctionBidList){

            if(inGameSellersList.contains(bid.getSellerUUID())){

                sellerBidList.add(bid);

            }

        }

        return Collections.max(sellerBidList, Comparator.comparing(Bid :: getUtility));

    }


    public List<Bid> getBids(){

        return  auctionBidList;
    }

    public String getAuctionUuid() { return auctionUuid; }

    public abstract List<Object> getSpecificParametersForItem();

}


