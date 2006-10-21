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
 * A location can be either a position, site or have a start and
 * end, only start, only end or is an empty element if the position
 * is unknown.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _baseLocationTypeSequence
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence _baseLocationTypeSequence;

    /**
     * Field _position
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType _position;

    /**
     * Field _positionInterval
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType _positionInterval;

    /**
     * Field _site
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType _site;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBaseLocationTypeSequenceReturns the value of field
     * 'baseLocationTypeSequence'.
     * 
     * @return the value of field 'baseLocationTypeSequence'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence getBaseLocationTypeSequence()
    {
        return this._baseLocationTypeSequence;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence getBaseLocationTypeSequence()

    /**
     * Method getPositionReturns the value of field 'position'.
     * 
     * @return the value of field 'position'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getPosition()
    {
        return this._position;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getPosition()

    /**
     * Method getPositionIntervalReturns the value of field
     * 'positionInterval'.
     * 
     * @return the value of field 'positionInterval'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType getPositionInterval()
    {
        return this._positionInterval;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType getPositionInterval()

    /**
     * Method getSiteReturns the value of field 'site'.
     * 
     * @return the value of field 'site'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getSite()
    {
        return this._site;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getSite()

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
     * Method setBaseLocationTypeSequenceSets the value of field
     * 'baseLocationTypeSequence'.
     * 
     * @param baseLocationTypeSequence the value of field
     * 'baseLocationTypeSequence'.
     */
    public void setBaseLocationTypeSequence(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence baseLocationTypeSequence)
    {
        this._baseLocationTypeSequence = baseLocationTypeSequence;
    } //-- void setBaseLocationTypeSequence(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequence)

    /**
     * Method setPositionSets the value of field 'position'.
     * 
     * @param position the value of field 'position'.
     */
    public void setPosition(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType position)
    {
        this._position = position;
    } //-- void setPosition(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType)

    /**
     * Method setPositionIntervalSets the value of field
     * 'positionInterval'.
     * 
     * @param positionInterval the value of field 'positionInterval'
     */
    public void setPositionInterval(org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType positionInterval)
    {
        this._positionInterval = positionInterval;
    } //-- void setPositionInterval(org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType)

    /**
     * Method setSiteSets the value of field 'site'.
     * 
     * @param site the value of field 'site'.
     */
    public void setSite(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType site)
    {
        this._site = site;
    } //-- void setSite(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType)

    /**
     * Method unmarshalBaseLocationType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType unmarshalBaseLocationType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationType unmarshalBaseLocationType(java.io.Reader)

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
