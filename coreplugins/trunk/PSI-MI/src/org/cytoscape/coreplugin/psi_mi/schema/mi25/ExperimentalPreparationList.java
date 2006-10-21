/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi25;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Terms describing the experimental sample preparation. 
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentalPreparationList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * This element is controlled by the PSI-MI controlled
     * vocabulary "experimentalPreparation", root term id MI:0346.
     */
    private java.util.Vector _experimentalPreparationList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentalPreparationList() {
        super();
        _experimentalPreparationList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExperimentalPreparation
     * 
     * @param vExperimentalPreparation
     */
    public void addExperimentalPreparation(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation vExperimentalPreparation)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentalPreparationList.addElement(vExperimentalPreparation);
    } //-- void addExperimentalPreparation(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation)

    /**
     * Method addExperimentalPreparation
     * 
     * @param index
     * @param vExperimentalPreparation
     */
    public void addExperimentalPreparation(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation vExperimentalPreparation)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentalPreparationList.insertElementAt(vExperimentalPreparation, index);
    } //-- void addExperimentalPreparation(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation)

    /**
     * Method enumerateExperimentalPreparation
     */
    public java.util.Enumeration enumerateExperimentalPreparation()
    {
        return _experimentalPreparationList.elements();
    } //-- java.util.Enumeration enumerateExperimentalPreparation() 

    /**
     * Method getExperimentalPreparation
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation getExperimentalPreparation(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentalPreparationList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation) _experimentalPreparationList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation getExperimentalPreparation(int)

    /**
     * Method getExperimentalPreparation
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation[] getExperimentalPreparation()
    {
        int size = _experimentalPreparationList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation) _experimentalPreparationList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation[] getExperimentalPreparation()

    /**
     * Method getExperimentalPreparationCount
     */
    public int getExperimentalPreparationCount()
    {
        return _experimentalPreparationList.size();
    } //-- int getExperimentalPreparationCount() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {

        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeAllExperimentalPreparation
     */
    public void removeAllExperimentalPreparation()
    {
        _experimentalPreparationList.removeAllElements();
    } //-- void removeAllExperimentalPreparation() 

    /**
     * Method removeExperimentalPreparation
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation removeExperimentalPreparation(int index)
    {
        java.lang.Object obj = _experimentalPreparationList.elementAt(index);
        _experimentalPreparationList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation removeExperimentalPreparation(int)

    /**
     * Method setExperimentalPreparation
     * 
     * @param index
     * @param vExperimentalPreparation
     */
    public void setExperimentalPreparation(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation vExperimentalPreparation)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentalPreparationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _experimentalPreparationList.setElementAt(vExperimentalPreparation, index);
    } //-- void setExperimentalPreparation(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation)

    /**
     * Method setExperimentalPreparation
     * 
     * @param experimentalPreparationArray
     */
    public void setExperimentalPreparation(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation[] experimentalPreparationArray)
    {
        //-- copy array
        _experimentalPreparationList.removeAllElements();
        for (int i = 0; i < experimentalPreparationArray.length; i++) {
            _experimentalPreparationList.addElement(experimentalPreparationArray[i]);
        }
    } //-- void setExperimentalPreparation(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparation)

    /**
     * Method unmarshalExperimentalPreparationList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList unmarshalExperimentalPreparationList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalPreparationList unmarshalExperimentalPreparationList(java.io.Reader)

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
