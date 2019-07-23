package agentbuyer.streamtasks;

import agents.AgentBuyer;
import help.AuctionMessage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Task that process incoming stream printing it on stdout
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class StdoutPrintStreamTask extends AuctionStream {


    public StdoutPrintStreamTask(long activeTime){

        super(activeTime);

        AgentBuyer.logger.info("Creating task " + " Start Stream Task");


    }


    @Override
    public void onStart() {

        AgentBuyer.logger.info("Task " + " Start Stream Task" + " on start ");

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                negTask.done(true);


            }
        }, getActiveTime());


    }

    @Override
    public void onEnd() {

        AgentBuyer.logger.info("Task " + " Start Stream Task" + " on end ");

    }

    @Override
    public void processMessage(AuctionMessage auctionMessage) {

        System.out.println("Received data " + auctionMessage.getValuesForSubcontext("sensor_data", "value"));




    }
}
