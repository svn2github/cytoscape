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
 * Describes inferred interactions, usually combining data from
 * more than one experiment. Examples: 1: Show the topology of
 * binary interactions within a complex. 2: Interaction inferred
 * from multiple experiments which on their own would not support
 * the interaction. Example: A-B in experiment 1, B-C- in
 * experiment 2, A-C is the inferred interaction.
 * 
 * @version $Revision$ $Date$
 */
public class InferredInteractionList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _inferredInteractionList
     */
    private java.util.Vector _inferredInteractionList;


      //----------------/
     //- Constructors -/
    //----------------/

    public InferredInteractionList() {
        super();
        _inferredInteractionList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addInferredInteraction
     * 
     * @param vInferredInteraction
     */
    public void addInferredInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction vInferredInteraction)
        throws java.lang.IndexOutOfBoundsException
    {
        _inferredInteractionList.addElement(vInferredInteraction);
    } //-- void addInferredInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction)

    /**
     * Method addInferredInteraction
     * 
     * @param index
     * @param vInferredInteraction
     */
    public void addInferredInteraction(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction vInferredInteraction)
        throws java.lang.IndexOutOfBoundsException
    {
        _inferredInteractionList.insertElementAt(vInferredInteraction, index);
    } //-- void addInferredInteraction(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction)

    /**
     * Method enumerateInferredInteraction
     */
    public java.util.Enumeration enumerateInferredInteraction()
    {
        return _inferredInteractionList.elements();
    } //-- java.util.Enumeration enumerateInferredInteraction() 

    /**
     * Method getInferredInteraction
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction getInferredInteraction(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _inferredInteractionList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction) _inferredInteractionList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction getInferredInteraction(int)

    /**
     * Method getInferredInteraction
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction[] getInferredInteraction()
    {
        int size = _inferredInteractionList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction) _inferredInteractionList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction[] getInferredInteraction()

    /**
     * Method getInferredInteractionCount
     */
    public int getInferredInteractionCount()
    {
        return _inferredInteractionList.size();
    } //-- int getInferredInteractionCount() 

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
     * Method removeAllInferredInteraction
     */
    public void removeAllInferredInteraction()
    {
        _inferredInteractionList.removeAllElements();
    } //-- void removeAllInferredInteraction() 

    /**
     * Method removeInferredInteraction
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction removeInferredInteraction(int index)
    {
        java.lang.Object obj = _inferredInteractionList.elementAt(index);
        _inferredInteractionList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction removeInferredInteraction(int)

    /**
     * Method setInferredInteraction
     * 
     * @param index
     * @param vInferredInteraction
     */
    public void setInferredInteraction(int index, org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction vInferredInteraction)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _inferredInteractionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _inferredInteractionList.setElementAt(vInferredInteraction, index);
    } //-- void setInferredInteraction(int, org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction)

    /**
     * Method setInferredInteraction
     * 
     * @param inferredInteractionArray
     */
    public void setInferredInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction[] inferredInteractionArray)
    {
        //-- copy array
        _inferredInteractionList.removeAllElements();
        for (int i = 0; i < inferredInteractionArray.length; i++) {
            _inferredInteractionList.addElement(inferredInteractionArray[i]);
        }
    } //-- void setInferredInteraction(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteraction)

    /**
     * Method unmarshalInferredInteractionList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList unmarshalInferredInteractionList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.InferredInteractionList unmarshalInferredInteractionList(java.io.Reader)

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
