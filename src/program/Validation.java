package program;

import enums.AuctionStrategies;
import enums.AuctionSubtypes;
import enums.AuctionTypes;

import java.util.Arrays;


/**
 * Class that contains static methods for
 * validation input parameters
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class Validation {

    public static final String[] defaultTopics = {"Pollution", "Temperature"};
    public static final String[] flagValues = {"true", "false"};
//    public static final String[] defaultTopics2 =  Arrays.stream(AuctionTopics.values()).toArray(String[]::new);


    /**
     * Method witch validates buyer input parameters
     * @args all input parameters for program
     */
    public static boolean validateBuyerParameters(String ... args){


        // wrong number of arguments
        if(!(args.length == 18 || args.length == 19 ||args.length == 15 || args.length == 16) && !args[0].equals("agent_buyer_run")){
            return false;
        }


        if(args.length == 18 || args.length == 19) {

                if (!Arrays.asList(defaultTopics).contains(args[3])) {
                    return false;
                }

                if (!Arrays.asList(AuctionTypes.values()).contains(AuctionTypes.valueOf(args[4].toUpperCase()))) {
                    return false;
                }

                if (!Arrays.asList(AuctionSubtypes.values()).contains(AuctionSubtypes.valueOf(args[5].toUpperCase()))) {
                    return false;
                }

                if (!Arrays.asList(flagValues).contains(args[10])) {

                    return false;
                }

                try {
                    Integer.parseInt(args[8]);
                    Integer.parseInt(args[9]);
                    Integer.parseInt(args[11]);
                    Integer.parseInt(args[12]);
                    Integer.parseInt(args[13]);
                    Integer.parseInt(args[14]);
                    Integer.parseInt(args[15]);
                    Integer.parseInt(args[16]);
                    Integer.parseInt(args[17]);
                    if (args[4].toUpperCase().equals("DUTCH_AUCTION")) {

                        Integer.parseInt(args[18]);
                    }

                } catch (NumberFormatException e) {

                    return false;
                }
            }



        if(args.length == 15 || args.length == 16) {

            if (!Arrays.asList(defaultTopics).contains(args[3])) {
                return false;
            }

            if (!Arrays.asList(AuctionTypes.values()).contains(AuctionTypes.valueOf(args[4].toUpperCase()))) {
                return false;
            }

            if (!Arrays.asList(AuctionSubtypes.values()).contains(AuctionSubtypes.valueOf(args[5].toUpperCase()))) {
                return false;
            }

            if (!Arrays.asList(flagValues).contains(args[10])) {

                return false;
            }

            try {
                Integer.parseInt(args[8]);
                Integer.parseInt(args[9]);
                Integer.parseInt(args[11]);
                Integer.parseInt(args[12]);
                Integer.parseInt(args[13]);

                if (args[4].toUpperCase().equals("DUTCH_AUCTION")) {

                    Integer.parseInt(args[14]);
                }

            } catch (NumberFormatException e) {

                return false;
            }
          }



         return true;




    }


    /**
     * Method witch validates buyer input parameters
     * @args  all input parameters for program
     */
    public static boolean validateSellerParameters(String ... args){


        // wrong number of arguments
        if(!(args.length <= 9) && !args[0].equals("agent_seller_run")){
            return false;
        }

        if (!Arrays.asList(AuctionStrategies.values()).contains(AuctionStrategies.valueOf(args[7].toUpperCase()))) {

            return false;

        }

        for(int i = 9 ; i < args.length; i ++){

            if(!Arrays.asList(defaultTopics).contains(args[i])){

                return false;

            }

        }


        try {
            Integer.parseInt(args[3]);
            Integer.parseInt(args[8]);
        } catch (NumberFormatException e) {
            return false;

        }



        return true;




    }


    /**
     * Method witch validates buyer input parameters
     * @args all input parameters for done.helper.program
     */
    public static boolean validateHelperParameters(String ... args){


        // wrong number of arguments
        if(!(args.length <= 4) && !args[0].equals("agent_helper_run")){
            return false;
        }

        if (!Arrays.asList(AuctionStrategies.values()).contains(AuctionStrategies.valueOf(args[4].toUpperCase()))) {

            return false;

        }

        for(int i = 6 ; i < args.length; i ++){

            if(!Arrays.asList(defaultTopics).contains(args[i])){

                return false;

            }

        }

        try {
            Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            return false;

        }



        return true;





    }


}
