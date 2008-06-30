package cytoscape.plugin.cheminfo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Cell Renderer of the column to display 2 dimentional molecule structure
 * 
 * @author <a href="mailto:djiao@indiana.edu">David Jiao</a>
 * @version $Revision: $ $Date: $
 */
public class MoleculeCellRenderer extends JPanel 
        implements TableCellRenderer {
	JLabel label;
	HashMap imageMap = new HashMap();
	HashMap selectedImageMap = new HashMap();
    
    public MoleculeCellRenderer(Dimension dimension) {
        super();
        this.setLayout(new BorderLayout());
        label = new JLabel();
        label.setPreferredSize(dimension);
        setOpaque(true);
        
        label.setOpaque(true);
        add(label, BorderLayout.CENTER);
    }
    
    public void setPreferredSize(int x, int y) {
        super.setPreferredSize(new Dimension(x, y));
        int l = (x > y) ? y : x;
        label.setPreferredSize(new Dimension(l - 1, l - 1));
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        StructureDepictor depictor = (StructureDepictor)value;
        Dimension d = label.getPreferredSize();
        int width = (int)d.getWidth();
        int height = (int)d.getHeight();
              
        if (isSelected) {
            // celll is selected
            label.setBackground(table.getSelectionBackground());
            label.setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
            Image selectedImage = (Image)selectedImageMap.get(depictor.getNode().getIdentifier());
            if (selectedImage == null) {
            	selectedImage = depictor.depictWithUCSFSmi2Gif(width, height, "cyan");
            	selectedImageMap.put(depictor.getNode().getIdentifier(), selectedImage);
            }
            if (selectedImage == null) {
            	if (!depictor.hasMolecule()) {
            		label.setText("No Smiles/InChI!");
            	} else {
            		label.setText("Error Getting Image!");
            	}
            	label.setIcon(null);
            } else {
                label.setIcon(new ImageIcon(selectedImage));
            }
        } else {
            label.setForeground(table.getForeground());
            label.setBackground(table.getBackground());            
            setForeground(table.getForeground());
            setBackground(table.getBackground());
            Image image = (Image)imageMap.get(depictor.getNode().getIdentifier());
            if (image == null) {
            	image = depictor.depictWithUCSFSmi2Gif(width, height, "white");
            	imageMap.put(depictor.getNode().getIdentifier(), image);            	
            }
            if (image == null) {
            	if (!depictor.hasMolecule()) {
            		label.setText("No Smiles/InChI!");
            	} else {
            		label.setText("Error Getting Image!");
            	}
            	label.setIcon(null);
            } else {
            	label.setIcon(new ImageIcon(image));
            }
        }
        if (hasFocus) {
            // show tooltips?
            Border border = null;
            if (isSelected) {
                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            setBorder(border);                 
        } else {
            setBorder(getNoFocusBorder());
        }

        return this;
    }

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return SAFE_NO_FOCUS_BORDER;
        } else {
            return noFocusBorder;
        }
    }
    
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1); 
    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);   

}