package ManualLayout.control.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import ManualLayout.control.actions.align.*;
import cytoscape.view.*;

public class AlignPanel extends JPanel {

  public AlignPanel ( ) {

    ImageIcon hari =  new ImageIcon( getClass().getResource("/H_ALIGN_RIGHT.gif") );
    ImageIcon haci =  new ImageIcon( getClass().getResource("/H_ALIGN_CENTER.gif") );
    ImageIcon hali =  new ImageIcon( getClass().getResource("/H_ALIGN_LEFT.gif") );
    ImageIcon vati =  new ImageIcon( getClass().getResource("/V_ALIGN_TOP.gif") );
    ImageIcon vaci =  new ImageIcon( getClass().getResource("/V_ALIGN_CENTER.gif") );
    ImageIcon vabi =  new ImageIcon( getClass().getResource("/V_ALIGN_BOTTOM.gif") );

    HAlignRight har = new HAlignRight( hari );
    HAlignCenter hac = new HAlignCenter( haci );
    HAlignLeft hal = new HAlignLeft( hali );

    VAlignTop vat = new VAlignTop( vati );
    VAlignCenter vac = new VAlignCenter( vaci );
    VAlignBottom vab = new VAlignBottom( vabi );

    setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gridBagConstraints;

    //JPanel h_panel = new JPanel();
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);

    add( createJButton( hal, "Horizontal Left"),gridBagConstraints);   
    add( createJButton( hac, "Horizontal Center"),gridBagConstraints);
    add( createJButton( har, "Horizontal Right"),gridBagConstraints);
 
    //JPanel v_panel = new JPanel();
    add( createJButton( vat, "Vertical Top"),gridBagConstraints);
    add( createJButton( vac, "Vertical Center"),gridBagConstraints);
    add( createJButton( vab, "Vertical Bottom"),gridBagConstraints);

    //setLayout( new BorderLayout() );
    //add( h_panel, BorderLayout.EAST );
    //add( v_panel, BorderLayout.WEST );

    setBorder( new TitledBorder( "Align" ) );

  }

  protected JButton createJButton ( Action a, String tt ) {
    JButton b = new JButton( a );
    b.setToolTipText( tt );
    b.setPreferredSize( new Dimension( 27, 18 ) );
    return b;
  }


}
    

