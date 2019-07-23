package agentseller.datacenter;

import java.util.List;

/**
 * Class that represent container for sensors group
 * associated with topic (same type of sensors)
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class DataGroup {

    private String topic;
    private List<SensorSchema> sensorSchemaList;

    public DataGroup(String topic, List<SensorSchema> sensorSchemaList){

        this.topic = topic;
        this.sensorSchemaList = sensorSchemaList;

    }

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public List<SensorSchema> getSensorSchemaList() { return sensorSchemaList; }
    public void setSensorSchemaList(List<SensorSchema> sensorSchemaList) { this.sensorSchemaList = sensorSchemaList; }

}
