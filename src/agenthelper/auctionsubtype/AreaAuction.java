package agenthelper.auctionsubtype;


import agenthelper.helper.HelperProperties;
import agenthelper.helper.HelperSensorSchema;
import agentseller.datacenter.SensorSchema;
import bsh.EvalError;
import bsh.Interpreter;
import enums.AuctionProperties;
import help.Operations;
import strategies.AuctionStrategy;

import java.util.*;

/**
 * Class witch define utility calculation and behaviours for
 * area auction subtype
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AreaAuction extends AuctionSubtype {


    private List<HelperSensorSchema> sensorsInArea;


    private List<UtilityContainer> utilityContainers;


    public static Double siloOffer = 0.0;

    public AreaAuction(Properties properties, List<HelperSensorSchema> sensorsInArea, AuctionStrategy auctionStrategy){

        super(properties, sensorsInArea, auctionStrategy);

        this.sensorsInArea = sensorsInArea;

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

        String sellerDeviceFunction = HelperProperties.getInstance().getDeviceNumberFunction();


        // list of all valid sensor combination
        List<List<HelperSensorSchema>> allCombinationList = new ArrayList<>();

        // make all sensor combinations based on device number attribute restriction
        for(int k = deviceRestriction; k <= sensorsInArea.size(); k++) { Operations.getCombinations2(sensorsInArea,  k, allCombinationList); }

        // calculate utility for each sensor combination
        for(int t = 0; t < allCombinationList.size() ; t++) {

            double buyerUtility = 0;
            double sellerUtility = 0;
            double priceSum = 0;
            double qualitySum = 0;
            double deviceSum = 0;
            double sellerRatingSum = 0;
            double sellerDeviceSum = 0;

            int deviceNum = allCombinationList.get(t).size();

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

                interpreter.set("deviceNumber", deviceNum);
                interpreter.set("deviceRestriction", deviceRestriction);
                deviceSum += (double) interpreter.eval(deviceFunction);
            } catch (EvalError evalError) { evalError.printStackTrace(); }


            // calculate final buyer
            buyerUtility += deviceSum + qualitySum - priceSum;


            // if quality for sensor combination is lesser than quality restriction throw combination (buyer utility = -1)
            if(qualitySum < qualityRestriction) buyerUtility = -1;

            /* BUYER UTILITY */


            // calculate helper utility only for positive buyer utility
            if(buyerUtility > 0){



                /* HELPER UTILITY */

                interpreter = new Interpreter();


                // calculate utility for device number attribute
                try{
                    interpreter.set("deviceNumber", deviceNum);
                    sellerDeviceSum = (double) interpreter.eval(sellerDeviceFunction);
                }catch (EvalError evalError){evalError.printStackTrace();}


                // calculate total seller utility
                sellerUtility = sellerDeviceSum + priceSum ;

                /* HELPER UTILITY */


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



    public List<HelperSensorSchema> getSensorsInArea() { return sensorsInArea; }
    public void setSensorsInArea(List<HelperSensorSchema> sensorsInArea) { this.sensorsInArea = sensorsInArea; }


}


