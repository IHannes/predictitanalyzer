import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;

public class main{
    static HttpURLConnection connection;
    static BufferedReader reader;
    static String line;
    static ArrayList<String> respcon = new ArrayList<String>();
    static StringBuffer responseContent = new StringBuffer();
    static ArrayList<ArrayList<String>> zd = new ArrayList(); 
    static StringBuilder events = new StringBuilder();
    static StringBuilder possibilities = new StringBuilder();
    static ArrayList<String> tmp = new ArrayList<String>();

public static void main(String[] args) {
    getData();
    getEvents(responseContent.toString());
    String[] allEvents = ListEvents();
    String selectedEvent = selectEvent(allEvents);
    int eventPosition = getPosition(selectedEvent, allEvents);
    String possibilities = getPossibilities(selectedEvent, allEvents, eventPosition, responseContent.toString());
    //System.out.println(possibilities);
    String selectedPossibility = "";
    if(!possibilities.equals("Yes or No")){
    String[] allPosibilities = listPossibilities(possibilities);
    selectedPossibility = selectPossibilities(allPosibilities);
    }
    getPrices(selectedPossibility, selectedEvent);
    //System.out.println(responseContent);
    
}
    static String getData(){
        try {
            URL url = new URL("https://www.predictit.org/api/marketdata/all/");
            connection =(HttpURLConnection) url.openConnection();
    
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2400);
            connection.setReadTimeout(2400);
    
            int status = connection.getResponseCode();
            System.out.println(status);
    
            if (status> 299){
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((line = reader.readLine()) != null){
                    //respcon.add(line);
                    responseContent.append(line);
                    responseContent.append("\n");
                }
                reader.close();
            }
            else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null){
                    //respcon.add(line);
                    responseContent.append(line);
                }
                reader.close();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    
        return responseContent.toString();
    }

    static void getEvents(String content){
        int counter = 0;
        for(int i = 0; i<=content.length(); i++){
            if(i+10 < content.length()){
            if((content.substring(i, i+9).equals("ShortName") || content.substring(i, i+9).equals("shortName") || content.substring(i, i+9).equals("Shortname") || content.substring(i, i+9).equals("shortname")) && content.substring(i+9, i+180).contains("?")){
               i+=13;
               counter=13;
               while(content.charAt(i) != '"'){
                events.append(content.charAt(i));
                i++;
                counter++;
               }
               events.append('|');
            }
        }
    }
}

    static String[] ListEvents(){
        int counter = 0;
        int counter1 = 0;
        int counter2 = 0;
        String[] choices = new String[(int) (events.length() - events.toString().replace("?", "").length())];
        StringBuilder strb = new StringBuilder();
        for(int i = 0; i<events.length(); i++){
            if(events.charAt(i)!='|'){
                strb.append(events.charAt(i));
            }
            else{
                choices[counter] = strb.toString();
                counter++;
                strb.setLength(0);
            }
        }
        for(int a = 0; a<choices.length; a++){
          if(a+1<choices.length){
            if(choices[a].equals(choices[a+1])){
            choices[a] = "null";
          counter1++;}
          }
        }
        String returnChoices[] = new String[choices.length-counter1];
        for(int b = 0; b<choices.length; b++){
          if(!choices[b].equals("null")){
            returnChoices[counter2] = choices[b];
            counter2++;
          }
        }
        return returnChoices;
    }

    static String selectEvent(String[] choices){
        return (String) JOptionPane.showInputDialog(null, "Select the event you would like to analyze", "Selection of Event", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
    }

    static int getPosition(String item, String[] array){
        for(int i = 0; i<=array.length; i++){
          if(array[i].equals(item)){
                return i;
            }
        }
        return 0;
    }

   static String getPossibilities(String selectedEvent, String[] allEvents, int eventPosition, String content){
    StringBuilder strbb = new StringBuilder();
    int start = content.toString().indexOf(allEvents[eventPosition]);
    int end = content.toString().indexOf(allEvents[eventPosition+1]);
    for(int i = start; i<=end; i++){if(i+9<content.length()){
        if(content.substring(i, i+9).equals("shortname") || content.substring(i, i+9).equals("Shortname") || content.substring(i, i+9).equals("shortName") || content.substring(i, i+9).equals("ShortName")){
            i+=13;
               
               while(content.charAt(i) != '"'){
                strbb.append(content.charAt(i));
                i++;
               }
               strbb.append('|');
            }}
        }
        if((strbb.toString().equals("") || (strbb.toString().contains("files for") && !strbb.toString().contains("Democratic")))){
          return "Yes or No";
        }
        
            if(findI(strbb.toString()) != 0){
            strbb.delete(findI(strbb.toString()), strbb.indexOf("?")+1);
            }
            return strbb.toString();
        }
    

   static int findI(String str){
    ArrayList<Integer> tmp = new ArrayList<Integer>();
    for(int i = 0; i<str.length(); i++){
        if(str.charAt(i) == '|'){
            tmp.add((Integer) i);
        }
    }
    try {
        return tmp.get(tmp.size()-2);
    } catch (Exception err) {
    System.err.println(err);
    return 0;
    }
}


   static int numberOfPossibilities(String possibilities){
    int count = 0;
    for(int i = 0; i<possibilities.length(); i++){
        if(possibilities.charAt(i) == '|'){
            count++;
        }
    }
    return count;
   }
   static String[] listPossibilities(String possibilities){
    String[] choices = new String[numberOfPossibilities(possibilities)];
    int counter = 0;
    StringBuilder strb = new StringBuilder();
    for(int i = 0; i<possibilities.length(); i++){
        if(possibilities.charAt(i)!='|'){
            strb.append(possibilities.charAt(i));
        }
        else{
            choices[counter] = strb.toString();
            counter++;
            strb.setLength(0);
        }
     
    }
    return choices;
}
    static String selectPossibilities(String[] allPosibilities){
        return (String) JOptionPane.showInputDialog(null, "Select the possibility you would like to analyze", "Selection of Event", JOptionPane.QUESTION_MESSAGE, null, allPosibilities, allPosibilities[0]);
    }
      
    static void getPrices(String selectedPossibility, String selectedEvent){
      int startOneIndex =  responseContent.toString().indexOf(selectedEvent);
      int yesBuyIndex = 0;
      int noBuyIndex = 0;
      int yesSellIndex = 0;
      int noSellIndex = 0;
      int isOpenIndex = 0;
      if (!selectedPossibility.equals("")) {
        int startTwoIndex = responseContent.toString().indexOf(selectedPossibility, startOneIndex);
        yesBuyIndex = responseContent.toString().indexOf("bestBuyYesCost", startTwoIndex);
        noBuyIndex = responseContent.toString().indexOf("bestBuyNoCost", startTwoIndex);
        yesSellIndex = responseContent.toString().indexOf("bestSellYesCost", startTwoIndex);
        noSellIndex = responseContent.toString().indexOf("bestSellNoCost", startTwoIndex);
        isOpenIndex = responseContent.toString().indexOf("status", startTwoIndex);
      } else {
        yesBuyIndex = responseContent.toString().indexOf("bestBuyYesCost", startOneIndex);
        noBuyIndex = responseContent.toString().indexOf("bestBuyNoCost", startOneIndex);
        yesSellIndex = responseContent.toString().indexOf("bestSellYesCost", startOneIndex);
        noSellIndex = responseContent.toString().indexOf("bestSellNoCost", startOneIndex);
        isOpenIndex = responseContent.toString().indexOf("status", startOneIndex);
      }
      String status =  responseContent.substring(isOpenIndex+8, isOpenIndex+14);
      String bestBuyYesCost = responseContent.substring(yesBuyIndex+15, yesBuyIndex+22);
      String bestBuyNoCost =  responseContent.substring(noBuyIndex+15, noBuyIndex+21); 
      String bestSellYesCost = responseContent.substring(yesSellIndex+15, yesSellIndex+23);
      String bestSellNoCost = responseContent.substring(noSellIndex+15, noSellIndex+22);
      JOptionPane.showMessageDialog(null, "THE BET IS " + status.toUpperCase() + "\n" + "Best Buy Yes Cost is: " + bestBuyYesCost + "\n" + "Best Buy No Cost is: " + bestBuyNoCost + "\n" + "Best sell yes cost is: " + bestSellYesCost + "\n" + "Best sell no cost is:" + bestSellNoCost);
    }
    
}