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
 * List of experiments in which this interaction has been
 * determined.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _items
     */
    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentList() {
        super();
        _items = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addExperimentListItem
     * 
     * @param vExperimentListItem
     */
    public void addExperimentListItem(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem vExperimentListItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vExperimentListItem);
    } //-- void addExperimentListItem(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem)

    /**
     * Method addExperimentListItem
     * 
     * @param index
     * @param vExperimentListItem
     */
    public void addExperimentListItem(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem vExperimentListItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vExperimentListItem, index);
    } //-- void addExperimentListItem(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem)

    /**
     * Method enumerateExperimentListItem
     */
    public java.util.Enumeration enumerateExperimentListItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumerateExperimentListItem() 

    /**
     * Method getExperimentListItem
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem getExperimentListItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem) _items.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem getExperimentListItem(int)

    /**
     * Method getExperimentListItem
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem[] getExperimentListItem()
    {
        int size = _items.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem) _items.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem[] getExperimentListItem()

    /**
     * Method getExperimentListItemCount
     */
    public int getExperimentListItemCount()
    {
        return _items.size();
    } //-- int getExperimentListItemCount() 

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
     * Method removeAllExperimentListItem
     */
    public void removeAllExperimentListItem()
    {
        _items.removeAllElements();
    } //-- void removeAllExperimentListItem() 

    /**
     * Method removeExperimentListItem
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem removeExperimentListItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem removeExperimentListItem(int)

    /**
     * Method setExperimentListItem
     * 
     * @param index
     * @param vExperimentListItem
     */
    public void setExperimentListItem(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem vExperimentListItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException();
        }
        _items.setElementAt(vExperimentListItem, index);
    } //-- void setExperimentListItem(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem)

    /**
     * Method setExperimentListItem
     * 
     * @param experimentListItemArray
     */
    public void setExperimentListItem(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem[] experimentListItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < experimentListItemArray.length; i++) {
            _items.addElement(experimentListItemArray[i]);
        }
    } //-- void setExperimentListItem(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentListItem)

    /**
     * Method unmarshalExperimentList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList unmarshalExperimentList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentList unmarshalExperimentList(java.io.Reader)

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
