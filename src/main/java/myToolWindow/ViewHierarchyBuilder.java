package myToolWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/** "Static" class that parses an Android XML interface layout file into @link{ViewNode}*/
public class ViewHierarchyBuilder {

    private ViewHierarchyBuilder(){}

    public static void parseXML(String xmlString, ArrayList<ViewNode> nodes){

        String[] array = xmlString.split("<");

        Stack<ViewNode> parentHierarchy = new Stack<>();

        for (int i =0; i<array.length; i++){

            ArrayList<String> element = new ArrayList(Arrays.asList(array[i].split("\n")));
            String className =element.get(0).split(" ")[0];
            if(!(className.contains("/")|| className.contains("?")
                    || className.contains("!") || className.contains(">")) && className.length() > 0) {

                ViewNode parentNode = null;
                if (parentHierarchy.size() >0){
                    parentNode = parentHierarchy.peek();
                }
                ViewNode node = new ViewNode(className, parentNode);

                // since sometimes an attribute is on the same line as the class name //
                ArrayList<String> e = new ArrayList(Arrays.asList(element.get(0).split(" ")));
                for (int k = 1; k < e.size(); k++) {
                    element.add(e.get(k));
                }
                // element 0 is the class name
                boolean elementClosed = false;
                for (int j = 1; j < element.size(); j++) {

                    String[] rawContent = element.get(j).split("=| ");
                    ArrayList<String> content = new ArrayList<>();
                    for (String s : rawContent){
                        content.add(s.trim());
                    }/*
                    for (int c = 0; c<content1.length;c++){
                        content1[c] = content1[c].trim();
                        String[] contestSplit = content1[c].split(" ");
                        for (int c2 = 0; c2<contestSplit.length; c2++){
                            content.add(contestSplit[c2]);
                        }
                    }*/
                    // attributes come in key, value pairs. if it's odd, there's probably a closing tag
                    int end = content.size();
                    if (content.size()%2 != 0) {
                        end = content.size() -1;
                    }
                    if (content.size() >=2){
                        for(int attIdx = 0; attIdx < end-1; attIdx++){
                            node.attributes.put(content.get(attIdx).trim(), content.get(attIdx).trim());
                        }
                    }
                    if (content.size()%2 != 0 ){
                        if(content.get(content.size()-1).contains("/")) {
                            elementClosed = true;
                            break;
                        }
                    }

                }
                nodes.add(node);
                if (!elementClosed) {
                    parentHierarchy.push(node);
                }
            }

            if (className.contains("/")){
                parentHierarchy.pop();
            }

        }
    }
}
