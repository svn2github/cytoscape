
package org.isb.iavila.ontology;

public class OntologyTerm {
    
    protected int id;
    protected String name;
    
    public OntologyTerm (int id, String name){
        this.id = id;
        this.name = name;
    }
    
    /**
     * @return the term id
     */
    public int getID (){return this.id;}
    
    /**
     * @return the name of this term
     */
    public String getName (){return this.name;}
    
    /**
     * @return getName()
     */
    public String toString (){ return getName();}
}