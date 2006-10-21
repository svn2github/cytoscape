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
 * Class Parameter.
 * 
 * @version $Revision$ $Date$
 */
public class Parameter extends org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterType
implements java.io.Serializable
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _uncertainty
     */
    private java.math.BigDecimal _uncertainty;

    /**
     * Reference to the experiment in which this parameter has been
     * determined. If not given, it is assumed that this is valid
     * for all experiments attached to the interaction.
     */
    private int _experimentRef;

    /**
     * keeps track of state for field: _experimentRef
     */
    private boolean _has_experimentRef;


      //----------------/
     //- Constructors -/
    //----------------/

    public Parameter() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getExperimentRefReturns the value of field
     * 'experimentRef'. The field 'experimentRef' has the following
     * description: Reference to the experiment in which this
     * parameter has been determined. If not given, it is assumed
     * that this is valid for all experiments attached to the
     * interaction.
     * 
     * @return the value of field 'experimentRef'.
     */
    public int getExperimentRef()
    {
        return this._experimentRef;
    } //-- int getExperimentRef() 

    /**
     * Method getUncertaintyReturns the value of field
     * 'uncertainty'.
     * 
     * @return the value of field 'uncertainty'.
     */
    public java.math.BigDecimal getUncertainty()
    {
        return this._uncertainty;
    } //-- java.math.BigDecimal getUncertainty() 

    /**
     * Method hasExperimentRef
     */
    public boolean hasExperimentRef()
    {
        return this._has_experimentRef;
    } //-- boolean hasExperimentRef() 

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
     * Method setExperimentRefSets the value of field
     * 'experimentRef'. The field 'experimentRef' has the following
     * description: Reference to the experiment in which this
     * parameter has been determined. If not given, it is assumed
     * that this is valid for all experiments attached to the
     * interaction.
     * 
     * @param experimentRef the value of field 'experimentRef'.
     */
    public void setExperimentRef(int experimentRef)
    {
        this._experimentRef = experimentRef;
        this._has_experimentRef = true;
    } //-- void setExperimentRef(int) 

    /**
     * Method setUncertaintySets the value of field 'uncertainty'.
     * 
     * @param uncertainty the value of field 'uncertainty'.
     */
    public void setUncertainty(java.math.BigDecimal uncertainty)
    {
        this._uncertainty = uncertainty;
    } //-- void setUncertainty(java.math.BigDecimal) 

    /**
     * Method unmarshalParameter
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter unmarshalParameter(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.Parameter unmarshalParameter(java.io.Reader)

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
