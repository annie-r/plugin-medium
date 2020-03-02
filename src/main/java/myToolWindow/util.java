package myToolWindow;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class util {



    public static boolean isMissingLabelTestClass(ViewNode node){
        if (node.className.contains("Image") || node.className.contains("FloatingActionButton")){
            return true;
        }
        return false;
    }

    public static String getLabel(ViewNode node){
        if (node.attributes.containsKey("android:text")){
          return node.attributes.get("android:text");
        }
        if (node.attributes.containsKey("android:contentDescription")){
            return node.attributes.get("android:contentDescription");
        }
        return "?";
    }
}
