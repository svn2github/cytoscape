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
 * All experiments in which the interactions of this entry have
 * been determined
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentList1 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Describes one set of experimental parameters, usually
     * associated with a single publication.
     */
    private java.util.Vector _experimentDescriptionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentList1() {
        super();
        _experimentDescriptionList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList1()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExperimentDescription
     * 
     * @param vExperimentDescription
     */
    public void addExperimentDescription(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType vExperimentDescription)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentDescriptionList.addElement(vExperimentDescription);
    } //-- void addExperimentDescription(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType)

    /**
     * Method addExperimentDescription
     * 
     * @param index
     * @param vExperimentDescription
     */
    public void addExperimentDescription(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType vExperimentDescription)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentDescriptionList.insertElementAt(vExperimentDescription, index);
    } //-- void addExperimentDescription(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType)

    /**
     * Method enumerateExperimentDescription
     */
    public java.util.Enumeration enumerateExperimentDescription()
    {
        return _experimentDescriptionList.elements();
    } //-- java.util.Enumeration enumerateExperimentDescription() 

    /**
     * Method getExperimentDescription
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType getExperimentDescription(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentDescriptionList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType) _experimentDescriptionList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType getExperimentDescription(int)

    /**
     * Method getExperimentDescription
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType[] getExperimentDescription()
    {
        int size = _experimentDescriptionList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType) _experimentDescriptionList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType[] getExperimentDescription()

    /**
     * Method getExperimentDescriptionCount
     */
    public int getExperimentDescriptionCount()
    {
        return _experimentDescriptionList.size();
    } //-- int getExperimentDescriptionCount() 

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
     * Method removeAllExperimentDescription
     */
    public void removeAllExperimentDescription()
    {
        _experimentDescriptionList.removeAllElements();
    } //-- void removeAllExperimentDescription() 

    /**
     * Method removeExperimentDescription
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType removeExperimentDescription(int index)
    {
        java.lang.Object obj = _experimentDescriptionList.elementAt(index);
        _experimentDescriptionList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType removeExperimentDescription(int)

    /**
     * Method setExperimentDescription
     * 
     * @param index
     * @param vExperimentDescription
     */
    public void setExperimentDescription(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType vExperimentDescription)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentDescriptionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _experimentDescriptionList.setElementAt(vExperimentDescription, index);
    } //-- void setExperimentDescription(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType)

    /**
     * Method setExperimentDescription
     * 
     * @param experimentDescriptionArray
     */
    public void setExperimentDescription(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType[] experimentDescriptionArray)
    {
        //-- copy array
        _experimentDescriptionList.removeAllElements();
        for (int i = 0; i < experimentDescriptionArray.length; i++) {
            _experimentDescriptionList.addElement(experimentDescriptionArray[i]);
        }
    } //-- void setExperimentDescription(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentType)

    /**
     * Method unmarshalExperimentList1
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList1 unmarshalExperimentList1(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList1) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList1.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList1 unmarshalExperimentList1(java.io.Reader)

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
