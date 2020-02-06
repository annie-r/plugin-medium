package myToolWindow;

import java.util.HashMap;

public class ViewNode {
    protected final String className;
    protected HashMap<String, String> attributes;

    public ViewNode(String classArg){
        className = classArg;
        attributes = new HashMap<>();
    }



}
