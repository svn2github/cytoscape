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
 * Class ExperimentalInteractor.
 * 
 * @version $Revision$ $Date$
 */
public class ExperimentalInteractor implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Either refer to an already defined protein interactor in
     * this entry or insert description.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice _experimentalInteractorChoice;

    /**
     * If no experimentRef is given, it is assumed this refers to
     * all experiments linked to the interaction.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType _experimentRefList;


      //----------------/
     //- Constructors -/
    //----------------/

    public ExperimentalInteractor() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getExperimentRefListReturns the value of field
     * 'experimentRefList'. The field 'experimentRefList' has the
     * following description: If no experimentRef is given, it is
     * assumed this refers to all experiments linked to the
     * interaction.
     * 
     * @return the value of field 'experimentRefList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType getExperimentRefList()
    {
        return this._experimentRefList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType getExperimentRefList()

    /**
     * Method getExperimentalInteractorChoiceReturns the value of
     * field 'experimentalInteractorChoice'. The field
     * 'experimentalInteractorChoice' has the following
     * description: Either refer to an already defined protein
     * interactor in this entry or insert description.
     * 
     * @return the value of field 'experimentalInteractorChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice getExperimentalInteractorChoice()
    {
        return this._experimentalInteractorChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice getExperimentalInteractorChoice()

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
     * Method setExperimentRefListSets the value of field
     * 'experimentRefList'. The field 'experimentRefList' has the
     * following description: If no experimentRef is given, it is
     * assumed this refers to all experiments linked to the
     * interaction.
     * 
     * @param experimentRefList the value of field
     * 'experimentRefList'.
     */
    public void setExperimentRefList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType experimentRefList)
    {
        this._experimentRefList = experimentRefList;
    } //-- void setExperimentRefList(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentRefListType)

    /**
     * Method setExperimentalInteractorChoiceSets the value of
     * field 'experimentalInteractorChoice'. The field
     * 'experimentalInteractorChoice' has the following
     * description: Either refer to an already defined protein
     * interactor in this entry or insert description.
     * 
     * @param experimentalInteractorChoice the value of field
     * 'experimentalInteractorChoice'.
     */
    public void setExperimentalInteractorChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice experimentalInteractorChoice)
    {
        this._experimentalInteractorChoice = experimentalInteractorChoice;
    } //-- void setExperimentalInteractorChoice(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractorChoice)

    /**
     * Method unmarshalExperimentalInteractor
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor unmarshalExperimentalInteractor(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ExperimentalInteractor unmarshalExperimentalInteractor(java.io.Reader)

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
