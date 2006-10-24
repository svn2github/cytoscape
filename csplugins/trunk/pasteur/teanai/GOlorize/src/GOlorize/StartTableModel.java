/*
 * StartTableModel.java
 *
 * Created on July 31, 2006, 6:32 PM
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
public class StartTableModel extends AbstractTableModel{
    private Object[] columnNames;
    //private Object[][] data;
    private ArrayList dataAL=new ArrayList();
    private int SELECT_COLUMN=0;
    private int GO_TERM_COLUMN=1;
    private int DESCRIPTION_COLUMN=2;
    
    private int LAYOUT_COLUMN=4;
    private int REMOVE_COLUMN=5;
      
    
    /** Creates a new instance of StartTableModel */
   
    
    
    public StartTableModel(Object[] columnNames,ArrayList al) {
     
            
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
    
    public void fireColor(){
        this.fireTableDataChanged();
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
        if (col == SELECT_COLUMN /*|| col == DESCRIPTION_COLUMN||col==LAYOUT_COLUMN||col==REMOVE_COLUMN*/)//{ //si j'utilise pas combobox amigo sgd, laisser ke if col ==1'
            return true;
        //}
        //else {
            return false;
        //}
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
class MouseStartPanelHandler extends MouseAdapter{
            ///Listeners for table links
    Point point;
    String urlGO = "http://godatabase.org/cgi-bin/go.cgi?view=details&depth=1&query=";
    String urlSGD = "http://db.yeastgenome.org/cgi-bin/GO/go.pl?goid=";
    StartPanelPanel resultPanel;
    GoBin goBin;
    HashMap goColor ;
    JTable jTable1;
    
    public MouseStartPanelHandler(StartPanelPanel resultPane){
        super();
        this.resultPanel=resultPane;
        this.goBin=resultPanel.getGoBin();
        goColor = goBin.getGoColor();
        jTable1=resultPane.getJTable();
        ///Browser.getDialogPanel(); a voir ce truc peut etre bien pour pas nouvelle fenetre a chque fois ???
    }

    public void mouseClicked(MouseEvent ev){ 
        point = ev.getPoint();




        int tableColumn = jTable1.columnAtPoint(ev.getPoint());
                int modelColumn = jTable1.convertColumnIndexToModel(tableColumn);
                


        //listen to column "go term"
        if (modelColumn==resultPanel.getGoTermColumn()) {


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
        if (modelColumn==resultPanel.getDescriptionColumn()){
            String term = (String)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getGoTermColumn());
           //if (!this.resultPanel.isAutoColor()){
                JColorChooser colorChooser = new JColorChooser();

                Color newColor = JColorChooser.showDialog(
                            jTable1,
                            "Choose GO Color",
                            (Color)goColor.get(term)
                            );
                ((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getDescriptionColumn())).setBackground(newColor);
                //((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),2)).setText(jTable1.getValueAt())
                goColor.put(term,newColor);
                resultPanel.getGoBin().freeAutomaticColor(term);
                resultPanel.goBin.getGoColor().put(term,newColor);
                resultPanel.goBin.synchroColor();
           //}
           /*else {
                if (!resultPanel.getGoBin().isAutomaticlyColored(term)){
                    if (resultPanel.getGoBin().freeAutomaticColor()){
                        //System.out.println("lasjkdhfasdh fkjash fkjahsd fkjhas df");
                        Color newColor = resultPanel.getGoBin().getNextAutomaticColor(term);
                        ((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getDescriptionColumn())).setBackground(newColor);

                        goColor.put(term,newColor);
                        resultPanel.goBin.goColor.put(term,newColor);
                        resultPanel.goBin.synchroColor();
                    }
                    else {
                        resultPanel.setAutoColor(false);
                    }
                }
                else {
                    resultPanel.getGoBin().freeAutomaticColor(term);
                    ((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getDescriptionColumn())).setBackground(null);
                    resultPanel.goBin.goColor.put(term,null);
                    resultPanel.goBin.synchroColor();
                }
           }*/
//jTable1.

        }
                
        if (modelColumn==resultPanel.getRemoveColumn()&&ev.getClickCount()>=1){
            
            String term=(String)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getGoTermColumn());
            //goBin.goTerm_Annotation.remove(term);
            Iterator it = goBin.getStartPanelIterator();
            StartPanelPanel spp;
            while (it.hasNext()){
                spp = (StartPanelPanel)it.next();
                spp.removeLine(term);
            }
            //resultPanel.removeLine(term);
            goBin.getGoTerm_Annotation().remove(term);
            goBin.getLayoutPanel().removeLine(term);

        }    
        if (modelColumn==resultPanel.getLayoutColumn()&&ev.getClickCount()>=1){
            String term=(String)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getGoTermColumn());       
            JLabel lay = (JLabel)(this.jTable1.getValueAt(jTable1.rowAtPoint(point),modelColumn));
            
            if (!((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getLayoutColumn())).getBackground().equals(goBin.getLayoutColor())){
                Iterator it =goBin.getStartPanelIterator();
                goBin.getLayoutPanel().addLine(term,resultPanel.getAnnotation(term),resultPanel.getAnnotation(term).getOntology());
                while (it.hasNext()){
                    StartPanelPanel startt= (StartPanelPanel)it.next();
                    if (startt.getTermIndex(term)>=0){
                        ((JLabel)startt.getJTable().getValueAt(startt.getTermIndex(term),startt.getLayoutColumn())).setBackground(goBin.getLayoutColor());
                        
                    }
                    
                }
                            }
            else {
                //((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getLayoutColumn())).setBackground(null);
                Iterator it =goBin.getStartPanelIterator();
                goBin.getLayoutPanel().removeLine(term);
                while (it.hasNext()){
                    StartPanelPanel startt= (StartPanelPanel)it.next();
                    if (startt.getTermIndex(term)>=0){
                        ((JLabel)startt.getJTable().getValueAt(startt.getTermIndex(term),startt.getLayoutColumn())).setBackground(goBin.getNoLayoutColor());
                    }
                    
                }          
            }
            //lay.revalidate();
            //jTable1.validate();
            ((StartTableModel)jTable1.getModel()).fireColor();
            
        }
        
    }
}

class MouseMotionStartPanelHandler extends MouseMotionAdapter {
    Cursor hand = new Cursor(Cursor.HAND_CURSOR);
    StartPanelPanel start;
    JTable jTable1;
    public MouseMotionStartPanelHandler(StartPanelPanel star){
        this.start=star;
        this.jTable1=start.getJTable();
    }
    public void mouseMoved(MouseEvent ev) {
        if (jTable1.columnAtPoint(ev.getPoint())==start.getGoTermColumn()){
            jTable1.setCursor(hand);
        }
        else {
            jTable1.setCursor(Cursor.getDefaultCursor());
        }
    }
}


class ColorRendererForStartLayout extends JLabel
                           implements TableCellRenderer {
    //...\
    
    boolean isBordered;
    Color color = Color.getColor("white");
    StartPanelPanel start;
    Color noLayout = Color.WHITE;
    Color layout = Color.GREEN;
    //Color newColor;
    
    public ColorRendererForStartLayout(boolean isBordered) {
        this.isBordered = isBordered;
        
        setOpaque(true); //MUST do this for background to show up.
    }
    public ColorRendererForStartLayout(boolean isBordered,StartPanelPanel start) {
        this.isBordered = isBordered;
        
        this.start = start;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object label,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        Color newColor = (Color)color;
        String text = " V ";//((JLabel)table.getValueAt(row,start.getLayoutColumn())).getText();
        String term=(String)table.getValueAt(row,start.getGoTermColumn());
        if (start.goBin.getLayoutPanel().getTermIndex(term)>=0){
           // color = layout;
            
        }
        else {
        //    color = noLayout;
           text = "   "; 
        }
        //this.setBackground(color);
        //if (color.getRed()<100 && color.getGreen()<100 && color.getBlue()<100)
        //    this.setForeground(Color.getColor("white"));
        //else this.setForeground(Color.getColor("black"));
        this.setText(text);
        this.setHorizontalAlignment(this.CENTER);
        
        
        
        //setToolTipText(...); //Discussed in the following section
        return this;
    }
}
class ColorRendererForRemoveStart extends JLabel
                           implements TableCellRenderer {
    //...\
    
    boolean isBordered;
    Color color = null;
    StartPanelPanel start;
    //Color red=Color.RED;
    
    public ColorRendererForRemoveStart(boolean isBordered) {
        this.isBordered = isBordered;
        
        setOpaque(true); //MUST do this for background to show up.
    }
    public ColorRendererForRemoveStart(boolean isBordered,StartPanelPanel start) {
        this.isBordered = isBordered;
        
        this.start = start;
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

