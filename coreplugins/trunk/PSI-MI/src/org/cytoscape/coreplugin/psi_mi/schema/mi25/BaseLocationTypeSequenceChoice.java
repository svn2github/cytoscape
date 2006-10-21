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
 * Class BaseLocationTypeSequenceChoice.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequenceChoice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The integer position gives the begin position of the
     * feature. The first base or amino acid is position 1. In
     * combination with the numeric value, the attribute 'status'
     * allows to express fuzzy positions, e.g. 'less than 4'.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType _begin;

    /**
     * The begin position may be varying or unclear, but
     * localisable to a certain range. Usually written as e.g. 3..5.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType _beginInterval;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequenceChoice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBeginReturns the value of field 'begin'. The field
     * 'begin' has the following description: The integer position
     * gives the begin position of the feature. The first base or
     * amino acid is position 1. In combination with the numeric
     * value, the attribute 'status' allows to express fuzzy
     * positions, e.g. 'less than 4'.
     * 
     * @return the value of field 'begin'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType getBegin()
    {
        return this._begin;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType getBegin()

    /**
     * Method getBeginIntervalReturns the value of field
     * 'beginInterval'. The field 'beginInterval' has the following
     * description: The begin position may be varying or unclear,
     * but localisable to a certain range. Usually written as e.g.
     * 3..5.
     * 
     * @return the value of field 'beginInterval'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType getBeginInterval()
    {
        return this._beginInterval;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType getBeginInterval()

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
     * Method setBeginSets the value of field 'begin'. The field
     * 'begin' has the following description: The integer position
     * gives the begin position of the feature. The first base or
     * amino acid is position 1. In combination with the numeric
     * value, the attribute 'status' allows to express fuzzy
     * positions, e.g. 'less than 4'.
     * 
     * @param begin the value of field 'begin'.
     */
    public void setBegin(org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType begin)
    {
        this._begin = begin;
    } //-- void setBegin(org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType)

    /**
     * Method setBeginIntervalSets the value of field
     * 'beginInterval'. The field 'beginInterval' has the following
     * description: The begin position may be varying or unclear,
     * but localisable to a certain range. Usually written as e.g.
     * 3..5.
     * 
     * @param beginInterval the value of field 'beginInterval'.
     */
    public void setBeginInterval(org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType beginInterval)
    {
        this._beginInterval = beginInterval;
    } //-- void setBeginInterval(org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType)

    /**
     * Method unmarshalBaseLocationTypeSequenceChoice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice unmarshalBaseLocationTypeSequenceChoice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequenceChoice unmarshalBaseLocationTypeSequenceChoice(java.io.Reader)

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
