
package org.isb.bionet.gui.wizard;

import javax.swing.*;

public class AttributesPanel extends JPanel{
    
    // Attributes to add to nodes and/or edges
    // 1. Label name for nodes and edges. SYNONYMS
    // 2. IDs in other dbs for nodes (for example: SPROT, TrEMBL, PIR, GenBank ids, ENSEMBL, etc.) SYNONYMS
    // 3. GenBank definition for nodes SYNONYMS
    // 4. GO annotation for nodes SYNONYMS and GO
    // 5. Score's for edges INTERACTIONS
    // 6. Data source for edges INTERACTIONS
    // 7. Genename SYNONYMS
    // 8. Function (iProClass) SYNONYMS
    // 9. Pathway (iProClass) SYNONYMS and INTERACTIONS
    // 10. Product names SYNONYMS
    protected JRadioButton xrefs, definition,hpfp,dburls;
    
    public AttributesPanel (){
        create();
    }
    
    public boolean getAddXrefs (){
        return this.xrefs.isSelected();
    }
    
    public boolean getAddDefinition (){
        return this.definition.isSelected();
    }
    
    public boolean getAddHPF (){
        return this.hpfp.isSelected();
    }
    
    public boolean getAddDbUrls (){
        return this.dburls.isSelected();
    }
    
    protected void create (){
        JPanel attsPanel = createAttsPanel();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(attsPanel);     
    }
    
    protected JPanel createAttsPanel (){
        
        this.xrefs = new JRadioButton("Attach database cross reference IDs to nodes.");
        this.definition = new JRadioButton("Attach a GenBank \"definition\" node attribute.");
        this.hpfp = new JRadioButton("Attach a Human Proteome Folding Project URL to nodes.");
        this.dburls = new JRadioButton("Attach database URLs to nodes.");
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(this.xrefs);
        panel.add(this.definition);
        panel.add(this.hpfp);
        panel.add(this.dburls);
        
        return panel;
    }
    
    
}