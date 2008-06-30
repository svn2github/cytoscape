package cytoscape.plugin.cheminfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MoleculeViewDialog extends JDialog {
    private JLabel label;

    public MoleculeViewDialog(Frame owner) {
        super(owner);
        init();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
 
    public void setSize(int x, int y) {
        super.setSize(x, y);
        int l = (x > y) ? y : x;
        label.setPreferredSize(new Dimension(l - 15, l - 15));
    }
    
    public void setDepictor(StructureDepictor depictor) {
    	this.setTitle(depictor.getNodeText());
    	Dimension d = label.getPreferredSize();
    	label.setIcon(new ImageIcon(depictor.depictWithUCSFSmi2Gif((int)d.getWidth(), (int)d.getHeight(), "white")));
        this.getContentPane().add(label, BorderLayout.CENTER);  
    }

    public void init() {
        label = new JLabel();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.WHITE);
        setResizable(false);
    }
}
