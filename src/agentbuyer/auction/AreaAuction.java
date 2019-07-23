package agentbuyer.auction;

import help.AreaDots;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent area auction subtype
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AreaAuction extends AuctionSubtype {

    private AreaDots areaDots;


    public AreaAuction(AreaDots areaDots) {

        this.areaDots = areaDots;

    }


    @Override
    public List<Object> getSpecificParametersForItem()  {

        List<Object> specificParametersList = new ArrayList<>();

        specificParametersList.add(Integer.valueOf(areaDots.getMin_x()).toString());
        specificParametersList.add(Integer.valueOf(areaDots.getMax_x()).toString());
        specificParametersList.add(Integer.valueOf(areaDots.getMin_y()).toString());
        specificParametersList.add(Integer.valueOf(areaDots.getMax_y()).toString());

        return specificParametersList;

    }


    public AreaDots getAreaDots() { return areaDots; }
    public void setAreaDots(AreaDots areaDots) { this.areaDots = areaDots; }


    @Override
    public String toString(){

        return "Area: "  + areaDots.getMin_x() + " " + areaDots.getMin_x() + " " + areaDots.getMax_y() + " " + areaDots.getMin_y();

    }

}
