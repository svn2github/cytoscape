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
 * A numeric parameter, e.g. for a kinetic value
 * 
 * @version $Revision$ $Date$
 */
public class ParameterType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The kind of parameter, e.g. "dissociation constant".
     */
    private java.lang.String _term;

    /**
     * Accession number of the term in the associated controlled
     * vocabulary.
     */
    private java.lang.String _termAc;

    /**
     * The unit of the term, e.g. "kiloDalton".
     */
    private java.lang.String _unit;

    /**
     * Accession number of the unit in the associated controlled
     * vocabulary.
     */
    private java.lang.String _unitAc;

    /**
     * Base of the parameter expression. Defaults to 10.
     */
    private short _base = 10;

    /**
     * keeps track of state for field: _base
     */
    private boolean _has_base;

    /**
     * Exponent of the value.
     */
    private short _exponent = 0;

    /**
     * keeps track of state for field: _exponent
     */
    private boolean _has_exponent;

    /**
     * The "main" value of the parameter.
     */
    private java.math.BigDecimal _factor;


      //----------------/
     //- Constructors -/
    //----------------/

    public ParameterType() {
        super();
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteBase
     */
    public void deleteBase()
    {
        this._has_base= false;
    } //-- void deleteBase() 

    /**
     * Method deleteExponent
     */
    public void deleteExponent()
    {
        this._has_exponent= false;
    } //-- void deleteExponent() 

    /**
     * Method getBaseReturns the value of field 'base'. The field
     * 'base' has the following description: Base of the parameter
     * expression. Defaults to 10.
     * 
     * @return the value of field 'base'.
     */
    public short getBase()
    {
        return this._base;
    } //-- short getBase() 

    /**
     * Method getExponentReturns the value of field 'exponent'. The
     * field 'exponent' has the following description: Exponent of
     * the value.
     * 
     * @return the value of field 'exponent'.
     */
    public short getExponent()
    {
        return this._exponent;
    } //-- short getExponent() 

    /**
     * Method getFactorReturns the value of field 'factor'. The
     * field 'factor' has the following description: The "main"
     * value of the parameter.
     * 
     * @return the value of field 'factor'.
     */
    public java.math.BigDecimal getFactor()
    {
        return this._factor;
    } //-- java.math.BigDecimal getFactor() 

    /**
     * Method getTermReturns the value of field 'term'. The field
     * 'term' has the following description: The kind of parameter,
     * e.g. "dissociation constant".
     * 
     * @return the value of field 'term'.
     */
    public java.lang.String getTerm()
    {
        return this._term;
    } //-- java.lang.String getTerm() 

    /**
     * Method getTermAcReturns the value of field 'termAc'. The
     * field 'termAc' has the following description: Accession
     * number of the term in the associated controlled vocabulary.
     * 
     * @return the value of field 'termAc'.
     */
    public java.lang.String getTermAc()
    {
        return this._termAc;
    } //-- java.lang.String getTermAc() 

    /**
     * Method getUnitReturns the value of field 'unit'. The field
     * 'unit' has the following description: The unit of the term,
     * e.g. "kiloDalton".
     * 
     * @return the value of field 'unit'.
     */
    public java.lang.String getUnit()
    {
        return this._unit;
    } //-- java.lang.String getUnit() 

    /**
     * Method getUnitAcReturns the value of field 'unitAc'. The
     * field 'unitAc' has the following description: Accession
     * number of the unit in the associated controlled vocabulary.
     * 
     * @return the value of field 'unitAc'.
     */
    public java.lang.String getUnitAc()
    {
        return this._unitAc;
    } //-- java.lang.String getUnitAc() 

    /**
     * Method hasBase
     */
    public boolean hasBase()
    {
        return this._has_base;
    } //-- boolean hasBase() 

    /**
     * Method hasExponent
     */
    public boolean hasExponent()
    {
        return this._has_exponent;
    } //-- boolean hasExponent() 

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
     * Method setBaseSets the value of field 'base'. The field
     * 'base' has the following description: Base of the parameter
     * expression. Defaults to 10.
     * 
     * @param base the value of field 'base'.
     */
    public void setBase(short base)
    {
        this._base = base;
        this._has_base = true;
    } //-- void setBase(short) 

    /**
     * Method setExponentSets the value of field 'exponent'. The
     * field 'exponent' has the following description: Exponent of
     * the value.
     * 
     * @param exponent the value of field 'exponent'.
     */
    public void setExponent(short exponent)
    {
        this._exponent = exponent;
        this._has_exponent = true;
    } //-- void setExponent(short) 

    /**
     * Method setFactorSets the value of field 'factor'. The field
     * 'factor' has the following description: The "main" value of
     * the parameter.
     * 
     * @param factor the value of field 'factor'.
     */
    public void setFactor(java.math.BigDecimal factor)
    {
        this._factor = factor;
    } //-- void setFactor(java.math.BigDecimal) 

    /**
     * Method setTermSets the value of field 'term'. The field
     * 'term' has the following description: The kind of parameter,
     * e.g. "dissociation constant".
     * 
     * @param term the value of field 'term'.
     */
    public void setTerm(java.lang.String term)
    {
        this._term = term;
    } //-- void setTerm(java.lang.String) 

    /**
     * Method setTermAcSets the value of field 'termAc'. The field
     * 'termAc' has the following description: Accession number of
     * the term in the associated controlled vocabulary.
     * 
     * @param termAc the value of field 'termAc'.
     */
    public void setTermAc(java.lang.String termAc)
    {
        this._termAc = termAc;
    } //-- void setTermAc(java.lang.String) 

    /**
     * Method setUnitSets the value of field 'unit'. The field
     * 'unit' has the following description: The unit of the term,
     * e.g. "kiloDalton".
     * 
     * @param unit the value of field 'unit'.
     */
    public void setUnit(java.lang.String unit)
    {
        this._unit = unit;
    } //-- void setUnit(java.lang.String) 

    /**
     * Method setUnitAcSets the value of field 'unitAc'. The field
     * 'unitAc' has the following description: Accession number of
     * the unit in the associated controlled vocabulary.
     * 
     * @param unitAc the value of field 'unitAc'.
     */
    public void setUnitAc(java.lang.String unitAc)
    {
        this._unitAc = unitAc;
    } //-- void setUnitAc(java.lang.String) 

    /**
     * Method unmarshalParameterType
     * 
     * @param reader
     */
    public static org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterType unmarshalParameterType(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterType) Unmarshaller.unmarshal(org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterType.class, reader);
    } //-- org.cytoscape.coreplugin.psi_mi.schema.mi25.ParameterType unmarshalParameterType(java.io.Reader)

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
