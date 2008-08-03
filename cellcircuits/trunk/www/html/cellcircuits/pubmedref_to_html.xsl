<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns="http://www.cs.rpi.edu/HTML"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:ns1="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<xsl:output method='text' version='1.0' encoding='UTF-8' indent='yes'/> 

<xsl:template match="/">
    <xsl:apply-templates/> 
</xsl:template>


<xsl:template match="PubmedArticleSet">
    <xsl:apply-templates select="PubmedArticle/MedlineCitation"/>
</xsl:template> 


<xsl:template match="PubmedArticle/MedlineCitation">
&lt;li&gt;
	<xsl:apply-templates select="Article/AuthorList"/>
	<xsl:apply-templates select="Article"/>
	<xsl:apply-templates select="PMID"/>
&lt;/li&gt;
</xsl:template>


<xsl:template match="Article">
	<xsl:value-of select="ArticleTitle"/>
	<xsl:value-of select="' '"/>
	<!-- check to get rid of trailing periods in Journal Title-->
	&lt;i&gt;<xsl:variable name="JTitle" select="Journal/Title"/>
 	<xsl:choose>
	  <xsl:when test="contains($JTitle, '.')">
            <xsl:value-of select="substring-before($JTitle,'.')"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="$JTitle"/>
	  </xsl:otherwise>
        </xsl:choose>

	<xsl:value-of select="', '"/>&lt;/i&gt;
	&lt;b&gt;
	<!-- make sure volume is present -->
        <xsl:variable name="Vol" select="Journal/JournalIssue/Volume"/>
	<xsl:if test="normalize-space($Vol) != ''">
	  <xsl:value-of select="$Vol"/>
	</xsl:if>
	<!-- make sure issue is present -->
        <xsl:variable name="Issue" select="Journal/JournalIssue/Issue"/>
	<xsl:if test="normalize-space($Issue) != ''">
	  <xsl:value-of select="'('"/>
	  <xsl:value-of select="$Issue"/>
	  <xsl:value-of select="')'"/>
	</xsl:if>&lt;/b&gt;<xsl:value-of select="':'"/><xsl:value-of select="Pagination/MedlinePgn"/><xsl:value-of select="', ('"/>

	<!-- get the year, it can come from two different places -->
        <xsl:variable name="Year" select="Journal/JournalIssue/PubDate/Year"/>
 	<xsl:choose>
	  <xsl:when test="normalize-space($Year) = ''">
            <xsl:variable name="longYear" select="Journal/JournalIssue/PubDate/MedlineDate"/>
	    <xsl:value-of select="substring($longYear,1,4)"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <xsl:value-of select="$Year"/>
	  </xsl:otherwise>
        </xsl:choose>
	<xsl:value-of select="'). '"/>
</xsl:template>

<xsl:template match="PMID">
	<xsl:value-of select="'PubMed ID: '"/>
	&lt;a href="<xsl:value-of select="'http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=pubmed&amp;dopt=Abstract&amp;list_uids='"/><xsl:value-of select="."/>"&gt;<xsl:value-of select="."/>&lt;/a&gt;
</xsl:template>



<xsl:template match="Article/AuthorList">
	<!-- find number of authors-->
	<xsl:variable name="authorCount">
	<xsl:for-each select="Author">
		<xsl:value-of select="position()"/>
	</xsl:for-each>
	</xsl:variable>

	<!-- odd instead of replacing values (selecting position) 
		concatonates them thus must mod by 10 to get a value 
		equal to the position-->
	<xsl:for-each select="Author">	
	    <xsl:choose>
		<xsl:when test="position() = $authorCount mod 10">
		  <xsl:value-of select="LastName"/>
		  <xsl:value-of select= "' '"/>
		  <xsl:value-of select="Initials"/>		
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="LastName"/>
		  <xsl:value-of select= "' '"/>
		  <xsl:value-of select="Initials"/>
		  <xsl:value-of select= "', '"/>
		</xsl:otherwise>
	    </xsl:choose>
	</xsl:for-each>
	<xsl:value-of select="'. '"/>
</xsl:template>

</xsl:stylesheet>


