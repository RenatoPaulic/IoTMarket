package agenthelper.helper;


import strategies.AuctionStrategy;

/**
 * Class that represent container for agent helper properties
 * Singleton class available in whole program
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class HelperProperties {

    private AuctionStrategy auctionStrategy;
    private String deviceNumberFunction;
    private int maxSensorNum;

    private static final HelperProperties instance = new HelperProperties();

    private HelperProperties() {

    }




    public static HelperProperties getInstance() {
        return instance;
    }

    public void init(AuctionStrategy auctionStrategy, String deviceNumberFunction, int maxSensorNum){

        this.auctionStrategy = auctionStrategy;
        this.maxSensorNum = maxSensorNum;
        this.deviceNumberFunction = deviceNumberFunction;

    }


    public AuctionStrategy getAuctionStrategy() { return auctionStrategy; }
    public void setAuctionStrategy(AuctionStrategy auctionStrategy) { this.auctionStrategy = auctionStrategy; }
    public int getMaxSensorNum() { return maxSensorNum; }
    public void setMaxSensorNum(int maxSensorNum) { this.maxSensorNum = maxSensorNum; }
    public String getDeviceNumberFunction() { return deviceNumberFunction; }
    public void setDeviceNumberFunction(String deviceNumberFunction) { this.deviceNumberFunction = deviceNumberFunction; }

}
