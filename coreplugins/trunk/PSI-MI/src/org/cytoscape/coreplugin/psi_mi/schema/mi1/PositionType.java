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
 * A specified position a a sequence.
 * 
 * @version $Revision$ $Date$
 */
public class PositionType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _position
     */
    private long _position;

    /**
     * keeps track of state for field: _position
     */
    private boolean _has_position;


      //----------------/
     //- Constructors -/
    //----------------/

    public PositionType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getPositionReturns the value of field 'position'.
     * 
     * @return the value of field 'position'.
     */
    public long getPosition()
    {
        return this._position;
    } //-- long getPosition() 

    /**
     * Method hasPosition
     */
    public boolean hasPosition()
    {
        return this._has_position;
    } //-- boolean hasPosition() 

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
     * Method setPositionSets the value of field 'position'.
     * 
     * @param position the value of field 'position'.
     */
    public void setPosition(long position)
    {
        this._position = position;
        this._has_position = true;
    } //-- void setPosition(long) 

    /**
     * Method unmarshalPositionType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType unmarshalPositionType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.PositionType unmarshalPositionType(java.io.Reader)

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
