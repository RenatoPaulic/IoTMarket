package agentseller.datacenter;

/**
 * Class that represent container for real sensor
 */
public class RealSensorSchema extends SensorSchema{

    private int longitude;
    private int latitude;
    private int sensorType;

    private String sensorTopic;

    private int auctionWins;
    private int auctionParticipated;

    public RealSensorSchema(){

    }

    public RealSensorSchema(String sensorId, int longitude, int latitude, int price, int quality, String sensorTopic){

        super(sensorId,price,quality,1);
        this.longitude = longitude;
        this.latitude = latitude;
        this.sensorTopic = sensorTopic;

    }


    public int getLongitude() {return longitude; }
    public void setLongitude(int longitude) { this.longitude = longitude; }
    public int getLatitude() { return latitude; }
    public void setLatitude(int latitude) { this.latitude = latitude; }
    public int getSensorType() { return sensorType; }
    public void setSensorType(int sensorType) { this.sensorType = sensorType; }
    public String getSensorTopic() { return sensorTopic; }
    public void setSensorTopic(String sensorTopic) { this.sensorTopic = sensorTopic; }

    public int getAuctionWins() { return auctionWins; }
    public void setAuctionWins(int auctionWins) { this.auctionWins = auctionWins; }
    public int getAuctionParticipated() { return auctionParticipated; }
    public void setAuctionParticipated(int auctionParticipated) { this.auctionParticipated = auctionParticipated; }

    @Override
    public String toString(){

        return "ID: " + getSensorId()  + " Lon:" + getLongitude() + " Lat: " + getLatitude() + " Price: " + getPrice() + " Type: " + sensorType + " Topic: " + sensorTopic;


    }




}
