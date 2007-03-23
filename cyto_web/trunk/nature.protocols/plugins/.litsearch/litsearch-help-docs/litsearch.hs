<?xml version='1.0' encoding='ISO-8859-1'  ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">
<helpset version="2.0">

<!-- title -->
<title>Literature Search</title>

<!-- maps -->
<maps>
    <homeID>Welcome_to_Agilent_Literature_Search</homeID>
    <mapref location="litsearch.jhm" />
</maps>

<!-- views -->
<view mergetype="javax.help.UniteAppendMerge">
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>litsearchTOC.xml</data>
</view>

<view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
     JavaHelpSearch</data>
</view>

<view>
    <name>favorites</name>
    <label>Favorites</label>
    <type>javax.help.FavoritesView</type>
</view>

</helpset>
