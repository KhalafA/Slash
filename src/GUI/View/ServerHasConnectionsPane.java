package GUI.View;

import Standard.Application;
import Standard.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerHasConnectionsPane extends JPanel {
    private JTable table;
    private JButton kick;
    private DefaultTableModel tableModel;

    private Application application;

    public ServerHasConnectionsPane(final Application application){
        this.application = application;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Constants.mainViewWidth,Constants.mainViewHeight));

        JScrollPane pane = new JScrollPane();
        table = new JTable();
        pane.setViewportView(table);

        JPanel eastPanel = new JPanel();
        kick = new JButton(Constants.kickBtn);
        eastPanel.add(kick);

        JPanel northPanel = new JPanel();

        add(northPanel, BorderLayout.NORTH);
        add(eastPanel, BorderLayout.EAST);
        add(pane,BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{Constants.idColTitle,Constants.nameColTitle, Constants.statusColTitle},0);
        table.setModel(tableModel);

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
    }



    public void removeRow(int row){
        tableModel.removeRow(row);
    }

    public void newClient(String name, int ID){
        tableModel.addRow(new Object[]{ID,name,Constants.PAUSED});
    }

    public void updateTableRow(int id, boolean capturing){
        String status = capturing ? Constants.CAPTURING : Constants.PAUSED;

        int row = findRowWithID(id);
        table.getModel().setValueAt(status, row,Constants.statusCol );
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
}
