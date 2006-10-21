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
 * Class BaseLocationTypeSequence2Choice.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequence2Choice implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The integer position gives the end position of the feature.
     * The first base or amino acid is position 1. In combination
     * with the numeric value, the attribute 'status' allows to
     * express fuzzy positions, e.g. 'more than 400'.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType _end;

    /**
     * The end position may be varying or unclear, but localisable
     * to a certain range. Usually written as e.g. 3..5.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType _endInterval;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequence2Choice() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getEndReturns the value of field 'end'. The field
     * 'end' has the following description: The integer position
     * gives the end position of the feature. The first base or
     * amino acid is position 1. In combination with the numeric
     * value, the attribute 'status' allows to express fuzzy
     * positions, e.g. 'more than 400'.
     * 
     * @return the value of field 'end'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType getEnd()
    {
        return this._end;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType getEnd()

    /**
     * Method getEndIntervalReturns the value of field
     * 'endInterval'. The field 'endInterval' has the following
     * description: The end position may be varying or unclear, but
     * localisable to a certain range. Usually written as e.g.
     * 3..5.
     * 
     * @return the value of field 'endInterval'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType getEndInterval()
    {
        return this._endInterval;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType getEndInterval()

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
     * Method setEndSets the value of field 'end'. The field 'end'
     * has the following description: The integer position gives
     * the end position of the feature. The first base or amino
     * acid is position 1. In combination with the numeric value,
     * the attribute 'status' allows to express fuzzy positions,
     * e.g. 'more than 400'.
     * 
     * @param end the value of field 'end'.
     */
    public void setEnd(org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType end)
    {
        this._end = end;
    } //-- void setEnd(org.cytoscape.coreplugin.psi_mi.schema.mi25.PositionType)

    /**
     * Method setEndIntervalSets the value of field 'endInterval'.
     * The field 'endInterval' has the following description: The
     * end position may be varying or unclear, but localisable to a
     * certain range. Usually written as e.g. 3..5.
     * 
     * @param endInterval the value of field 'endInterval'.
     */
    public void setEndInterval(org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType endInterval)
    {
        this._endInterval = endInterval;
    } //-- void setEndInterval(org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType)

    /**
     * Method unmarshalBaseLocationTypeSequence2Choice
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice unmarshalBaseLocationTypeSequence2Choice(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.BaseLocationTypeSequence2Choice unmarshalBaseLocationTypeSequence2Choice(java.io.Reader)

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
