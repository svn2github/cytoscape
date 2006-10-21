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
 * Describes the biological source of an object, in simple form
 * only the NCBI taxid.
 * 
 * @version $Revision$ $Date$
 */
public class BioSourceType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The names of the organism. The short label should be a
     * common name if it exists. The full name should be the full
     * name of the species (i.e. genus species).
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType _names;

    /**
     * Description of the cell type. Currently no
     * species-independent controlled vocabulary for cell types is
     * available, therefore the choice of reference database(s) is
     * open to the data provider.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType _cellType;

    /**
     * The subcellular compartement of the object. It is strongly
     * recommended to refer to the Gene Ontology cellular component
     * in this element.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType _compartment;

    /**
     * Description of the source tissue. Currently no
     * species-independent controlled vocabulary for tissues is
     * available, therefore the choice of reference database(s) is
     * open to the data provider.
     */
    private org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType _tissue;


      //----------------/
     //- Constructors -/
    //----------------/

    public BioSourceType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getCellTypeReturns the value of field 'cellType'. The
     * field 'cellType' has the following description: Description
     * of the cell type. Currently no species-independent
     * controlled vocabulary for cell types is available, therefore
     * the choice of reference database(s) is open to the data
     * provider.
     * 
     * @return the value of field 'cellType'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType getCellType()
    {
        return this._cellType;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType getCellType()

    /**
     * Method getCompartmentReturns the value of field
     * 'compartment'. The field 'compartment' has the following
     * description: The subcellular compartement of the object. It
     * is strongly recommended to refer to the Gene Ontology
     * cellular component in this element.
     * 
     * @return the value of field 'compartment'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType getCompartment()
    {
        return this._compartment;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType getCompartment()

    /**
     * Method getNamesReturns the value of field 'names'. The field
     * 'names' has the following description: The names of the
     * organism. The short label should be a common name if it
     * exists. The full name should be the full name of the species
     * (i.e. genus species).
     * 
     * @return the value of field 'names'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()
    {
        return this._names;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType getNames()

    /**
     * Method getTissueReturns the value of field 'tissue'. The
     * field 'tissue' has the following description: Description of
     * the source tissue. Currently no species-independent
     * controlled vocabulary for tissues is available, therefore
     * the choice of reference database(s) is open to the data
     * provider.
     * 
     * @return the value of field 'tissue'.
     */
    public org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType getTissue()
    {
        return this._tissue;
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType getTissue()

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
     * Method setCellTypeSets the value of field 'cellType'. The
     * field 'cellType' has the following description: Description
     * of the cell type. Currently no species-independent
     * controlled vocabulary for cell types is available, therefore
     * the choice of reference database(s) is open to the data
     * provider.
     * 
     * @param cellType the value of field 'cellType'.
     */
    public void setCellType(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType cellType)
    {
        this._cellType = cellType;
    } //-- void setCellType(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType)

    /**
     * Method setCompartmentSets the value of field 'compartment'.
     * The field 'compartment' has the following description: The
     * subcellular compartement of the object. It is strongly
     * recommended to refer to the Gene Ontology cellular component
     * in this element.
     * 
     * @param compartment the value of field 'compartment'.
     */
    public void setCompartment(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType compartment)
    {
        this._compartment = compartment;
    } //-- void setCompartment(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType)

    /**
     * Method setNamesSets the value of field 'names'. The field
     * 'names' has the following description: The names of the
     * organism. The short label should be a common name if it
     * exists. The full name should be the full name of the species
     * (i.e. genus species).
     * 
     * @param names the value of field 'names'.
     */
    public void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType names)
    {
        this._names = names;
    } //-- void setNames(org.cytoscape.coreplugin.psi_mi.schema.mi1.NamesType)

    /**
     * Method setTissueSets the value of field 'tissue'. The field
     * 'tissue' has the following description: Description of the
     * source tissue. Currently no species-independent controlled
     * vocabulary for tissues is available, therefore the choice of
     * reference database(s) is open to the data provider.
     * 
     * @param tissue the value of field 'tissue'.
     */
    public void setTissue(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType tissue)
    {
        this._tissue = tissue;
    } //-- void setTissue(org.cytoscape.coreplugin.psi_mi.schema.mi1.OpenCvType)

    /**
     * Method unmarshalBioSourceType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType unmarshalBioSourceType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi1.BioSourceType unmarshalBioSourceType(java.io.Reader)

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
