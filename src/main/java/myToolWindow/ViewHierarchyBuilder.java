package myToolWindow;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/** "Static" class that parses an Android XML interface layout file into @link{ViewNode}*/
public class ViewHierarchyBuilder {

    private ViewHierarchyBuilder(){}


    public static void parseXMLContinuous(String xmlString, ArrayList<ViewNode> nodes) {
        Stack<ViewNode> parentHierarchy = new Stack<>();
        int rowCounter = 0;
        int columnCounter = 0;

        for(int index = 0; index<xmlString.length(); index++){
            char token = xmlString.charAt(index);
            // beginning of node
            if(token=='<' && isClassNameOpenTag(xmlString.charAt(index+1))){
                index++;
                token = xmlString.charAt(index);
                columnCounter++;
                //get view class name
                String className = "";
                while (token!=' '
                        && token!='\n'){
                    className += xmlString.charAt(index);
                    index++;
                    token = xmlString.charAt(index);
                }

                ViewNode parentNode = null;
                if (!parentHierarchy.isEmpty()){
                    parentNode = parentHierarchy.peek();
                }
                ViewNode node = new ViewNode(className, parentNode, rowCounter ,columnCounter);

                String rawNodeString = "";
                while(token!='>'){
                    rawNodeString+=token;

                    index++;
                    token = xmlString.charAt(index);
                    columnCounter++;
                }
                ArrayList<String> nodeString = new ArrayList<>(Arrays.asList(rawNodeString.split("\n")));

                boolean elementClosed = parseNodeAttributes(nodeString, node);
                nodes.add(node);
                if (!elementClosed) {
                    parentHierarchy.push(node);
                }

            } // end of node
            else if (token=='/' && !parentHierarchy.isEmpty()){
                parentHierarchy.pop();
            }


            if (token=='\n'){
                rowCounter++;
                columnCounter=0;
            }
        }

    }

    public static void parseXML(String xmlString, ArrayList<ViewNode> nodes){

        String[] array = xmlString.split("<");

        Stack<ViewNode> parentHierarchy = new Stack<>();
        int newLineCounter = 0;
        int columnCounter = 0;
        for (int i =0; i<array.length; i++){

            ArrayList<String> nodeString = new ArrayList(Arrays.asList(array[i].split("\n")));
            // don't want to regex on ' ' before = because want to eventually be robust to more white space
            String className =nodeString.get(0).split(" ")[0];
            if(isClassNameOpenTag(className)) {

                ViewNode parentNode = null;
                if (parentHierarchy.size() >0){
                    parentNode = parentHierarchy.peek();
                }
                ViewNode node = new ViewNode(className, parentNode, newLineCounter ,columnCounter);

                boolean elementClosed = parseNodeAttributes(nodeString, node);

                nodes.add(node);
                if (!elementClosed) {
                    parentHierarchy.push(node);
                }
            }

            if (className.contains("/")){
                parentHierarchy.pop();
            }

            // to track logical
            newLineCounter += StringUtils.countMatches(array[i], "\n");
            // because of how we split the string, the white spaces for this will be caught in the
            // tail of the previous element's array
            //since we already removed '<', this will is the location directly before the '<'
            //!!!need to fix since the nodeString doesn't gauntee order'
            int iterator = array[i].length()-1;
            columnCounter=0;
            while(iterator >= 0 &&
                    array[i].charAt(iterator)== ' '){
                iterator -= 1;
                columnCounter += 1;
            }

            //columnCounter = StringUtils.countMatches(nodeString.get(nodeString.size()-1)," ");
        }
    }

    static boolean parseNodeAttributes(ArrayList<String> nodeString, ViewNode node){
        // since sometimes an attribute is on the same line as the class name //
       /* ArrayList<String> e = new ArrayList(Arrays.asList(nodeString.get(0).split(" ")));
        for (int k = 1; k < e.size(); k++) {
            nodeString.add(e.get(k));
        }*/
        // element 0 is the class name
        boolean elementClosed = false;
        for (int j = 0; j < nodeString.size(); j++) {
            // parsing of the attributes in case there is more than a single key value pair on a line
            // must check if the closing tag is on the same line.
            // must remove extrenious white space. Probably not robust
            String[] rawContent = nodeString.get(j).split("=| ");
            ArrayList<String> content = new ArrayList<>();
            for (String s : rawContent){
                content.add(s.trim());
            }
            content.removeIf(s->(s.length()==0));
            // attributes come in key, value pairs. if it's odd, there's probably a closing tag
            int end = content.size();
            if (content.size()%2 != 0) {
                end = content.size() -1;
            }
            if (content.size() >=2){
                for(int attIdx = 0; attIdx < end-1; attIdx+=2){
                    node.attributes.put(content.get(attIdx).trim(), content.get(attIdx+1).trim());
                }
            }
            if (content.size()%2 != 0 ){
                if(content.get(content.size()-1).contains("/")) {
                    elementClosed = true;
                    break;
                }
            }

        }
        return elementClosed;
    }

    static boolean isClassNameOpenTag(String className){
        return !(className.contains("/")|| className.contains("?")
                || className.contains("!") || className.contains(">")) && className.length() > 0;
    }

    static boolean isClassNameOpenTag(char nextToken){
        return nextToken!='/' && nextToken!='?' && nextToken!='!';
    }
}
