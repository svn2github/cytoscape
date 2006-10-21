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
 * Refers to a unique object in an external database.
 * 
 * @version $Revision$ $Date$
 */
public class DbReferenceType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Name of the external database. Taken from the controlled
     * vocabulary of databases.
     */
    private java.lang.String _db;

    /**
     * Accession number of the database in the database CV. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "database citation", root term id MI:0444.
     */
    private java.lang.String _dbAc;

    /**
     * Primary identifier of the object in the external database,
     * e.g. UniProt accession number.
     */
    private java.lang.String _id;

    /**
     * Secondary identifier of the object in the external database,
     * e.g. UniProt ID.
     */
    private java.lang.String _secondary;

    /**
     * The version number of the object in the external database.
     */
    private java.lang.String _version;

    /**
     * Reference type, e.g. "identity" if this reference referes to
     * an identical object in the external database, or "see-also"
     * for additional information. Controlled by CV.
     */
    private java.lang.String _refType;

    /**
     * Reference type accession number from the CV of reference
     * types. This element is controlled by the PSI-MI controlled
     * vocabulary "xref type", root term id MI:0353. 
     */
    private java.lang.String _refTypeAc;

    /**
     * Field _attributeList
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public DbReferenceType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType getAttributeList()

    /**
     * Method getDbReturns the value of field 'db'. The field 'db'
     * has the following description: Name of the external
     * database. Taken from the controlled vocabulary of databases.
     * 
     * @return the value of field 'db'.
     */
    public java.lang.String getDb()
    {
        return this._db;
    } //-- java.lang.String getDb() 

    /**
     * Method getDbAcReturns the value of field 'dbAc'. The field
     * 'dbAc' has the following description: Accession number of
     * the database in the database CV. This element is controlled
     * by the PSI-MI controlled vocabulary "database citation",
     * root term id MI:0444.
     * 
     * @return the value of field 'dbAc'.
     */
    public java.lang.String getDbAc()
    {
        return this._dbAc;
    } //-- java.lang.String getDbAc() 

    /**
     * Method getIdReturns the value of field 'id'. The field 'id'
     * has the following description: Primary identifier of the
     * object in the external database, e.g. UniProt accession
     * number.
     * 
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Method getRefTypeReturns the value of field 'refType'. The
     * field 'refType' has the following description: Reference
     * type, e.g. "identity" if this reference referes to an
     * identical object in the external database, or "see-also" for
     * additional information. Controlled by CV.
     * 
     * @return the value of field 'refType'.
     */
    public java.lang.String getRefType()
    {
        return this._refType;
    } //-- java.lang.String getRefType() 

    /**
     * Method getRefTypeAcReturns the value of field 'refTypeAc'.
     * The field 'refTypeAc' has the following description:
     * Reference type accession number from the CV of reference
     * types. This element is controlled by the PSI-MI controlled
     * vocabulary "xref type", root term id MI:0353. 
     * 
     * @return the value of field 'refTypeAc'.
     */
    public java.lang.String getRefTypeAc()
    {
        return this._refTypeAc;
    } //-- java.lang.String getRefTypeAc() 

    /**
     * Method getSecondaryReturns the value of field 'secondary'.
     * The field 'secondary' has the following description:
     * Secondary identifier of the object in the external database,
     * e.g. UniProt ID.
     * 
     * @return the value of field 'secondary'.
     */
    public java.lang.String getSecondary()
    {
        return this._secondary;
    } //-- java.lang.String getSecondary() 

    /**
     * Method getVersionReturns the value of field 'version'. The
     * field 'version' has the following description: The version
     * number of the object in the external database.
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
     * Method setAttributeListSets the value of field
     * 'attributeList'.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi25.AttributeListType)

    /**
     * Method setDbSets the value of field 'db'. The field 'db' has
     * the following description: Name of the external database.
     * Taken from the controlled vocabulary of databases.
     * 
     * @param db the value of field 'db'.
     */
    public void setDb(java.lang.String db)
    {
        this._db = db;
    } //-- void setDb(java.lang.String) 

    /**
     * Method setDbAcSets the value of field 'dbAc'. The field
     * 'dbAc' has the following description: Accession number of
     * the database in the database CV. This element is controlled
     * by the PSI-MI controlled vocabulary "database citation",
     * root term id MI:0444.
     * 
     * @param dbAc the value of field 'dbAc'.
     */
    public void setDbAc(java.lang.String dbAc)
    {
        this._dbAc = dbAc;
    } //-- void setDbAc(java.lang.String) 

    /**
     * Method setIdSets the value of field 'id'. The field 'id' has
     * the following description: Primary identifier of the object
     * in the external database, e.g. UniProt accession number.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method setRefTypeSets the value of field 'refType'. The
     * field 'refType' has the following description: Reference
     * type, e.g. "identity" if this reference referes to an
     * identical object in the external database, or "see-also" for
     * additional information. Controlled by CV.
     * 
     * @param refType the value of field 'refType'.
     */
    public void setRefType(java.lang.String refType)
    {
        this._refType = refType;
    } //-- void setRefType(java.lang.String) 

    /**
     * Method setRefTypeAcSets the value of field 'refTypeAc'. The
     * field 'refTypeAc' has the following description: Reference
     * type accession number from the CV of reference types. This
     * element is controlled by the PSI-MI controlled vocabulary
     * "xref type", root term id MI:0353. 
     * 
     * @param refTypeAc the value of field 'refTypeAc'.
     */
    public void setRefTypeAc(java.lang.String refTypeAc)
    {
        this._refTypeAc = refTypeAc;
    } //-- void setRefTypeAc(java.lang.String) 

    /**
     * Method setSecondarySets the value of field 'secondary'. The
     * field 'secondary' has the following description: Secondary
     * identifier of the object in the external database, e.g.
     * UniProt ID.
     * 
     * @param secondary the value of field 'secondary'.
     */
    public void setSecondary(java.lang.String secondary)
    {
        this._secondary = secondary;
    } //-- void setSecondary(java.lang.String) 

    /**
     * Method setVersionSets the value of field 'version'. The
     * field 'version' has the following description: The version
     * number of the object in the external database.
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
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType unmarshalDbReferenceType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.DbReferenceType unmarshalDbReferenceType(java.io.Reader)

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
