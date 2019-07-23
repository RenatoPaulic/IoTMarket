package program;

import agents.AgentBuyer;
import agents.AgentHelper;
import agents.AgentSeller;
import kafka.MessageProducer;
import kafka.TopicController;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Main class - validate input parameters and starts agents
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class Agent {

   public static final Logger logger = Logger.getLogger(Class.class.getName());

   public static String kafkaServer;
   public static String zookeeperServer;

    static{
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss");
        System.setProperty("current_date", dateFormat.format(new Date()));

    }


    public static void main(String args[]) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("log4j.properties");
        PropertyConfigurator.configure(url);


        zookeeperServer = args[1];
        kafkaServer = args[2];


        logger.info("------------------------ PROGRAM BUYER START ------------------------");


        switch (args[0]){

            case "agent_buyer_run":

                if(Validation.validateBuyerParameters(args)) {

                    // set up buyer agent - default parameters, auction parameters and specific parameters
                    AgentBuyer agentBuyer = AgentBuyer.setUpBuyerAgent(args);


                    // start auction
                    agentBuyer.start();


                }

                break;


            case "agent_seller_run":

                if(Validation.validateBuyerParameters(args)){

                    // set up seller agent
                    AgentSeller agentSeller = AgentSeller.setUpSellerAgent(args);



                    // start listening for auctions
                    agentSeller.start();

                }


                break;


            case "agent_helper_run":

                if(Validation.validateHelperParameters(args)){

                    // set up seller agent
                    AgentHelper agentHelper = AgentHelper.setUpHelperAgent(args);


                    // start listening for auctions
                    agentHelper.start();

                }

                break;


        }




    }





   public static void end(){

        Agent.logger.info("Agent end");
        System.exit(0);
   }

}
