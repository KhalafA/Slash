import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerHasConnectionsPane extends JPanel {
    private JTable table;
    private JButton kick;
    private DefaultTableModel tableModel;

    public ServerHasConnectionsPane(){
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

            }
        });

        //table = new JTable(columnNames);
    }

    public void newClient(String name, int ID){
        tableModel.addRow(new Object[]{ID,name,"paused"});
    }

    public void updateTableRow(int id, boolean capturing){
        String name = "";
        String status = capturing ? "Capturing" : "Paused";

        for(int row = 0;row < tableModel.getRowCount();row++) {

            if(tableModel.getValueAt(row, 0).toString().equals(id+"")){

                name = tableModel.getValueAt(row, 1).toString();
                updateRowInfo(row, id, name, status);
                break;
            }
        }
    }

    private void updateRowInfo(int row, int id, String name, String status){
        tableModel.removeRow(row);
        tableModel.addRow(new Object[]{id,name,status});
    }


}
