package customs;

import javax.swing.table.DefaultTableModel;

/**
 * The add and removeRow() are yet tobe effectively overridden as per Dashboard's specification.
 * These are course oriented, since all the tables deals with courses?
 */
public class KDefaultTableModel extends DefaultTableModel implements Preference {
    KTable table;


    public KTable getTable(){
        return table;
    }

    public void setTable(KTable kTable){
        this.table = kTable;
    }

    public int getSelectedRow(){
        return table.getSelectedRow();
    }

    @Override
    public void addRow(Object[] rowData) {
        super.addRow(rowData);
    }

    @Override
    public void removeRow(int row) {
        super.removeRow(row);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    /**
     * Gets the row-index of the row (more formally, the first row found) holding this string in the first column.
     * A return of -1 signals absence of such a row in this table's model.
     */
    public int getRowOf(String key){
        for (int i = 0; i < this.getRowCount(); i++) {
            if (this.getValueAt(i,0).toString().equalsIgnoreCase(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void setPreferences() {

    }

}
