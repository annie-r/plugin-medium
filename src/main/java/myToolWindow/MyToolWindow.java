package myToolWindow;


import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;



public class MyToolWindow {
    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel test;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private Project project;
    JLabel lblUsername;

    private int lastYCoord = 0;
    private int height = 20;

    HashMap<JButton,ViewNode> buttonMap = new HashMap<>();

    public JPanel createComponent(){
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 452, 120);
        mainPanel.setLayout(null);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(0, 0, 400, height);
        mainPanel.add(lblUsername);

        refreshToolWindowButton = new JButton();
        refreshToolWindowButton.setBounds(30, 74, 83, 16);
        mainPanel.add(refreshToolWindowButton);

        myToolWindowContent = mainPanel;

        return mainPanel;
    }

    public MyToolWindow(ToolWindow toolWindow, Project projectArg) {
        project = projectArg;
        myToolWindowContent = createComponent();
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        refreshToolWindowButton.addActionListener(e -> testThing());

        //this.currentDateTime();
    }

    public void testAction(){
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        final Document document = editor.getDocument();
        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        // Replace the selection with a fixed string.
        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, "editor_basics")
        );
        // De-select the text range that was just replaced
        primaryCaret.moveToLogicalPosition(new LogicalPosition(8, 4));
        //primaryCaret.removeSelection();


    }

    public void testThing(){
        String fileText = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument().getText();
        ArrayList<ViewNode> nodes = new ArrayList<>();
        ViewHierarchyBuilder.parseXML(fileText, nodes);
        String t = "Test: ";
        if (fileText == null){
            t = "test";
        } else {
            for (ViewNode n : nodes){
                if(n.isMissingLabelTestClass()) {
                    lastYCoord += height;
                    JButton label = new JButton(n.getLabel());
                    label.setBounds(0, lastYCoord, 300, height);
                    myToolWindowContent.add(label);
                    buttonMap.put(label, n);
                    label.addActionListener(e -> labelButtonHandler(e));
                }

            }
        }

        lblUsername.setText("test");
        myToolWindowContent.revalidate();

    }

    public void labelButtonHandler(ActionEvent e){
        JButton source = (JButton) e.getSource();
        ViewNode target = buttonMap.get(source);
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        primaryCaret.moveToLogicalPosition(target.posInDoc);
    }

    public void currentDateTime() {
        // Get current date and time
        Calendar instance = Calendar.getInstance();
        test.setText("test");
        currentDate.setText(String.valueOf(instance.get(Calendar.DAY_OF_MONTH)) + "/"
                                + String.valueOf(instance.get(Calendar.MONTH) + 1) + "/" +
                                String.valueOf(instance.get(Calendar.YEAR)));
        currentDate.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Calendar-icon.png")));
        int min = instance.get(Calendar.MINUTE);
        String strMin;
        if (min < 10) {
            strMin = "0" + String.valueOf(min);
        } else {
            strMin = String.valueOf(min);
        }
        currentTime.setText(instance.get(Calendar.HOUR_OF_DAY) + ":" + strMin);
        currentTime.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Time-icon.png")));
        // Get time zone
        long gmt_Offset = instance.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
        String str_gmt_Offset = String.valueOf(gmt_Offset / 3600000);
        str_gmt_Offset = (gmt_Offset > 0) ? "GMT + " + str_gmt_Offset : "GMT - " + str_gmt_Offset;
        timeZone.setText(str_gmt_Offset);
        timeZone.setIcon(new ImageIcon(getClass().getResource("/myToolWindow/Time-zone-icon.png")));


    }

    public JPanel getContent() {
        return myToolWindowContent;
    }
}
