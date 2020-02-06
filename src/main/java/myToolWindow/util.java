package myToolWindow;

import layoutinspector.model.ViewProperty;
import layoutinspector.parser.ViewPropertyParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class util {
    /*need to figure out multiple things with same class name. map is wrong thing*/
    public static void parseXML(String xmlString, HashMap<String, HashMap<String,String>> map){
        ArrayList<ViewNode> nodes = new ArrayList<>();

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
/**
    public static ViewNode createViewNode(
            ViewNode parent,
            String data
    ){
        int delimIndex = data.indexOf('<');
        //if (delimIndex < 0) {
        //    throw IllegalArgumentException("Invalid format for ViewNode, missing @: $data")
        //}
        String name = data.substring(0, delimIndex);
        data = data.substring(delimIndex + 1);
        delimIndex = data.indexOf(' ');
        String hash = data.substring(0, delimIndex);
        ViewNode node = new ViewNode(parent, name, hash);
        //node.index = if (parent == null) 0 else parent!!.children.size;

        if (data.length() > delimIndex + 1) {
            loadProperties(node, data.substring(delimIndex + 1));
            //node.id = node.getProperty("mID", "id")!!.value
        }
        //node.displayInfo = DisplayInfoFactory.createDisplayInfoFromNode(node)

        return node;
    }

    private static void loadProperties(
            ViewNode node,
            String data
    ) {
        int start = 0;
        boolean stop;

        do {
            int index = data.indexOf('=', start);
            String fullName = data.substring(start, index);

            int index2 = data.indexOf(' ', index + 1);
            int length = Integer.parseInt(data.substring(index + 1, index2));
            start = index2 + 1 + length;


            String value = data.substring(index2 + 1, index2 + 1 + length);
            ViewProperty property = parse(fullName, value);

            //node.properties.add(property)
            //node.namedProperties[property.fullName] = property
            node.addPropertyToGroup(property);


            stop = (start >= data.length());
            if (!stop) {
                start += 1;
            }
        } while (!stop);

        //node.properties.sort()
    }

    private static ViewProperty parse(String propertyFullName, String value){
        int colonIndex = propertyFullName.indexOf(':');
        String category;
        String name;
        if (colonIndex != -1) {
            category = propertyFullName.substring(0, colonIndex);
            name = propertyFullName.substring(colonIndex + 1);
        } else {
            category = null;
            name = propertyFullName;
        }
        return new ViewProperty(propertyFullName, name, category, value);
    }
*/
}
