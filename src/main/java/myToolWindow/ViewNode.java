package myToolWindow;

import com.intellij.openapi.editor.LogicalPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ViewNode {
    final ArrayList<String> missingLabelClasses = new ArrayList<>(Arrays.asList("Image","FloatingActionButton"));
    protected final String className;
    protected HashMap<String, String> attributes;
    protected final ViewNode parent;
    protected final LogicalPosition posInDoc;
    protected LogicalPosition posOfLabel = null;

    public ViewNode(String classArg, ViewNode parentArg, int line, int column){
        className = classArg;
        attributes = new HashMap<>();
        parent = parentArg;
        posInDoc = new LogicalPosition(line,column);
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
            return attributes.get("android:text");
        }
        if (attributes.containsKey("android:contentDescription")){
            return attributes.get("android:contentDescription");
        }
        return "?";
    }



}
