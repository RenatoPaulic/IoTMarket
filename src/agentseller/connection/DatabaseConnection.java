package agentseller.connection;


import agentseller.datacenter.RealSensorSchema;
import agentseller.datacenter.SensorSchema;
import agentseller.datacenter.VirtualSensorSchema;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

public interface DatabaseConnection {

    /**
     * @return database connection
     */
    Connection getConnection();





    /**
     * @param topic topic name that refers to sensor topic property
     * @return all sensors with wanted topic
     */
    List<SensorSchema> getAllSensorsForTopic(String topic);

    /**
     *
     * @param id virtual sensor id
     * @return all sensors within virtual sensor with given id
     */
    List<SensorSchema> getAllSubsensorsForVirtualSensor(String id);

    /**
     * @param auctionUUID auction UUID
     * @param auctionType auction type
     * @param args auction parameters
     */
    void updateAuctionSpecPropertiesTable(String auctionUUID, int auctionType, String... args);

    /**
     * @param auctionUUID auction UUID
     * @param datacenterUUID data center UUID
     * @param properties auction Properties
     */
    void updateAuctionProperties(String auctionUUID, String datacenterUUID, Properties properties);





    /**
     * @param sensorID sensor ID
     */
    void updateSensorPart(String sensorID);

    /**
     * Method witch update sensor earnings
     * @param sensorID sensor ID
     * @param price sensor price
     */
    void updateSensorsPayment(String sensorID, int price);


    /**
     * @param winnerFlag flag that determinants auction result (true - win, false - lose)
     * @param winningSensors list of wining sensor combination
     */
    void updateDataCenterProperties(Boolean winnerFlag, List<SensorSchema> winningSensors);



    /**
     *
     * @param id device id
     * @return number of auction in witch sensor was sold
     */
    int getNumOfDeviceWin(String id);

    /**
     * @param id device id
     * @return number of auctions in witch sensor participated in combination that could be sold
     */
    int getNumOfDeviceParticipated(String id);

    /**
     * @return number of auction in witch data center won
     */
    int getNumOfAuctionWins();

    /**
     * @return number of auctions in witch data center participated
     */
    int getNumOfAuctionParticipated();

    /**
     * @return payment that data center claim each win
     */
    int getDataCenterPayment();


    /**
     * @param sensorId sensor ID
     * @return real sensor corresponding to given sensor ID
     */
    RealSensorSchema getRealSensor(String sensorId);

    /**
     * @param description sensor description
     * @return virtual sensor corresponding to given description
     */
    VirtualSensorSchema getVirtualSensor(String description);


}
