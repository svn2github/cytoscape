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
 * A protein participating in an interaction.
 * 
 * @version $Revision$ $Date$
 */
public class ProteinParticipantType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Either refer to an already defined protein interactor in
     * this entry or insert description.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice _proteinParticipantTypeChoice;

    /**
     * Sequence features relevant for the interaction, for example
     * binding domains
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureList _featureList;

    /**
     * Confidence in participant detection.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence _confidence;

    /**
     * The role of the participant in the interaction, e.g. "bait".
     * Choose an enumerated value.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType _role;

    /**
     * True if the protein has been tagged in the experiment.
     */
    private boolean _isTaggedProtein;

    /**
     * keeps track of state for field: _isTaggedProtein
     */
    private boolean _has_isTaggedProtein;

    /**
     * True if the protein has been overexpressed in the experiment.
     */
    private boolean _isOverexpressedProtein;

    /**
     * keeps track of state for field: _isOverexpressedProtein
     */
    private boolean _has_isOverexpressedProtein;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProteinParticipantType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteIsOverexpressedProtein
     */
    public void deleteIsOverexpressedProtein()
    {
        this._has_isOverexpressedProtein= false;
    } //-- void deleteIsOverexpressedProtein() 

    /**
     * Method deleteIsTaggedProtein
     */
    public void deleteIsTaggedProtein()
    {
        this._has_isTaggedProtein= false;
    } //-- void deleteIsTaggedProtein() 

    /**
     * Method getConfidenceReturns the value of field 'confidence'.
     * The field 'confidence' has the following description:
     * Confidence in participant detection.
     * 
     * @return the value of field 'confidence'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence getConfidence()
    {
        return this._confidence;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence getConfidence()

    /**
     * Method getFeatureListReturns the value of field
     * 'featureList'. The field 'featureList' has the following
     * description: Sequence features relevant for the interaction,
     * for example binding domains
     * 
     * @return the value of field 'featureList'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureList getFeatureList()
    {
        return this._featureList;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureList getFeatureList()

    /**
     * Method getIsOverexpressedProteinReturns the value of field
     * 'isOverexpressedProtein'. The field 'isOverexpressedProtein'
     * has the following description: True if the protein has been
     * overexpressed in the experiment.
     * 
     * @return the value of field 'isOverexpressedProtein'.
     */
    public boolean getIsOverexpressedProtein()
    {
        return this._isOverexpressedProtein;
    } //-- boolean getIsOverexpressedProtein() 

    /**
     * Method getIsTaggedProteinReturns the value of field
     * 'isTaggedProtein'. The field 'isTaggedProtein' has the
     * following description: True if the protein has been tagged
     * in the experiment.
     * 
     * @return the value of field 'isTaggedProtein'.
     */
    public boolean getIsTaggedProtein()
    {
        return this._isTaggedProtein;
    } //-- boolean getIsTaggedProtein() 

    /**
     * Method getProteinParticipantTypeChoiceReturns the value of
     * field 'proteinParticipantTypeChoice'. The field
     * 'proteinParticipantTypeChoice' has the following
     * description: Either refer to an already defined protein
     * interactor in this entry or insert description.
     * 
     * @return the value of field 'proteinParticipantTypeChoice'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice getProteinParticipantTypeChoice()
    {
        return this._proteinParticipantTypeChoice;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice getProteinParticipantTypeChoice()

    /**
     * Method getRoleReturns the value of field 'role'. The field
     * 'role' has the following description: The role of the
     * participant in the interaction, e.g. "bait". Choose an
     * enumerated value.
     * 
     * @return the value of field 'role'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType getRole()
    {
        return this._role;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType getRole()

    /**
     * Method hasIsOverexpressedProtein
     */
    public boolean hasIsOverexpressedProtein()
    {
        return this._has_isOverexpressedProtein;
    } //-- boolean hasIsOverexpressedProtein() 

    /**
     * Method hasIsTaggedProtein
     */
    public boolean hasIsTaggedProtein()
    {
        return this._has_isTaggedProtein;
    } //-- boolean hasIsTaggedProtein() 

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
     * Method setConfidenceSets the value of field 'confidence'.
     * The field 'confidence' has the following description:
     * Confidence in participant detection.
     * 
     * @param confidence the value of field 'confidence'.
     */
    public void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence confidence)
    {
        this._confidence = confidence;
    } //-- void setConfidence(org.cytoscape.coreplugin.psi_mi.schema.mi1.Confidence)

    /**
     * Method setFeatureListSets the value of field 'featureList'.
     * The field 'featureList' has the following description:
     * Sequence features relevant for the interaction, for example
     * binding domains
     * 
     * @param featureList the value of field 'featureList'.
     */
    public void setFeatureList(org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureList featureList)
    {
        this._featureList = featureList;
    } //-- void setFeatureList(org.cytoscape.coreplugin.psi_mi.schema.mi1.FeatureList)

    /**
     * Method setIsOverexpressedProteinSets the value of field
     * 'isOverexpressedProtein'. The field 'isOverexpressedProtein'
     * has the following description: True if the protein has been
     * overexpressed in the experiment.
     * 
     * @param isOverexpressedProtein the value of field
     * 'isOverexpressedProtein'.
     */
    public void setIsOverexpressedProtein(boolean isOverexpressedProtein)
    {
        this._isOverexpressedProtein = isOverexpressedProtein;
        this._has_isOverexpressedProtein = true;
    } //-- void setIsOverexpressedProtein(boolean) 

    /**
     * Method setIsTaggedProteinSets the value of field
     * 'isTaggedProtein'. The field 'isTaggedProtein' has the
     * following description: True if the protein has been tagged
     * in the experiment.
     * 
     * @param isTaggedProtein the value of field 'isTaggedProtein'.
     */
    public void setIsTaggedProtein(boolean isTaggedProtein)
    {
        this._isTaggedProtein = isTaggedProtein;
        this._has_isTaggedProtein = true;
    } //-- void setIsTaggedProtein(boolean) 

    /**
     * Method setProteinParticipantTypeChoiceSets the value of
     * field 'proteinParticipantTypeChoice'. The field
     * 'proteinParticipantTypeChoice' has the following
     * description: Either refer to an already defined protein
     * interactor in this entry or insert description.
     * 
     * @param proteinParticipantTypeChoice the value of field
     * 'proteinParticipantTypeChoice'.
     */
    public void setProteinParticipantTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice proteinParticipantTypeChoice)
    {
        this._proteinParticipantTypeChoice = proteinParticipantTypeChoice;
    } //-- void setProteinParticipantTypeChoice(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantTypeChoice)

    /**
     * Method setRoleSets the value of field 'role'. The field
     * 'role' has the following description: The role of the
     * participant in the interaction, e.g. "bait". Choose an
     * enumerated value.
     * 
     * @param role the value of field 'role'.
     */
    public void setRole(org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType role)
    {
        this._role = role;
    } //-- void setRole(org.cytoscape.coreplugin.psi_mi.schema.mi1.types.RoleType)

    /**
     * Method unmarshalProteinParticipantType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType unmarshalProteinParticipantType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.ProteinParticipantType unmarshalProteinParticipantType(java.io.Reader)

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
