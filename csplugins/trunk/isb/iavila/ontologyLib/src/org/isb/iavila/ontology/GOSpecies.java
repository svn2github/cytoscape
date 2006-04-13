package org.isb.iavila.ontology;

public class GOSpecies {
 
    protected int ID;
    protected String genus;
    protected String species;
    protected String commonName;
    
    public GOSpecies (int id, String genus, String species, String common_name){
        this.ID = id;
        this.genus = genus;
        this.species = species;
        this.commonName = common_name;
    }
    
    public int getID () {return this.ID;}
    
    public String getGenus () {return this.genus;}
    
    public String getSpecies () {return this.species;}
    
    public String getCommonName (){return this.commonName;}
    
    public String toString (){
        if(this.commonName != null && this.commonName.length() > 0)
            return this.genus + " " + this.species + " (" + this.commonName + ")";
        return this.genus + " " + this.species;
    }
}