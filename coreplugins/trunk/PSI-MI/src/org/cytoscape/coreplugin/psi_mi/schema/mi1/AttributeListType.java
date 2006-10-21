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
 * A list of additional attributes. Open tag-value list to allow
 * the inclusion of additional data.
 * 
 * @version $Revision$ $Date$
 */
public class AttributeListType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _attributeList
     */
    private java.util.Vector _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public AttributeListType() {
        super();
        _attributeList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAttribute
     * 
     * @param vAttribute
     */
    public void addAttribute(org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute vAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _attributeList.addElement(vAttribute);
    } //-- void addAttribute(org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute)

    /**
     * Method addAttribute
     * 
     * @param index
     * @param vAttribute
     */
    public void addAttribute(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute vAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        _attributeList.insertElementAt(vAttribute, index);
    } //-- void addAttribute(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute)

    /**
     * Method enumerateAttribute
     */
    public java.util.Enumeration enumerateAttribute()
    {
        return _attributeList.elements();
    } //-- java.util.Enumeration enumerateAttribute() 

    /**
     * Method getAttribute
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute getAttribute(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _attributeList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute) _attributeList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute getAttribute(int)

    /**
     * Method getAttribute
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute[] getAttribute()
    {
        int size = _attributeList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute) _attributeList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute[] getAttribute()

    /**
     * Method getAttributeCount
     */
    public int getAttributeCount()
    {
        return _attributeList.size();
    } //-- int getAttributeCount() 

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
     * Method removeAllAttribute
     */
    public void removeAllAttribute()
    {
        _attributeList.removeAllElements();
    } //-- void removeAllAttribute() 

    /**
     * Method removeAttribute
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute removeAttribute(int index)
    {
        java.lang.Object obj = _attributeList.elementAt(index);
        _attributeList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute removeAttribute(int)

    /**
     * Method setAttribute
     * 
     * @param index
     * @param vAttribute
     */
    public void setAttribute(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute vAttribute)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _attributeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _attributeList.setElementAt(vAttribute, index);
    } //-- void setAttribute(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute)

    /**
     * Method setAttribute
     * 
     * @param attributeArray
     */
    public void setAttribute(org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute[] attributeArray)
    {
        //-- copy array
        _attributeList.removeAllElements();
        for (int i = 0; i < attributeArray.length; i++) {
            _attributeList.addElement(attributeArray[i]);
        }
    } //-- void setAttribute(org.cytoscape.coreplugin.psi_mi.schema.mi1.Attribute)

    /**
     * Method unmarshalAttributeListType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType unmarshalAttributeListType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType unmarshalAttributeListType(java.io.Reader)

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
