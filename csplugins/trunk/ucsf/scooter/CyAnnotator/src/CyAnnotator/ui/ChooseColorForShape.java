/*
 * ChooseColorForShape.java
 *
 * Created on Jun 11, 2010, 10:36:37 AM
 */

package CyAnnotator.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author Avinash Thummala
 */

//This class will create a JFrame to finalize a color to be used for either drawing or filling up a shape

public class ChooseColorForShape extends JFrame {

    CreateTWithShapeAnnotation tWithShapeAnnotationCreator;
   
    public ChooseColorForShape(CreateTWithShapeAnnotation tWithShapeAnnotationCreator) {

        this.tWithShapeAnnotationCreator=tWithShapeAnnotationCreator;
        initComponents(this.getContentPane());
    }

    private void initComponents(Container pane) {

        jColorChooser1 = new JColorChooser();
        jButton1 = new JButton();
        jButton2 = new JButton();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setTitle("Choose Color for Shapes");

        pane.setLayout(null);

        pane.add(jColorChooser1);
        jColorChooser1.setBounds(0, 0, jColorChooser1.getPreferredSize().width, jColorChooser1.getPreferredSize().height);

        jButton1.setText("Apply");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        pane.add(jButton1);
        jButton1.setBounds(130, 360, 70, jButton1.getPreferredSize().height);

        jButton2.setText("Cancel");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        pane.add(jButton2);
        jButton2.setBounds(220, 360, jButton2.getPreferredSize().width, jButton2.getPreferredSize().height);

        this.setContentPane(pane);

        this.setSize(429, 418);        
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        tWithShapeAnnotationCreator.setShapeColor(jColorChooser1.getColor());
        this.dispose();
    }

    private void jButton2ActionPerformed(ActionEvent evt) {
        this.dispose();
    }


    // Variables declaration - do not modify
    private JButton jButton1;
    private JButton jButton2;
    private JColorChooser jColorChooser1;
    // End of variables declaration

}
