package agentseller.connection;

import agentseller.datacenter.DataCenter;
import agentseller.datacenter.RealSensorSchema;
import agentseller.datacenter.SensorSchema;
import agentseller.datacenter.VirtualSensorSchema;
import enums.AuctionProperties;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlLiteConnection implements DatabaseConnection {


    private String url;

    public SqlLiteConnection(String url){

        this.url = "jdbc:sqlite:" + url;

    }



    @Override
    public Connection getConnection() {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }







    @Override
    public void updateAuctionSpecPropertiesTable(String auctionUUID, int auctionType, String ... args){

        String sql = "";

        if (auctionType == 1) {
            sql = "INSERT INTO AUCTION_SPEC_PAR(auction_uuid,description) VALUES(?,?) ";
        } else if (auctionType == 2) {
            sql = "INSERT INTO AUCTION_SPEC_PAR(auction_uuid,min_x,max_x,min_y,max_y) VALUES(?,?,?,?,?) ";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (auctionType == 1) {

                pstmt.setString(1, auctionUUID);
                pstmt.setString(2, args[0]);

            } else if (auctionType == 2) {

                pstmt.setString(1, auctionUUID);
                pstmt.setString(2, args[0] );
                pstmt.setString(3, args[1]);
                pstmt.setString(4, args[2]);
                pstmt.setString(5, args[3]);
            }


            pstmt.executeUpdate();

        }catch (SQLException e) {
            System.out.println(e.getMessage());

        }


    }



    @Override
    public void updateAuctionProperties(String auctionUUID, String dataCenterUUID, Properties properties) {
        String sql = "INSERT INTO AUCTION_PART(auction_uuid,data_center_uuid) VALUES(?,?)  ";

        String sql2 = "INSERT INTO AUCTION_PROP(auction_uuid, auction_type, auction_subtype, device_num_utility_function," +
                "device_quality_utility_function, device_num_restriction, device_quality_restriction, stream_time, helper_flag) VALUES(?,?,?,?,?,?,?,?,?);";


        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

            pstmt.setString(1, auctionUUID);
            pstmt.setString(2, dataCenterUUID);

            pstmt2.setString(1, auctionUUID);
            pstmt2.setString(2, String.valueOf(properties.get(AuctionProperties.AUCTION_TYPE)));
            pstmt2.setString(3, String.valueOf(properties.get(AuctionProperties.AUCTION_SUBTYPE)));
            pstmt2.setString(4, String.valueOf(properties.get(AuctionProperties.DEVICE_NUM_FUNCTION)));
            pstmt2.setString(5, String.valueOf(properties.get(AuctionProperties.QUALITY_FUNCTION)));
            pstmt2.setInt(6, Integer.parseInt((String) properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION)));
            pstmt2.setInt(7, Integer.parseInt((String) properties.get(AuctionProperties.QUALITY_RESTRICTION)));
            pstmt2.setInt(8, Integer.parseInt((String) properties.get(AuctionProperties.STREAM_TIME)));
            pstmt2.setString(9, String.valueOf(properties.get(AuctionProperties.HELPER_FLAG)));

            pstmt.executeUpdate();
            pstmt2.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }


    }




    @Override
    public void updateSensorPart(String sensorId){

        String sql = "UPDATE SENSOR_PAYMENT SET   "
                + "num_of_auction_part = num_of_auction_part + ? "
                + "WHERE data_center_id = ? "
                + "AND sensor_id = ? ";


        int numOfAuctionWin = 1;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numOfAuctionWin);
            pstmt.setInt(2, DataCenter.getInstance().getDatacenterId());
            pstmt.setString(3, sensorId);


            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());


        }


    }


    @Override
    public void updateSensorsPayment(String sensorID, int price){

        String sql = "UPDATE SENSOR_PAYMENT SET  "
                + "num_of_auction_win = num_of_auction_win + ? "
                + ", money_earned = money_earned + ? "
                + "WHERE data_center_id = ? "
                + "AND sensor_id = ? ";

        int numOfAuctionWin = 1;


        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numOfAuctionWin);
            pstmt.setInt(2, price);
            pstmt.setInt(3, DataCenter.getInstance().getDatacenterId());
            pstmt.setString(4, sensorID);


            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());


        }

    }


    @Override
    public void updateDataCenterProperties(Boolean winnerFlag, List<SensorSchema> winningSensors) {

        String sql = "UPDATE DATA_CENTER_PROP SET  "
                + "auction_win = auction_win + ? "
                + ", auction_participated = auction_participated + ? "
                + ", money_win = money_win + ? "
                + "WHERE data_center_id = ? ";


        int priceWin = 0;

        if(winnerFlag) {

            for(SensorSchema sensorSchema : winningSensors){

                priceWin += sensorSchema.getPrice();

                updateSensorsPayment(sensorSchema.getSensorId(), sensorSchema.getPrice());

            }


            /*
            for(SensorSchema sensorSchema : AreaAuction.partSensors){

                updateSensorPart(sensorSchema.getSensorId());

            }
            */

        }

        priceWin += DataCenter.getInstance().getPayment();

        int winnerValue = winnerFlag ? 1 : 0;
        int auctionParticipated = 1;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){


            pstmt.setInt(1, winnerValue);
            pstmt.setInt(2, auctionParticipated);
            pstmt.setInt(3, priceWin);
            pstmt.setInt(4, DataCenter.getInstance().getDatacenterId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }


    }












    @Override
    public int getNumOfDeviceWin(String id ){

        String sql = "SELECT num_of_auction_win FROM SENSOR_PAYMENT" + " WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() + " AND sensor_id = " + "'" + id + "'";

        int numOfWins = 0;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                numOfWins = rs.getInt("num_of_auction_win");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return numOfWins;


    }


    @Override
    public int getNumOfDeviceParticipated(String id){

        String sql = "SELECT num_of_auction_part FROM SENSOR_PAYMENT" + " WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() + " AND sensor_id = " + "'" + id + "'";

        int numOfPart = 0;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                numOfPart = rs.getInt("num_of_auction_part");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return numOfPart;


    }


    @Override
    public int getNumOfAuctionWins(){

        String sql = "SELECT auction_win FROM DATA_CENTER_PROP" + " WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() ;

        int numOfWins = 0;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                numOfWins = rs.getInt("auction_win");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return numOfWins;



    }

    @Override
    public int getNumOfAuctionParticipated(){


        String sql = "SELECT auction_participated FROM DATA_CENTER_PROP" + " WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() ;

        int numOfParticipation = 0;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {


                numOfParticipation = rs.getInt("auction_participated");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return numOfParticipation;



    }


    @Override
    public int getDataCenterPayment(){

        String sql = "SELECT data_center_payment FROM DATA_CENTER_PROP" + " WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() ;

        int payment = 0;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                payment = rs.getInt("data_center_payment");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return payment;



    }

    @Override
    public List<SensorSchema> getAllSubsensorsForVirtualSensor(String id){

        List<SensorSchema> sensorSchemas = new ArrayList<>();

        String sql = "SELECT data_schema.sensor_id AS id, price, quality, type FROM VIRTUAL_SENSOR_CON JOIN DATA_SCHEMA ON virtual_sensor_con.sensor_id = data_schema.sensor_id  WHERE virtual_sensor_con.data_center_id = " + DataCenter.getInstance().getDatacenterId() + " AND virtual_sensor_id = '" + id + "'" ;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                String sensorId = rs.getString("id");
                int price = rs.getInt("price");
                int quality = rs.getInt("quality");
                int type = rs.getInt("type");

                sensorSchemas.add(new SensorSchema(sensorId,price,quality,type));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return sensorSchemas;



    }

    @Override
    public List<SensorSchema> getAllSensorsForTopic(String topic){

        List<SensorSchema> sensorSchemas = new ArrayList<>();

        String sql = "SELECT * FROM DATA_SCHEMA WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() + " AND topic = '" + topic + "'" ;

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                String sensorId = rs.getString("sensor_id");
                int longitude = rs.getInt("longitude");
                int latitude = rs.getInt("latitude");
                int price = rs.getInt("price");
                int quality = rs.getInt("quality");
                int sensorType = rs.getInt("type");
                String description = rs.getString("description");
                String sensorTopic = rs.getString("topic");

                SensorSchema sensorSchema = null;

                if(sensorType == 1) {

                    sensorSchema = new RealSensorSchema(sensorId, longitude, latitude, price, quality, sensorTopic);

                }else if(sensorType == 2){

                    sensorSchema = new VirtualSensorSchema(sensorId,price,quality,description,sensorTopic);
                }


                sensorSchemas.add(sensorSchema);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return sensorSchemas;

    }




    public RealSensorSchema getRealSensor(String sensorId){



        String sql = "SELECT * FROM DATA_SCHEMA WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() + " AND sensor_id = '" + sensorId + "' AND type = 1" ;

        RealSensorSchema realSensorSchema = new RealSensorSchema();

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                int longitude = rs.getInt("longitude");
                int latitude = rs.getInt("latitude");
                int price = rs.getInt("price");
                int quality = rs.getInt("quality");
                String sensorTopic = rs.getString("topic");

                realSensorSchema =  new RealSensorSchema(sensorId, longitude, latitude, price, quality, sensorTopic);

                System.out.println(realSensorSchema.toString());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return realSensorSchema;


    }


    @Override
    public VirtualSensorSchema getVirtualSensor(String description){



        String sql = "SELECT * FROM DATA_SCHEMA WHERE data_center_id = " + DataCenter.getInstance().getDatacenterId() + " AND description = '" + description + "' AND type = 2" ;

        VirtualSensorSchema virtualSensorSchema = new VirtualSensorSchema();

        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {

                String sensorId = rs.getString("sensor_id");
                int price = rs.getInt("price");
                int quality = rs.getInt("quality");
                String sensorTopic = rs.getString("topic");

                virtualSensorSchema =  new VirtualSensorSchema(sensorId, price, quality, description, sensorTopic);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return virtualSensorSchema;


    }



}

