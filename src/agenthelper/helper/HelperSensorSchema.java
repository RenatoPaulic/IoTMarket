package agenthelper.helper;

import agentseller.datacenter.SensorSchema;

/**
 * Class that represent container for cooperative sensor
 */
public class HelperSensorSchema extends SensorSchema {

    private String datacenterUUID ;
    private int type;

    public HelperSensorSchema(String sensorId, int sensorPrice, int sensorQuality, String datacenterUUID) {

        super(sensorId, sensorPrice, sensorQuality,1);

        this.type = 1;

        this.datacenterUUID = datacenterUUID;

    }


    public String getDatacenterUUID() {
        return datacenterUUID;
    }

    public void setDatacenterUUID(String datacenterUUID) {
        this.datacenterUUID = datacenterUUID;
    }
}
