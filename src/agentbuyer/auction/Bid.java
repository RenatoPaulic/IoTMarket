package agentbuyer.auction;

import java.util.Objects;

/**
 * Class that represent one bid in auction
 * Contain seller UUID (who placed the bid)
 * and proposed utility
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class Bid {

    private Double utility;
    private String sellerUUID;

    public Bid(String sellerUUID, double utility) {

        this.sellerUUID = sellerUUID;
        this.utility = utility;
    }

    public Double getUtility() {
        return utility;
    }
    public void setUtility(double utility) {
        this.utility = utility;
    }
    public String getSellerUUID() {
        return sellerUUID;
    }
    public void setSellerUUID(String seller) {
        this.sellerUUID = seller;
    }




    @Override
    public String toString(){

        return "Bid: " + "Seller - " + sellerUUID + " " + "Utility - " + utility;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bid bid = (Bid) o;
        return Objects.equals(utility, bid.utility);
    }

    @Override
    public int hashCode() {

        return Objects.hash(utility);
    }
}
