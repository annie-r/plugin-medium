package myToolWindow;

import java.util.HashMap;

public class ViewNode {
    protected final String className;
    protected HashMap<String, String> attributes;
    protected final ViewNode parent;

    public ViewNode(String classArg, ViewNode parentArg){
        className = classArg;
        attributes = new HashMap<>();
        parent = parentArg;
    }



}
