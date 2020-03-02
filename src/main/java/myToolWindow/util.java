package myToolWindow;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Boolean.TRUE;

public class util {
    /*need to figure out multiple things with same class name. map is wrong thing*/
    public static void parseXML(String xmlString, ArrayList<ViewNode> nodes){
        //ArrayList<ViewNode> nodes = new ArrayList<>();

        String[] array = xmlString.split("<");
        //map = new HashMap<>();

        for (int i =0; i<array.length; i++){
            HashMap<String, HashMap<String,String>> eMap = new HashMap<>();
            ArrayList<String> element = new ArrayList(Arrays.asList(array[i].split("\n")));
            String className =element.get(0).split(" ")[0];
            if(!(className.contains("/")|| className.contains("?")
            || className.contains("!") || className.contains(">")) && className.length() > 0) {
                ViewNode node = new ViewNode(className);

                // since sometimes an attribute is on the same line as the class name //
                ArrayList<String> e = new ArrayList(Arrays.asList(element.get(0).split(" ")));
                for (int k = 1; k < e.size(); k++) {
                    element.add(e.get(k));
                }
                // element 0 is the class name

                for (int j = 1; j < element.size(); j++) {
                    String[] content = element.get(j).split("=");
                    if (content.length == 2) {
                        node.attributes.put(content[0].trim(), content[1].trim());
                    }
                }
                nodes.add(node);
            }

        }
    }

    public static boolean isMissingLabelTestClass(ViewNode node){
        if (node.className.contains("Image") || node.className.contains("FloatingActionButton")){
            return true;
        }
        return false;
    }

    public static String getLabel(ViewNode node){
        if (node.attributes.containsKey("android:text")){
          return node.attributes.get("android:text");
        }
        if (node.attributes.containsKey("android:contentDescription")){
            return node.attributes.get("android:contentDescription");
        }
        return "?";
    }
}
