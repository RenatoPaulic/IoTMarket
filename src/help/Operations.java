package help;


import agenthelper.helper.HelperProperties;
import agenthelper.helper.HelperSensorSchema;
import agentseller.datacenter.SensorSchema;
import enums.AuctionProperties;
import enums.AuctionSubtypes;
import enums.AuctionTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class that provides additional methods used
 * easies calculation and managing
 */
public class Operations {

    public static void combinationUtil(SensorSchema[] arr, SensorSchema[] data, int start, int end, int index, int r, List<List<SensorSchema>> allCombinationList)
    {

        if (index == r) {
            ArrayList<SensorSchema> combination = new ArrayList<>();
            for (int j=0; j<r; j++) { combination.add(data[j]); }
            allCombinationList.add(combination);
            return;
        }

        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r, allCombinationList);
        }
    }

    public static void getCombinations(List<SensorSchema> arr, int restriction, List<List<SensorSchema>> allCombinationList)
    {

        SensorSchema[] ar = arr.toArray(new SensorSchema[arr.size()]);
        SensorSchema[] data= new SensorSchema[restriction];
        int n = arr.size();

        combinationUtil(ar, data, 0, n-1, 0, restriction,allCombinationList);

    }




    public static Properties buildProperties(List<String> keys, List<String> values) {

        if (keys.size() != values.size()) {
            return null;
        }

        Properties properties = new Properties();

        for (int i = 0; i < keys.size(); i++) {

            if (keys.get(i).equals("AUCTION_TYPE")) {

                properties.put(AuctionProperties.valueOf(keys.get(i)), AuctionTypes.valueOf(values.get(i)));

            } else if (keys.get(i).equals("AUCTION_SUBTYPE")) {

                properties.put(AuctionProperties.valueOf(keys.get(i)), AuctionSubtypes.valueOf(values.get(i)));
            } else {

                properties.put(AuctionProperties.valueOf(keys.get(i)), values.get(i));
            }

        }


        return properties;
    }




    public static void combinationUtil(HelperSensorSchema[] arr, HelperSensorSchema[] data, int start, int end, int index, int r, List<List<HelperSensorSchema>> allCombinationList)
    {

        if (index == r) {
            ArrayList<HelperSensorSchema> combination = new ArrayList<>();
            for (int j=0; j<r; j++) { combination.add(data[j]); }
            allCombinationList.add(combination);
            return;
        }

        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r, allCombinationList);
        }
    }

    public static void getCombinations2(List<HelperSensorSchema> arr, int restriction, List<List<HelperSensorSchema>> allCombinationList)
    {

        HelperSensorSchema[] ar = arr.toArray(new HelperSensorSchema[arr.size()]);
        HelperSensorSchema[] data= new HelperSensorSchema[restriction];
        int n = arr.size();

        combinationUtil(ar, data, 0, n-1, 0, restriction,allCombinationList);

    }


}
