package labelWindow;

import com.intellij.openapi.editor.LogicalPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ViewNode {
    final ArrayList<String> missingLabelClasses = new ArrayList<>(Arrays.asList("Image","FloatingActionButton"));
    protected final String className;
    protected HashMap<String, NodeAttribute> attributes;
    protected final ViewNode parent;
    protected LogicalPosition posInDoc;

    public static String EMPTY_LABEL_STRING = "?";

    public ViewNode(String classArg, ViewNode parentArg, int line, int column){
        className = classArg;
        attributes = new HashMap<>();
        parent = parentArg;
        posInDoc = new LogicalPosition(line,column);
    }

    public void addAttribute(String atrName, String atrValue, int startRow, int startColumn, int endRow, int endColumn){
        attributes.put(atrName, new NodeAttribute(atrName, atrValue,
                startRow, startColumn, endRow, endColumn));
    }

    public boolean isMissingLabelTestClass(){
        for( String name : missingLabelClasses){
            if (className.contains(name)){
                return true;
            }
        }

        return false;
    }

    public String getLabelValue(){
        NodeAttribute labelAttribute = getLabelAttribute();
        if (labelAttribute != null){
            return labelAttribute.value;
        }
        return EMPTY_LABEL_STRING;
    }

    public String getLabelAttributeName(){
        NodeAttribute labelAttribute = getLabelAttribute();
        if (labelAttribute != null){
            return labelAttribute.name;
        }
        // TODO: make this more robust given class type
        return "android:contentDescription";
    }

    private NodeAttribute getLabelAttribute(){
        if (attributes.containsKey("android:text")){
            return attributes.get("android:text");
        }
        if (attributes.containsKey("android:contentDescription")){
            return attributes.get("android:contentDescription");
        }
        return null;
    }

    public LogicalPosition getLabelStartPosition() {
        NodeAttribute labelAttribute = getLabelAttribute();
        if (labelAttribute != null){
            return labelAttribute.startPos;
        }
        return this.posInDoc;
    }

    public LogicalPosition getLabelEndPosition() {
        NodeAttribute labelAttribute = getLabelAttribute();
        if (labelAttribute != null){
            return labelAttribute.endPos;
        }
        return new LogicalPosition(this.posInDoc.line, this.posInDoc.column+className.length());
    }

    public class NodeAttribute{
        public final String name;
        String value;

        //LogicalPosition in the document where the attribute name begins
        LogicalPosition startPos;
        //LogicalPosition in the document where the attribute value ends
        LogicalPosition endPos;

        public NodeAttribute(String nameArg, String valueArg, int startRow, int startColumn, int endRow, int endColumn){
            name = nameArg;
            value = valueArg;
            startPos = new LogicalPosition(startRow, startColumn);
            endPos = new LogicalPosition(endRow,endColumn);
        }

    }
}
