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
 * Describes molecules which have been used in specific experiments
 * if these molecules are different from the one listed as
 * interactors. Example: The author of a paper makes a statement
 * about human proteins, but has really worked with mouse proteins.
 * In this case the human protein would be the main interactor,
 * while the experimentalForm would be the mouse protein listed in
 * this element. Optionally this can refer to the experiment(s) in
 * which this form has been used. 
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentalInteractorList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _experimentalInteractorList
     */
    private java.util.Vector _experimentalInteractorList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentalInteractorList() {
        super();
        _experimentalInteractorList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExperimentalInteractor
     * 
     * @param vExperimentalInteractor
     */
    public void addExperimentalInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor vExperimentalInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentalInteractorList.addElement(vExperimentalInteractor);
    } //-- void addExperimentalInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor)

    /**
     * Method addExperimentalInteractor
     * 
     * @param index
     * @param vExperimentalInteractor
     */
    public void addExperimentalInteractor(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor vExperimentalInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        _experimentalInteractorList.insertElementAt(vExperimentalInteractor, index);
    } //-- void addExperimentalInteractor(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor)

    /**
     * Method enumerateExperimentalInteractor
     */
    public java.util.Enumeration enumerateExperimentalInteractor()
    {
        return _experimentalInteractorList.elements();
    } //-- java.util.Enumeration enumerateExperimentalInteractor() 

    /**
     * Method getExperimentalInteractor
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor getExperimentalInteractor(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentalInteractorList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor) _experimentalInteractorList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor getExperimentalInteractor(int)

    /**
     * Method getExperimentalInteractor
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor[] getExperimentalInteractor()
    {
        int size = _experimentalInteractorList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor) _experimentalInteractorList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor[] getExperimentalInteractor()

    /**
     * Method getExperimentalInteractorCount
     */
    public int getExperimentalInteractorCount()
    {
        return _experimentalInteractorList.size();
    } //-- int getExperimentalInteractorCount() 

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
     * Method removeAllExperimentalInteractor
     */
    public void removeAllExperimentalInteractor()
    {
        _experimentalInteractorList.removeAllElements();
    } //-- void removeAllExperimentalInteractor() 

    /**
     * Method removeExperimentalInteractor
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor removeExperimentalInteractor(int index)
    {
        java.lang.Object obj = _experimentalInteractorList.elementAt(index);
        _experimentalInteractorList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor removeExperimentalInteractor(int)

    /**
     * Method setExperimentalInteractor
     * 
     * @param index
     * @param vExperimentalInteractor
     */
    public void setExperimentalInteractor(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor vExperimentalInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _experimentalInteractorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _experimentalInteractorList.setElementAt(vExperimentalInteractor, index);
    } //-- void setExperimentalInteractor(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor)

    /**
     * Method setExperimentalInteractor
     * 
     * @param experimentalInteractorArray
     */
    public void setExperimentalInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor[] experimentalInteractorArray)
    {
        //-- copy array
        _experimentalInteractorList.removeAllElements();
        for (int i = 0; i < experimentalInteractorArray.length; i++) {
            _experimentalInteractorList.addElement(experimentalInteractorArray[i]);
        }
    } //-- void setExperimentalInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor)

    /**
     * Method unmarshalExperimentalInteractorList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList unmarshalExperimentalInteractorList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorList unmarshalExperimentalInteractorList(java.io.Reader)

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
