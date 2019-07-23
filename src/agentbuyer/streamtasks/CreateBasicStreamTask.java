package agentbuyer.streamtasks;

import agents.AgentBuyer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import program.Agent;
import taskcontrol.basictasks.ITask;
import taskcontrol.executors.TaskExecutor;

import java.util.Properties;

/**
 * Task that creates basic kafka stream without any transformations
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class CreateBasicStreamTask implements ITask {

    private KafkaStreams stream;
    private TaskExecutor taskExecutor;

    public CreateBasicStreamTask(TaskExecutor taskExecutor, String topic){

        this.taskExecutor = taskExecutor;

        AgentBuyer.logger.info("Creating task: " + "Create Stream Task");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Agent");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Agent.kafkaServer);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        String inputStream = "input-" + topic;
        String outputStream = "output-" + topic;

        StreamsBuilder builder = new StreamsBuilder();

        builder.<String, String>stream(inputStream).mapValues(value -> value).to(outputStream);

        stream = new KafkaStreams(builder.build(), props);


    }

    @Override
    public void execute() {

        AgentBuyer.logger.info("Task: " + " Create Stream Task "  + "starting stream ");

        stream.start();
        taskExecutor.notifyTaskResult(true);

    }

}

