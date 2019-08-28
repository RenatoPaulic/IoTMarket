package program;

import agentcontrol.AuctionAgent;
import agents.AgentBuyer;
import agents.AgentHelper;
import agents.AgentSeller;
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

   public static void main(String args[]) {

        zookeeperServer = args[1];
        kafkaServer = args[2];

       AuctionAgent auctionAgent = null;


        switch (args[0]){

            case "agent_buyer_run":

                if(Validation.validateBuyerParameters(args)) {

                    // set up buyer agent - default parameters, auction parameters and specific parameters and start auction
                    auctionAgent = AgentBuyer.setUpBuyerAgent(args);


                }

                break;


            case "agent_seller_run":

                if(Validation.validateSellerParameters(args)){


                    // set up seller agent and start listening for auctions
                    auctionAgent = AgentSeller.setUpSellerAgent(args);

                }


                break;


            case "agent_helper_run":

                if(Validation.validateHelperParameters(args)){

                    // set up seller agent and  start listening for auctions
                    auctionAgent = AgentHelper.setUpHelperAgent(args);


                }

                break;


        }


       System.setProperty("current_date", new SimpleDateFormat("dd-MM-yyyy hh.mm.ss").format(new Date()));

       ClassLoader loader = Thread.currentThread().getContextClassLoader();
       URL url = loader.getResource("log4j.properties");
       PropertyConfigurator.configure(url);


       if(auctionAgent != null)  auctionAgent.start();



    }





}
