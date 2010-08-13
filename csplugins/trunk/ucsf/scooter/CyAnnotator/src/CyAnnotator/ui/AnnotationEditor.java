//This class will add an Annotation editor to the Cytoscape Panel

package CyAnnotator.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
// import java.awt.*;

/**
 * This is the main panel that provides an interface
 * to all of the annotation methods
 */
public class AnnotationEditor extends JPanel {

    //Different JPanels for various Annotations
    InfoPanel infoPanel;
    TextPanel textPanel;
    TWithShapePanel tWithShapePanel;
    ImagePanel imagePanel;
    ShapePanel shapePanel;
    
    public AnnotationEditor() {
        initComponents();
    }

    public void initComponents(){

       infoPanel=new InfoPanel();
       textPanel=new TextPanel();
       tWithShapePanel=new TWithShapePanel();
       imagePanel=new ImagePanel();
       shapePanel=new ShapePanel();

       this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

       //Put up the info Panel into a JScrollPane
       JScrollPane scrollPane=new JScrollPane(infoPanel);

       scrollPane.setPreferredSize( new Dimension(infoPanel.getPreferredSize().width, 200) );
       scrollPane.setWheelScrollingEnabled(true);
       scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
       scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

       this.add(scrollPane);
       this.add(textPanel);
       this.add(tWithShapePanel);
       this.add(imagePanel);
       this.add(shapePanel);

    }

}
