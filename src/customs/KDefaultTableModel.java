package customs;

import javax.swing.table.DefaultTableModel;

/**
 * These are course oriented, since all the tables deals with courses?
 */
public class KDefaultTableModel extends DefaultTableModel implements Preference {
    private KTable table;


    public KTable getTable(){
        return table;
    }

    public void setTable(KTable kTable){
        this.table = kTable;
    }

    public int getSelectedRow(){
        return table.getSelectedRow();
    }

    /**
     * Gets the row-index of the row (more formally, the first row found) holding this string
     * in its first column. A return of -1 signals absence of such a row in this model.
     * This call is case-insensitive.
     */
    public int getRowOf(String key){
        for (int i = 0; i < this.getRowCount(); i++) {
            if (key.equalsIgnoreCase(String.valueOf(this.getValueAt(i,0)))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void setPreferences() {
    }

}
