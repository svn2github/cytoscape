/**
 * Writes and reads an XML file that contains a set of modes, their assigned phenotype
 * inequalities, and other properties.
 *
 * @author Iliana Avila-Campillo
 */
package phenotypeGenetics.xml;

import phenotypeGenetics.*;
import java.util.*;
import java.io.*; 
import java.awt.*;
import org.jdom.*; 
import org.jdom.Document;
import org.jdom.input.*; 
import org.jdom.output.*;
import cytoscape.data.readers.TextJarReader;
//TODO: Rename class to ModeXMLFileReaderWriter

public class ModeXMLFileWriter {
  
  // XML tags:
  public static final String MODE_SET_ELEMENT = "mode_set";
  public static final String MODE_ELEMENT = "mode";
  public static final String MODE_NAME_ELEMENT = "mode_name";
  public static final String INEQ_ELEMENT = "phenotype_inequality";
  public static final String DISC_ENCODING_ELEMENT = "discrete_encoding";
  public static final String DIR_ELEMENT = "direction";
  public static final String COLOR_ELEMENT = "edge_color";
  public static final String EDGE_TYPE_ELEMENT = "edge_type";
  

  /**
   * Reads an XML file that describes a set of Modes, creates
   * the Modes and returns them
   *
   * @param file_path the XML file
   * @return an array of Modes
   */
  public static Mode [] read (String file_path) throws Exception {
    System.out.println("ModeXMLFileWriter.read(" + file_path + ")");
    int jarIndex = file_path.indexOf("jar:/");
    File file = null;
    
    // For jared files:
    if(jarIndex != -1){
      String shortName = file_path.substring(jarIndex);
      TextJarReader tjReader = new TextJarReader(shortName);
      tjReader.read();
      File temp = File.createTempFile("phenGenModes",".xml");
      BufferedWriter out = new BufferedWriter (new FileWriter(temp));
      out.write(tjReader.getText());
      out.close();
      file = new File(temp.getPath());
    }else{
      file = new File(file_path);
    }
    
    SAXBuilder builder = new SAXBuilder(); 
    Document doc = builder.build(file);
    Element root = doc.getRootElement();
    
    java.util.List modes = root.getChildren(MODE_ELEMENT);
    
    if(modes == null){
      System.out.println("The file contains no modes.");
      return new Mode[0];
    }
    
    ListIterator it = modes.listIterator();
    ArrayList createdModes = new ArrayList();
    while(it.hasNext()){
      
      Element modeElement = (Element)it.next();
      Element modeNameElement = modeElement.getChild(MODE_NAME_ELEMENT);
      Mode mode = null;
      if(modeNameElement.equals(DiscretePhenoValueInequality.UNASSIGNED_MODE_NAME)){
        mode = DiscretePhenoValueInequality.UNASSIGNED_MODE;
      }else{
        mode = new Mode();
      }
      
      if(modeNameElement == null){
        throw new IllegalStateException("Error reading mode, it does not have a " +
                                        MODE_NAME_ELEMENT + " tag");
      }
      mode.setName(modeNameElement.getText().trim());
      
      java.util.List ineqs = modeElement.getChildren(INEQ_ELEMENT);
      
      if(ineqs == null){
        continue;
      }
      
      ListIterator it2 = ineqs.listIterator();
      
      while(it2.hasNext()){
        Element ineqElement = (Element)it2.next();
        Element encodingElement = ineqElement.getChild(DISC_ENCODING_ELEMENT);

        if(encodingElement == null){
          // If we don't have the encoding, we can't do anything with this!
          throw new IllegalStateException("Error reading " + INEQ_ELEMENT +
                                          ", it does not have a " + 
                                          DISC_ENCODING_ELEMENT + " tag");
        }
            
        String stringEncoding = encodingElement.getText().trim();
        // Get 4 int values from this encoding
        char [] charEncodings = stringEncoding.toCharArray();
        // the array must have 4 characters (one for each of: WT, A, B, AB)
        if(charEncodings.length != 4){
          throw new IllegalStateException("Error reading discrete_encoding  "
                                          + stringEncoding + 
                                          ", it does not contain 4 characters");
        }
        
        int pWT = Integer.parseInt( ( String.valueOf(charEncodings[0]) ) );
        int pA = Integer.parseInt( ( String.valueOf(charEncodings[1]) ) );
        int pB = Integer.parseInt( ( String.valueOf(charEncodings[2]) ) );
        int pAB = Integer.parseInt( ( String.valueOf(charEncodings[3]) ) );
        
        DiscretePhenoValueInequality ineq = 
          DiscretePhenoValueInequality.getPhenoInequality(pWT,pA,pB,pAB);
        
        mode.addPhenotypeInequality(ineq);
        ineq.setMode(mode);
        
        // Now get the inequalitie's other elements
        Element rgbElement = ineqElement.getChild(COLOR_ELEMENT);
        if(rgbElement != null){
          String rgbString = rgbElement.getText().trim();
          int rgb = Integer.parseInt(rgbString);
          ineq.setColor(new Color(rgb));
        }
        
        Element directionElement = ineqElement.getChild(DIR_ELEMENT);
        if(directionElement != null){
          ineq.setDirection(directionElement.getText().trim());
        }
        
        Element edgeTypeElement = ineqElement.getChild(EDGE_TYPE_ELEMENT);
        if(edgeTypeElement != null){
          String edgeType = edgeTypeElement.getText().trim();
          ineq.setEdgeType(edgeType);
        }
        
      }//while it2 has more ineqs
      
      createdModes.add(mode);
    
    }//while it has more mode elements

    // Assign a Mode to those inequalities without a Mode
    DiscretePhenoValueInequality [] ineqs = DiscretePhenoValueInequality.getInequalitiesSet();
    for(int i = 0; i < ineqs.length; i++){
      if(ineqs[i].getMode() == null){
        ineqs[i].setMode(DiscretePhenoValueInequality.UNASSIGNED_MODE);
      }
    }//for i
    
    return (Mode[])createdModes.toArray( new Mode[createdModes.size()] );
  }//read
  
  /**
   * Writes an XML file describing the given modes
   *
   * @param modes the Modes to be written to an XML file
   * @param file the File to write to
   */
  public static void write (Mode [] modes, File file){
    
    // Create the document
    Element rootElement = new Element(MODE_SET_ELEMENT);
    Document doc = new Document(rootElement);
    
    for(int i = 0; i < modes.length; i++){
      
      Element modeElement = new Element(MODE_ELEMENT);
      Element modeNameElement = new Element(MODE_NAME_ELEMENT);
      modeNameElement.setText(modes[i].getName());
      modeElement.addContent(modeNameElement);
      
      Iterator it = modes[i].getPhenotypeInequalities().iterator();
      while(it.hasNext()){
      
        DiscretePhenoValueInequality ineq = (DiscretePhenoValueInequality)it.next();
        Element ineqElement = new Element(INEQ_ELEMENT);
        
        Element encodingElement = new Element(DISC_ENCODING_ELEMENT);
        encodingElement.setText(DiscretePhenoValueInequality.getEncodingAsString(ineq));
        
        Element directionElement = new Element(DIR_ELEMENT);
        directionElement.setText(ineq.getDirection());
        
        Element colorElement = new Element(COLOR_ELEMENT);
        Color color = ineq.getColor();
        colorElement.setText(Integer.toString(color.getRGB()));
        
        Element edgeTypeElement = new Element(EDGE_TYPE_ELEMENT);
        edgeTypeElement.setText(ineq.getEdgeType());
        
        ineqElement.addContent(encodingElement);
        ineqElement.addContent(directionElement);
        ineqElement.addContent(colorElement);
        ineqElement.addContent(edgeTypeElement);
        
        modeElement.addContent(ineqElement);
      
      }// while each inequality in mode i
      
      rootElement.addContent(modeElement);
    
    }//for each mode i
    
    // Write the document
    XMLOutputter outp = new XMLOutputter();
    outp.setIndent("\t");
    outp.setNewlines(true);
  
    try{
      FileOutputStream stream = new FileOutputStream(file); 
      outp.output(doc,stream);
    }catch(IOException exception){
      exception.printStackTrace();
    }
  
  }//write

}//ModeXMLFileWriter
