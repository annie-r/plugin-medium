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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;



public class MyToolWindow implements FocusListener {
    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel test;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private Project project;
    private JLabel lblUsername;

    private int lastYCoord = 0;
    private int height = 50;

    private HashMap<JTextField,ViewNode> buttonMap = new HashMap<>();

    public JPanel createComponent(){
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 452, 120);
        mainPanel.setLayout(null);

        refreshToolWindowButton = new JButton("see labels");
        refreshToolWindowButton.setBounds(0, 0, 83, 40);
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

        //reset window
        for (JTextField field : buttonMap.keySet()){
            myToolWindowContent.remove(field);

        }
        myToolWindowContent.revalidate();
        myToolWindowContent.repaint();
        lastYCoord = 0;
        buttonMap.clear();

        ArrayList<ViewNode> nodes = new ArrayList<>();
        ViewHierarchyBuilder.parseXMLContinuous(fileText, nodes);
        String t = "Test: ";
        for (ViewNode n : nodes){
            if(n.isMissingLabelTestClass()) {
                lastYCoord += height;
                JTextField label = new JTextField(n.getLabelValue());
                label.setBounds(0, lastYCoord, 300, height);
                myToolWindowContent.add(label);
                buttonMap.put(label, n);

                /* when the textbox gets focus */
                label.addFocusListener(this);

                // not used right now
                /* when enter is hit */
                label.addActionListener(e -> labelButtonHandler(e));

                label.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        //JOptionPane.showMessageDialog(null,
                          //      "Change");
                    }
                    public void removeUpdate(DocumentEvent e) {
                        //JOptionPane.showMessageDialog(null,
                          //      "Remove");
                    }
                    public void insertUpdate(DocumentEvent e) {
                        //JOptionPane.showMessageDialog(null,
                          //      "Insert");
                    }


                });
            }

        }


        lblUsername.setText("test");
        myToolWindowContent.revalidate();

    }
/*
    class LabelAction extends AbstractAction{
        ViewNode node;
        public LabelAction(ViewNode nodeArg){
            node = nodeArg;
        }

        public void actionPerformed(ActionEvent e){
            JTextField source = (JTextField) e.getSource();
            ViewNode target = buttonMap.get(source);
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
            primaryCaret.moveToLogicalPosition(target.posInDoc);
        }

    }
*/

    public void labelButtonHandler(ActionEvent e){
        JTextField source = (JTextField) e.getSource();
        ViewNode target = buttonMap.get(source);
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        primaryCaret.moveToLogicalPosition(target.getLabelStartPosition());
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    @Override
    public void focusGained(FocusEvent e) {
        JTextField source = (JTextField) e.getSource();
        ViewNode target = buttonMap.get(source);
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        primaryCaret.moveToLogicalPosition(target.getLabelStartPosition());
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextField source = (JTextField) e.getSource();
        ViewNode target = buttonMap.get(source);

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        final Project project = editor.getProject();
        final Document document = editor.getDocument();

        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        primaryCaret.moveToLogicalPosition(target.getLabelStartPosition());
        int offset = primaryCaret.getOffset();
        int lineLength = target.getLabelEndPosition().column - target.getLabelStartPosition().column;


        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(0,5,
                        "hello")
        );
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(offset,
                        offset+lineLength,
                        target.getLabelAttributeName()+"="+source.getText())
        );

    }
}
