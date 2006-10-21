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

import java.util.Vector;
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
     * A short string, suitable to remember the object. Can be e.g.
     * a gene name, the first author of a paper, etc.
     */
    private java.lang.String _shortLabel;

    /**
     * A full, detailed name or description of the object. Can be
     * e.g. the full title of a publication, or the scientific name
     * of a species.
     */
    private java.lang.String _fullName;

    /**
     * Field _aliasList
     */
    private java.util.Vector _aliasList;


      //----------------/
     //- Constructors -/
    //----------------/

    public NamesType() {
        super();
        _aliasList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addAlias
     * 
     * @param vAlias
     */
    public void addAlias(org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias vAlias)
        throws java.lang.IndexOutOfBoundsException
    {
        _aliasList.addElement(vAlias);
    } //-- void addAlias(org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias)

    /**
     * Method addAlias
     * 
     * @param index
     * @param vAlias
     */
    public void addAlias(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias vAlias)
        throws java.lang.IndexOutOfBoundsException
    {
        _aliasList.insertElementAt(vAlias, index);
    } //-- void addAlias(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias)

    /**
     * Method enumerateAlias
     */
    public java.util.Enumeration enumerateAlias()
    {
        return _aliasList.elements();
    } //-- java.util.Enumeration enumerateAlias() 

    /**
     * Method getAlias
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias getAlias(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _aliasList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias) _aliasList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias getAlias(int)

    /**
     * Method getAlias
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias[] getAlias()
    {
        int size = _aliasList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias) _aliasList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias[] getAlias()

    /**
     * Method getAliasCount
     */
    public int getAliasCount()
    {
        return _aliasList.size();
    } //-- int getAliasCount() 

    /**
     * Method getFullNameReturns the value of field 'fullName'. The
     * field 'fullName' has the following description: A full,
     * detailed name or description of the object. Can be e.g. the
     * full title of a publication, or the scientific name of a
     * species.
     * 
     * @return the value of field 'fullName'.
     */
    public java.lang.String getFullName()
    {
        return this._fullName;
    } //-- java.lang.String getFullName() 

    /**
     * Method getShortLabelReturns the value of field 'shortLabel'.
     * The field 'shortLabel' has the following description: A
     * short string, suitable to remember the object. Can be e.g. a
     * gene name, the first author of a paper, etc.
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
     * Method removeAlias
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias removeAlias(int index)
    {
        java.lang.Object obj = _aliasList.elementAt(index);
        _aliasList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias removeAlias(int)

    /**
     * Method removeAllAlias
     */
    public void removeAllAlias()
    {
        _aliasList.removeAllElements();
    } //-- void removeAllAlias() 

    /**
     * Method setAlias
     * 
     * @param index
     * @param vAlias
     */
    public void setAlias(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias vAlias)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _aliasList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _aliasList.setElementAt(vAlias, index);
    } //-- void setAlias(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias)

    /**
     * Method setAlias
     * 
     * @param aliasArray
     */
    public void setAlias(org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias[] aliasArray)
    {
        //-- copy array
        _aliasList.removeAllElements();
        for (int i = 0; i < aliasArray.length; i++) {
            _aliasList.addElement(aliasArray[i]);
        }
    } //-- void setAlias(org.cytoscape.coreplugin.psi_mi.schema.mi25.Alias)

    /**
     * Method setFullNameSets the value of field 'fullName'. The
     * field 'fullName' has the following description: A full,
     * detailed name or description of the object. Can be e.g. the
     * full title of a publication, or the scientific name of a
     * species.
     * 
     * @param fullName the value of field 'fullName'.
     */
    public void setFullName(java.lang.String fullName)
    {
        this._fullName = fullName;
    } //-- void setFullName(java.lang.String) 

    /**
     * Method setShortLabelSets the value of field 'shortLabel'.
     * The field 'shortLabel' has the following description: A
     * short string, suitable to remember the object. Can be e.g. a
     * gene name, the first author of a paper, etc.
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
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType unmarshalNamesType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.NamesType unmarshalNamesType(java.io.Reader)

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
