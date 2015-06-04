package GUI.View;

import Standard.Application;
import Standard.Constants;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerHasConnectionsPane extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JButton kick;
    private JButton allowControl;
    private JButton declineControl;

    private Application application;

    private JPanel btnPanel;

    public ServerHasConnectionsPane(final Application application){
        this.application = application;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Constants.mainViewWidth,Constants.mainViewHeight));

        JScrollPane pane = new JScrollPane();
        table = new JTable();

        kick = new JButton(Constants.kickBtn);
        allowControl = new JButton("Allow Control");
        declineControl = new JButton("Decline Control");

        btnPanel = new JPanel();

        btnPanel.add(kick);
        btnPanel.add(allowControl);
        btnPanel.add(declineControl);

        pane.setViewportView(table);

        add(btnPanel, BorderLayout.SOUTH);
        add(pane,BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{Constants.idColTitle,Constants.nameColTitle, Constants.statusColTitle, "Control status"},0);
        table.setModel(tableModel);

        kick.setVisible(false);
        allowControl.setVisible(false);
        declineControl.setVisible(false);

        addListeners();
    }

    private void addListeners(){

        buttonListeners();
        selectionListener();

    }

    private void buttonListeners(){
        kick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();

                if(row != -1){
                    application.kick(tableModel.getValueAt(row, Constants.idCol));
                    removeRow(row);
                }
            }
        });

        allowControl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();

                if(row != -1){
                    application.grantControl(tableModel.getValueAt(row, Constants.idCol));
                }
            }
        });

        declineControl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();

                if(row != -1){
                    application.declineControl(tableModel.getValueAt(row, Constants.idCol));
                }
            }
        });
    }

    private void selectionListener(){
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = table.getSelectedRow();

                kick.setVisible(true);

                if(tableModel.getValueAt(row, 3).toString().equals("Requested")){
                    allowControl.setVisible(true);
                    declineControl.setVisible(true);
                }else {
                    allowControl.setVisible(false);
                    declineControl.setVisible(false);
                }
            }
        });
    }

    public void removeRow(int row){
        tableModel.removeRow(row);
    }

    public void newClient(String name, int ID){
        tableModel.addRow(new Object[]{ID,name,Constants.PAUSED, ""});
    }

    public void updateClientStatus(int id, boolean capturing){
        String status = capturing ? Constants.CAPTURING : Constants.PAUSED;

        int row = findRowWithID(id);
        table.getModel().setValueAt(status, row,Constants.statusCol);
    }

    private void toggleBtn(){
        boolean temp = allowControl.isEnabled();

    }

    private int findRowWithID(int id){
        int foundRow = -1;

        for(int row = 0;row < tableModel.getRowCount();row++) {
            if(tableModel.getValueAt(row, Constants.idCol).toString().equals(id+"")){
                foundRow = row;
                break;
            }
        }

        return foundRow;
    }

    public void removeClient(int id) {
        int row = findRowWithID(id);

        removeRow(row);
    }

    public void updateRequestField(int id, boolean wantToControl) {
        int row = findRowWithID(id);

        String request = wantToControl ? "Requested" : "";
        table.getModel().setValueAt(request, row,3 );

        if(table.getSelectedRow() != -1){
            if(table.getSelectedRow() == row && wantToControl){
                allowControl.setVisible(true);
                declineControl.setVisible(true);
            }else {
                allowControl.setVisible(false);
                declineControl.setVisible(false);
            }
        }
    }
}
