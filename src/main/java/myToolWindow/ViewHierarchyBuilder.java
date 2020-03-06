package myToolWindow;

import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

class XMLIterator {
    Character token;
    int rowCounter;
    int columnCounter;
    String xmlString;
    int index;

    public XMLIterator(String xmlStringArg){
        this(xmlStringArg, 0,0,0);
    }

    public XMLIterator(String xmlStringArg,int indexArg, int rowCounterArg, int columnCounterArg ){
        rowCounter = rowCounterArg;
        columnCounter = columnCounterArg;
        xmlString = xmlStringArg;
        index = indexArg;
        if (xmlString.length() > index ){
            token = xmlString.charAt(index);
        }
    }

    public void incrementIdx(){
        index++;
        if(index < xmlString.length()) {
            token = xmlString.charAt(index);
            columnCounter++;

            if (token == '\n') {
                rowCounter++;
                columnCounter = 0;
            }
        }
    }

    public char getCharAtIndex(){
        return xmlString.charAt(index);
    }
}

/** "Static" class that parses an Android XML interface layout file into @link{ViewNode}*/
public class ViewHierarchyBuilder {

    private ViewHierarchyBuilder(){}

    public static void parseXMLContinuous(String xmlStringArg, ArrayList<ViewNode> nodes) {
        Stack<ViewNode> parentHierarchy = new Stack<>();

        XMLIterator iter = new XMLIterator(xmlStringArg);

        while(iter.index<iter.xmlString.length()){
            //iter.token = xmlString.charAt(index);
            // beginning of node
            if(iter.token=='<' && isClassNameOpenTag(iter.xmlString.charAt(iter.index+1))){
                int classNameColumn = iter.columnCounter;
                int classNameRow = iter.rowCounter;
                iter.incrementIdx();
                //get view class name
                String className = "";
                while (iter.token!=' '
                        && iter.token!='\n'){
                    className += iter.getCharAtIndex();
                    iter.incrementIdx();
                }

                ViewNode parentNode = null;
                if (!parentHierarchy.isEmpty()){
                    parentNode = parentHierarchy.peek();
                }
                ViewNode node = new ViewNode(className, parentNode, classNameRow ,classNameColumn);

                String rawNodeString = "";

                int attrStartRow = iter.rowCounter;
                int clmnStartRow = iter.columnCounter;

                /*
                while(iter.token!='>'){
                    rawNodeString+=iter.token;

                    iter.incrementIdx();
                }
                ArrayList<String> nodeString = new ArrayList<>(Arrays.asList(rawNodeString.split("\n")));
*/
                boolean elementClosed = parseNodeAttributeContinuous(node, iter);
                nodes.add(node);
                if (!elementClosed) {
                    parentHierarchy.push(node);
                }

            } // end of node
            else if (iter.token=='/' && !parentHierarchy.isEmpty()){
                parentHierarchy.pop();
            }
            iter.incrementIdx();
        }

    }

    private static Character incremIdx(String xmlString, int index, int rowCounter, int columnCounter){
        index++;
        Character token = null;
        if(index < xmlString.length()) {
            token = xmlString.charAt(index);
            columnCounter++;

            if (token == '\n') {
                rowCounter++;
                columnCounter = 0;
            }
        }
        return token;
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
        }
    }


    static boolean parseNodeAttributeContinuous(ViewNode node, XMLIterator iter){
        String attrName = "";
        String attrValue = "";
        int startRow = iter.rowCounter;
        int startColumn = iter.columnCounter;

        int prevRow = iter.rowCounter;
        int prevColumn = iter.columnCounter;
        //to distinguish between '/' in a value and '/' signaling end
        boolean inAttribute = false;
        boolean elementClosed = false;
        while(true) {
            if (iter.token == '=') {
                inAttribute = true;
            } // this doesn't account for string values with spaces in them
            else if ((iter.token == ' ' || iter.token =='\n') && !attrName.isEmpty() && !attrValue.isEmpty()){
                node.addAttribute(attrName.trim(), attrValue.trim(), startRow, startColumn, prevRow, prevColumn);
                attrName = "";
                attrValue = "";
                inAttribute = false;
            }
            else if (iter.token == '/' && !inAttribute) {
                elementClosed = true;
                break;
            } else if (iter.token == '>') {
                break;
            } else if (inAttribute) {
                attrValue += iter.token;
            } else if (iter.token !='\n'){
                if(attrName.isEmpty()){
                    startRow = iter.rowCounter;
                    startColumn = iter.columnCounter;
                }
                attrName += iter.token;
            }
            prevRow = iter.rowCounter;
            prevColumn = iter.columnCounter;
            iter.incrementIdx();
        }
        return elementClosed;
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
                    //node.attributes.put(content.get(attIdx).trim(), content.get(attIdx+1).trim());
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
