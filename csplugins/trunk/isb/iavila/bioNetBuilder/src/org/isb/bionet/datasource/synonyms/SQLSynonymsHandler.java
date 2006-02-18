
package org.isb.bionet.datasource.synonyms;

import java.util.*;
import java.sql.*;

import org.isb.bionet.datasource.*;
import org.isb.xmlrpc.handler.db.*;


public class SQLSynonymsHandler extends SQLDBHandler implements SynonymsSource {
    
    protected final Hashtable SUPPORTED_TRANSLATIONS = new Hashtable();
    
    
    /**
     * Empty constructor
     */
    public SQLSynonymsHandler() {
        
        super("jdbc:mysql:///metainfo?user=cytouser&password=bioNetBuilder", SQLDBHandler.MYSQL_JDBC_DRIVER);
        
        // Look for the current go database
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"synonyms\"");
        String currentSynDb = null;
        try{
           if(rs.next()){
               currentSynDb = rs.getString(1); 
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Current synonyms database is: [" + currentSynDb + "]");
        if(currentSynDb == null || currentSynDb.length() == 0){
            throw new IllegalStateException("Oh no! We don't know the name of the current synonyms database!!!!!");
        }
        execute("USE " + currentSynDb);  
        
        this.SUPPORTED_TRANSLATIONS.put(PROLINKS_ID+":"+GI_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+PROLINKS_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(KEGG_ID+":"+GI_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+KEGG_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+PROD_NAME, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+GENE_NAME, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+SPROT_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+TREMBL_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+UNIPROT_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(REFSEQ_ID+":"+ GI_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID+":"+ REFSEQ_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(GI_ID + ":" + PIR_ID, Boolean.TRUE);
        this.SUPPORTED_TRANSLATIONS.put(ORF_ID + ":" + GI_ID, Boolean.TRUE);
    }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public SQLSynonymsHandler(String mysql_url) {
        super(mysql_url, SQLDBHandler.MYSQL_JDBC_DRIVER);
    }
    
    //------------- SynonymsSource methods ---------//
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param target_id_type one of the supported id types
     * @return true if a translation from source_id_type to target_id_type is supported, false otherwise
     */
    public Boolean translationIsSupported (String source_id_type, String target_id_type){
        if(this.SUPPORTED_TRANSLATIONS.contains(source_id_type+":"+target_id_type)) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param source_ids a Vector of ids of type source_id_type
     * @param target_id_type the type of id that the input ids should be translated to
     * @return a Hashtable with (source_id,Vector) entries, the vector contains all translations of the source_id
     * to the target_id (most translations are one-to-many)
     * NOTE: source_id_type cannot be COMMON_NAME.
     */
    // This is not very elegant. For every new ID type, need to add all pairs. Maybe we only care from
    // gi to all others, and back? This would mean Synonyms would need to have a "canonical" id type.
    // Not nice. What is the best way?????
    public Hashtable getSynonyms (String source_id_type, Vector source_ids, String target_id_type){
        
        if(source_ids.size() == 0) return new Hashtable();
        
        if(source_id_type.equals(PROLINKS_ID) && target_id_type.equals(GI_ID)){
            return prolinksToGi(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(PROLINKS_ID)){
            return giToProlinks(source_ids);
        }
        
        if(source_id_type.equals(KEGG_ID) && target_id_type.equals(GI_ID)){
            return keggToGi(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(KEGG_ID)){
            return giToKegg(source_ids);
        }
        
        // Common name to id? Not reliable. User needs to keep track of this map.
        if(source_id_type.equals(GI_ID) && target_id_type.equals(GENE_NAME)){
            return getGeneNames(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(PROD_NAME)){
            return getProdNames(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(ORF_ID)){
            return giToOrf(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(TREMBL_ID)){
            return giToTrembl(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(SPROT_ID)){
            return giToSprot(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(UNIPROT_ID)){
            return giToUniprot(source_ids);
        }
        
        if(source_id_type.equals(REFSEQ_ID) && target_id_type.equals(GI_ID)){
            return refseqToGi(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(REFSEQ_ID)){
            return giToRefseq(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) &&target_id_type.equals(PIR_ID)){
            return giToPir(source_ids);
        }
        
        if(source_id_type.equals(ORF_ID) && target_id_type.equals(GI_ID)){
            return orfToGi(source_ids);
        }
        
        System.out.println("Translation from " + source_id_type + " to " + target_id_type + "is not supported!");
    
        return new Hashtable();
    
   }
    
    /**
     * 
     * @param id an ID
     * @return one of:<br>
     * PROLINKS_ID, KEGG_ID, GI_ID, or ID_NOT_FOUND
     */
    public String getIdType (String id){
        String [] tokens = id.split(":");
        if(tokens.length == 0) return ID_NOT_FOUND;
        if(tokens[0].equals(PROLINKS_ID)) return PROLINKS_ID;
        if(tokens[0].equals(KEGG_ID)) return KEGG_ID;
        if(tokens[0].equals(GI_ID)) return GI_ID;
        if(tokens[0].equals(GENE_NAME)) return GENE_NAME;
        if(tokens[0].equals(PROD_NAME)) return PROD_NAME;
        return ID_NOT_FOUND;
    }
    
    /**
     * @param pattern a pattern to match against
     * @return a Vector of species that match the given pattern, each element in the Vector is a Hashmap with elements:<br>
     * TAXID->String readable as integer
     * SPECIES_NAME->String, human readable name of matching species
     */
    public Vector getSpeciesLike (String pattern){
        
        String sqlPattern = null;
        
        if(pattern == null || pattern.length() == 0){
            sqlPattern = "%";
        }else{
        
            String [] words = pattern.split("\\s");
            sqlPattern= "";
            for(int i = 0; i < words.length; i++){
                sqlPattern += "%" + words[i];
            }
            sqlPattern += "%";
        }
        
        String sql = "SELECT * FROM ncbi_taxid_species WHERE name LIKE \"" + sqlPattern + "\""; 
        ResultSet rs = query(sql);
        
        Vector species = new Vector();
        
        try{
            while(rs.next()){
                int taxid = rs.getInt(1);
                String scientificName = rs.getString(2);
                Hashtable entry = new Hashtable();
                entry.put(TAXID, String.valueOf(taxid));
                entry.put(SPECIES_NAME, scientificName);
                species.add(entry);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Vector();
        }
        
        return species;
    
    }
    
    /**
     * @param species_taxid the taxid of the species in which to look for genes
     * @param pattern the pattern to match against
     * @return a Hashtable that has as a key a String that represents a GI_ID, and as a value the gene/product name that matched the pattern, if the pattern
     * contains commas, then it is considered to be a list of comma separated patterns
     */
    public Hashtable getGenesLike (String species_taxid, String pattern){
        
        if(species_taxid == null || species_taxid.length() == 0) return new Hashtable();
        
        String sqlPattern = null;
        String like = "";
        
        if(pattern == null || pattern.length() == 0){
            sqlPattern = "%"; // any character
            like = " LIKE %";
        }else{
          String [] patterns = pattern.split(",");
          like = "";
          for(int i = 0; i < patterns.length; i++){
              String [] words = patterns[i].split("\\s");
              sqlPattern= "";
              for(int j = 0; j < words.length; j++){
                  sqlPattern += "%" + words[j];          
              }
              sqlPattern += "%";
              if(like.length() == 0){
                  like += " LIKE \"" + sqlPattern + "\"";
              }else{
                  like += " OR genename.genename LIKE \"" + sqlPattern + "\"";
              }
          }
        }
            
       // 1. Look in refseq_genename, refseq_prodname
        String sql = "SELECT genename.protgi,genename.genename"+ 
            " FROM refseq_genename AS genename, refseq_taxid AS taxid" + 
            " WHERE taxid.taxid = " + species_taxid + " AND taxid.protgi = genename.protgi AND (genename.genename " + like + ")";
        ResultSet rs = query(sql);
        Hashtable giToName = new Hashtable();
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String name = rs.getString(2);
                giToName.put(protgi,name);
            }    
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        return giToName;
      
    }
    
    /**
     * Gets a Hashtable of (gi, Vector of genenames) entries
     */
    public Hashtable getGeneNames (Vector gis){
        
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        
        if(inStatement.length() == 0) return new Hashtable();
        
        String sql = "SELECT protgi,genename FROM refseq_genename WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable names = new Hashtable();
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String name = rs.getString(2);
                Vector nv = (Vector)names.get(protgi);
                if(nv == null){
                    nv = new Vector();
                    names.put(protgi,nv);
                }
                nv.add(name);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // GenBank and the existing data sources have a very small overlap, so I will ignore it for now
        return names;
    }
    /**
     * Gets a Hashtable of (gi, Vector of prodnames) entries
     */
    public Hashtable getProdNames (Vector gis){
        
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        String sql = "SELECT protgi,prodname FROM refseq_prodname WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable names = new Hashtable();
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String name = rs.getString(2);
                Vector pv = (Vector)names.get(protgi);
                if(pv == null){
                    pv = new Vector();
                    names.put(protgi,pv);
                }
                pv.add(name);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // GenBank and the existing data sources have a very small overlap, so I will ignore it for now
        return names;
    }
    
    /**
     * Get a Hashtable of (gi, codedby id) entries
     */
    public Hashtable getEncodedBy (Vector gis){
        
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        String sql = "SELECT protgi,codedby FROM refseq_codedby WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable names = new Hashtable();
        try{
            while(rs.next()){
                String protgi = rs.getString(1);
                String name = rs.getString(2);
                names.put(GI_ID + ":" + protgi,name);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // GenBank and the existing data sources have a very small overlap, so I will ignore it for now
        return names;
    }
    
    /**
     * Gets a Hashtable of (gi, String) entries
     * @param gis
     * @return a Hashtable of (gi, String) entries
     */
    public Hashtable getDefinitions (Vector gis){
        
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        String sql = "SELECT protgi,definition FROM refseq_definition WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable defs = new Hashtable();
        try{
            while(rs.next()){
                String protgi = rs.getString(1);
                String def = rs.getString(2);
                defs.put(GI_ID + ":" + protgi, def);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // GenBank and the existing data sources have a very small overlap, so I will ignore it for now
        return defs;
    }
    
    /**
     * Gets a Hashtable of (gi, Vector of xref ids) entries
     * @param gis
     * @return a Hashtable of (gi, Vector of xref ids) entries
     */
    public Hashtable getXrefIds (Vector gis){
        
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        // RefSeq accession
        
        Hashtable xrefs = new Hashtable();
        String sql = "SELECT protgi, accession FROM refseq_accession WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String acc = rs.getString(2);
                Vector ids = new Vector();
                ids.add(REFSEQ_ID + ":" + acc);
                xrefs.put(protgi, ids);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // RefSeq GeneID
        sql = "SELECT protgi, geneid FROM refseq_geneid WHERE protgi IN (" + inStatement + ")";
        rs = query(sql);
        
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String geneid = rs.getString(2);
                Vector ids = (Vector)xrefs.get(protgi);
                if(ids == null) ids = new Vector();
                ids.add(GENE_ID + ":" + geneid);
                xrefs.put(protgi, ids);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // RefSeq HGNC
        sql = "SELECT protgi, hgncid FROM refseq_hgnc WHERE protgi IN (" + inStatement + ")";
        rs = query(sql);
        
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String id = rs.getString(2);
                Vector ids = (Vector)xrefs.get(protgi);
                if(ids == null) ids = new Vector();
                ids.add(HGNC_ID+ ":" + id);
                xrefs.put(protgi, ids);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // RefSeq HPRD
        sql = "SELECT protgi, hprdid FROM refseq_hprd WHERE protgi IN (" + inStatement + ")";
        rs = query(sql);
        
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String id = rs.getString(2);
                Vector ids = (Vector)xrefs.get(protgi);
                if(ids == null) ids = new Vector();
                ids.add(HPRD_ID+ ":" + id);
                xrefs.put(protgi, ids);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // KEGG
        sql = "SELECT gi, keggid FROM kegg_gi WHERE gi IN (" + inStatement + ")";
        rs = query(sql);
        
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String id = rs.getString(2);
                Vector ids = (Vector)xrefs.get(protgi);
                if(ids == null) ids = new Vector();
                ids.add(KEGG_ID+ ":" + id);
                xrefs.put(protgi, ids);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // Prolinks
        sql = "SELECT protgi, prolinksid FROM prolinks_protgi WHERE protgi IN (" + inStatement + ")";
        rs = query(sql);
        
        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String id = rs.getString(2);
                Vector ids = (Vector)xrefs.get(protgi);
                if(ids == null) ids = new Vector();
                ids.add(PROLINKS_ID + ":" + id);
                xrefs.put(protgi, ids);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        giToUniprot(gis);
        
        // Uniprot
        Hashtable giToUniprot = giToUniprot(gis);
        it = giToUniprot.keySet().iterator();
        while(it.hasNext()){
            String gi = (String)it.next();
            Vector uniprots = (Vector)giToUniprot.get(gi);
            Vector ids = (Vector)xrefs.get(gi);
            if(ids == null) { ids = new Vector(); xrefs.put(gi,ids);}
            ids.addAll(uniprots);
        }
        
        //  PIR
        Hashtable giToPir = giToPir(gis);
        it = giToPir.keySet().iterator();
        while(it.hasNext()){
            String gi = (String)it.next();
            Vector pirs = (Vector)giToPir.get(gi);
            Vector ids = (Vector)xrefs.get(gi);
            if(ids == null) { ids = new Vector(); xrefs.put(gi,ids);}
            ids.addAll(pirs);
        }
        
        return xrefs;
        
    }
    
    //--------- Helper protected methods -----------//
    
    protected Hashtable orfToGi (Vector orfs){
        Iterator it = orfs.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(ORF_ID + ":");
            if(index >= 0){
                id = id.substring(index + ORF_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = "\"" + id + "\"";
                else inStatement += ",\"" + id + "\"";
            }
        }//while
       
        if(inStatement.length() == 0) return new Hashtable();
        
        String sql = "SELECT locustag, protgi FROM refseq_locustag WHERE locustag IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable ltToGi = new Hashtable();
        try{
            while(rs.next()){
                String orf = ORF_ID + ":" + rs.getString(1);
                String gi = GI_ID + ":" + rs.getString(2);
                Vector gis = (Vector)ltToGi.get(orf);
                if(gis == null){gis = new Vector(); ltToGi.put(orf,gis);}
                gis.add(gi);
            }
        }catch(Exception e){e.printStackTrace();return new Hashtable();}
        
        return ltToGi;
         
    }
    
    protected Hashtable giToPir (Vector gis ){
       
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
       
        if(inStatement.length() == 0) return new Hashtable();
        
        String sql = "SELECT protgi, pirid FROM gi2pir WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable giToPir = new Hashtable();
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String pir = PIR_ID + ":" + rs.getString(2);
                Vector pirs = (Vector)giToPir.get(gi);
                if(pirs == null){pirs = new Vector(); giToPir.put(gi,pirs);}
                pirs.add(pir);
            }
        }catch(Exception e){e.printStackTrace();return new Hashtable();}
        
        return giToPir;
        
    }
    
    
    
    protected Hashtable giToRefseq (Vector ids){
        
        Iterator it = ids.iterator();
        if(!it.hasNext()) return new Hashtable(); 
        
        String inStatement = "";
        
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(GI_ID + ":");
            if(index >= 0){
                id = id.substring(index + GI_ID.length() + 1);
                if(inStatement.length() == 0) {
                    inStatement =  id;
                }else{ 
                    inStatement +=  "," + id;
                }  
            }
        }//while
        
        System.out.println("inStatement.length = [" + inStatement.length() + "]");
        
        if(inStatement.length() == 0) return new Hashtable();
        
        String sql = "SELECT protgi, accession FROM refseq_accession WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable giToAcc =  new Hashtable();
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String acc = REFSEQ_ID + ":" + rs.getString(2);
                Vector accs = (Vector)giToAcc.get(gi);
                if(accs == null) { accs = new Vector(); giToAcc.put(gi,accs);}
                accs.add(acc);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new Hashtable();
        }
        return giToAcc;
    }
    
    protected Hashtable refseqToGi (Vector ids){
        Iterator it = ids.iterator();
        if(!it.hasNext()) return new Hashtable(); 
        String inStatement = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(REFSEQ_ID + ":");
            if(index >= 0){
                id = id.substring(index + REFSEQ_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = "\"" + id + "\"";
                else inStatement += ",\"" + id + "\"";
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        String sql = "SELECT accession, protgi FROM refseq_accession WHERE accession IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        Hashtable accToGi =  new Hashtable();
        try{
            while(rs.next()){
                String acc = REFSEQ_ID + ":" + rs.getString(1);
                String gi = GI_ID + ":" + rs.getString(2);
                Vector gis = (Vector)accToGi.get(acc);
                if(gis == null) { gis = new Vector(); accToGi.put(acc,gis);}
                gis.add(gi);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new Hashtable();
        }
        return accToGi;
    }
    
    protected Hashtable giToUniprot (Vector gis){
        
        Hashtable giToUniprot = new Hashtable();
        
        Hashtable giToTrembl = giToTrembl(gis);
        Iterator it = giToTrembl.keySet().iterator();
        while(it.hasNext()){
            String gi = (String)it.next();
            Vector ids = (Vector)giToTrembl.get(gi);
            Iterator it2 = ids.iterator();
            Vector newIds = new Vector();
            while(it2.hasNext()){
                String id = (String)it2.next();
                id = id.replaceFirst(TREMBL_ID,UNIPROT_ID);
                newIds.add(id);
            }
            giToUniprot.put(gi,newIds);
        }
        
        Hashtable giToSprot = giToSprot(gis);
        it = giToSprot.keySet().iterator();
        while(it.hasNext()){
            String gi = (String)it.next();
            Vector ids = (Vector)giToSprot.get(gi);
            Iterator it2 = ids.iterator();
            Vector newIds = new Vector();
            while(it2.hasNext()){
                String id = (String)it2.next();
                id = id.replaceFirst(SPROT_ID,UNIPROT_ID);
                newIds.add(id);
            }
            giToUniprot.put(gi,newIds);
        }
       
        return giToUniprot;
    }
    
    
    protected Hashtable giToTrembl (Vector gis){
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length()+ 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        String sql = "SELECT protgi, tremblac FROM gi2trembl WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        
        Hashtable table = new Hashtable();
        
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String tremblid = TREMBL_ID + ":" + rs.getString(2);
                Vector ids = (Vector)table.get(gi);
                if(ids == null) { ids = new Vector(); table.put(gi, ids);}
                ids.add(tremblid);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new Hashtable();
        }
        return table;
        
    }
    
    protected Hashtable giToSprot (Vector gis){
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String inStatement = "";
        while(it.hasNext()){
            String giID = (String)it.next();
            int index = giID.indexOf(GI_ID + ":");
            if(index >= 0){
                giID = giID.substring(index + GI_ID.length() + 1);
                if(inStatement.length() == 0) inStatement = giID;
                else inStatement += "," + giID;
            }
        }//while
        if(inStatement.length() == 0) return new Hashtable();
        String sql = "SELECT protgi, sprotac FROM gi2sprot WHERE protgi IN (" + inStatement + ")";
        ResultSet rs = query(sql);
        
        Hashtable table = new Hashtable();
        
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String sprotid = SPROT_ID + ":" + rs.getString(2);
                Vector ids = (Vector)table.get(gi);
                if(ids == null) { ids = new Vector(); table.put(gi, ids);}
                ids.add(sprotid);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new Hashtable();
        }
        return table;
        
    }
    
    /**
     * Gets a Hashtable of (gi, Vector of Orfs) entries
     */
    protected Hashtable giToOrf (Vector gis){
        Iterator it = gis.iterator();
        if(!it.hasNext())
            return new Hashtable();
        
        String in = "";
        while(it.hasNext()){
            String gi = (String)it.next();
            int index = gi.indexOf(GI_ID + ":");
            if(index >= 0){
                gi = gi.substring(index+GI_ID.length() + 1);
                if(in.length() > 0){
                    in +=  "," + gi;
                }else{
                    in = gi;
                }//else
            }//if
        }//while
        
        if(in.length() == 0) return new Hashtable();
        String sql = "SELECT protgi, locustag FROM refseq_locustag WHERE protgi IN (" + in + ")";
        ResultSet rs = query(sql);
        
        Hashtable giToLocus = new Hashtable();
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String lt =  rs.getString(2);
                Vector locuses = (Vector)giToLocus.get(gi);
                if(locuses == null){
                    locuses = new Vector();
                    giToLocus.put(gi,locuses);
                }
                locuses.add(lt);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new Hashtable();
        }
        return giToLocus;
        
    }
    
    /**
     * Gets a Hashtable of (prolink_id, Vector of gis) entries
     * @param prolinks_ids
     * @return Hashtable of (prolink_id, Vector of gis) entries
     */
    protected Hashtable prolinksToGi (Vector prolinks_ids){
        
        Iterator it = prolinks_ids.iterator();
        if(!it.hasNext())
            return new Hashtable();
        
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(PROLINKS_ID + ":");
            if(index >= 0){
                id = id.substring(index+PROLINKS_ID.length() + 1);
                if(or.length() > 0){
                    or += " OR prolinksid = " + id;
                }else{
                    or = " prolinksid = " + id;
                }//else
            }//if
        }//while
        
        if(or.length() == 0) return new Hashtable();
        String sql = "SELECT prolinksid, protgi FROM prolinks_protgi WHERE" + or;
        
        ResultSet rs = query(sql);
        try{
            Hashtable pToG = new Hashtable();
            while(rs.next()){
                String p = PROLINKS_ID + ":" + rs.getString(1);
                String gi = GI_ID + ":" + rs.getString(2);
                if(!gi.endsWith(":0")){
                    Vector gis = (Vector)pToG.get(p);
                    if(gis == null){
                        gis = new Vector();
                        pToG.put(p,gis);
                    }
                    gis.add(gi);
                }
            }
            return pToG;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
           
    }
    
    /**
     * Gets a Hashtable with (gi_id, Vector of prolinks_ids) entries
     * @param gi_ids
     * @return a Hashtable with (gi_id, Vector of prolinks_ids) entries
     */
    protected Hashtable giToProlinks (Vector gi_ids){
        
        Iterator it = gi_ids.iterator();
        if(!it.hasNext())
            return new Hashtable();
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(GI_ID + ":");
            if(index >= 0){
                id = id.substring(index + GI_ID.length() + 1);
                if(or.length() > 0)
                    or += " OR protgi = " + id;
                else
                    or = " protgi = " + id;
            }
         
        }
        
        if(or.length() == 0) return new Hashtable();
        
        String sql = "SELECT protgi,prolinksid FROM prolinks_protgi WHERE" + or;
        
        ResultSet rs = query(sql);
        try{
            Hashtable gToP = new Hashtable();
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String p = PROLINKS_ID + ":" + rs.getString(2);
                Vector pids = (Vector)gToP.get(gi);
                if(pids == null){
                    pids = new Vector();
                    gToP.put(gi, pids);
                }
                pids.add(p);
            }
            return gToP;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
           
    }
   
    /**
     * Get a Hashtable of (keggid, Vector of gis) entries
     * @param kegg_ids
     * @return a Hashtable of (keggid, Vector of gis) entries
     */
    protected Hashtable keggToGi (Vector kegg_ids){
        
        Iterator it = kegg_ids.iterator();
        
        if(!it.hasNext()) return new Hashtable();
       
        String or = ""; 
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(KEGG_ID + ":");
            if(index >= 0){
                id = id.substring(index + KEGG_ID.length() + 1);
                if(or.length() > 0)
                    or += " OR keggid = \"" + id + "\"";
                else
                    or = " keggid = \"" + id + "\"";
            }
            
        }
        
        if(or.length() == 0) return new Hashtable();
        
        String sql = "SELECT keggid, gi FROM kegg_gi WHERE " + or;
        ResultSet rs = query(sql);
        
        try{
            Hashtable kToG = new Hashtable();
            while(rs.next()){
                String keggid = KEGG_ID + ":" + rs.getString(1);
                String gi = GI_ID + ":" + rs.getString(2);
                Vector gis = (Vector)kToG.get(keggid);
                if(gis == null){
                    gis = new Vector();
                    kToG.put(keggid,gis);
                }
                gis.add(gi);
            }
            return kToG;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
    }
    
    /**
     * Get a Hashtable of (gi, Vector of keggids) entries
     * @param gi_ids
     * @return a Hashtable of (gi, Vector of keggids) entries
     */
    protected Hashtable giToKegg (Vector gi_ids){
        
        Iterator it = gi_ids.iterator();
        
        if(!it.hasNext()) return new Hashtable();
        
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(GI_ID + ":");
            if(index >= 0){
                id = id.substring(index + GI_ID.length() + 1);
                if(or.length() > 0)
                    or += " OR gi = " + id;
                else
                    or = " gi = " + id;
            }
            
        }
        if(or.length() == 0) return new Hashtable();
        String sql = "SELECT gi, keggid FROM kegg_gi WHERE " + or;
        ResultSet rs = query(sql);
        
        try{
            Hashtable gToK = new Hashtable();
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                String keggid = KEGG_ID  + ":" + rs.getString(2);
                Vector kids = (Vector)gToK.get(gi);
                if(kids == null){
                    kids = new Vector();
                    gToK.put(gi,kids );
                }
                kids.add(keggid);
            }
            return gToK;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
    }
    
   
    //------------- DataSource methods -------------//

    /**
     * @return the name of the data source, for example, "KEGG", "Prolinks", etc.
     */
    public String getDataSourceName (){
        return "Synonyms";
    }
    
    /**
     * @return the type of backend implementation (how requests to the data source
     * are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB, MEMORY, MIXED
     */
    //TODO: Need to figure out if DB is running locally or remotely
    public String getBackendType (){
        return DataSource.REMOTE_DB; // for now
    }

    /**
     * @return an empty Vector
     */
    public Vector getSupportedSpecies (){
        return new Vector();
    }
    
    /**
     * 
     * @return
     */
    public Boolean supportsSpecies (String sp){
        // for now. Maybe move this method from DataSource?
        return Boolean.FALSE;
    }

    /**
     * @return a String denoting the version of the data source (could be a release date,
     * a version number, etc).
     */

    public String getVersion (){
        return ""; //for now
    }

    /**
     * @return false
     */
    public boolean requiresPassword (){
        return false;
    }

    /**
     * Runs tests on the data source
     * @return a vector of results
     */
    public Vector test (){
        return new Vector();
    }
    
}