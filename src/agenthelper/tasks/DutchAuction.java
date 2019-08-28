package agenthelper.tasks;

import agenthelper.auctionsubtype.AuctionSubtype;
import agenthelper.helper.HelperSensorSchema;
import agents.AgentHelper;
import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;

import java.util.List;

/**
 * Class witch implement Dutch auction communication protocol
 * for helper side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class DutchAuction extends HelperAuction {

    private long waitTime;

    private Double wantedOffer;

    public DutchAuction(String buyerUuid, String sellerUuid, AuctionSubtype auctionSubtype, long waitTime){

        super(buyerUuid,sellerUuid, auctionSubtype);

        this.waitTime = waitTime;

    }


    @Override
    public String getAuctionOffer(String value) {

        Double highestUtility = Double.parseDouble(value);
        Double offer = auctionSubtype.getOffer(highestUtility);

        return offer.toString();

    }



    @Override
    public void onStart() {

        Agent.logger.info("HelperAuction on start");

        // get offer that will seller wait to accept
        wantedOffer = Double.parseDouble(getAuctionOffer("0"));

        System.out.println("Wanted offer " + wantedOffer );

    }

    @Override
    public void onEnd() {

        Agent.logger.info("HelperAuction on end");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {


        System.out.println("Here received");

        // if auction_round message - extract offer and if it is acceptable, accept it
        if(auctionMessage.getHeader().equals("auction_round")){

            Double winningOffer = Double.parseDouble(auctionMessage.getValueForContext("winning_offer"));

            String mess;

            if(wantedOffer >= winningOffer){

                mess = new MessageBuilder()
                        .addMark("S")
                        .addHeader("round_response")
                        .addSender(getSellerUUID())
                        .addContexts("proposed_value")
                        .addValuesForContexts("Accept")
                        .build()
                        .toString();

                MessageProducer.getInstance().sendMessage(getBuyerUUID(),mess);

            }




        }

        // if auction_end message - see if winner
        if(auctionMessage.getHeader().equals("auction_end")){


            if(auctionMessage.getValue().equals(getSellerUUID())) {

                Agent.logger.info( " AUCTION WIN ");
                System.out.println(" AUCTION WIN ");

                MessageBuilder messageBuilder = new MessageBuilder()
                        .addMark("H")
                        .addHeader("auction_result")
                        .addSender(getSellerUUID())
                        .addContexts("result")
                        .addValuesForContexts("win");


                Agent.logger.info("Task " + " HelperAuction Result Task " + " AUCTION WIN");


                List<HelperSensorSchema> sensorSchemaList =  getAuctionSubtype().getTmpOfferData();


                messageBuilder.addSubcontexts("winning_data", "seller_uuid", "sensor_id");

                for (HelperSensorSchema data : sensorSchemaList) {

                    HelperSensorSchema helperSensorSchema =  data;

                    messageBuilder.addValuesForSubcontexts("winning_data", helperSensorSchema.getDatacenterUUID() , helperSensorSchema.getSensorId());

                }



                MessageProducer.getInstance().sendMessage("H-" + getBuyerUUID(), messageBuilder.build().toString());

                roundTask.done(true);
            }else{

                Agent.logger.info( " AUCTION LOSE ");
                System.out.println(" AUCTION LOSE ");

                String mess = new MessageBuilder().addMark("H")
                        .addHeader("auction_result")
                        .addSender(getSellerUUID())
                        .addContexts("result")
                        .addValuesForContexts("lose")
                        .build()
                        .toString();

                MessageProducer.getInstance().sendMessage("H-" + getBuyerUUID(), mess);

                roundTask.done(false);
            }



        }

    }


}
