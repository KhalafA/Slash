package GUI.View;

import Standard.Application;

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
        setPreferredSize(new Dimension(450,220));

        JScrollPane pane = new JScrollPane();
        table = new JTable();
        pane.setViewportView(table);

        JPanel eastPanel = new JPanel();
        kick = new JButton("Kick");
        eastPanel.add(kick);

        JPanel northPanel = new JPanel();

        add(northPanel, BorderLayout.NORTH);
        add(eastPanel, BorderLayout.EAST);
        add(pane,BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{"Count","Name", "Status"},0);
        table.setModel(tableModel);

        kick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();

                if(row != -1){
                    application.kick(tableModel.getValueAt(row, 0));
                    removeRow(row);
                }
            }
        });
    }



    public void removeRow(int row){
        tableModel.removeRow(row);
    }

    public void newClient(String name, int ID){
        tableModel.addRow(new Object[]{ID,name,"paused"});
    }

    public void updateTableRow(int id, boolean capturing){
        String name = "";
        String status = capturing ? "Capturing" : "Paused";

        int row = findRowWithID(id);
        table.getModel().setValueAt(status, row,2 );
    }

    private int findRowWithID(int id){
        int foundRow = -1;

        for(int row = 0;row < tableModel.getRowCount();row++) {
            if(tableModel.getValueAt(row, 0).toString().equals(id+"")){
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
