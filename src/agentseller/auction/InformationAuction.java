package agentseller.auction;

import agentseller.datacenter.DataCenter;
import agentseller.datacenter.DataGroup;
import agentseller.datacenter.SensorSchema;
import agentseller.datacenter.VirtualSensorSchema;
import enums.AuctionProperties;
import strategies.AuctionStrategy;
import bsh.EvalError;
import bsh.Interpreter;

import java.util.*;

/**
 * Class witch define utility calculation and behaviours for
 * information auction subtype
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class InformationAuction extends AuctionSubtype {

    private String description;
    private List<SensorSchema> subSensors;

    private List<Double> buyerList;
    private List<Double> sellerList;


    private VirtualSensorSchema virtualSensor;

    public InformationAuction(Properties properties, DataGroup dataGroup, String description, AuctionStrategy auctionStrategy){

        super(properties,dataGroup,auctionStrategy);

        this.description = description;

        // get virtual sensor for description
        for(SensorSchema sensorSchema : dataGroup.getSensorSchemaList()){

            System.out.println("In data group " + sensorSchema.getSensorId());


            virtualSensor = DataCenter.getInstance().getDatabaseConnection().getVirtualSensor(description);


        }

        buyerList = new ArrayList<>();
        sellerList = new ArrayList<>();

        // get all subsensors (real sensors) for virtual sensor
        subSensors = DataCenter.getInstance().getDatabaseConnection().getAllSubsensorsForVirtualSensor(virtualSensor.getSensorId());

        // add sensor to offer combination - only one combination
        tmpOfferData.add(virtualSensor);

    }

    @Override
    public boolean checkParticipation(){


        // if there are any offers in list return true, else false
        return buyerList.size() > 0;

    }

    @Override
    public void calculateUtility(){

        // extract all parameters relevant for utility calculating
        int deviceNumberRestriction = Integer.parseInt((String)properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION));
        int qualityRestriction = Integer.parseInt((String)properties.get(AuctionProperties.QUALITY_RESTRICTION));
        String utilityFunction = (String) properties.get(AuctionProperties.DEVICE_NUM_FUNCTION);
        String qualityFunction = (String) properties.get(AuctionProperties.QUALITY_FUNCTION);


        double qualitySum = 0.0;

        Interpreter interpreter = new Interpreter();


        for (int t = 0; t < subSensors.size(); t++) {

            try {

                interpreter.set("deviceQuality", subSensors.get(t).getQuality());
                interpreter.set("qualityRestriction", qualityRestriction);
                qualitySum += (double)interpreter.eval(qualityFunction);
            } catch (EvalError evalError) { evalError.printStackTrace(); }
        }

        // if quality and device number satisfied restrictions
        if(qualitySum >= qualityRestriction && subSensors.size() >= deviceNumberRestriction) {

            double deviceNumSum = 0;
            double buyerUtility = 0;
            // get virtual sensor price
            double sensorPrice = dataGroup.getSensorSchemaList().get(0).getPrice();


            try {
                interpreter.set("deviceRestriction", deviceNumberRestriction);
                interpreter.set("deviceNumber", subSensors);
                deviceNumSum += (double) interpreter.eval(utilityFunction);
            } catch (EvalError evalError) {
                evalError.printStackTrace();
            }

            // calculate buyer utility
            buyerUtility = deviceNumSum + qualitySum - sensorPrice;

            // if positive
            if(buyerUtility > 0) {

                // fill utility buyer and seller utility list
                for (int i = (int) sensorPrice; i < buyerUtility; i++) {

                    // seler utility ne punim po seler
                    sellerList.add((double) i);
                    buyerList.add(buyerUtility - i);

                }

                // sort offers
                Arrays.sort(sellerList.toArray(), Collections.reverseOrder());
                Arrays.sort(buyerList.toArray());

            }

            System.out.println("Seller utility list: " + sellerList);
            System.out.println("Buyer utility list: " + buyerList);

        }


    }



    @Override
    public Double getOffer(Double highestUtility) {


        // list of all possible offers
        List<Double> possibleOffers = new ArrayList<>();

        // default offer is -1.0
        Double offer = -1.0;

        for(Double value : buyerList){

            // put all offers higher then highest offered in list of possible offers
            if(value > highestUtility){

                possibleOffers.add(value);
            }

        }

        // if there is any offer - pick best offer depending on strategy, and save it
        if(possibleOffers.size()>0)
        {

            offer = auctionStrategy.pickUtility(highestUtility,possibleOffers, new ArrayList<>());

            int index = buyerList.indexOf(offer);

            tmpBuyerUtility = buyerList.get(index);
            tmpSellerUtility = sellerList.get(index);

        }

        return offer;




    }


    public String getDescription() { return description; }
    public List<SensorSchema> getSubSensors() { return subSensors; }
    public List<Double> getBuyerList() { return buyerList; }
    public List<Double> getSellerList() { return sellerList; }
}
