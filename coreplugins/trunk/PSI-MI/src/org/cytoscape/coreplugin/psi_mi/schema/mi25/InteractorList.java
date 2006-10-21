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
 * List of all interactors occurring in the entry
 * 
 * @version $Revision$ $Date$
 */
public class InteractorList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A molecule object in its native state, as described in
     * databases.
     */
    private java.util.Vector _interactorList;


      //----------------/
     //- Constructors -/
    //----------------/

    public InteractorList() {
        super();
        _interactorList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addInteractor
     * 
     * @param vInteractor
     */
    public void addInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType vInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactorList.addElement(vInteractor);
    } //-- void addInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType)

    /**
     * Method addInteractor
     * 
     * @param index
     * @param vInteractor
     */
    public void addInteractor(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType vInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactorList.insertElementAt(vInteractor, index);
    } //-- void addInteractor(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType)

    /**
     * Method enumerateInteractor
     */
    public java.util.Enumeration enumerateInteractor()
    {
        return _interactorList.elements();
    } //-- java.util.Enumeration enumerateInteractor() 

    /**
     * Method getInteractor
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType getInteractor(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactorList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType) _interactorList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType getInteractor(int)

    /**
     * Method getInteractor
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType[] getInteractor()
    {
        int size = _interactorList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType) _interactorList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType[] getInteractor()

    /**
     * Method getInteractorCount
     */
    public int getInteractorCount()
    {
        return _interactorList.size();
    } //-- int getInteractorCount() 

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
     * Method removeAllInteractor
     */
    public void removeAllInteractor()
    {
        _interactorList.removeAllElements();
    } //-- void removeAllInteractor() 

    /**
     * Method removeInteractor
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType removeInteractor(int index)
    {
        java.lang.Object obj = _interactorList.elementAt(index);
        _interactorList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType removeInteractor(int)

    /**
     * Method setInteractor
     * 
     * @param index
     * @param vInteractor
     */
    public void setInteractor(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType vInteractor)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _interactorList.setElementAt(vInteractor, index);
    } //-- void setInteractor(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType)

    /**
     * Method setInteractor
     * 
     * @param interactorArray
     */
    public void setInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType[] interactorArray)
    {
        //-- copy array
        _interactorList.removeAllElements();
        for (int i = 0; i < interactorArray.length; i++) {
            _interactorList.addElement(interactorArray[i]);
        }
    } //-- void setInteractor(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorElementType)

    /**
     * Method unmarshalInteractorList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorList unmarshalInteractorList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractorList unmarshalInteractorList(java.io.Reader)

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
