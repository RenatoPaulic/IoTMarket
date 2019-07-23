package agentseller.auction;

import agentseller.datacenter.DataCenter;
import agentseller.datacenter.DataGroup;
import agentseller.datacenter.RealSensorSchema;
import agentseller.datacenter.SensorSchema;
import enums.AuctionProperties;
import help.AreaDots;
import help.Operations;
import strategies.AuctionStrategy;
import bsh.EvalError;
import bsh.Interpreter;


import java.util.*;

/**
 * Class witch define utility calculation and behaviours for
 * area auction subtype
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AreaAuction extends AuctionSubtype {


    private AreaDots areaDots;
    private List<SensorSchema> sensorsInArea;


    private List<UtilityContainer> utilityContainers;

    public static Set<SensorSchema> partSensors = null;

    public static Double siloOffer = 0.0;


    public AreaAuction(Properties properties, DataGroup dataGroup, AreaDots areaDots, AuctionStrategy auctionStrategy){

        super(properties, dataGroup, auctionStrategy);

        this.areaDots = areaDots;
        sensorsInArea = getDataFromArea();

        System.out.println("ajmo 2 " + sensorsInArea);


        utilityContainers = new ArrayList<>();

    }

    @Override
    public boolean checkParticipation(){

        // if there is at least one combination return true, else false
        return utilityContainers.size() > 0;


    }

    @Override
    public void calculateUtility(){

        // extract all parameters relevant for utility calculating
        int deviceRestriction = Integer.parseInt((String)properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION));
        int qualityRestriction = Integer.parseInt((String)properties.get(AuctionProperties.QUALITY_RESTRICTION));
        String deviceFunction = (String) properties.get(AuctionProperties.DEVICE_NUM_FUNCTION);
        String qualityFucntion = (String) properties.get(AuctionProperties.QUALITY_FUNCTION);

        String sellerDeviceFunction = DataCenter.getInstance().getDeviceNumberFunction();
        String sellerRatingFunction = DataCenter.getInstance().getRatingFunction() ;

        // list of all valid sensor combination
        List<List<SensorSchema>> allCombinationList = new ArrayList<>();

        // make all sensor combinations based on device number attribute restriction
        for(int k = deviceRestriction; k <= sensorsInArea.size(); k++) { Operations.getCombinations(sensorsInArea,  k, allCombinationList); }

        // calculate utility for each sensor combination
        for(int t = 0; t < allCombinationList.size() ; t++) {

            double buyerUtility = 0;
            double sellerUtility = 0;
            double priceSum = 0;
            double qualitySum = 0;
            double deviceSum = 0;
            double sellerRatingSum = 0;
            double sellerDeviceSum = 0;

            int deviceNumber = allCombinationList.get(t).size();

            Interpreter interpreter = new Interpreter();

            /* BUYER UTILITY */


            // calculate utility for quality attribute
            // calculate price sum for all sensor combination
            for(int r = 0 ; r < allCombinationList.get(t).size(); r++) {
                priceSum += allCombinationList.get(t).get(r).getPrice();

                try {

                    interpreter.set("deviceQuality", allCombinationList.get(t).get(r).getQuality());
                    interpreter.set("qualityRestriction", qualityRestriction);
                    qualitySum += (double)interpreter.eval(qualityFucntion);
                } catch (EvalError evalError) { evalError.printStackTrace(); }
            }


            // calculate utility for device number attribute
            try {

                interpreter.set("deviceNumber", deviceNumber);
                interpreter.set("deviceRestriction", deviceRestriction);
                deviceSum += (double) interpreter.eval(deviceFunction);
            } catch (EvalError evalError) { evalError.printStackTrace(); }


            // calculate final buyer
            buyerUtility += deviceSum + qualitySum - priceSum - DataCenter.getInstance().getPayment();


            // if quality for sensor combination is lesser than quality restriction throw combination (buyer utility = -1)
            if(qualitySum < qualityRestriction) buyerUtility = -1;

            /* BUYER UTILITY */


            // calculate seller utility only for positive buyer utility
            if(buyerUtility > 0){



                /* SELLER UTILITY */

                interpreter = new Interpreter();


                // calculate utility for sensor rating attribute
                // for each sensor in combination separately
                for(int r = 0 ; r < allCombinationList.get(t).size(); r++) {

                    interpreter = new Interpreter();

                    // get number of wins and participations for sensor
                    int deviceAuctionWin = DataCenter.getInstance().getDatabaseConnection().getNumOfDeviceWin(allCombinationList.get(t).get(r).getSensorId());
                    int deviceAuctionParticipated = DataCenter.getInstance().getDatabaseConnection().getNumOfDeviceParticipated(allCombinationList.get(t).get(r).getSensorId());


                    // if sensor participation is 0 - rating is 1, else calculate rating
                    double deviceRating = 1;

                    if(deviceAuctionParticipated > 0) deviceRating = 1 - (double)deviceAuctionWin/deviceAuctionParticipated;

                    try {

                        interpreter.set("deviceRating", deviceRating);
                        sellerRatingSum += (double)interpreter.eval(sellerRatingFunction);
                    } catch (EvalError evalError) { evalError.printStackTrace(); }

                }


                // calculate utility for device number attribute
                try{
                    interpreter.set("deviceNumber", deviceNumber);
                    sellerDeviceSum = (double) interpreter.eval(sellerDeviceFunction);
                }catch (EvalError evalError){evalError.printStackTrace();}


                // calculate total seller utility
                sellerUtility = sellerDeviceSum + sellerRatingSum + priceSum + DataCenter.getInstance().getPayment();

                /* SELLER UTILITY */


                // make utility container class to hold calculate utilities
                UtilityContainer utilityContainer = new UtilityContainer(allCombinationList.get(t), buyerUtility, sellerUtility) ;

                // don't put duplicates in final utility list - same buyer and same seller utilities
                if(!utilityContainers.contains(utilityContainer)){

                    utilityContainers.add(utilityContainer);

                }



                // make copy of utility container list to manage original utility container list
                List<UtilityContainer> utilityContainersCopy = new ArrayList<>();
                utilityContainersCopy.addAll(utilityContainers);

                boolean flag = false;

                // go through each utility container in list
                for (UtilityContainer u : utilityContainersCopy) {

                    // remove all combinations where seller utility and buyer utility is lesser than one temporarily added
                    if ((buyerUtility >= u.getBuyerUtility() && sellerUtility >= u.getSellerUtility()) ){


                        flag = true;
                        // remove if
                        utilityContainers.remove(u);

                    // remove temporarily added combination if list contains combinations witch have greater seller and buyer utilities
                    }else if((buyerUtility <= u.getBuyerUtility() && sellerUtility <= u.getSellerUtility())){

                        flag = false;

                        // remove if

                        utilityContainers.remove(utilityContainer);
                        break;
                    }


                }

                if(flag){

                    utilityContainers.add(utilityContainer);

                }



            }






        }


        // sort utilities
        Collections.sort(utilityContainers, new Comparator<UtilityContainer>() {
            @Override
            public int compare(UtilityContainer o1, UtilityContainer o2) {
                return o1.getBuyerUtility().compareTo(o2.getBuyerUtility());
            }
        });


        System.out.println("***********************");
        System.out.println("final");

        for (UtilityContainer utilityContainer : utilityContainers){

            System.out.println(utilityContainer.getCombination() + " " + utilityContainer.getBuyerUtility() + " " + utilityContainer.getSellerUtility() );

        }

        System.out.println("***********************");


        System.out.println("--------------------------------------------------------");



        partSensors = new HashSet<>();

        for(UtilityContainer u : utilityContainers){
            partSensors.addAll(u.getCombination());
        }




    }


    /**
     * Method that filters sensors that are in wanted area represent by AreaDots class
     * @return sensors in wanted area
     */
    private List<SensorSchema> getDataFromArea()
    {

        List<SensorSchema> sensorsInArea = new ArrayList<>();

        for(SensorSchema sensorSchema : dataGroup.getSensorSchemaList()){

            if(sensorSchema.getType() == 1) {
                RealSensorSchema sensor = DataCenter.getInstance().getDatabaseConnection().getRealSensor(sensorSchema.getSensorId());


                // check if sensor is in area
                if (sensor.getLatitude() >= areaDots.getMin_x() && sensor.getLatitude() <= areaDots.getMax_x() && sensor.getLongitude() >= areaDots.getMin_y() && sensor.getLongitude() <= areaDots.getMax_y()) {
                    sensorsInArea.add(sensorSchema);
                    System.out.println("vba " + sensor.getSensorId() + " " + sensor.getLongitude() + " " + sensor.getLongitude());
                }
            }
        }

        return sensorsInArea;

    }



    @Override
    public Double getOffer(Double highestUtility) {

        // list of possible offers
        List<Double> possibleOffers = new ArrayList<>();
        List<Double> corespondingSeller = new ArrayList<>();

        // put all offers higher then highest offered in list of possible offers
        for (UtilityContainer u : utilityContainers) {

            if (u.getBuyerUtility() > highestUtility) {

                possibleOffers.add(u.getBuyerUtility());
                corespondingSeller.add(u.getSellerUtility());

            }

        }

        // sort
        Collections.sort(possibleOffers);
        Collections.sort(corespondingSeller);
        Collections.reverse(possibleOffers);


        // if there is any offer - pick best offer depending on strategy, and save it, else set default offer -1.0
        double offer = possibleOffers.size() == 0 ? -1 : auctionStrategy.pickUtility(highestUtility, possibleOffers, corespondingSeller);


        UtilityContainer utilityContainer;

        for (UtilityContainer u : utilityContainers) {

            if (u.getBuyerUtility() == offer) {

                utilityContainer = u;

                tmpBuyerUtility = utilityContainer.getBuyerUtility();
                tmpSellerUtility = utilityContainer.getSellerUtility();
                tmpOfferData = utilityContainer.getCombination();

                break;
            }
        }


        siloOffer = offer;

        return offer;

    }


    public AreaDots getAreaDots() { return areaDots; }
    public void setAreaDots(AreaDots areaDots) { this.areaDots = areaDots; }
    public List<SensorSchema> getSensorsInArea() { return sensorsInArea; }
    public void setSensorsInArea(List<SensorSchema> sensorsInArea) { this.sensorsInArea = sensorsInArea; }


}


