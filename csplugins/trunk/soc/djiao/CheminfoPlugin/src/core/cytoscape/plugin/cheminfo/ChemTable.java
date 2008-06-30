package cytoscape.plugin.cheminfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import cytoscape.Cytoscape;

public class ChemTable extends JTable implements ClipboardOwner {
    private JPopupMenu popupMenu;

    private int xc;

    private int yc;

    private String[] popupItems = { "Copy Selected", "View Structure" };

    private MoleculeViewDialog moleculeDialog;

    public ChemTable(ChemTableModel model) {
        super(model);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setRowHeight(121);
        
        TableColumn col = getColumnModel().getColumn(2);
        col.setWidth(121);
        col.setPreferredWidth(121);
        col.setResizable(false);
        MoleculeCellRenderer mcr = new MoleculeCellRenderer(new Dimension(120, 120));
        col.setCellRenderer(mcr);
        
        getColumnModel().getColumn(1).setPreferredWidth(250);
        getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);
        setSelectionBackground(Color.CYAN);
        
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setupPopup();
        
        moleculeDialog = new MoleculeViewDialog(Cytoscape.getDesktop());
        moleculeDialog.setSize(new Dimension(320, 320));
        
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
            
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
            
            private void showPopup(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    //store the position where popup was called
                    xc = e.getX();
                    yc = e.getY();
                    Point point = new Point(xc, yc);
                    int rc = rowAtPoint(point);
                    int cc = columnAtPoint(point);
                    
                    ((JMenuItem)popupMenu.getComponent(1)).setEnabled(true);
                    ((JMenuItem)popupMenu.getComponent(0)).setEnabled(true);
                    
                    //show the popup
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }                
            }
        });
    }
    
    public void sortAllRowsBy(int colIndex, boolean ascending) {
        List data = ((ChemTableModel)getModel()).getRecords();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        ((ChemTableModel)getModel()).fireTableStructureChanged();
    }  
    
    private void setupPopup() {
        this.popupMenu = new JPopupMenu();
        
        for (int i = 0; i < popupItems.length; i++) {
            JMenuItem menuItem = new JMenuItem(popupItems[i]);
            popupMenu.add(menuItem);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    String command = ev.getActionCommand();
                    if (command.equals("Copy Selected")) {
                        copySelected();
                    } else if (command.equals("View Structure")) {
                        displayStructure();
                    }
                }
            });
        }     
    }
    
    private void displayStructure() {
        Point point = new Point(xc, yc);
        int rc = rowAtPoint(point);
        int cc = columnAtPoint(point);
        StructureDepictor depictor = (StructureDepictor)getModel().getValueAt(rc, 2);
        moleculeDialog.setDepictor(depictor);
        moleculeDialog.setLocationRelativeTo(Cytoscape.getDesktop());
        moleculeDialog.pack();
        moleculeDialog.setVisible(true);
    }
    
    private void copySelected() {
        List values = ((ChemTableModel)getModel()).getValuesAt(getSelectedRows());
        Iterator it = values.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            List row = (List)it.next();
            Iterator lit = row.iterator();
            sb.append(lit.next());
            sb.append('\t');
            sb.append(lit.next());
            sb.append('\n');
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }
    

    /**
     * Empty implementation of the ClipboardOwner interface.
     */
    public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
      //do nothing
    }
}
