/**  Copyright (c) 2005 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * Populate a <code>Project</code> object from a valid XML document.
 *
 * @see Project
 *
 * @version %I%, %G%  
 * @author pshannon@systemsbiology.org
 */
package phenotypeGenetics;
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;
import cytoscape.data.readers.TextJarReader;

public class ProjectXmlReader { 
  String xmlFilename;
  Project project;

  public ProjectXmlReader (String xmlFilename) throws Exception
  {
    if (xmlFilename.startsWith ("jar://")) {
      TextJarReader tjReader = new TextJarReader (xmlFilename);
      tjReader.read ();
      File temp = File.createTempFile ("phenotypeGenetics", ".xml");
      BufferedWriter out = new BufferedWriter (new FileWriter (temp));
      out.write (tjReader.getText ());
      out.close();
      this.xmlFilename = temp.getPath ();
    }else {
      this.xmlFilename = xmlFilename;
    }

  } // ctor
  //-------------------------------------------------------------------------
  public void read () throws Exception
  {
    SAXBuilder builder = new SAXBuilder (); 
    Document doc = builder.build (new File (xmlFilename));
    Element root = doc.getRootElement ();
    ListIterator iterator;

    String name = root.getChild ("name").getText().trim();
    String organism = root.getChild ("organism").getText().trim();
    project = new Project (name, organism);

    List notes = root.getChildren ("note");
    if (notes != null) {
      iterator = notes.listIterator ();
      while (iterator.hasNext ()) {
        Element noteE = (Element) iterator.next ();
        String note = noteE.getText().trim();
        project.addNote (note);
      } // while
    } // if notes

    List experiments = root.getChildren ("experiment");
    if (experiments != null) {
      iterator = experiments.listIterator ();
      while (iterator.hasNext ()) {
        Element experiment = (Element) iterator.next ();
        project.addExperiment (parseExperiment (experiment));
      } // while
    } // if experiments

  } // read
  //-------------------------------------------------------------------------
  protected Experiment parseExperiment (Element root)
  {
    String name = root.getAttribute ("name").getValue ();
    Experiment experiment = new Experiment (name);
   
    List notes = root.getChildren ("note");
    if (notes != null) {
      ListIterator iterator = notes.listIterator ();
      while (iterator.hasNext ()) {
        Element noteE = (Element) iterator.next ();
        String note = noteE.getText().trim();
        experiment.addNote (note);
      } // while
    } // if notes

    List conditions = root.getChildren ("condition");
    if (conditions != null) {
      ListIterator iterator = conditions.listIterator ();
      while (iterator.hasNext ()) {
        Condition condition = parseCondition ((Element) iterator.next ());
        experiment.addCondition (condition);
      } // while
    } // if conditions

    Element observations = root.getChild ("observations");
    List phenotypes = observations.getChildren ("phenotype");
    if (phenotypes != null) {
      ListIterator iterator = phenotypes.listIterator ();
      while (iterator.hasNext ()) {
        Phenotype phenotype = parsePhenotype ((Element) iterator.next ());
        experiment.addObservation (phenotype);
      } // while
    } // if phenotypes

    return experiment;

  } // parseExperiment
  //-------------------------------------------------------------------------
  protected Condition parseCondition (Element root)
  {
    Attribute attrib;
    Condition condition = new Condition ();

    attrib = root.getAttribute ("category");
    if (attrib != null) {
      String category = attrib.getValue ();
      if (category != null && category.length () > 0) {
        if (category.equals ("genetic"))
          condition.setCategory (Condition.GENETIC);
        else if (category.equals ("environmental"))
          condition.setCategory (Condition.ENVIRONMENTAL);
      } // if good value
    } // if category attribute

    attrib = root.getAttribute ("alleleForm");
    // Try the old attribute name
    if (attrib == null) {
      attrib = root.getAttribute ("genotype");
    }
    if (attrib != null) {
      String alleleForm = attrib.getValue ();
      if (alleleForm != null && alleleForm.length () > 0) {
        if (alleleForm.equals ("lf"))
          condition.setAlleleForm (Condition.LF);
        else if (alleleForm.equals ("lf(partial)"))
          condition.setAlleleForm (Condition.LF_PARTIAL);
        else if (alleleForm.equals ("gf"))
          condition.setAlleleForm (Condition.GF);
        else if (alleleForm.equals ("gf(partial)"))
          condition.setAlleleForm (Condition.GF_PARTIAL);
        else if (alleleForm.equals ("dn"))
          condition.setAlleleForm (Condition.DN);
      } // if good value
    } // if alleleForm attribute

    attrib = root.getAttribute ("allele");
    // Try the old attribute name
    if (attrib == null) {
      attrib = root.getAttribute ("manipulation");
    }
    if (attrib != null) {
      String allele = attrib.getValue ();
      condition.setAllele(allele);
    } // if allele attribute

    attrib = root.getAttribute ("gene");
    if (attrib != null) {
      String gene = attrib.getValue ();
      if (gene != null && gene.length () > 0)
        condition.setGene (gene);
    } // if gene attribute

    // now parse obligatory pairs:  name=xxxx  value=yyyy
    // assign these only if both are present
    attrib = root.getAttribute ("name");
    if (attrib != null) {
      String name = attrib.getValue ();
      if (name != null && name.length () > 0) {
        attrib = root.getAttribute ("value");
        if (attrib != null) {
          String value = attrib.getValue ();
          if (value != null && value.length () > 0) {
            condition.setAttribute (name, value);
          } // if good value (and thus, all conditions met)
        } // if value attrib
      } // if good name
    } // if name attrib

    return condition;

  } // parseCondition
  //-------------------------------------------------------------------------
  protected Phenotype parsePhenotype (Element root)
  {
    Attribute attrib;
    Phenotype phenotype = new Phenotype ();

    // parse obligatory pairs:  name=xxxx  value=yyyy
    // assign these only if both are present

    attrib = root.getAttribute ("name");
    if (attrib != null) {
      String name = attrib.getValue ();
      if (name != null && name.length () > 0) {
        attrib = root.getAttribute ("value");
        if (attrib != null) {
          String value = attrib.getValue ();
          if (value != null && value.length () > 0) {
            phenotype.setName (name);
            phenotype.setValue (value);
          } // if good value (and thus, all conditions met)
        } // if value attrib
      } // if good name
    } // if name attrib

    return phenotype;

  } // parsePhenotype
  //-------------------------------------------------------------------------
  public Project getProject ()
  {
    return project;
  }
  //-------------------------------------------------------------------------
} // class ProjectXmlReader
