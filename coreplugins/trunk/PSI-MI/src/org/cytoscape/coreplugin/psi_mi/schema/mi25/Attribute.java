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
 * Class Attribute.
 * 
 * @version $Revision$ $Date$
 */
public class Attribute implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
    private java.lang.String _content = "";

    /**
     * The name of the attribute.
     */
    private java.lang.String _name;

    /**
     * Enables control of the attribute type through reference to
     * an external controlled vocabulary. Root element in the PSI
     * MI CV is MI:0590.
     */
    private java.lang.String _nameAc;


      //----------------/
     //- Constructors -/
    //----------------/

    public Attribute() {
        super();
        setContent("");
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Attribute()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getContentReturns the value of field 'content'. The
     * field 'content' has the following description: internal
     * content storage
     * 
     * @return the value of field 'content'.
     */
    public java.lang.String getContent()
    {
        return this._content;
    } //-- java.lang.String getContent() 

    /**
     * Method getNameReturns the value of field 'name'. The field
     * 'name' has the following description: The name of the
     * attribute.
     * 
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method getNameAcReturns the value of field 'nameAc'. The
     * field 'nameAc' has the following description: Enables
     * control of the attribute type through reference to an
     * external controlled vocabulary. Root element in the PSI MI
     * CV is MI:0590.
     * 
     * @return the value of field 'nameAc'.
     */
    public java.lang.String getNameAc()
    {
        return this._nameAc;
    } //-- java.lang.String getNameAc() 

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
     * Method setContentSets the value of field 'content'. The
     * field 'content' has the following description: internal
     * content storage
     * 
     * @param content the value of field 'content'.
     */
    public void setContent(java.lang.String content)
    {
        this._content = content;
    } //-- void setContent(java.lang.String) 

    /**
     * Method setNameSets the value of field 'name'. The field
     * 'name' has the following description: The name of the
     * attribute.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method setNameAcSets the value of field 'nameAc'. The field
     * 'nameAc' has the following description: Enables control of
     * the attribute type through reference to an external
     * controlled vocabulary. Root element in the PSI MI CV is
     * MI:0590.
     * 
     * @param nameAc the value of field 'nameAc'.
     */
    public void setNameAc(java.lang.String nameAc)
    {
        this._nameAc = nameAc;
    } //-- void setNameAc(java.lang.String) 

    /**
     * Method unmarshalAttribute
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.Attribute unmarshalAttribute(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Attribute) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.Attribute.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Attribute unmarshalAttribute(java.io.Reader)

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
