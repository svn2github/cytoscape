// BioDataServerInterface.java:  define the interface
//----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.data.servers;
//----------------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
import java.rmi.*;
import cytoscape.data.annotation.*;
import cytoscape.data.synonyms.*;
//----------------------------------------------------------------------------------------
public interface BioDataServerInterface extends Remote {

    // annotations (typically of genes, but possibly of any entity whatsoever

  public void addAnnotation (Annotation annotation) throws Exception;
  public int getAnnotationCount () throws Exception;
  public AnnotationDescription [] getAnnotationDescriptions () throws Exception;
  public Annotation getAnnotation (String species, String curator, String type) throws Exception;
  public Annotation getAnnotation (AnnotationDescription description) throws Exception;
  public int [] getClassifications (String species, String curator, String type, String entity) 
                throws Exception;
  public int [] getClassifications (AnnotationDescription description, String entity)
                throws Exception;
  public String [][] getAllAnnotations (AnnotationDescription description, String entity)
                throws Exception;


  public void clear () throws Exception;
  public String describe () throws Exception;

    //  names & synonyms, typically of genes, but possibly of any entity whatsoever
  public void addThesaurus (String species, Thesaurus thesaurus) throws Exception;
  public String getCanonicalName (String species, String commonName) throws Exception;
  public String [] getAllCommonNames (String species, String commonName) throws Exception;
  public String getCommonName (String species, String canonicalName) throws Exception;

//----------------------------------------------------------------------------------------
} // interface BioDataServerInterface
