package myToolWindow;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.util.Calendar;

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

    public JPanel createComponent(){
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 452, 120);
        mainPanel.setLayout(null);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(30, 25, 83, 16);
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

    public void testThing(){
        String t = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument().getText();
        util.parseXML(t);
        if (t == null){
            t = "test";
        }
        //JLabel test2 = new JLabel(t);
        //test2.setBounds(30, 0, 83, 16);
        //myToolWindowContent.add(test2);
        lblUsername.setText(t);
        myToolWindowContent.revalidate();

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
