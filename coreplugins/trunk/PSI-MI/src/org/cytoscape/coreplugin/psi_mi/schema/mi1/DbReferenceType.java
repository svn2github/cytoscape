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
 * Refers to a unique object in an external database.
 * 
 * @version $Revision$ $Date$
 */
public class DbReferenceType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _db
     */
    private java.lang.String _db;

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _secondary
     */
    private java.lang.String _secondary;

    /**
     * Field _version
     */
    private java.lang.String _version;


      //----------------/
     //- Constructors -/
    //----------------/

    public DbReferenceType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.DbReferenceType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getDbReturns the value of field 'db'.
     * 
     * @return the value of field 'db'.
     */
    public java.lang.String getDb()
    {
        return this._db;
    } //-- java.lang.String getDb() 

    /**
     * Method getIdReturns the value of field 'id'.
     * 
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Method getSecondaryReturns the value of field 'secondary'.
     * 
     * @return the value of field 'secondary'.
     */
    public java.lang.String getSecondary()
    {
        return this._secondary;
    } //-- java.lang.String getSecondary() 

    /**
     * Method getVersionReturns the value of field 'version'.
     * 
     * @return the value of field 'version'.
     */
    public java.lang.String getVersion()
    {
        return this._version;
    } //-- java.lang.String getVersion() 

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
     * Method setDbSets the value of field 'db'.
     * 
     * @param db the value of field 'db'.
     */
    public void setDb(java.lang.String db)
    {
        this._db = db;
    } //-- void setDb(java.lang.String) 

    /**
     * Method setIdSets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method setSecondarySets the value of field 'secondary'.
     * 
     * @param secondary the value of field 'secondary'.
     */
    public void setSecondary(java.lang.String secondary)
    {
        this._secondary = secondary;
    } //-- void setSecondary(java.lang.String) 

    /**
     * Method setVersionSets the value of field 'version'.
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(java.lang.String version)
    {
        this._version = version;
    } //-- void setVersion(java.lang.String) 

    /**
     * Method unmarshalDbReferenceType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.DbReferenceType unmarshalDbReferenceType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.DbReferenceType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.DbReferenceType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.DbReferenceType unmarshalDbReferenceType(java.io.Reader)

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
