import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;

//TODO: IF files for Presdient -> Yes or No

public class main {
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
    //System.out.println(respcon);
    /*int a = content.indexOf("Will the Senate break legislative filibuster with less than 3/5 support in 2022?");
    int b = content.indexOf("bestBuyNoCost", a);
    System.out.println(a);
    System.out.println(b);
    System.out.println(content.substring(b+15, b+20));*/

    getEvents(responseContent.toString());
    String[] allEvents = ListEvents();
    String selectedEvent = selectEvent(allEvents);
    int eventPosition = getPosition(selectedEvent, allEvents);
    String possibilities = getPossibilities(selectedEvent, allEvents, eventPosition, responseContent.toString());
    String[] allPosibilities = listPossibilities(possibilities);
    System.out.println(allPosibilities[0]);
    String selectedPossibility = selectPossibilities(allPosibilities);
    System.out.println(responseContent);
    
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

   /*  static void processData(ArrayList<String> content){
        for(int i = 0; i<content.size(); i++){
            if(content.get(i).equals("url")){
                i+=3;
                while(! content.get(i).equals("url")){
                    tmp.add(content.get(i));
                }
                zd.add(new ArrayList<String>().add(tmp));
                tmp.clear();
            }
        }
   }*/


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
               //System.out.println(events);
               events.append('|');
            }
            /*else if(content.substring(i,i).equals("?")){
                //System.out.println(" ");
            }*/
        }
    }

}

    static String[] ListEvents(){
        int counter = 0;
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
        //System.out.println(choices[7]);
        return choices;
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
    int ct = 0;
    int start = content.toString().indexOf(allEvents[eventPosition]);
    int end = content.toString().indexOf(allEvents[eventPosition+1]);
    for(int i = start; i<=end; i++){
        if(content.substring(i, i+9).equals("shortname") || content.substring(i, i+9).equals("Shortname") || content.substring(i, i+9).equals("shortName") || content.substring(i, i+9).equals("ShortName")){
            i+=13;
               ct=13;
               while(content.charAt(i) != '"'){
                strbb.append(content.charAt(i));
                i++;
                ct++;
               }
               //System.out.println(events);
               strbb.append('|');
            }
        }
        if(strbb.toString().equals("")){
            return "Yes or No";
        }
        else{
            if(findI(strbb.toString()) != 0){
            strbb.delete(findI(strbb.toString()), strbb.indexOf("?")+1);
            }
            return strbb.toString();
        }
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
    if(possibilities.equals("Yes or No")){
        String[] choices = new String[2];
        choices[0] = "Yes";
        choices[1] = "No";
        return choices;
    }
    else{
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
}
    static String selectPossibilities(String[] allPosibilities){
        return (String) JOptionPane.showInputDialog(null, "Select the possibility you would like to analyze", "Selection of Event", JOptionPane.QUESTION_MESSAGE, null, allPosibilities, allPosibilities[0]);
    }
}
