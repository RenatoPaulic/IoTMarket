package agents;

import agentbuyer.auction.AuctionSubtype;
import agentbuyer.auctiontasks.BuyerAuction;
import agentbuyer.factory.AuctionFactory;
import agentbuyer.factory.AuctionSubtypeFactory;
import agentbuyer.protocol.BasicAuctionProtocol;
import agentcontrol.AuctionAgent;
import agentcontrol.AuctionProtocol;
import bsh.EvalError;
import bsh.Interpreter;
import enums.AuctionProperties;
import enums.AuctionSubtypes;
import enums.AuctionTypes;
import help.AreaDots;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import program.Agent;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 * Class that represent agent buyer
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AgentBuyer implements AuctionAgent {



    private String auctionTopic;
    private AuctionProtocol auctionProtocol;

    public AgentBuyer(AuctionProtocol auctionProtocol, String auctionTopic){

        this.auctionTopic = auctionTopic;
        this.auctionProtocol = auctionProtocol;




    }

    @Override
    public void start(){

        Agent.logger.info("--------------------- STARTING BUYER AGENT ---------------------" );
        Agent.logger.info("Buyer agent: Initializing auction tasks" );
        auctionProtocol.initAuctionBehaviors(auctionTopic);

    }





    /**
     * Method that builds Properties from default input parameters
     * @param topic represent kafka topic, wanted data
     * @param auctionType  represent auction type
     * @param auctionSubtype  represent auction subtype
     * @param deviceNumberFunction  represent utility function for device number attribute
     * @param qualityFunction  represent utility function for device quality attribute
     * @param deviceNumberRestriction  represent restriction for device number attribute
     * @param qualityRestriction  represent restriction for device quality attribute
     * @param helperFlag  represent flag that determines helper agent participation in auction
     * @param streamTime  represent data streaming time
     * @return Properties - properties map that contains all default parameters
     */
    private static Properties setUpDefaultParameters(String topic, AuctionTypes auctionType, AuctionSubtypes auctionSubtype, String deviceNumberFunction, String qualityFunction, int deviceNumberRestriction, int qualityRestriction, boolean helperFlag, int streamTime){

        if(auctionSubtype.toString().equals("INFORMATION_AUCTION")){helperFlag=false;}


        Properties properties = new Properties();

        properties.put(AuctionProperties.TOPIC, topic);
        properties.put(AuctionProperties.AUCTION_TYPE, auctionType);
        properties.put(AuctionProperties.AUCTION_SUBTYPE, auctionSubtype);
        properties.put(AuctionProperties.DEVICE_NUM_FUNCTION, deviceNumberFunction);
        properties.put(AuctionProperties.QUALITY_FUNCTION ,qualityFunction);
        properties.put(AuctionProperties.DEVICE_NUM_RESTRICTION, deviceNumberRestriction);
        properties.put(AuctionProperties.QUALITY_RESTRICTION, qualityRestriction);
        properties.put(AuctionProperties.HELPER_FLAG, helperFlag);
        properties.put(AuctionProperties.STREAM_TIME, streamTime);

        Agent.logger.info("Creating Buyer agent");
        Agent.logger.info("--------------------");
        Agent.logger.info("Default parameters");
        Agent.logger.info("Topic: " + properties.get(AuctionProperties.TOPIC));
        Agent.logger.info("HelperAuction type: " + properties.get(AuctionProperties.AUCTION_TYPE));
        Agent.logger.info("HelperAuction subtype: " + properties.get(AuctionProperties.AUCTION_SUBTYPE));
        Agent.logger.info("Device number utility function: " + properties.get(AuctionProperties.DEVICE_NUM_FUNCTION));
        Agent.logger.info("Quality utility function: " + properties.get(AuctionProperties.QUALITY_FUNCTION));
        Agent.logger.info("Device number restriction: " + properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION));
        Agent.logger.info("Quality restriction: " + properties.get(AuctionProperties.QUALITY_RESTRICTION));
        Agent.logger.info("Helper flag: " + properties.get(AuctionProperties.HELPER_FLAG));
        Agent.logger.info("--------------------");

        return properties;

    }



    /**
     * Method witch set up buyer agent
     * @param parameters  input parameters
     * @return AgentBuyer class
     */
    public static AgentBuyer setUpBuyerAgent( String ... parameters){

        System.setProperty("agent_name", "Buyer");
        System.setProperty("auction_uuid", UUID.randomUUID().toString());

        Properties properties = setUpDefaultParameters(parameters[3], AuctionTypes.valueOf(parameters[4].toUpperCase()), AuctionSubtypes.valueOf(parameters[5].toUpperCase()), parameters[6], parameters[7],Integer.parseInt(parameters[8]), Integer.parseInt(parameters[9]), Boolean.parseBoolean(parameters[10]), Integer.parseInt(parameters[11]));

        Object[] auctionSubtypeObjectList = new Object[1];

        // for area auction set up coordinates in AreaDots class
        if(properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.AREA_AUCTION){

            String minx = parameters[parameters.length - 4];
            String maxx = parameters[parameters.length - 3];
            String miny = parameters[parameters.length - 2];
            String maxy = parameters[parameters.length - 1];

            AreaDots areaDots = new AreaDots(Integer.parseInt(minx),Integer.parseInt(maxx), Integer.parseInt(miny), Integer.parseInt(maxy));

            auctionSubtypeObjectList[0] = areaDots;

            Agent.logger.info("--------------------");
            Agent.logger.info("Specific parameters");
            Agent.logger.info("Min x: " + minx);
            Agent.logger.info("Max x: " + maxx);
            Agent.logger.info("Min y: " + miny);
            Agent.logger.info("Max y: " + maxy);
            Agent.logger.info("--------------------");

            // for information auction set up sensor description
        }else if(properties.get(AuctionProperties.AUCTION_SUBTYPE) == AuctionSubtypes.INFORMATION_AUCTION){

            String description = parameters[parameters.length - 1];

            auctionSubtypeObjectList[0] = description;

            Agent.logger.info("--------------------");
            Agent.logger.info("Specific parameters");
            Agent.logger.info("Description: " + description);
            Agent.logger.info("--------------------");

        }

        AuctionSubtype auctionSubtype =  AuctionSubtypeFactory.createAuctionSubtype((AuctionSubtypes) properties.get(AuctionProperties.AUCTION_SUBTYPE), auctionSubtypeObjectList);

        Object[] auctionObjectList = new Object[5];

        Long firstTimeParameter = Long.parseLong(parameters[12]);
        Long secondTimeParameter = Long.parseLong(parameters[13]);

        auctionObjectList[0] = auctionSubtype;
        auctionObjectList[1] = firstTimeParameter;
        auctionObjectList[2] = secondTimeParameter;

        // for Dutch auction add extra parameter for start price, calculating as maximum utility that buyer can get
        // with attribute for device number that is 10 and device quality that is 5
        if(properties.get(AuctionProperties.AUCTION_TYPE) == AuctionTypes.DUTCH_AUCTION){

            Interpreter interpreter = new Interpreter();

            int deviceNumber = 10;
            int deviceQuality = 5;

            double deviceFunction = 0;
            double qualityFunction = 0;


            try {
                // calculating utility for device number attribute
                interpreter.set("deviceNumber", deviceNumber);
                interpreter.set("deviceRestriction", properties.get(AuctionProperties.DEVICE_NUM_RESTRICTION));
                deviceFunction = (double)interpreter.eval(properties.get(AuctionProperties.DEVICE_NUM_FUNCTION).toString());

                // calculate utility for device quality attribute
                interpreter.set("deviceQuality",  deviceQuality);
                interpreter.set("qualityRestriction", properties.get(AuctionProperties.QUALITY_RESTRICTION));
                qualityFunction = (double)interpreter.eval(properties.get(AuctionProperties.QUALITY_FUNCTION).toString());
            } catch (EvalError evalError) { evalError.printStackTrace(); }


            double startOffer = deviceFunction + qualityFunction * deviceNumber;

            auctionObjectList[3] = startOffer;

            auctionObjectList[4] = Double.parseDouble(parameters[14]);

        }


        BuyerAuction auction = AuctionFactory.createAuction((AuctionTypes) properties.get(AuctionProperties.AUCTION_TYPE),  auctionObjectList);


        AuctionProtocol auctionProtocol = new BasicAuctionProtocol(properties,auctionSubtype, auction);

        AgentBuyer agentBuyer = new AgentBuyer(auctionProtocol, properties.get(AuctionProperties.TOPIC).toString());

        return agentBuyer;

    }





}
