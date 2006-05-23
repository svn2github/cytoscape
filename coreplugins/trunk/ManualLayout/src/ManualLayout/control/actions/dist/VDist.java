package ManualLayout.control.actions.dist;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cytoscape.data.*;

import giny.view.*;
import giny.model.*;

import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import ManualLayout.control.actions.AbstractControlAction;

public class VDist extends AbstractControlAction {

  public VDist ( ImageIcon icon ) {
    super( icon );
  }

  protected void control ( List nodes ) {

    Collections.sort(nodes, new YComparator());

    double d = Y_max - Y_min;
    d = d / (nodes.size()-1);

    for ( int i = 0; i < nodes.size(); i++ ) {
       ( ( NodeView )nodes.get(i) ).setYPosition( Y_min + i*d );
    }
  }
  private class YComparator implements Comparator {
        public int compare(Object o1, Object o2) {

                NodeView n1 = (NodeView)o1;
                NodeView n2 = (NodeView)o2;

                if ( n1.getYPosition() == n2.getYPosition() )
                        return 0;
                else if ( n1.getYPosition() < n2.getYPosition() )
                        return -1;
                else
                        return 1;
        }

        public boolean equals(Object o1, Object o2) {
                NodeView n1 = (NodeView)o1;
                NodeView n2 = (NodeView)o2;
                if ( n1.getYPosition() == n2.getYPosition() )
                        return true;
                else
                        return false;
        }
  }

}
