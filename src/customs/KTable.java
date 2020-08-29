package customs;

import main.App;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class KTable extends JTable implements Preference {


    public KTable(KDefaultTableModel defaultTableModel){
        super();
        this.setModel(defaultTableModel);
        defaultTableModel.setTable(this);
        this.setPreferences();
    }

    public void centerAlignColumns(int... columns){
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for (int t : columns) {
            try {
                this.getColumnModel().getColumn(t).setCellRenderer(centerRenderer);
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

    public void centerAlignAllColumns(){
        for (int i = 0; i < getColumnCount(); i++) {
            try {
                final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                this.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

    public void setPreferences(){
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}
