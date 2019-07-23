package agentseller.datacenter;

import java.util.Objects;

/**
 * Class that represent container for basic sensor information
 */
public class SensorSchema {

    private String sensorId;
    private int price;
    private int quality;
    private int type;

    public SensorSchema(){

    }

    public SensorSchema(String sensorId, int price, int quality, int type){

        this.sensorId = sensorId;
        this.price = price;
        this.quality = quality;
        this.type = type;

    }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public int getQuality() { return quality; }
    public void setQuality(int quality) { this.quality = quality; }
    public int getType(){ return  type;}


    @Override
    public String toString(){

       return "ID: " + sensorId + " Price: " + getPrice()  + " Quality: " + quality;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorSchema that = (SensorSchema) o;
        return Objects.equals(sensorId, that.sensorId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sensorId);
    }
}
