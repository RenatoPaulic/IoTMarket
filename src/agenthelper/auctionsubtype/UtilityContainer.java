package agenthelper.auctionsubtype;

import agenthelper.helper.HelperSensorSchema;

import java.util.List;
import java.util.Objects;

/**
 * Container class witch represent one complete auction offer for helper
 * in area auction
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class UtilityContainer {

    private List<HelperSensorSchema> combination;
    private Double buyerUtility;
    private Double sellerUtility;

    public UtilityContainer(List<HelperSensorSchema> combination, Double buyerUtility, Double sellerUtility) {
        this.combination = combination;
        this.buyerUtility = buyerUtility;
        this.sellerUtility = sellerUtility;
    }



    public List<HelperSensorSchema> getCombination() { return combination; }
    public void setCombination(List<HelperSensorSchema> combination) { this.combination = combination; }
    public Double getBuyerUtility() { return buyerUtility; }
    public void setBuyerUtility(Double buyerUtility) { this.buyerUtility = buyerUtility; }
    public Double getSellerUtility() { return sellerUtility; }
    public void setSellerUtility(Double sellerUtility) { this.sellerUtility = sellerUtility; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtilityContainer that = (UtilityContainer) o;
        return Objects.equals(buyerUtility, that.buyerUtility) &&
                Objects.equals(sellerUtility, that.sellerUtility);
    }

    @Override
    public int hashCode() {

        return Objects.hash(buyerUtility, sellerUtility);
    }





}
