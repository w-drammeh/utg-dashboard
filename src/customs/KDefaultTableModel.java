package customs;

import javax.swing.table.DefaultTableModel;

/**
 * <h1>class KDefaultTableModel</h1>
 * The add and removeRow() are yet tobe effectively overridden!
 */
public class KDefaultTableModel extends DefaultTableModel {
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
     * <p>Gets the row index of the row (more formally, the first row found) holding this string in the first column.</p>
     * -1 is returned to signal absence
     */
    public int getRowOf(String key){
        for (int i = 0; i < this.getRowCount(); i++) {
            if (this.getValueAt(i,0).toString().equalsIgnoreCase(key)) {
                return i;
            }
        }

        return -1;
    }

}
