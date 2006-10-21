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

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Confidence in this interaction. Usually a statistical measure.
 * 
 * @version $Revision$ $Date$
 */
public class Confidence implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _unit
     */
    private java.lang.String _unit;

    /**
     * Field _value
     */
    private java.lang.String _value;


      //----------------/
     //- Constructors -/
    //----------------/

    public Confidence() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getUnitReturns the value of field 'unit'.
     * 
     * @return the value of field 'unit'.
     */
    public java.lang.String getUnit()
    {
        return this._unit;
    } //-- java.lang.String getUnit() 

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
    public void setUnit(java.lang.String unit)
    {
        this._unit = unit;
    } //-- void setUnit(java.lang.String) 

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
     * Method unmarshalConfidence
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence unmarshalConfidence(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence unmarshalConfidence(java.io.Reader)

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
