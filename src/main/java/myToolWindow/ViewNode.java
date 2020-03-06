package myToolWindow;

import com.intellij.openapi.editor.LogicalPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ViewNode {
    final ArrayList<String> missingLabelClasses = new ArrayList<>(Arrays.asList("Image","FloatingActionButton"));
    protected final String className;
    protected HashMap<String, NodeAttribute> attributes;
    protected final ViewNode parent;
    protected final LogicalPosition posInDoc;
    protected LogicalPosition posOfLabel = null;

    public ViewNode(String classArg, ViewNode parentArg, int line, int column){
        className = classArg;
        attributes = new HashMap<>();
        parent = parentArg;
        posInDoc = new LogicalPosition(line,column);
    }

    public void addAttribute(String atrName, String atrValue, int line, int column){
        attributes.put(atrName, new NodeAttribute(atrName, atrValue, line, column));
    }

    public void setPosOfLabel(int line, int column){

    }

    public boolean isMissingLabelTestClass(){
        for( String name : missingLabelClasses){
            if (className.contains(name)){
                return true;
            }
        }

        return false;
    }

    public String getLabel(){
        if (attributes.containsKey("android:text")){
            return attributes.get("android:text").value;
        }
        if (attributes.containsKey("android:contentDescription")){
            return attributes.get("android:contentDescription").value;
        }
        return "?";
    }

    public class NodeAttribute{
        public final String name;
        String value;
        public LogicalPosition position;

        public NodeAttribute(String nameArg, String valueArg, int row, int column){
            name = nameArg;
            value = valueArg;
            position = new LogicalPosition(row, column);
        }

    }



}
