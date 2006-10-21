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
 * List of interactions
 * 
 * @version $Revision$ $Date$
 */
public class InteractionList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * A set of molecules interacting. 
     */
    private java.util.Vector _interactionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public InteractionList() {
        super();
        _interactionList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addInteraction
     * 
     * @param vInteraction
     */
    public void addInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction vInteraction)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactionList.addElement(vInteraction);
    } //-- void addInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction)

    /**
     * Method addInteraction
     * 
     * @param index
     * @param vInteraction
     */
    public void addInteraction(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction vInteraction)
        throws java.lang.IndexOutOfBoundsException
    {
        _interactionList.insertElementAt(vInteraction, index);
    } //-- void addInteraction(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction)

    /**
     * Method enumerateInteraction
     */
    public java.util.Enumeration enumerateInteraction()
    {
        return _interactionList.elements();
    } //-- java.util.Enumeration enumerateInteraction() 

    /**
     * Method getInteraction
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction getInteraction(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactionList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction) _interactionList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction getInteraction(int)

    /**
     * Method getInteraction
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction[] getInteraction()
    {
        int size = _interactionList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction) _interactionList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction[] getInteraction()

    /**
     * Method getInteractionCount
     */
    public int getInteractionCount()
    {
        return _interactionList.size();
    } //-- int getInteractionCount() 

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
     * Method removeAllInteraction
     */
    public void removeAllInteraction()
    {
        _interactionList.removeAllElements();
    } //-- void removeAllInteraction() 

    /**
     * Method removeInteraction
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction removeInteraction(int index)
    {
        java.lang.Object obj = _interactionList.elementAt(index);
        _interactionList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction removeInteraction(int)

    /**
     * Method setInteraction
     * 
     * @param index
     * @param vInteraction
     */
    public void setInteraction(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction vInteraction)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _interactionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _interactionList.setElementAt(vInteraction, index);
    } //-- void setInteraction(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction)

    /**
     * Method setInteraction
     * 
     * @param interactionArray
     */
    public void setInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction[] interactionArray)
    {
        //-- copy array
        _interactionList.removeAllElements();
        for (int i = 0; i < interactionArray.length; i++) {
            _interactionList.addElement(interactionArray[i]);
        }
    } //-- void setInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.Interaction)

    /**
     * Method unmarshalInteractionList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionList unmarshalInteractionList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InteractionList unmarshalInteractionList(java.io.Reader)

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
