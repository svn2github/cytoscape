/*
 * LayoutTableModel.java
 *
 * Created on August 11, 2006, 3:13 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */


package GOlorize;
import javax.swing.table.* ;



import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
/**
 *
 * @author ogarcia
 */
public class LayoutTableModel extends AbstractTableModel{
    private Object[] columnNames;
    //private Object[][] data;
    private ArrayList dataAL=new ArrayList();
    private int SELECT_COLUMN=0;
    private int GO_TERM_COLUMN=1;
    private int DESCRIPTION_COLUMN=2;
    
    private int LAYOUT_COLUMN=4;
    private int REMOVE_COLUMN=5;
            
    
    /** Creates a new instance of StartTableModel */
   
    
    
    public LayoutTableModel(Object[] columnNames,ArrayList al) {
     
            
        super();
        this.columnNames = columnNames;
        //this.data=data;
        this.dataAL=al;
        
        
    } 
    
   
    
    
    public void addLine(Object[] obj){
        
        dataAL.add(obj);
        this.fireTableDataChanged();
        
    }
    public void removeLine(int i){
        dataAL.remove(i);
        //this.fireTableDataChanged();
        //this.fireTableRowsDeleted(i,i);
        this.fireTableDataChanged();
    }
    public void removeLine(String term){
        for (int i=0;i<getRowCount();i++){
            if (((String)getValueAt(i,GO_TERM_COLUMN)).equals(term)){
                removeLine(i);
                break;
            }
        }
    }
    
    
    
    public int getColumnCount() {
                return columnNames.length;
                
    }

    public int getRowCount() {
        return dataAL.size();
    }

    public String getColumnName(int col) {


         return (String)columnNames[col];


    }

    public Object getValueAt(int row, int col) {

        return ((Object[])dataAL.get(row))[col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {


        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col == SELECT_COLUMN/* || col == DESCRIPTION_COLUMN*/||col==LAYOUT_COLUMN/*||col==REMOVE_COLUMN*/){ //si j'utilise pas combobox amigo sgd, laisser ke if col ==1'
            return true;
        }
        else {
            return false;
        }
        //if ((col > 0)) {
        //    return false;
        //} else {
        //    return true;
        //}
    }


    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {


        ((Object[])dataAL.get(row))[col] = value;
        fireTableCellUpdated(row, col);


    }
    
}
class MouseLayoutPanelHandler extends MouseAdapter{
            ///Listeners for table links
    Point point;
    String urlGO = "http://godatabase.org/cgi-bin/go.cgi?view=details&depth=1&query=";
    String urlSGD = "http://db.yeastgenome.org/cgi-bin/GO/go.pl?goid=";
    LayoutPanel layoutPanel;
    GoBin goBin;
    HashMap goColor ;
    JTable jTable1;
    public MouseLayoutPanelHandler(LayoutPanel layoutPanel){
        super();
        this.layoutPanel=layoutPanel;
        this.goBin=layoutPanel.getGoBin();
        goColor = goBin.getGoColor();
        jTable1=layoutPanel.getJTable();
        ///Browser.getDialogPanel(); a voir ce truc peut etre bien pour pas nouvelle fenetre a chque fois ???
    }

    public void mouseClicked(MouseEvent ev){ 
        point = ev.getPoint();




        int tableColumn = jTable1.columnAtPoint(ev.getPoint());
                int modelColumn = jTable1.convertColumnIndexToModel(tableColumn);
                


        //listen to column "go term"
        if (/*jTable1.columnAtPoint(point)*/modelColumn==layoutPanel.getGoTermColumn()) {


            //ligne en dessous a arevoir je ne la comprends pas trop les getmodifiers me donnent pas les valeurs que je veux!!!!!!!!!
            if (SwingUtilities.isLeftMouseButton(ev) ){
                String GOid = (String)jTable1.getValueAt(jTable1.rowAtPoint(point),/*jTable1.columnAtPoint(point)*/modelColumn);

                try {

                    GOlorize.BiNGO.Browser.init(); ///A REFAIRE CHOIX PAS PREVU + NOUVELLE FENETRE A CHAQUE FOIS ??
                    //if (jTable1.getColumnName(1)=="GO-ID : SGD")
                    //     Browser.displayURL(urlSGD+GOid);
                    //else 
                         GOlorize.BiNGO.Browser.displayURL(urlGO+GOid);

                }
                catch (Exception ee){
                    JOptionPane.showMessageDialog(goBin,"Could not open website :" + ee);
                }
            }
        }








        ////DE LA COULEUR !!!!!
        if (modelColumn==layoutPanel.getDescriptionColumn()){

            //JButton button = new JButton();
            //button.setActionCommand("edit");
            //button.addActionListener(this);
            //button.setBorderPainted(false);
            JColorChooser colorChooser = new JColorChooser();

            Color newColor = JColorChooser.showDialog(
                        jTable1,
                        "Choose GO Color",
                        (Color)goColor.get(jTable1.getValueAt(jTable1.rowAtPoint(point),layoutPanel.getGoTermColumn()))
                        );
            ((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),layoutPanel.getDescriptionColumn())).setBackground(newColor);
            //((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),2)).setText(jTable1.getValueAt())
            goColor.put(jTable1.getValueAt(jTable1.rowAtPoint(point),layoutPanel.getGoTermColumn()),newColor);






            layoutPanel.getGoBin().getGoColor().put(jTable1.getValueAt(jTable1.rowAtPoint(point),ResultPanel.GO_TERM_COLUMN),newColor);
            layoutPanel.getGoBin().synchroColor();

//jTable1.

        }
                
        if (modelColumn==layoutPanel.getRemoveColumn()){
            String term=(String)jTable1.getValueAt(jTable1.rowAtPoint(point),layoutPanel.getGoTermColumn());
            
            layoutPanel.removeLine(term);
            //resultPanel.removeLine(term);
            //goBin.goTerm_Annotation.remove(term);

        }    


    }
}

class MouseMotionLayoutPanelHandler extends MouseMotionAdapter {
    Cursor hand = new Cursor(Cursor.HAND_CURSOR);
    LayoutPanel layoutPanel;
    JTable jTable1;
    public MouseMotionLayoutPanelHandler(LayoutPanel layoutPanel){
        this.layoutPanel=layoutPanel;
        this.jTable1=layoutPanel.getJTable();
    }
    public void mouseMoved(MouseEvent ev) {
        if (jTable1.columnAtPoint(ev.getPoint())==layoutPanel.getGoTermColumn()){
            jTable1.setCursor(hand);
        }
        else {
            jTable1.setCursor(Cursor.getDefaultCursor());
        }
    }
}
/*
class ColorRendererForLayoutLayout extends JLabel
                           implements TableCellRenderer {
    //...\
    
    boolean isBordered;
    Color color = null;
    LayoutPanel layout;
    Color red=Color.GREEN;
    
    public ColorRendererForLayoutLayout(boolean isBordered) {
        this.isBordered = isBordered;
        
        setOpaque(true); //MUST do this for background to show up.
    }
    public ColorRendererForLayoutLayout(boolean isBordered, LayoutPanel layout) {
        this.isBordered = isBordered;
        
        this. layout= layout;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object label,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        Color newColor = Color.WHITE;//(Color)color;
        String text = "layout";//((JLabel)table.getValueAt(row,start.getLayoutColumn())).getText();
        
        this.setBackground(color);
        //if (color.getRed()<100 && color.getGreen()<100 && color.getBlue()<100)
        //    this.setForeground(Color.getColor("white"));
        //else this.setForeground(Color.getColor("black"));
        this.setText(text);
        this.setHorizontalAlignment(this.CENTER);
        
        
        
        //setToolTipText(...); //Discussed in the following section
        return this;
    }
}
 */
class ColorRendererForRemoveLayout extends JLabel
                           implements TableCellRenderer {
    //...\
    
    boolean isBordered;
    Color color = null;
    LayoutPanel layout;
    //Color red=Color.RED;
    
    public ColorRendererForRemoveLayout(boolean isBordered) {
        this.isBordered = isBordered;
        
        setOpaque(true); //MUST do this for background to show up.
    }
    public ColorRendererForRemoveLayout(boolean isBordered,LayoutPanel layout) {
        this.isBordered = isBordered;
        
        this.layout = layout;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object label,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        
        String text = "X";//((JLabel)table.getValueAt(row,start.getLayoutColumn())).getText();
        
        //this.setBackground(red);
        //if (color.getRed()<100 && color.getGreen()<100 && color.getBlue()<100)
        //    this.setForeground(Color.getColor("white"));
        //else this.setForeground(Color.getColor("black"));
        this.setText(text);
        this.setForeground(Color.RED);
        
        
        
        //setToolTipText(...); //Discussed in the following section
        return this;
    }
}
