
package org.isb.bionet.gui.wizard;

import javax.swing.*;
import java.util.Hashtable;
import org.isb.bionet.datasource.synonyms.SynonymsSource;

public class AttributesPanel extends JPanel{
    
    // Attributes to add to nodes and/or edges
    // 1. Label name for nodes and edges. SYNONYMS
    // 2. IDs in other dbs for nodes (for example: SPROT, TrEMBL, PIR, GenBank ids, ENSEMBL, etc.) SYNONYMS
    // 3. GenBank/RefSeq definition for nodes SYNONYMS
    // 4. GO annotation for nodes SYNONYMS and GO
    // 5. Score's for edges INTERACTIONS
    // 6. Data source for edges INTERACTIONS
    // 7. Genename SYNONYMS
    // 8. Function (iProClass) SYNONYMS
    // 9. Pathway (iProClass) SYNONYMS and INTERACTIONS
    // 10. Product names SYNONYMS
    
    public static final String XREFS = "XREFS";
    public static final String DEFS = "Definition";
    public static final String HPFP = "HPFP";
    public static final String ENCODED_BY = "Encoded by";
    public static final String GENE_NAME = SynonymsSource.GENE_NAME;
    public static final String PROD_NAME = SynonymsSource.PROD_NAME;
    public static final String LOCUS_NAME = SynonymsSource.ORF_ID;
    public static final String DB_URLS = "DB URLs";
    
    protected JRadioButton xrefs, definition,hpfp,dburls, encodedBy, geneName, prodName, locusName;
    
    public AttributesPanel (){
        create();
    }
    
    public Hashtable getSelectedAttributesTable (){
        Hashtable table = new Hashtable();
        table.put(XREFS, new Boolean(getAddXrefs()));
        table.put(DEFS, new Boolean(getAddDefinition()));
        table.put(HPFP,new Boolean(getAddHPF()));
        table.put(ENCODED_BY, new Boolean(getAddGeneName()));
        table.put(GENE_NAME, new Boolean(getAddProdName()));
        table.put(PROD_NAME, new Boolean(getAddProdName()));
        table.put(DB_URLS, new Boolean(getAddDbUrls()));
        table.put(LOCUS_NAME, new Boolean(getAddLocusTag()));
        return table;
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
    
    public boolean getAddEncodedBy (){
        return this.encodedBy.isSelected();
    }
    
    public boolean getAddGeneName (){
        return this.geneName.isSelected();
    }
    
    public boolean getAddProdName (){
        return this.prodName.isSelected();
    }
    
    public boolean getAddLocusTag (){
        return this.locusName.isSelected();
    }
    
    protected void create (){
        JPanel attsPanel = createAttsPanel();
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        add(attsPanel);     
    }
    
    protected JPanel createAttsPanel (){
        
        this.xrefs = new JRadioButton("Attach available cross reference IDs to nodes.");
        this.geneName = new JRadioButton("Attach gene name to nodes.");
        this.prodName = new JRadioButton("Attach product name to nodes.");
        this.locusName = new JRadioButton("Attach locus tag to nodes.");
        this.encodedBy = new JRadioButton("Attach RefSeq accession of encoding molecules.");
        this.definition = new JRadioButton("Attach a RefSeq \"definition\" node attribute.");
        this.hpfp = new JRadioButton("<html>Attach a Proteome Folding Project URL (\"HPFP_URL\")<br>to nodes.</html>");
        this.hpfp.setSelected(true);
        this.dburls = new JRadioButton("Attach available database URLs to nodes.");
        this.dburls.setSelected(true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(this.xrefs);
        panel.add(this.geneName);
        panel.add(this.prodName);
        panel.add(this.locusName);
        panel.add(this.encodedBy);
        panel.add(this.definition);
        panel.add(this.hpfp);
        panel.add(this.dburls);
        return panel;
    }
    
    
}