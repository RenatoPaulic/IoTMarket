package help;

import java.util.*;


/**
 * AuctionMessage builder class
 * @author  Renato Paulić
 * @version 1.0
 * @since   16.6.2019
 */
public class MessageBuilder {

    private String mark = "";
    private String header = "";
    private String sender = "";

    private List<String> contexts;
    private List<String> values;

    private Map<String, Integer> counterMap;

    public MessageBuilder(){

        contexts = new ArrayList<>();
        values = new ArrayList<>();
        counterMap = new HashMap<>();
    }


    public MessageBuilder addMark(String mark)  {this.mark = mark; return this;}
    public MessageBuilder addHeader(String header) { this.header = header; return this; }
    public MessageBuilder addSender(String sender) { this.sender = sender; return this; }


    /**
     * Method witch add context to message
     * @param context context name
     * @return instance of class
     */
    public MessageBuilder addContexts(String... context){

        contexts.addAll(Arrays.asList(context));


        return this;

    }

    /**
     * Method witch add values for contexts
     * @param value value for context
     * @return instance of class
     */
    public MessageBuilder addValuesForContexts(String... value){

        values.addAll(Arrays.asList(value));

        return this;

    }

    /**
     * Method witch add subcontexts for context
     * @param context context name
     * @param subcontext subcontexts for context
     * @return instance of class
     */
    public MessageBuilder addSubcontexts(String context, String... subcontext){

        String subcontexts = "";

        for(String con : subcontext){

           subcontexts += con + "|";
        }

        contexts.add(context + "=" + subcontexts.substring(0,subcontexts.length() - 1 ));
        return this;

    }

    /**
     * Method witch add values for subcontext
     * @param context context name
     * @param value values for subcontexts
     * @return instance of class
     */
    public MessageBuilder addValuesForSubcontexts(String context, String... value){

        String subcontexts = "";

        for(String con : value){

            subcontexts += con + "|";
        }

        subcontexts = subcontexts.substring(0, subcontexts.length() - 1);

        values.add(subcontexts);

        if(counterMap.get(context) == null){

            counterMap.put(context,1);

        }else {

             counterMap.replace(context, counterMap.get(context), counterMap.get(context) + 1);
        }

        return this;

    }

    /**
     * Method witch builds auction message based on parameters defined in class
     * @return  AuctionMessage which corresponds to parameters defined in class
     */
    public AuctionMessage build() {

        String mess = mark + ";" + header + ";" + sender + ";";

        String context = "";
        String value = "";

        for(int i  = 0 ; i < contexts.size() ; i++){

            String con = contexts.get(i);

            String val;

            if(con.contains("=")){

                if(counterMap.get(con.split("=")[0]) != null){

                    val = "";

                    con += "{" + counterMap.get(con.split("=")[0])  + "}";

                    for(int j = i; j < counterMap.get(con.split("=")[0])  + i ; j ++){

                        val += values.get(j) + "!";

                    }

                    val = val.substring(0, val.length()-1);

                }else{

                    val = values.get(i).substring(0, values.get(i).length() - 1);
                }

            }else {
                 val = values.get(i);
            }



                context += con + ":";
                value += val + ":";


        }

        context = context.substring(0, context.length() - 1);
        value = value.substring(0, value.length() - 1);

        mess += context + ";" + value;

        return new AuctionMessage(mess);

    }


}


