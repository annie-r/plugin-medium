package myToolWindow;

import java.util.ArrayList;
import java.util.HashMap;

public class util {
    public static void parseXML(String xmlString){
        String[] array = xmlString.split("<");
        HashMap<String, HashMap<String,String>> map = new HashMap<>();

        for (int i =0; i<array.length; i++){
            HashMap<String, HashMap<String,String>> eMap = new HashMap<>();
            String[] element = array[i].split("\n");
            // element 0 is the class name
            HashMap<String,String> attributeMap = new HashMap<>();
            for (int j =1; j<element.length; j++){
                String[] content = element[j].split("=");
                if (content.length == 2) {
                    attributeMap.put(content[0], content[1]);
                }
            }
            map.put(element[0], attributeMap);

        }
    }
}
