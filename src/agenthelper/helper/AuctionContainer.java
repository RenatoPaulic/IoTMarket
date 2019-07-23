package agenthelper.helper;


import java.util.*;

/**
 * Container class for building seller cooperation
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionContainer {

    private List<String> basicParameters;
    private Properties properties;
    private String helperUuid;
    private Set<HelperSensorSchema> dataList;
    private int maxSetSize;
    private int reservedSpace;
    private String buyerUuid;

    public AuctionContainer(List<String> basicParameters, Properties properties, String buyerUuid, int maxSetSize){

        this.basicParameters = basicParameters;
        this.properties = properties;
        helperUuid = UUID.randomUUID().toString();

        // cooperative sensors set
        dataList = new HashSet<>();

        this.buyerUuid = buyerUuid;

        // tmp cooperative size
        reservedSpace = 0;

        // maximum cooperative size
        this.maxSetSize = maxSetSize;

    }



    public List<String> getBasicParameters() { return basicParameters; }
    public void setBasicParameters(List<String> basicParameters) { this.basicParameters = basicParameters; }
    public Properties getProperties() { return properties; }
    public void setProperties(Properties properties) { this.properties = properties; }
    public String getHelperUuid() { return helperUuid; }
    public void setHelperUuid(String helperUuid) { this.helperUuid = helperUuid; }


    public void addData(List<HelperSensorSchema> data){

        dataList.addAll(data);

    }

    public Set<HelperSensorSchema> getAllData(){

        return  dataList;
    }


    public int getMaxSetSize(){

        return maxSetSize;
    }

    public boolean isFull(){

        return maxSetSize == reservedSpace;

    }

    public void reserveSetSpace(int sensorsToAdd){

        reservedSpace += sensorsToAdd;

    }

    public int getReservedSpace(){return  reservedSpace;}

    public String getBuyerUuid() {
        return buyerUuid;
    }

    public void setBuyerUuid(String buyerUuid) {
        this.buyerUuid = buyerUuid;
    }
}