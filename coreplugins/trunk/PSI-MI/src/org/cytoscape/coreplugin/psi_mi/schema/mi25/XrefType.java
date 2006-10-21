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
 * Crossreference to an external database. Crossreferences to
 * literature databases, e.g. PubMed, should not be put into this
 * structure, but into the bibRef element where possible.
 * 
 * @version $Revision$ $Date$
 */
public class XrefType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Primary reference to an external database.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType _primaryRef;

    /**
     * Further external objects describing the object.
     */
    private java.util.Vector _secondaryRefList;


      //----------------/
     //- Constructors -/
    //----------------/

    public XrefType() {
        super();
        _secondaryRefList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addSecondaryRef
     * 
     * @param vSecondaryRef
     */
    public void addSecondaryRef(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType vSecondaryRef)
        throws java.lang.IndexOutOfBoundsException
    {
        _secondaryRefList.addElement(vSecondaryRef);
    } //-- void addSecondaryRef(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType)

    /**
     * Method addSecondaryRef
     * 
     * @param index
     * @param vSecondaryRef
     */
    public void addSecondaryRef(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType vSecondaryRef)
        throws java.lang.IndexOutOfBoundsException
    {
        _secondaryRefList.insertElementAt(vSecondaryRef, index);
    } //-- void addSecondaryRef(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType)

    /**
     * Method enumerateSecondaryRef
     */
    public java.util.Enumeration enumerateSecondaryRef()
    {
        return _secondaryRefList.elements();
    } //-- java.util.Enumeration enumerateSecondaryRef() 

    /**
     * Method getPrimaryRefReturns the value of field 'primaryRef'.
     * The field 'primaryRef' has the following description:
     * Primary reference to an external database.
     * 
     * @return the value of field 'primaryRef'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType getPrimaryRef()
    {
        return this._primaryRef;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType getPrimaryRef()

    /**
     * Method getSecondaryRef
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType getSecondaryRef(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _secondaryRefList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType) _secondaryRefList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType getSecondaryRef(int)

    /**
     * Method getSecondaryRef
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType[] getSecondaryRef()
    {
        int size = _secondaryRefList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType) _secondaryRefList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType[] getSecondaryRef()

    /**
     * Method getSecondaryRefCount
     */
    public int getSecondaryRefCount()
    {
        return _secondaryRefList.size();
    } //-- int getSecondaryRefCount() 

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
     * Method removeAllSecondaryRef
     */
    public void removeAllSecondaryRef()
    {
        _secondaryRefList.removeAllElements();
    } //-- void removeAllSecondaryRef() 

    /**
     * Method removeSecondaryRef
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType removeSecondaryRef(int index)
    {
        java.lang.Object obj = _secondaryRefList.elementAt(index);
        _secondaryRefList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType removeSecondaryRef(int)

    /**
     * Method setPrimaryRefSets the value of field 'primaryRef'.
     * The field 'primaryRef' has the following description:
     * Primary reference to an external database.
     * 
     * @param primaryRef the value of field 'primaryRef'.
     */
    public void setPrimaryRef(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType primaryRef)
    {
        this._primaryRef = primaryRef;
    } //-- void setPrimaryRef(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType)

    /**
     * Method setSecondaryRef
     * 
     * @param index
     * @param vSecondaryRef
     */
    public void setSecondaryRef(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType vSecondaryRef)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _secondaryRefList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _secondaryRefList.setElementAt(vSecondaryRef, index);
    } //-- void setSecondaryRef(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType)

    /**
     * Method setSecondaryRef
     * 
     * @param secondaryRefArray
     */
    public void setSecondaryRef(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType[] secondaryRefArray)
    {
        //-- copy array
        _secondaryRefList.removeAllElements();
        for (int i = 0; i < secondaryRefArray.length; i++) {
            _secondaryRefList.addElement(secondaryRefArray[i]);
        }
    } //-- void setSecondaryRef(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType)

    /**
     * Method unmarshalXrefType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType unmarshalXrefType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType unmarshalXrefType(java.io.Reader)

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
