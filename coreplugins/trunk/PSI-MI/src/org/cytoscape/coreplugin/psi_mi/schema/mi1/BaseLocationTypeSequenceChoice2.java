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
 * Class BaseLocationTypeSequenceChoice2.
 * 
 * @version $Revision$ $Date$
 */
public class BaseLocationTypeSequenceChoice2 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _end
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType _end;

    /**
     * Field _endInterval
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType _endInterval;


      //----------------/
     //- Constructors -/
    //----------------/

    public BaseLocationTypeSequenceChoice2() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getEndReturns the value of field 'end'.
     * 
     * @return the value of field 'end'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getEnd()
    {
        return this._end;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType getEnd()

    /**
     * Method getEndIntervalReturns the value of field
     * 'endInterval'.
     * 
     * @return the value of field 'endInterval'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType getEndInterval()
    {
        return this._endInterval;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType getEndInterval()

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
     * Method setEndSets the value of field 'end'.
     * 
     * @param end the value of field 'end'.
     */
    public void setEnd(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType end)
    {
        this._end = end;
    } //-- void setEnd(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType)

    /**
     * Method setEndIntervalSets the value of field 'endInterval'.
     * 
     * @param endInterval the value of field 'endInterval'.
     */
    public void setEndInterval(org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType endInterval)
    {
        this._endInterval = endInterval;
    } //-- void setEndInterval(org.cytoscape.coreplugin.psi_mi.schema.mi1.IntervalType)

    /**
     * Method unmarshalBaseLocationTypeSequenceChoice2
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2 unmarshalBaseLocationTypeSequenceChoice2(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BaseLocationTypeSequenceChoice2 unmarshalBaseLocationTypeSequenceChoice2(java.io.Reader)

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
