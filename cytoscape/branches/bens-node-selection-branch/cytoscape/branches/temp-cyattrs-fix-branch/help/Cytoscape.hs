<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<helpset version="1.0">

  <!-- title -->
  <title>Cytoscape</title>

  <!-- maps -->
  <maps>
     <homeID>intro</homeID>
     <mapref location="Cytoscape.jhm"/>
  </maps>

  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Merging HelpSets</label>
    <type>javax.help.TOCView</type>
    <data>CytoscapeTOC.xml</data>
  </view>

<!--
  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>CytoscapeIndex.xml</data>
  </view>
-->

  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      CytoscapeSearch
    </data>
  </view>

</helpset>
