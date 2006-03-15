<?xml version="1.0"?>
<xsl:stylesheet version="1.0" 
    xmlns="http://www.cs.rpi.edu/XGMML"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:ns1="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="">
<xsl:output method='xml' version='1.0' encoding='UTF-8' indent='yes'/> 

<xsl:template match="/">
 <graph>
<xsl:apply-templates/> 
</graph>
</xsl:template>

<xsl:template match="pathway">
    <xsl:attribute name="id">
        <xsl:value-of select="@name"/>
    </xsl:attribute>
    <xsl:attribute name="label">
        <xsl:value-of select="@title"/>
    </xsl:attribute>
    
    <xsl:apply-templates/> 
</xsl:template>
<!-- Create a 'node' on finding an 'entry' -->
<xsl:template match="entry">
    <!-- enzyme to node -->
    <node>
        <xsl:attribute name="id">
            <xsl:value-of select="@id"/>
        </xsl:attribute>
        <xsl:attribute name="label">
            <xsl:value-of select="@name"/>
        </xsl:attribute>
        <xsl:if test="graphics/@name != ''">
            <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'graphicsLabel'"/>
                </xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="graphics/@name"/>
                </xsl:attribute>
            </att>
        </xsl:if>
        <xsl:if test="@link != ''">
            <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'link'"/>
                </xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="@link"/>
                </xsl:attribute>
            </att>
        </xsl:if>
            <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'type'"/>
                </xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </att>
        <xsl:if test="@reaction != ''">
            <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'reaction'"/>
                </xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="@reaction"/>
                </xsl:attribute>
            </att>
        </xsl:if>
        <xsl:apply-templates select="graphics"/>
    </node>
  
</xsl:template>
    <!-- Create an 'edge' on finding a 'relation' -->
<xsl:template match="relation">
    <!--<xsl:if test="@type = 'ECrel'"> -->
        <edge>
            <!-- create a text node holding the string, using xsl:value-of and
                it's helpful separator attribute -->
            <xsl:variable name="sibling-names">
                <xsl:value-of select="concat(@entry1, ' (', @type,') ', @entry2)"/>
            </xsl:variable>
        
            <xsl:attribute name="id">
                <xsl:value-of select="$sibling-names"/>
            </xsl:attribute>
            <xsl:attribute name="source">
                <xsl:value-of select="@entry1"/>
            </xsl:attribute>
            <xsl:attribute name="target">
                <xsl:value-of select="@entry2"/>
            </xsl:attribute>
            <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'interaction'"/>
                </xsl:attribute>
                <xsl:attribute name="value">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </att>
            <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'subtype'"/>
                </xsl:attribute>
                <att>
                    <xsl:attribute name="name">
                        <xsl:value-of select="'subtypeName'"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="subtype/@name"/>
                    </xsl:attribute>
                </att>
                <att>
                    <xsl:attribute name="name">
                        <xsl:value-of select="'subtypeValue'"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="subtype/@value"/>
                    </xsl:attribute>
                </att>
            </att>
            <graphics width="2" fill="#0000e1"/>
        </edge>  
    <!-- </xsl:if> -->
    
</xsl:template>
    <!-- Create 'node' on finding 'reaction' -->
<!-- <xsl:template match="reaction">
    <node>
        <xsl:attribute name="id">
            <xsl:value-of select="@name"/>
        </xsl:attribute>
        <xsl:attribute name="label">
            <xsl:value-of select="@type"/>
        </xsl:attribute>
        <att>
            <xsl:attribute name="name">
                <xsl:value-of select="'substrate'"/>
            </xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="substrate/@name"/>
            </xsl:attribute>
      
        </att>
        <att>
            <xsl:attribute name="name">
                <xsl:value-of select="'product'"/>
            </xsl:attribute>
            <xsl:attribute name="value">
                <xsl:value-of select="product/@name"/>
            </xsl:attribute>  
        </att>
     </node>  
</xsl:template> -->

<!-- graphics for non-reaction nodes -->
    <xsl:template match="graphics">  
        <graphics>
            <xsl:attribute name="fill">
                <xsl:value-of select="@bgcolor"/>
            </xsl:attribute>
            <xsl:attribute name="outline">
                <xsl:value-of select="@fgcolor"/>
            </xsl:attribute>
            <xsl:attribute name="type">
                <xsl:value-of select="@type"/>
            </xsl:attribute>
            <xsl:attribute name="x">
                <xsl:value-of select="@x"/>
            </xsl:attribute>
            <xsl:attribute name="y">
                <xsl:value-of select="@y"/>
            </xsl:attribute>
            <xsl:attribute name="h">
                <xsl:value-of select="@height"/>
            </xsl:attribute>
            <xsl:attribute name="w">
                <xsl:value-of select="@width"/>
            </xsl:attribute>
            <!-- <att>
                <xsl:attribute name="name">
                    <xsl:value-of select="'cytoscapeNodeGraphicsAttributes'"/>
                </xsl:attribute>  
                <att>
                    <xsl:attribute name= "value">
                        <xsl:value-of select="1.0"/>
                    </xsl:attribute>
                    <xsl:attribute name= "name">
                        <xsl:value-of select="'nodeTransparency'"/>
                    </xsl:attribute>
                </att>
                <att>
                    <xsl:attribute name= "value">
                        <xsl:value-of select="'Default-0-12'"/>
                    </xsl:attribute>
                    <xsl:attribute name= "name">
                        <xsl:value-of select="'nodeLabelFont'"/>
                    </xsl:attribute>
                </att>
                <att>
                    <xsl:attribute name= "value">
                        <xsl:value-of select="'solid'"/>
                    </xsl:attribute>
                    <xsl:attribute name= "name">
                        <xsl:value-of select="'borderLineType'"/>
                    </xsl:attribute>
                </att>
            </att> -->
        </graphics> 
    </xsl:template>

    
    
</xsl:stylesheet>

<!-- xmlns="http://www.cs.rpi.edu/XGMML" -->
