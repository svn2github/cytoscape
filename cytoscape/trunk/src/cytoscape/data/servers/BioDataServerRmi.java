// BioDataServerRmi.java
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import java.rmi.server.*;

import java.io.*;
import java.util.*;
import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
import cytoscape.data.synonyms.*;
//------------------------------------------------------------------------------
public class BioDataServerRmi extends UnicastRemoteObject
                            implements BioDataServerInterface, Serializable {

    // gene (or other biological entity) annotations are specific to
    //  - a species (i.e., halobacterium, or yeast)
    //  - a curator (i.e., KEGG or GO)
    //  - a type    (i.e., GO's biological process, or KEGG's metabolic pathway)
    // these three attributes are contained in an 'AttributeDescription', 
    // instances of which are used as keys in the following HashMap

  protected HashMap annotations;
  protected HashMap thesauri;

  protected Date startTime;
  protected Date lastAccessTime;

//------------------------------------------------------------------------------
public BioDataServerRmi () throws RemoteException 
{
  super ();
  annotations = new HashMap ();
  thesauri = new HashMap ();

  startTime = new Date ();
  lastAccessTime = new Date ();

}
//------------------------------------------------------------------------------
/**
 *  if the new annotation is to an already annotated species, curator and type,
 *  then simply add (with possible overwriting for identical 
 */
public void addAnnotation (Annotation newAnnotation)
{
  lastAccessTime = new Date ();
  AnnotationDescription key = new AnnotationDescription (newAnnotation.getSpecies (),
                                                         newAnnotation.getCurator (),
                                                         newAnnotation.getType ());
  if (annotations.containsKey (key)) {
    Annotation oldAnnotation = (Annotation) annotations.get (key);
    String [] newNames = newAnnotation.getNames ();
    for (int i=0; i < newNames.length; i++) {
      int [] classificationIDs = newAnnotation.getClassifications (newNames [i]);
      for (int c=0; c < classificationIDs.length; c++) 
        oldAnnotation.add (newNames [i], classificationIDs [c]);
      } // for i
   }
  else {
    annotations.put (key, newAnnotation);
    }

} // addAnnotation (Annotation)
//------------------------------------------------------------------------------
public void clear ()
{
  annotations = new HashMap ();
  lastAccessTime = new Date ();

}
//------------------------------------------------------------------------------
public int getAnnotationCount ()
{
  lastAccessTime = new Date ();
  return annotations.size ();
}
//------------------------------------------------------------------------------
public AnnotationDescription [] getAnnotationDescriptions ()
{
  lastAccessTime = new Date ();
  AnnotationDescription [] result =
       (AnnotationDescription []) annotations.keySet().toArray (new AnnotationDescription [0]);

  return result;

} // getAnnotationDescriptions
//------------------------------------------------------------------------------
public Annotation getAnnotation (String species, String curator, String type)
{
  lastAccessTime = new Date ();
  AnnotationDescription description = new AnnotationDescription (species, curator, type);
  return getAnnotation (description);

} // getAnnotations
//------------------------------------------------------------------------------
public Annotation getAnnotation (AnnotationDescription description)
{
  lastAccessTime = new Date ();
  return (Annotation) annotations.get (description);

} // getAnnotations
//------------------------------------------------------------------------------
public int [] getClassifications (String species, String curator, String type, String entity)
{
  lastAccessTime = new Date ();
  AnnotationDescription description = new AnnotationDescription (species, curator, type);
  return (getClassifications (description, entity));

} // getClassifications
//------------------------------------------------------------------------------
public int [] getClassifications (AnnotationDescription description, String entity)
{
  lastAccessTime = new Date ();
  Annotation annotation = (Annotation) annotations.get (description);
  return annotation.getClassifications (entity);

} // getClassifications
//------------------------------------------------------------------------------
public String [][] getAllAnnotations (AnnotationDescription description, String entity)
{
  lastAccessTime = new Date ();
  Annotation annotation = (Annotation) annotations.get (description);
  return annotation.getAllHierarchyPathsAsNames (entity);
}
//------------------------------------------------------------------------------
public String describe ()
{
  StringBuffer sb = new StringBuffer ();
  Annotation [] tmp = (Annotation [])annotations.values().toArray (new Annotation [0]);

  for (int i=0; i < tmp.length; i++) {
    sb.append (tmp [i]);
    sb.append ("\n");
    }

  Thesaurus [] tmp2 = (Thesaurus [])thesauri.values().toArray (new Thesaurus [0]);

  for (int i=0; i < tmp2.length; i++) {
    sb.append (tmp2 [i]);
    sb.append ("\n");
    }



  sb.append ("\n");
  sb.append ("    started: " + startTime);
  sb.append ("\n");
  
  sb.append ("last access: " + lastAccessTime);
  sb.append ("\n");
  
  return sb.toString ();

} // describe
//------------------------------------------------------------------------------
public void addThesaurus (String species, Thesaurus thesaurus) 
{
  thesauri.put (species, thesaurus);
}
//----------------------------------------------------------------------------------------
public String getCanonicalName (String species, String commonName)
{
  if (thesauri.containsKey (species)) {
    Thesaurus t = (Thesaurus) thesauri.get (species);
    if (t != null) {
      String result = t.getCanonicalName (commonName);
      if (result != null)
        return result;
      } // if thesaurus exists
    } // if key

  return commonName;

} // getCanonicalName
//----------------------------------------------------------------------------------------
public String [] getAllCommonNames (String species, String commonName)
{
  if (thesauri.containsKey (species)) {
    Thesaurus t = (Thesaurus) thesauri.get (species);
    if (t != null) {
      String [] result = t.getAllCommonNames (commonName);
      if (result != null)
        return result;
      } // if thesaurus exists
   } // if key

  String [] result = new String [1];
  result [0] = commonName;
  return result;
  
}
//----------------------------------------------------------------------------------------
public String getCommonName (String species, String canonicalName)
{
  if (thesauri.containsKey (species)) {
    Thesaurus t = (Thesaurus) thesauri.get (species);
    if (t != null) {
      String result = t.getCommonName (canonicalName);
      if (result != null)
        return result;
      } // if thesaurus exists
   } // if key

  return canonicalName;

}
//----------------------------------------------------------------------------------------
public static void main (String [] args) 
{
  if (args.length != 1) {
    System.err.println ("usage:  BioDataServerRmi <server name>");
    System.exit (1);
    }

  String name = args [0];
   
  try {
    BioDataServerRmi rmiServer = new BioDataServerRmi ();
    Naming.rebind (name, rmiServer);
    System.out.println ("BioDataServer bound as '" + name + "'");
    } 
  catch (Exception e) {
    System.err.println ("BioDataServer exception: " + e.getMessage());
    e.printStackTrace();
    }

} // main
//------------------------------------------------------------------------------
} // class BioDataServerRmi
