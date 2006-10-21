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

import java.util.Vector;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Root element of the Molecular Interaction Format
 * 
 * @version $Revision$ $Date$
 */
public class EntrySet implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _level
     */
    private int _level;

    /**
     * keeps track of state for field: _level
     */
    private boolean _has_level;

    /**
     * Field _version
     */
    private int _version;

    /**
     * keeps track of state for field: _version
     */
    private boolean _has_version;

    /**
     * Describes one or more interactions as a self-contained unit.
     * Multiple entries from different files can be concatenated
     * into a single entrySet.
     */
    private java.util.Vector _entryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public EntrySet() {
        super();
        _entryList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addEntry
     * 
     * @param vEntry
     */
    public void addEntry(org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _entryList.addElement(vEntry);
    } //-- void addEntry(org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry)

    /**
     * Method addEntry
     * 
     * @param index
     * @param vEntry
     */
    public void addEntry(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _entryList.insertElementAt(vEntry, index);
    } //-- void addEntry(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry)

    /**
     * Method enumerateEntry
     */
    public java.util.Enumeration enumerateEntry()
    {
        return _entryList.elements();
    } //-- java.util.Enumeration enumerateEntry() 

    /**
     * Method getEntry
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _entryList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry) _entryList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry getEntry(int)

    /**
     * Method getEntry
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry[] getEntry()
    {
        int size = _entryList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry) _entryList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry[] getEntry()

    /**
     * Method getEntryCount
     */
    public int getEntryCount()
    {
        return _entryList.size();
    } //-- int getEntryCount() 

    /**
     * Method getLevelReturns the value of field 'level'.
     * 
     * @return the value of field 'level'.
     */
    public int getLevel()
    {
        return this._level;
    } //-- int getLevel() 

    /**
     * Method getVersionReturns the value of field 'version'.
     * 
     * @return the value of field 'version'.
     */
    public int getVersion()
    {
        return this._version;
    } //-- int getVersion() 

    /**
     * Method hasLevel
     */
    public boolean hasLevel()
    {
        return this._has_level;
    } //-- boolean hasLevel() 

    /**
     * Method hasVersion
     */
    public boolean hasVersion()
    {
        return this._has_version;
    } //-- boolean hasVersion() 

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
     * Method removeAllEntry
     */
    public void removeAllEntry()
    {
        _entryList.removeAllElements();
    } //-- void removeAllEntry() 

    /**
     * Method removeEntry
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry removeEntry(int index)
    {
        java.lang.Object obj = _entryList.elementAt(index);
        _entryList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry removeEntry(int)

    /**
     * Method setEntry
     * 
     * @param index
     * @param vEntry
     */
    public void setEntry(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _entryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _entryList.setElementAt(vEntry, index);
    } //-- void setEntry(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry)

    /**
     * Method setEntry
     * 
     * @param entryArray
     */
    public void setEntry(org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry[] entryArray)
    {
        //-- copy array
        _entryList.removeAllElements();
        for (int i = 0; i < entryArray.length; i++) {
            _entryList.addElement(entryArray[i]);
        }
    } //-- void setEntry(org.cytoscape.coreplugin.psi_mi.schema.mi1.Entry)

    /**
     * Method setLevelSets the value of field 'level'.
     * 
     * @param level the value of field 'level'.
     */
    public void setLevel(int level)
    {
        this._level = level;
        this._has_level = true;
    } //-- void setLevel(int) 

    /**
     * Method setVersionSets the value of field 'version'.
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(int version)
    {
        this._version = version;
        this._has_version = true;
    } //-- void setVersion(int) 

    /**
     * Method unmarshalEntrySet
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet unmarshalEntrySet(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.EntrySet unmarshalEntrySet(java.io.Reader)

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
