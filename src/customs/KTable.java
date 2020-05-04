package customs;

import main.App;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * <h1>class KTable</h1>
 * <p><b>Description</b>: One of the most useful swing components as long as Dashboard is concerned.</p>
 * <p><i> </i></p>
 */
public class KTable extends JTable implements Preference {


    public KTable(KDefaultTableModel defaultTableModel){
        super();
        this.setModel(defaultTableModel);
        defaultTableModel.setTable(this);
        this.setPreferences();
    }

    public void centerAlignColumns(int... columns){
        for (int t : columns) {
            try {
                final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                this.getColumnModel().getColumn(t).setCellRenderer(centerRenderer);
            } catch (Exception e) {
                App.silenceException("Error centering column "+t);
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
                App.silenceException("Error centering column "+i);
            }
        }
    }

    public void setPreferences(){
        this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}
