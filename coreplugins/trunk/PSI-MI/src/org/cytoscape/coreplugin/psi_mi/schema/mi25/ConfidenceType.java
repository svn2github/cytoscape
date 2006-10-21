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
 * A confidence value.
 * 
 * @version $Revision$ $Date$
 */
public class ConfidenceType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _unit
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType _unit;

    /**
     * Field _value
     */
    private java.lang.String _value;


      //----------------/
     //- Constructors -/
    //----------------/

    public ConfidenceType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getUnitReturns the value of field 'unit'.
     * 
     * @return the value of field 'unit'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType getUnit()
    {
        return this._unit;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType getUnit()

    /**
     * Method getValueReturns the value of field 'value'.
     * 
     * @return the value of field 'value'.
     */
    public java.lang.String getValue()
    {
        return this._value;
    } //-- java.lang.String getValue() 

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
     * Method setUnitSets the value of field 'unit'.
     * 
     * @param unit the value of field 'unit'.
     */
    public void setUnit(org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType unit)
    {
        this._unit = unit;
    } //-- void setUnit(org.cytoscape.coreplugin.psi_mi.schema.mi25.OpenCvType)

    /**
     * Method setValueSets the value of field 'value'.
     * 
     * @param value the value of field 'value'.
     */
    public void setValue(java.lang.String value)
    {
        this._value = value;
    } //-- void setValue(java.lang.String) 

    /**
     * Method unmarshalConfidenceType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceType unmarshalConfidenceType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ConfidenceType unmarshalConfidenceType(java.io.Reader)

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
