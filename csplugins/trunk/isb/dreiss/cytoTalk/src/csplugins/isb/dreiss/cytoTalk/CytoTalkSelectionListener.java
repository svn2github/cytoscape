package csplugins.isb.dreiss.cytoTalk;

public interface CytoTalkSelectionListener {
   public boolean nodeSelected( String canonicalName, boolean selected );
   public boolean edgeSelected( String canonicalName, boolean selected );
}

