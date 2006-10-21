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
 * Names for an object.
 * 
 * @version $Revision$ $Date$
 */
public class NamesType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _shortLabel
     */
    private java.lang.String _shortLabel;

    /**
     * Field _fullName
     */
    private java.lang.String _fullName;


      //----------------/
     //- Constructors -/
    //----------------/

    public NamesType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getFullNameReturns the value of field 'fullName'.
     * 
     * @return the value of field 'fullName'.
     */
    public java.lang.String getFullName()
    {
        return this._fullName;
    } //-- java.lang.String getFullName() 

    /**
     * Method getShortLabelReturns the value of field 'shortLabel'.
     * 
     * @return the value of field 'shortLabel'.
     */
    public java.lang.String getShortLabel()
    {
        return this._shortLabel;
    } //-- java.lang.String getShortLabel() 

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
     * Method setFullNameSets the value of field 'fullName'.
     * 
     * @param fullName the value of field 'fullName'.
     */
    public void setFullName(java.lang.String fullName)
    {
        this._fullName = fullName;
    } //-- void setFullName(java.lang.String) 

    /**
     * Method setShortLabelSets the value of field 'shortLabel'.
     * 
     * @param shortLabel the value of field 'shortLabel'.
     */
    public void setShortLabel(java.lang.String shortLabel)
    {
        this._shortLabel = shortLabel;
    } //-- void setShortLabel(java.lang.String) 

    /**
     * Method unmarshalNamesType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType unmarshalNamesType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType unmarshalNamesType(java.io.Reader)

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
