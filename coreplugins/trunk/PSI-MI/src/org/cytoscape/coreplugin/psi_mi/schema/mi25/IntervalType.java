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
 * A interval on a sequence.
 * 
 * @version $Revision$ $Date$
 */
public class IntervalType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _begin
     */
    private long _begin;

    /**
     * keeps track of state for field: _begin
     */
    private boolean _has_begin;

    /**
     * Field _end
     */
    private long _end;

    /**
     * keeps track of state for field: _end
     */
    private boolean _has_end;


      //----------------/
     //- Constructors -/
    //----------------/

    public IntervalType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getBeginReturns the value of field 'begin'.
     * 
     * @return the value of field 'begin'.
     */
    public long getBegin()
    {
        return this._begin;
    } //-- long getBegin() 

    /**
     * Method getEndReturns the value of field 'end'.
     * 
     * @return the value of field 'end'.
     */
    public long getEnd()
    {
        return this._end;
    } //-- long getEnd() 

    /**
     * Method hasBegin
     */
    public boolean hasBegin()
    {
        return this._has_begin;
    } //-- boolean hasBegin() 

    /**
     * Method hasEnd
     */
    public boolean hasEnd()
    {
        return this._has_end;
    } //-- boolean hasEnd() 

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
    public void setBegin(long begin)
    {
        this._begin = begin;
        this._has_begin = true;
    } //-- void setBegin(long) 

    /**
     * Method setEndSets the value of field 'end'.
     * 
     * @param end the value of field 'end'.
     */
    public void setEnd(long end)
    {
        this._end = end;
        this._has_end = true;
    } //-- void setEnd(long) 

    /**
     * Method unmarshalIntervalType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType unmarshalIntervalType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.IntervalType unmarshalIntervalType(java.io.Reader)

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
