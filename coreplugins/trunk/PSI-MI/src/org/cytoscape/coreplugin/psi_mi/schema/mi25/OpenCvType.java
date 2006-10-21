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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Allows to reference an external controlled vocabulary, or to
 * directly include a value if no suitable external definition is
 * available.
 * 
 * @version $Revision$ $Date$
 */
public class OpenCvType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * This contains the controlled vocabulary terms, as a short
     * and optionally as a long form.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType _names;

    /**
     * Refers to the term of the controlled vocabulary in an
     * external database.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType _xref;

    /**
     * If no suitable external controlled vocabulary is available,
     * this attributeList can be used to describe the term.
     * Example: Attribute name: Mouse atlas tissue name; attribute
     * value: spinal cord, day 30.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public OpenCvType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: If no suitable external controlled vocabulary
     * is available, this attributeList can be used to describe the
     * term. Example: Attribute name: Mouse atlas tissue name;
     * attribute value: spinal cord, day 30.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: This contains the
     * controlled vocabulary terms, as a short and optionally as a
     * long form.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType getNames()

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Refers to the term of
     * the controlled vocabulary in an external database.
     * 
     * @return the value of field 'xref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType getXref()
    {
        return this._xref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType getXref()

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
     * Method setAttributeListSets the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: If no suitable external controlled vocabulary
     * is available, this attributeList can be used to describe the
     * term. Example: Attribute name: Mouse atlas tissue name;
     * attribute value: spinal cord, day 30.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType)

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: This contains the
     * controlled vocabulary terms, as a short and optionally as a
     * long form.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType)

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Refers to the term of
     * the controlled vocabulary in an external database.
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi25.XrefType)

    /**
     * Method unmarshalOpenCvType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType unmarshalOpenCvType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType unmarshalOpenCvType(java.io.Reader)

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
