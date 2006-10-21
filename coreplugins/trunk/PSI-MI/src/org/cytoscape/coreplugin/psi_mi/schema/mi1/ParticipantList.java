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
 * A list of molecules participating in this interaction.
 * 
 * @version $Revision$ $Date$
 */
public class ParticipantList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * An interaction has two or more participants, thereby
     * covering binary and n-ary interactions.
     */
    private java.util.Vector _proteinParticipantList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParticipantList() {
        super();
        _proteinParticipantList = new Vector();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addProteinParticipant
     * 
     * @param vProteinParticipant
     */
    public void addProteinParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType vProteinParticipant)
        throws java.lang.IndexOutOfBoundsException
    {
        _proteinParticipantList.addElement(vProteinParticipant);
    } //-- void addProteinParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType)

    /**
     * Method addProteinParticipant
     * 
     * @param index
     * @param vProteinParticipant
     */
    public void addProteinParticipant(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType vProteinParticipant)
        throws java.lang.IndexOutOfBoundsException
    {
        _proteinParticipantList.insertElementAt(vProteinParticipant, index);
    } //-- void addProteinParticipant(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType)

    /**
     * Method enumerateProteinParticipant
     */
    public java.util.Enumeration enumerateProteinParticipant()
    {
        return _proteinParticipantList.elements();
    } //-- java.util.Enumeration enumerateProteinParticipant() 

    /**
     * Method getProteinParticipant
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType getProteinParticipant(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _proteinParticipantList.size())) {
            throw new IndexOutOfBoundsException();
        }

        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType) _proteinParticipantList.elementAt(index);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType getProteinParticipant(int)

    /**
     * Method getProteinParticipant
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType[] getProteinParticipant()
    {
        int size = _proteinParticipantList.size();
        org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType[] mArray = new org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType) _proteinParticipantList.elementAt(index);
        }
        return mArray;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType[] getProteinParticipant()

    /**
     * Method getProteinParticipantCount
     */
    public int getProteinParticipantCount()
    {
        return _proteinParticipantList.size();
    } //-- int getProteinParticipantCount() 

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
     * Method removeAllProteinParticipant
     */
    public void removeAllProteinParticipant()
    {
        _proteinParticipantList.removeAllElements();
    } //-- void removeAllProteinParticipant() 

    /**
     * Method removeProteinParticipant
     * 
     * @param index
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType removeProteinParticipant(int index)
    {
        java.lang.Object obj = _proteinParticipantList.elementAt(index);
        _proteinParticipantList.removeElementAt(index);
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType) obj;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType removeProteinParticipant(int)

    /**
     * Method setProteinParticipant
     * 
     * @param index
     * @param vProteinParticipant
     */
    public void setProteinParticipant(int index, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType vProteinParticipant)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _proteinParticipantList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _proteinParticipantList.setElementAt(vProteinParticipant, index);
    } //-- void setProteinParticipant(int, org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType)

    /**
     * Method setProteinParticipant
     * 
     * @param proteinParticipantArray
     */
    public void setProteinParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType[] proteinParticipantArray)
    {
        //-- copy array
        _proteinParticipantList.removeAllElements();
        for (int i = 0; i < proteinParticipantArray.length; i++) {
            _proteinParticipantList.addElement(proteinParticipantArray[i]);
        }
    } //-- void setProteinParticipant(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType)

    /**
     * Method unmarshalParticipantList
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList unmarshalParticipantList(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ParticipantList unmarshalParticipantList(java.io.Reader)

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
