package control.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import control.actions.align.*;
import cytoscape.view.*;

public class AlignPanel extends JPanel {

  CyNetworkView window;

  public AlignPanel ( CyNetworkView window ) {

    this.window = window;

    ImageIcon hari =  new ImageIcon( getClass().getResource("/H_ALIGN_RIGHT.gif") );
    ImageIcon haci =  new ImageIcon( getClass().getResource("/H_ALIGN_CENTER.gif") );
    ImageIcon hali =  new ImageIcon( getClass().getResource("/H_ALIGN_LEFT.gif") );
    ImageIcon vati =  new ImageIcon( getClass().getResource("/V_ALIGN_TOP.gif") );
    ImageIcon vaci =  new ImageIcon( getClass().getResource("/V_ALIGN_CENTER.gif") );
    ImageIcon vabi =  new ImageIcon( getClass().getResource("/V_ALIGN_BOTTOM.gif") );

    HAlignRight har = new HAlignRight( window, hari );
    HAlignCenter hac = new HAlignCenter( window, haci );
    HAlignLeft hal = new HAlignLeft( window, hali );

    VAlignTop vat = new VAlignTop( window, vati );
    VAlignCenter vac = new VAlignCenter( window, vaci );
    VAlignBottom vab = new VAlignBottom( window, vabi );

    JPanel h_panel = new JPanel();
    h_panel.add( createJButton( hal, "Horizontal Left") );   
    h_panel.add( createJButton( hac, "Horizontal Center") );
    h_panel.add( createJButton( har, "Horizontal Right") );
 
    JPanel v_panel = new JPanel();
    v_panel.add( createJButton( vat, "Vertical Top") );
    v_panel.add( createJButton( vac, "Vertical Center") );
    v_panel.add( createJButton( vab, "Vertical Bottom") );

    setLayout( new BorderLayout() );
    add( h_panel, BorderLayout.EAST );
    add( v_panel, BorderLayout.WEST );

    setBorder( new TitledBorder( "Align Nodes" ) );

  }

  protected JButton createJButton ( Action a, String tt ) {
    JButton b = new JButton( a );
    b.setToolTipText( tt );
    b.setPreferredSize( new Dimension( 27, 18 ) );
    return b;
  }


}
    

