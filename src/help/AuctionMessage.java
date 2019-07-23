package help;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent message
 * used for agent communication
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class AuctionMessage {

    private String messageMark;
    private String header;
    private String sender;
    private String context;
    private String value;
    private String topic;

    public AuctionMessage(String message, String topic){

        decomposeMessage(message);
        this.topic = topic;
    }


    public AuctionMessage(String message){

        decomposeMessage(message);
    }


    /**
     * Method that decompose message in string into AuctionMessage default parts
     * @param  message  message in string
     */
    private void decomposeMessage(String message){

        String messageParameters[] = message.split(";");

        messageMark = messageParameters[0];
        sender = messageParameters[2];
        header = messageParameters[1];
        context = messageParameters[3];
        value = messageParameters[4];

    }


    /**
     * @param  contextName  context name for wanted values
     * @return  value corresponding to given context
     */
    public String getValueForContext(String contextName) {

        String[] contexts = getContext().split(":");
        String[] values = getValue().split(":");

        for(int i = 0 ; i < contexts.length ; i++){

            if(contexts[i].equals(contextName)) {

                return values[i];

            }

        }

        return null;

    }


    /**
     * @param  contextName  context name
     * @param  subContextName subcontext name for wanted value
     * @return   value corresponding to given subcontext (under given context)
     *
     */
    public  List<String> getValuesForSubcontext(String contextName, String subContextName) {

        String[] contexts = getContext().split(":");
        String[] values = getValue().split(":");

        for(int i = 0 ; i < contexts.length ; i++) {

            if (contexts[i].contains("=")) {

                String context = contexts[i].split("=")[0];

                if (context.equals(contextName)) {

                    Integer counter = Integer.parseInt(StringUtils.substringBetween(contexts[i], "{", "}"));

                    String subcontextNames[] = StringUtils.remove(contexts[i].split("=")[1], "{" + counter.toString() + "}").split("\\|");
                    String subcontextValues[] = values[i].split("!");

                    for(int r = 0; r < subcontextNames.length ; r ++) {

                        if(subcontextNames[r].equals(subContextName)){

                        List<String> valueList = new ArrayList<>();

                        for (int j = 0; j < counter; j++) {

                            String subcontextValue[] = subcontextValues[j].split("\\|");

                            for (int k = 0; k < subcontextValue.length; k++)

                                if(k == r)

                                  valueList.add(subcontextValue[k]);

                        }

                        return valueList;
                    }


                  }
                }
            }
        }

        return null;
    }



    /**
     * @param  contextName  context name
     * @return list of subcontexts corresponding to given context
     *
     */
    public List<String> getAllSubcontextsForContext(String contextName) {

        List<String> contextList = new ArrayList<>();

        String[] contexts = getContext().split(":");
        String[] values = getValue().split(":");

        for(int i = 0 ; i < contexts.length ; i++) {

            if (contexts[i].contains("=")) {

                String context = contexts[i].split("=")[0];
                String[] subcontext = contexts[i].split("=")[1].split("\\|");
                String[] value = values[i].split("\\|");

                if (context.equals(contextName)) {

                    StringUtils.remove(subcontext[subcontext.length - 1],StringUtils.substringBetween(subcontext[subcontext.length - 1], "{", "}"));

                    subcontext[subcontext.length - 1] = subcontext[subcontext.length - 1 ].substring(0,subcontext[subcontext.length - 1].length() - 3);

                    for (int j = 0; j < subcontext.length; j++) {

                        contextList.add(subcontext[j]);

                    }


                    break;
                }

            }
        }

        return contextList;



    }


    /**
     * @param  contextName  context name
     * @return  list of values corresponding to subcontexts for given context
     *
     */
    public  List<List<String>> getAllValuesForContext(String contextName) {

        List<List<String>> allValues = new ArrayList<>();

        String[] contexts = getContext().split(":");
        String[] values = getValue().split(":");

        for(int i = 0 ; i < contexts.length ; i++){

            if(contexts[i].contains("=")){

                String context = contexts[i].split("=")[0];
                String[] subcontext = contexts[i].split("=")[1].split("\\|");


                if(context.equals(contextName)) {

                    if (contexts[i].contains("{") && contexts[i].contains("}")) {

                        Integer counter = Integer.parseInt(StringUtils.substringBetween(contexts[i], "{", "}"));

                        String subcontextValues[] = values[i].split("!");

                        for (int j = 0; j < counter; j++) {

                            List<String> valueList = new ArrayList<>();
                            String subcontextValue[] = subcontextValues[j].split("\\|");

                            for (int k = 0; k < subcontextValue.length; k++) {
                                valueList.add(subcontextValue[k]);

                            }
                            allValues.add(valueList);
                        }


                    } else {

                        List<String> valueList = new ArrayList<>();

                        for (int j = 0; j < subcontext.length; j++) {
                            valueList.add(values[j]);
                        }
                        allValues.add(valueList);
                    }
                }
            }else {

                if(contexts[i].equals(contextName)) {

                    List<String> valueList = new ArrayList<>();
                    valueList.add(values[i]);
                    allValues.add(valueList);


                }
            }

        }

        return allValues;
    }


    /**
     * Method witch print all message parameters in hierarchy order
     */
    public  void listAllReceivedParameters(){

        String[] contexts = getContext().split(":");
        String[] values = getValue().split(":");

        System.out.println(getContext());
        System.out.println(getValue());

        for(int i = 0 ; i < contexts.length ; i++){

            if(contexts[i].contains("=")){

                String context = contexts[i].split("=")[0];
                String[] subcontext = contexts[i].split("=")[1].split("\\|");
                String[] value = values[i].split("\\|");

                System.out.println("Head Context : " + context);

                if(contexts[i].contains("{") && contexts[i].contains("}")){

                    Integer counter = Integer.parseInt(StringUtils.substringBetween(contexts[i], "{", "}"));


                    String subcontextNames[] = StringUtils.remove(contexts[i].split("=")[1], "{" + counter.toString() + "}").split("\\|");

                    String subcontextValues[] = values[i].split("!");

                    for(int j = 0 ; j < counter ; j ++){

                        String subcontextValue[] = subcontextValues[j].split("\\|");

                        for( int k = 0 ; k < subcontextValue.length ; k ++)

                            System.out.println("SubContext, value : " + subcontextNames[k] + " , " + subcontextValue[k]);
                    }



                }else {
                    for (int j = 0; j < subcontext.length; j++) {
                        System.out.println("SubContext, value : " + subcontext[j] + " , " + value[j]);
                    }
                }
            }else {
                System.out.println("Context, value : " + contexts[i] + " , " + values[i]);
            }

        }



    }





    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getMessageMark() { return messageMark; }
    public void setMessageMark(String messageMark) { this.messageMark = messageMark; }

    @Override
    public String toString(){

        return getMessageMark() + ";" + getHeader() + ";" + getSender() + ";" + getContext() + ";" + getValue();
    }

}

