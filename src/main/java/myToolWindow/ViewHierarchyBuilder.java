package myToolWindow;

import java.util.ArrayList;
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
                int spaceOffset = 0;
                int i =0;
                while(attrName.charAt(i++)==' '){
                    spaceOffset++;
                }

                node.addAttribute(attrName.trim(), attrValue.trim(), startRow, startColumn+spaceOffset-1,
                        prevRow, prevColumn);
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

    static boolean isClassNameOpenTag(String className){
        return !(className.contains("/")|| className.contains("?")
                || className.contains("!") || className.contains(">")) && className.length() > 0;
    }

    static boolean isClassNameOpenTag(char nextToken){
        return nextToken!='/' && nextToken!='?' && nextToken!='!';
    }
}
