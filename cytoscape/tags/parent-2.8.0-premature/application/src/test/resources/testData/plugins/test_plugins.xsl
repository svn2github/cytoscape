<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <!-- These params specify variables that will be passed in at transformation time -->
    <xsl:param name="cytoscapeVersion"/>
    <xsl:param name="fileUrl"/>
    

    <xsl:template match="/project">
    <project>
        <name><xsl:value-of select="name"/></name>
        <description><xsl:value-of select="description"/></description>
        <url><xsl:value-of select="url"/></url>
        
        <pluginlist>				
            <!-- 
                The urls for each of these plugins SHOULD start with http://...
                For testing these will be concatenated with file:///user_dir/url
                Do NOT set up your xml file with partial urls
            -->
            <xsl:for-each select="/project/pluginlist/plugin">
                <plugin>
                    <uniqueID><xsl:value-of select="uniqueID"/></uniqueID>
                    <name><xsl:value-of select="name"/></name>
                    <description><xsl:value-of select="description"/></description>
                    <pluginVersion><xsl:value-of select="pluginVersion"/></pluginVersion>
                    <cytoscapeVersions>
                        <xsl:for-each select="cytoscapeVersions/version">
                            <version>
                            <xsl:choose>
                                <xsl:when test="current()='current'"><xsl:value-of select="$cytoscapeVersion"/></xsl:when>
                                <xsl:otherwise><xsl:value-of select="current()"/></xsl:otherwise>
                            </xsl:choose>
                            </version>
                        </xsl:for-each>
                    </cytoscapeVersions>
                    
                    <filetype><xsl:value-of select="filetype"/></filetype>
                    <category><xsl:value-of select="category"/></category>
                    <url><xsl:value-of select="$fileUrl"/><xsl:value-of select="url"/></url>
                    
                    <xsl:if test="license">
                    <license>
                    	<text><xsl:value-of select="license/text"/></text>
                    </license>
                    </xsl:if>

                    <authorlist>
                        <xsl:for-each select="authorlist/author">
                        <author>
                            <name><xsl:value-of select="name"/></name>
                            <institution><xsl:value-of select="institution"/></institution>
                        </author>
                        </xsl:for-each>
                    </authorlist>
                    
                </plugin>
            </xsl:for-each>
        </pluginlist>

        <xsl:if test="themes">
            <themes>
                <xsl:for-each select="/project/themes/theme">
                    <theme>
                        <uniqueID><xsl:value-of select="uniqueID"/></uniqueID>
                        <name><xsl:value-of select="name"/></name>
                        <description><xsl:value-of select="description"/></description>
                        <themeVersion><xsl:value-of select="themeVersion"/></themeVersion>
                        <cytoscapeVersions>
                        <xsl:for-each select="cytoscapeVersions/version">
                            <version>
                            <xsl:choose>
                                <xsl:when test="current()='current'"><xsl:value-of select="$cytoscapeVersion"/></xsl:when>
                                <xsl:otherwise><xsl:value-of select="current()"/></xsl:otherwise>
                            </xsl:choose>
                            </version>
                        </xsl:for-each>
                        </cytoscapeVersions>
                
                        <pluginlist>
                            <xsl:for-each select="pluginlist/plugin">
                            <plugin>
                                <uniqueID><xsl:value-of select="uniqueID"/></uniqueID>
                                <pluginVersion><xsl:value-of select="pluginVersion"/></pluginVersion>
                            </plugin>
                            </xsl:for-each>
                        </pluginlist>
                    </theme>
                </xsl:for-each>
            </themes>
            
        </xsl:if>
    </project>
    </xsl:template>
        
</xsl:stylesheet>
