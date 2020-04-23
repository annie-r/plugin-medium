package myToolWindow;

import javax.swing.*;

public class myNewButton extends JButton {
    ViewNode node;
    JButton button;

    public myNewButton(ViewNode nodeArg){
        node = nodeArg;
        button = new JButton(node.getLabelValue());
    }

}
