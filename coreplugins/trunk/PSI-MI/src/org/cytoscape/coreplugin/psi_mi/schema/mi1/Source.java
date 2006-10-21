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
 * Desciption of the source of the entry, usually an organisation
 * 
 * @version $Revision$ $Date$
 */
public class Source implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _release
     */
    private java.lang.String _release;

    /**
     * Field _releaseDate
     */
    private org.exolab.castor.types.Date _releaseDate;

    /**
     * Name(s) of the data source, for example the organisation name
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType _names;

    /**
     * Bibliographic reference for the data source. Example: A
     * paper which describes all interactions of the entry.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType _bibref;

    /**
     * Cross reference for the data source. Example: Entry in a
     * database of databases. 
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType _xref;

    /**
     * Further description of the source.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType _attributeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Source() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Source()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAttributeListReturns the value of field
     * 'attributeList'. The field 'attributeList' has the following
     * description: Further description of the source.
     * 
     * @return the value of field 'attributeList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()
    {
        return this._attributeList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType getAttributeList()

    /**
     * Method getBibrefReturns the value of field 'bibref'. The
     * field 'bibref' has the following description: Bibliographic
     * reference for the data source. Example: A paper which
     * describes all interactions of the entry.
     * 
     * @return the value of field 'bibref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType getBibref()
    {
        return this._bibref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType getBibref()

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: Name(s) of the data
     * source, for example the organisation name.
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()

    /**
     * Method getReleaseReturns the value of field 'release'.
     * 
     * @return the value of field 'release'.
     */
    public java.lang.String getRelease()
    {
        return this._release;
    } //-- java.lang.String getRelease() 

    /**
     * Method getReleaseDateReturns the value of field
     * 'releaseDate'.
     * 
     * @return the value of field 'releaseDate'.
     */
    public org.exolab.castor.types.Date getReleaseDate()
    {
        return this._releaseDate;
    } //-- org.exolab.castor.types.Date getReleaseDate() 

    /**
     * Method getXrefReturns the value of field 'xref'. The field
     * 'xref' has the following description: Cross reference for
     * the data source. Example: Entry in a database of databases. 
     * 
     * @return the value of field 'xref'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType getXref()
    {
        return this._xref;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType getXref()

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
     * 'attributeList'. The field 'attributeList' has the following
     * description: Further description of the source.
     * 
     * @param attributeList the value of field 'attributeList'.
     */
    public void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType attributeList)
    {
        this._attributeList = attributeList;
    } //-- void setAttributeList(org.cytoscape.coreplugin.psi_mi.schema.mi1.AttributeListType)

    /**
     * Method setBibrefSets the value of field 'bibref'. The field
     * 'bibref' has the following description: Bibliographic
     * reference for the data source. Example: A paper which
     * describes all interactions of the entry.
     * 
     * @param bibref the value of field 'bibref'.
     */
    public void setBibref(org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType bibref)
    {
        this._bibref = bibref;
    } //-- void setBibref(org.cytoscape.coreplugin.psi_mi.schema.mi1.BibrefType)

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: Name(s) of the data
     * source, for example the organisation name.
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType)

    /**
     * Method setReleaseSets the value of field 'release'.
     * 
     * @param release the value of field 'release'.
     */
    public void setRelease(java.lang.String release)
    {
        this._release = release;
    } //-- void setRelease(java.lang.String) 

    /**
     * Method setReleaseDateSets the value of field 'releaseDate'.
     * 
     * @param releaseDate the value of field 'releaseDate'.
     */
    public void setReleaseDate(org.exolab.castor.types.Date releaseDate)
    {
        this._releaseDate = releaseDate;
    } //-- void setReleaseDate(org.exolab.castor.types.Date) 

    /**
     * Method setXrefSets the value of field 'xref'. The field
     * 'xref' has the following description: Cross reference for
     * the data source. Example: Entry in a database of databases. 
     * 
     * @param xref the value of field 'xref'.
     */
    public void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType xref)
    {
        this._xref = xref;
    } //-- void setXref(org.cytoscape.coreplugin.psi_mi.schema.mi1.XrefType)

    /**
     * Method unmarshalSource
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.Source unmarshalSource(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Source) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.Source.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Source unmarshalSource(java.io.Reader)

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
