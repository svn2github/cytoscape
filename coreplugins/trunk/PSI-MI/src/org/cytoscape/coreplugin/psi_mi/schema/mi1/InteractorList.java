/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.3</a>, using an XML
 * Schema.
 * $Id$
 */

package org.cytoscape.coreplugin.psi_mi.schema.mi1;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * List of all interactors occurring in the entry
 * 
 * @version $Revision$ $Date$
 */
public class InteractorList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A protein object participating in an interaction.
     */
    private java.util.Vector _proteinInteractorList;


      //----------------/
     //- Constructors -/
    //----------------/

    public InteractorList() {
        super();
        _proteinInteractorList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addProteinInteractor
     * 
     * @param vProteinInteractor
     */
    public void addProteinInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType vProteinInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        _proteinInteractorList.addElement(vProteinInteractor);
    } //-- void addProteinInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType)

    /**
     * Method addProteinInteractor
     * 
     * @param index
     * @param vProteinInteractor
     */
    public void addProteinInteractor(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType vProteinInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        _proteinInteractorList.insertElementAt(vProteinInteractor, index);
    } //-- void addProteinInteractor(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType)

    /**
     * Method enumerateProteinInteractor
     */
    public java.util.Enumeration enumerateProteinInteractor()
    {
        return _proteinInteractorList.elements();
    } //-- java.util.Enumeration enumerateProteinInteractor() 

    /**
     * Method getProteinInteractor
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType getProteinInteractor(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _proteinInteractorList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType) _proteinInteractorList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType getProteinInteractor(int)

    /**
     * Method getProteinInteractor
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType[] getProteinInteractor()
    {
        int size = _proteinInteractorList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType) _proteinInteractorList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType[] getProteinInteractor()

    /**
     * Method getProteinInteractorCount
     */
    public int getProteinInteractorCount()
    {
        return _proteinInteractorList.size();
    } //-- int getProteinInteractorCount() 

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
     * Method removeAllProteinInteractor
     */
    public void removeAllProteinInteractor()
    {
        _proteinInteractorList.removeAllElements();
    } //-- void removeAllProteinInteractor() 

    /**
     * Method removeProteinInteractor
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType removeProteinInteractor(int index)
    {
        java.lang.Object obj = _proteinInteractorList.elementAt(index);
        _proteinInteractorList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType removeProteinInteractor(int)

    /**
     * Method setProteinInteractor
     * 
     * @param index
     * @param vProteinInteractor
     */
    public void setProteinInteractor(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType vProteinInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _proteinInteractorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _proteinInteractorList.setElementAt(vProteinInteractor, index);
    } //-- void setProteinInteractor(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType)

    /**
     * Method setProteinInteractor
     * 
     * @param proteinInteractorArray
     */
    public void setProteinInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType[] proteinInteractorArray)
    {
        //-- copy array
        _proteinInteractorList.removeAllElements();
        for (int i = 0; i < proteinInteractorArray.length; i++) {
            _proteinInteractorList.addElement(proteinInteractorArray[i]);
        }
    } //-- void setProteinInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinInteractorType)

    /**
     * Method unmarshalInteractorList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList unmarshalInteractorList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.InteractorList unmarshalInteractorList(java.io.Reader)

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
