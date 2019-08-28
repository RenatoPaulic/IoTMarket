package agenthelper.tasks;


import agenthelper.auctionsubtype.AuctionSubtype;
import agenthelper.helper.HelperSensorSchema;

import help.AuctionMessage;
import help.MessageBuilder;
import kafka.MessageProducer;
import program.Agent;

import java.util.List;

/**
 * Class witch implement English auction communication protocol
 * for helper side
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class FirstPriceAuction extends HelperAuction {

    private long waitTime;

    public FirstPriceAuction(String buyerUuid, String sellerUuid, AuctionSubtype auctionSubtype, long waitTime){

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


    }

    @Override
    public void onEnd() {

        Agent.logger.info("HelperAuction on end");


    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {


        // if auction_round message - extract winning offer and place higher bid
        if(auctionMessage.getHeader().equals("auction_round")){


            String winningOffer = auctionMessage.getValueForContext("winning_offer");

            String offer = getAuctionOffer(winningOffer);

            String mess = new MessageBuilder()
                    .addMark("S")
                    .addHeader("round_response")
                    .addSender(getSellerUUID())
                    .addContexts("proposed_value")
                    .addValuesForContexts(offer)
                    .build()
                    .toString();


            MessageProducer.getInstance().sendMessage(getBuyerUUID(),mess);

        }

        // if auction_end message - see if winner
        if(auctionMessage.getHeader().equals("auction_end")){


            Agent.logger.info("Received message "  + " " + "SENDER: " + auctionMessage.getSender() + " " + " HEADER: " + auctionMessage.getHeader()
                    + " " + " CONTEXT: " + auctionMessage.getContext() + " " + " VALUE: " + auctionMessage.getValue() );

            if(auctionMessage.getValue().equals(getSellerUUID())) {

                Agent.logger.info( " AUCTION WINNER ");
                System.out.println( " AUCTION WINNER ");

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

                    HelperSensorSchema helperSensorSchema = (HelperSensorSchema) data;

                    messageBuilder.addValuesForSubcontexts("winning_data", helperSensorSchema.getDatacenterUUID() , helperSensorSchema.getSensorId());

                }



                MessageProducer.getInstance().sendMessage("H-" + getBuyerUUID(), messageBuilder.build().toString());

                roundTask.done(true);

            }else{

                Agent.logger.info( " AUCTION LOSE ");
                System.out.println( " AUCTION LOSE ");

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
