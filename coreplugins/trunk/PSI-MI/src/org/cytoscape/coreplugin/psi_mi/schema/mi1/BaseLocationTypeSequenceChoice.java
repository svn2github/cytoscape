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
 * Class BaseLocationTypeSequenceChoice.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequenceChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _begin
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType _begin;

    /**
     * Field _beginInterval
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType _beginInterval;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequenceChoice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBeginReturns the value of field 'begin'.
     * 
     * @return the value of field 'begin'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getBegin()
    {
        return this._begin;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getBegin()

    /**
     * Method getBeginIntervalReturns the value of field
     * 'beginInterval'.
     * 
     * @return the value of field 'beginInterval'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType getBeginInterval()
    {
        return this._beginInterval;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType getBeginInterval()

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
     * Method setBeginSets the value of field 'begin'.
     * 
     * @param begin the value of field 'begin'.
     */
    public void setBegin(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType begin)
    {
        this._begin = begin;
    } //-- void setBegin(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType)

    /**
     * Method setBeginIntervalSets the value of field
     * 'beginInterval'.
     * 
     * @param beginInterval the value of field 'beginInterval'.
     */
    public void setBeginInterval(org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType beginInterval)
    {
        this._beginInterval = beginInterval;
    } //-- void setBeginInterval(org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType)

    /**
     * Method unmarshalBaseLocationTypeSequenceChoice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice unmarshalBaseLocationTypeSequenceChoice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice unmarshalBaseLocationTypeSequenceChoice(java.io.Reader)

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
