package agentseller.datacenter;

import agentseller.connection.DatabaseConnection;
import strategies.AuctionStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent data center with all its characteristic
 * Singleton class available in whole program
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class DataCenter {

    private int datacenterId;
    private DatabaseConnection databaseConnection;
    private AuctionStrategy auctionStrategy;
    private List<DataGroup> dataGroups;
    private int payment;

    private String deviceNumberFunction;
    private String ratingFunction;

    private static final DataCenter instance = new DataCenter();

    private DataCenter() {

    }



    public void addDataGroup(DataGroup dataGroup) {
        dataGroups.add(dataGroup);
    }

    /**
     * Method witch creates new DataGroup based on given topic name and data in database
     * @param topic topic name
     */
    public void addDataGroupForTopic(String topic) {

        dataGroups.add(new DataGroup(topic, databaseConnection.getAllSensorsForTopic(topic)));


    }

    /**
     * @param topic topic name
     * @return DataGroup related to topic
     */
    public DataGroup getDataGroupByTopic(String topic) {

        for (DataGroup dataGroup : dataGroups) {

            if (dataGroup.getTopic().equals(topic))
                return dataGroup;
        }

        return null;

    }



    public static DataCenter getInstance() {
        return instance;
    }

    public void init(int datacenterId, DatabaseConnection databaseConnection, AuctionStrategy auctionStrategy, int payment, String deviceNumberFunction, String ratingFunction){

        this.datacenterId = datacenterId;
        this.databaseConnection = databaseConnection;
        this.auctionStrategy = auctionStrategy;
        this.payment = payment;
        this.deviceNumberFunction = deviceNumberFunction;
        this.ratingFunction = ratingFunction;
        dataGroups = new ArrayList<>();
    }

    public DatabaseConnection getDatabaseConnection(){ return databaseConnection; }
    public int getDatacenterId() { return datacenterId; }
    public void setDatacenterId(int datacenterId) { this.datacenterId = datacenterId; }
    public void setDatabaseConnection(DatabaseConnection databaseConnection) { this.databaseConnection = databaseConnection; }
    public AuctionStrategy getAuctionStrategy() { return auctionStrategy; }
    public void setAuctionStrategy(AuctionStrategy auctionStrategy) { this.auctionStrategy = auctionStrategy; }
    public void setDataGroups(List<DataGroup> dataGroups) { this.dataGroups = dataGroups; }
    public int getPayment() { return payment; }
    public void setPayment(int payment) { this.payment = payment; }
    public List<DataGroup> getDataGroups() { return dataGroups; }
    public String getDeviceNumberFunction() { return deviceNumberFunction; }
    public void setDeviceNumberFunction(String deviceNumberFunction) { this.deviceNumberFunction = deviceNumberFunction; }
    public String getRatingFunction() { return ratingFunction; }
    public void setRatingFunction(String ratingFunction) { this.ratingFunction = ratingFunction; }
}









