package agentseller.datacenter;

/**
 * Class that represent container for virtual sensor
 */
public class VirtualSensorSchema  extends SensorSchema{


    private int sensorType;
    private String description;
    private String sensorTopic;

    private int auctionWins;
    private int auctionParticipated;

    public VirtualSensorSchema(){

    }

    public VirtualSensorSchema(String sensorId, int price, int quality, String description, String sensorTopic){

        super(sensorId,price,quality,2);

        this.description = description;
        this.sensorTopic = sensorTopic;

    }

    public int getSensorType() { return sensorType; }
    public void setSensorType(int sensorType) { this.sensorType = sensorType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSensorTopic() { return sensorTopic; }
    public void setSensorTopic(String sensorTopic) { this.sensorTopic = sensorTopic; }

    public int getAuctionWins() { return auctionWins; }
    public void setAuctionWins(int auctionWins) { this.auctionWins = auctionWins; }
    public int getAuctionParticipated() { return auctionParticipated; }
    public void setAuctionParticipated(int auctionParticipated) { this.auctionParticipated = auctionParticipated; }

    @Override
    public String toString(){

        return "ID: " + getSensorId() + " Price: " + getPrice() + " Type: " + sensorType + " Description " + description + " Topic: " + sensorTopic;


    }

}
