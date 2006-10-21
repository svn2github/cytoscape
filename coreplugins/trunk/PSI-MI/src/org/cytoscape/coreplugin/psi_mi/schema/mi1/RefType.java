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
 * Refers to an object within the same file by its id.
 * 
 * @version $Revision$ $Date$
 */
public class RefType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _ref
     */
    private java.lang.String _ref;


      //----------------/
     //- Constructors -/
    //----------------/

    public RefType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getRefReturns the value of field 'ref'.
     * 
     * @return the value of field 'ref'.
     */
    public java.lang.String getRef()
    {
        return this._ref;
    } //-- java.lang.String getRef() 

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
     * Method setRefSets the value of field 'ref'.
     * 
     * @param ref the value of field 'ref'.
     */
    public void setRef(java.lang.String ref)
    {
        this._ref = ref;
    } //-- void setRef(java.lang.String) 

    /**
     * Method unmarshalRefType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType unmarshalRefType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.RefType unmarshalRefType(java.io.Reader)

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
