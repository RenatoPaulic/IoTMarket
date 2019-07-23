package agentbuyer.auction;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent information auction subtype
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class InformationAuction extends AuctionSubtype {

    private String informationDescription;


    public InformationAuction(String itemDescription){

        this.informationDescription = itemDescription;

    }



    @Override
    public List<Object> getSpecificParametersForItem()  {

        List<Object> descriptionList = new ArrayList<>();

        descriptionList.add(informationDescription);

        return descriptionList;

    }

    public String getInformationDescription() { return informationDescription; }
    public void setInformationDescription(String informationDescription) { this.informationDescription = informationDescription; }

    @Override
    public String toString(){

        return "Information description  "  + informationDescription;
    }
}
