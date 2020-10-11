package customs;

import main.App;
import main.MComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * This type works hand-in-hand with the customs.KTableModel type, and share their
 * mutual-functionality
 */
public class KTable extends JTable implements Preference {


    public KTable(KTableModel defaultTableModel){
        super();
        setModel(defaultTableModel);
        defaultTableModel.setTable(this);
        setPreferences();
    }

    public KScrollPane sizeMatchingScrollPane() {
        final KScrollPane scrollPane = new KScrollPane(this);
        scrollPane.setPreferredSize(getProperSize());
        getModel().addTableModelListener(tableModelEvent-> {
            scrollPane.setPreferredSize(getProperSize());
            MComponent.ready(scrollPane.getParent());
        });
        return scrollPane;
    }

    public Dimension getProperSize() {
        final int width = getPreferredSize().width;
        int height = getPreferredSize().height + 3;
        height += getTableHeader().getPreferredSize().height;
        return new Dimension(width, height);
    }

    public void setHeaderHeight(int height) {
        getTableHeader().setPreferredSize(new Dimension(getPreferredSize().width, height));
    }

    /**
     * Where columns is a list of the columns in this table to be center-aligned
     */
    public void centerAlignColumns(int... columns){
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for (int t : columns) {
            try {
                getColumnModel().getColumn(t).setCellRenderer(centerRenderer);
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
                getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            } catch (Exception e) {
                App.silenceException(e);
            }
        }
    }

    public void setPreferences(){
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

}
