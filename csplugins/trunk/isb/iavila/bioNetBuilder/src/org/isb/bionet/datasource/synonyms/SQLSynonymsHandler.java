
package org.isb.bionet.datasource.synonyms;

import java.util.*;
import java.sql.*;

import org.isb.bionet.datasource.*;
import org.isb.xmlrpc.handler.db.*;
// TODO:
// Add method translationIsSupported(sourceIDtype,targetIDtype);

public class SQLSynonymsHandler extends SQLDBHandler implements SynonymsSource {
    
    protected final Hashtable SUPPORTED_TRANSLATIONS = new Hashtable();
    
    
    /**
     * Empty constructor
     */
    public SQLSynonymsHandler() {
        
        super("jdbc:mysql://wavelength.systemsbiology.net/metainfo?user=cytouser&password=bioNetBuilder", SQLDBHandler.MYSQL_JDBC_DRIVER);
        
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
     * @return a Hashtable from the input source_ids to the resulting translation, empty if conversion not supported
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
        
        //if(source_id_type.equals(KEGG_ID) && target_id_type.equals(PROLINKS_ID)){
        //  return keggToProlinks(source_ids);
        //}
        
        //if(source_id_type.equals(PROLINKS_ID) && target_id_type.equals(KEGG_ID)){
        //   return prolinksToKegg(source_ids);
        //}
        
        // Common name to id? Not reliable. User needs to keep track of this map.
        if(source_id_type.equals(GI_ID) && target_id_type.equals(GENE_NAME)){
            return giToGeneName(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(PROD_NAME)){
            return giToProdName(source_ids);
        }
        
        //if(source_id_type.equals(PROLINKS_ID) && target_id_type.equals(GENE_NAME)){
        //  return prolinksToCommonName(source_ids);
        // }
         
        //if(source_id_type.equals(KEGG_ID) && target_id_type.equals(GENE_NAME)){
        //  return keggToCommonName(source_ids);
        //}
         
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
        
//        sql = "SELECT prodname.protgi,prodname.prodname"+ 
//        " FROM refseq_prodname AS prodname, refseq_taxid AS taxid" + 
//        " WHERE taxid.taxid = " + species_taxid + " AND taxid.protgi = prodname.protgi AND prodname.prodname LIKE \"" + sqlPattern + "\"";
//        rs = query(sql);
//    
//        try{
//            while(rs.next()){
//                String protgi = GI_ID + ":" + rs.getString(1);
//                String name = rs.getString(2);
//                if(!giToName.contains(protgi))
//                    giToName.put(protgi,name);
//            }    
//        }catch(SQLException ex){
//            ex.printStackTrace();
//            return giToName;
//        }
        
        //if(giToName.size() > 0) return giToName; // not sure if I want to return at this point
        
       // 2. Look in genbank_genename, genbank_prodname
        sql = "SELECT genename.protgi,genename.genename"+ 
        " FROM genbank_genename AS genename, genbank_taxid AS taxid" + 
        " WHERE taxid.taxid = " + species_taxid + " AND taxid.protgi = genename.protgi AND (genename.genename " + like + ")";
        rs = query(sql);

        try{
            while(rs.next()){
                String protgi = GI_ID + ":" + rs.getString(1);
                String name = rs.getString(2);
                if(!giToName.contains(protgi))
                    giToName.put(protgi,name);
            }    
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
    
//        sql = "SELECT prodname.protgi,prodname.prodname"+ 
//        " FROM genbank_prodname AS prodname, genbank_taxid AS taxid" + 
//        " WHERE taxid.taxid = " + species_taxid + " AND taxid.protgi = prodname.protgi AND prodname.prodname LIKE \"" + sqlPattern + "\"";
//        rs = query(sql);
//
//        try{
//            while(rs.next()){
//                String protgi = GI_ID + ":" + rs.getString(1);
//                String name = rs.getString(2);
//                if(!giToName.contains(protgi))
//                    giToName.put(protgi,name);
//            }    
//        }catch(SQLException ex){
//            ex.printStackTrace();
//            return giToName;
//        }
//        
        return giToName;
      
    }
    
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
        
        String sql = "SELECT protgi,genename FROM refseq_genename WHERE protgi IN (" + inStatement + ")";
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
        
        String sql = "SELECT protgi,prodname FROM refseq_prodname WHERE protgi IN (" + inStatement + ")";
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
     * 
     * @param gis
     * @return
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
     * 
     * @param gis
     * @return
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
        
        // TODO: GenBank, iProClass. Need to match more. For this, we need to use accessions without the version.
        return xrefs;
        
    }
    
    //--------- Helper protected methods -----------//
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
                String p = rs.getString(1);
                String gi = rs.getString(2);
                if(!gi.equals("0"))
                    pToG.put(PROLINKS_ID + ":" + rs.getString(1), GI_ID + ":" + rs.getString(2));
            }
            return pToG;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
           
    }
    
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
                gToP.put(GI_ID + ":" + rs.getString(1), PROLINKS_ID + ":" + rs.getString(2));
            }
            return gToP;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
           
    }
   
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
                kToG.put(KEGG_ID + ":" + rs.getString(1), GI_ID + ":" + rs.getString(2));
            }
            return kToG;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
    }
    
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
                gToK.put(GI_ID + ":" + rs.getString(1), KEGG_ID  + ":" + rs.getString(2));
            }
            return gToK;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
    }
    
    protected Hashtable giToGeneName (Vector gis){
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String giList = "(";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(GI_ID + ":");
            if(index >= 0){
              id = id.substring(index + GI_ID.length() + 1);
              if(giList.length() > 1){
                  giList += ","+id;
              }else{
                  giList += id;
              }
            }
        }//while it
        giList += ")";
        
        // 1. Try refseq
        
        String sql = "SELECT protgi,genename FROM refseq_genename WHERE protgi IN " + giList;
        ResultSet rs = query(sql);
        Hashtable giToName = new Hashtable();
        try{
            while(rs.next()){
                giToName.put(GI_ID + ":" + rs.getString(1), rs.getString(2));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // 2. Try Prolinks
        sql = "SELECT protgi, genename FROM prolinks_gi_genename WHERE protgi IN " + giList;
        rs = query(sql);
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                if(!giToName.contains(gi))
                giToName.put(gi, rs.getString(2));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // 3. Now try genbank
        
        sql = "SELECT protgi,genename FROM genbank_genename WHERE protgi IN " + giList;
        rs = query(sql);
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                if(!giToName.contains(gi))
                giToName.put(gi, rs.getString(2));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        return giToName;
        
    }
    
    protected Hashtable giToProdName (Vector gis){
        Iterator it = gis.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String giList = "(";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(GI_ID + ":");
            if(index >= 0){
              id = id.substring(index + GI_ID.length() + 1);
              if(giList.length() > 1){
                  giList += ","+id;
              }else{
                  giList += id;
              }
            }
        }//while it
        giList += ")";
        
        // 1. Try refseq
        
        String sql = "SELECT protgi,prodname FROM refseq_prodname WHERE protgi IN " + giList;
        ResultSet rs = query(sql);
        Hashtable giToName = new Hashtable();
        try{
            while(rs.next()){
                giToName.put(GI_ID + ":" + rs.getString(1), rs.getString(2));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        // 2. Now try genbank
        
        sql = "SELECT protgi,prodname FROM genbank_prodname WHERE protgi IN " + giList;
        rs = query(sql);
        try{
            while(rs.next()){
                String gi = GI_ID + ":" + rs.getString(1);
                if(!giToName.contains(gi))
                giToName.put(gi, rs.getString(2));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
        return giToName;
        
    }
    
    //TODO: Update this method
    protected Hashtable prolinksToKegg (Vector prolinks_ids){
        Iterator it = prolinks_ids.iterator();
        if(!it.hasNext()) return new Hashtable();
       
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(PROLINKS_ID + ":");
            if(index >= 0){
                id = id.substring(index + PROLINKS_ID.length() + 1);
                if(or.length() > 0)
                    or += " OR p.prolinksid = " + id;
                else
                    or = " p.prolinksid = " + id;
            }
        }
        if(or.length() == 0) return new Hashtable();
        String sql = "SELECT p.prolinksid, k.kegg_id FROM xref_kegg AS k, key_prolinks AS p WHERE k.oid = p.oid AND " + or;
        ResultSet rs = query(sql);
        try{
            Hashtable pToK = new Hashtable();
            while(rs.next()){
                pToK.put(SynonymsSource.PROLINKS_ID + ":" + rs.getString(1),
                         SynonymsSource.KEGG_ID + ":" + rs.getString(2));
            }
            return pToK;
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return new Hashtable();
    }
    
    //TODO: Update this method
    protected Hashtable keggToProlinks (Vector kegg_ids){
        Iterator it = kegg_ids.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(KEGG_ID + ":");
            if(index >= 0){
                id = id.substring(index + KEGG_ID.length() + 1);
                if(or.length() > 0)
                    or += " OR k.kegg_id = \"" + id + "\"";
                else
                    or = " k.kegg_id = \"" + id + "\"";
            }
        }
        if(or.length() == 0) return new Hashtable();
        String sql = "SELECT k.kegg_id, p.prolinksid FROM xref_kegg AS k, key_prolinks AS p WHERE k.oid = p.oid AND " + or;
        ResultSet rs = query(sql);
        try{
            Hashtable kToP = new Hashtable();
            while(rs.next()){
                kToP.put(SynonymsSource.KEGG_ID + ":" + rs.getString(1),
                         SynonymsSource.PROLINKS_ID + ":" + rs.getString(2));
            }
            return kToP;
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return new Hashtable();
    }
    
    //TODO: update this method
    protected Hashtable giToCommonName (Vector gi_ids){
        Iterator it = gi_ids.iterator();
        if(!it.hasNext()) return new Hashtable();
        
        // transfer gi_ids to oids, and then oids to genename
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(GI_ID + ":");
            if(index >= 0){
                id = id.substring(index + GI_ID.length() + 1);
                //TODO: Remove once the back end db is fixed
                try{Integer.parseInt(id);}catch(NumberFormatException e){continue;}
                if(or.length() > 0)
                    or += " OR gi = " + id;
                else
                    or = " gi = " + id;
            }
        }//while
        
        if(or.length() == 0) return new Hashtable();
        String sql = "SELECT ngi, oid FROM xref_gi WHERE " + or;
        ResultSet rs = query(sql);
        
        Hashtable oidToGi = new Hashtable();
        or = "";
        try{
            while(rs.next()){
                int gi = rs.getInt(1);
                int oid = rs.getInt(2);
                oidToGi.put(new Integer(oid), Integer.toString(gi));
                if(or.length() == 0){
                    or = " oid = " + oid;
                }else{
                    or += " OR oid = " + oid;
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
        if(or.length() == 0) return new Hashtable();
        
        sql = "SELECT oid, genename FROM gn_genename WHERE " + or;
        rs = query(sql);
        
        Hashtable giToCn = new Hashtable();
        try{
            while(rs.next()){
                int oid = rs.getInt(1);
                String cn = rs.getString(2);
                String gi = (String)oidToGi.get(new Integer(oid));
                giToCn.put(GI_ID+":"+gi,cn);
            }
        }catch(Exception ex){ex.printStackTrace(); return new Hashtable();}
        System.out.print("Num translated to common names = " + giToCn.size());
        return giToCn;
    } 
    
    //TODO: Update this method
    protected Hashtable prolinksToCommonName (Vector prolinks_ids){
        Hashtable prToCN = new Hashtable();
        
        Iterator it = prolinks_ids.iterator();
        
        if(!it.hasNext()) return prToCN;
        
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(PROLINKS_ID + ":");
            if(index >= 0){
                id = id.substring(index + PROLINKS_ID.length() + 1);
                if(or.length() == 0){
                    or = " prolinksid = " + id;
                }else{
                    or += " OR prolinksid = " + id;
                }//else
            }//index >=0
        }//while
        
        if(or.length() == 0) return prToCN;
        
        String sql = "SELECT prolinksid, genename FROM key_prolinks WHERE " + or;
        ResultSet rs = query(sql);
        
        try{
            while(rs.next()){
                String prolinksid = rs.getString(1);
                String cn = rs.getString(2);
                prToCN.put(PROLINKS_ID + ":" + prolinksid, cn);
            }
        }catch(Exception ex){ex.printStackTrace(); return new Hashtable();}
        
        return prToCN;
    }
    
    // TODO: Update this method
    protected Hashtable keggToCommonName (Vector kegg_ids){
        
        Hashtable kToCN = new Hashtable();
        
        Iterator it = kegg_ids.iterator();
        
        if(!it.hasNext()) return kToCN;
        
        String or = "";
        while(it.hasNext()){
            String id = (String)it.next();
            int index = id.indexOf(KEGG_ID + ":");
            if(index >= 0){
                id = id.substring(index + KEGG_ID.length() + 1);
                if(or.length() == 0){
                    or = " kegg_id = \"" + id + "\"";
                }else{
                    or += " OR kegg_id = \"" + id + "\"";
                }//else
            }//if index
        }//while
       
        if(or.length() == 0) return kToCN;
        
        String sql = "SELECT oid, kegg_id FROM xref_kegg WHERE " + or;
        ResultSet rs = query(sql);
        Hashtable oidToKegg = new Hashtable();
        or = "";
        try{
            while(rs.next()){
                int oid = rs.getInt(1);
                String kegg = rs.getString(2);
                oidToKegg.put(new Integer(oid), kegg);
                if(or.length() == 0){
                    or = " oid = " + oid;
                }else{
                    or += " OR oid = " + oid;
                }//else
            }//while
        }catch(Exception ex){ ex.printStackTrace(); return kToCN;}
        
        if(or.length() == 0) return kToCN;
        
        sql = "SELECT oid, genename FROM gn_genename WHERE " + or;
        rs = query(sql);
        try{
            while(rs.next()){
                int oid = rs.getInt(1);
                String cn = rs.getString(2);
                String keggid = (String)oidToKegg.get(new Integer(oid));
                kToCN.put(KEGG_ID + ":" + keggid,cn);
            }//while
        }catch(Exception ex){ ex.printStackTrace(); return new Hashtable();}
        
        return kToCN;
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