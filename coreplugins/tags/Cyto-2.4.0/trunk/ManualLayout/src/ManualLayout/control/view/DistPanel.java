package ManualLayout.control.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import ManualLayout.control.actions.dist.*;
import cytoscape.view.*;

public class DistPanel extends JPanel {

  public DistPanel ( ) {

    ImageIcon hali =  new ImageIcon( getClass().getResource("/H_DIST_LEFT.gif") );
    ImageIcon haci =  new ImageIcon( getClass().getResource("/H_DIST_CENTER.gif") );
    ImageIcon hari =  new ImageIcon( getClass().getResource("/H_DIST_RIGHT.gif") );
    ImageIcon vati =  new ImageIcon( getClass().getResource("/V_DIST_TOP.gif") );
    ImageIcon vaci =  new ImageIcon( getClass().getResource("/V_DIST_CENTER.gif") );
    ImageIcon vabi =  new ImageIcon( getClass().getResource("/V_DIST_BOTTOM.gif") );

    HDistLeft hal = new HDistLeft( hali );
    HDistCenter hac = new HDistCenter( haci );
    HDistRight har = new HDistRight( hari );

    VDistTop vat = new VDistTop( vati );
    VDistCenter vac = new VDistCenter( vaci );
    VDistBottom vab = new VDistBottom( vabi );

    setLayout(new java.awt.GridBagLayout());
    //JPanel h_panel = new JPanel();
    java.awt.GridBagConstraints gridBagConstraints;
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);

    add( createJButton( hal, "Horizontal Left"), gridBagConstraints);
    add( createJButton( hac, "Horizontal Center"), gridBagConstraints);
    add( createJButton( har, "Horizontal Right"), gridBagConstraints);
    //JPanel v_panel = new JPanel();
    add( createJButton( vat, "Vertical Top"), gridBagConstraints);
    add( createJButton( vac, "Vertical Center"), gridBagConstraints);
    add( createJButton( vab, "Vertical Bottom"), gridBagConstraints);

    //setLayout( new BorderLayout() );
    //add( h_panel, BorderLayout.EAST );
    //add( v_panel, BorderLayout.WEST );

    setBorder( new TitledBorder( "Distribute" ) );

  }

  protected JButton createJButton ( Action a, String tt ) {
    JButton b = new JButton( a );
    b.setToolTipText( tt );
    b.setPreferredSize( new Dimension( 27, 18 ) );
    return b;
  }


}
    

