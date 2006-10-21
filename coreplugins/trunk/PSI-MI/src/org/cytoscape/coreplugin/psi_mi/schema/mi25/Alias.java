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
 * Class Alias.
 * 
 * @version $Revision$ $Date$
 */
public class Alias implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
    private java.lang.String _content = "";

    /**
     * Field _typeAc
     */
    private java.lang.String _typeAc;

    /**
     * Field _type
     */
    private java.lang.String _type;


      //----------------/
     //- Constructors -/
    //----------------/

    public Alias() {
        super();
        setContent("");
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias()


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
     * Method getTypeReturns the value of field 'type'.
     * 
     * @return the value of field 'type'.
     */
    public java.lang.String getType()
    {
        return this._type;
    } //-- java.lang.String getType() 

    /**
     * Method getTypeAcReturns the value of field 'typeAc'.
     * 
     * @return the value of field 'typeAc'.
     */
    public java.lang.String getTypeAc()
    {
        return this._typeAc;
    } //-- java.lang.String getTypeAc() 

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
     * Method setTypeSets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(java.lang.String type)
    {
        this._type = type;
    } //-- void setType(java.lang.String) 

    /**
     * Method setTypeAcSets the value of field 'typeAc'.
     * 
     * @param typeAc the value of field 'typeAc'.
     */
    public void setTypeAc(java.lang.String typeAc)
    {
        this._typeAc = typeAc;
    } //-- void setTypeAc(java.lang.String) 

    /**
     * Method unmarshalAlias
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias unmarshalAlias(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias unmarshalAlias(java.io.Reader)

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
