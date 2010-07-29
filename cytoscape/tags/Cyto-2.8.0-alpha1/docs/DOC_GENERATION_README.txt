
TASK EXPLANATION
===============================================================================

"ant all"  creates all of the documentation based on the existing manual_raw.xml
and images files.

"ant create-help"
"ant create-html"
"ant create-pdf"

Each create the associated files.  PDF will only be created if XEP
(http://renderx.com) is installed on your system.  Edit the "xep.home"
property for your specific system.

"ant regenerate" re-downloads the manual and all images files from the wiki.

"ant clean" removes all temporary files so that we may check generated files
in.

"ant clean-gen" removes all generated files, but none of the downloaded files.

"ant clean-all" removes all generated AND downloaded files.



POTENTIAL ERRORS
===============================================================================

"Error: CALS tables must specify the number of columns."



This happens because you see the following structure in the docbook:

   <table cols="1">
       <caption/>
	       <tgroup>
		        <colspec colname="xxx1"/>

where the number of columns is specified in the table element rather than the
tgroup element, where it should be.  The following is correct

   <table>
       <caption/>
	       <tgroup cols="1">
		        <colspec colname="xxx1"/>

This is best solved by fixing the tables in the document rather than trying to
tweak the XML.  This is really a bug in how MoinMoin exports the DocBook.

This problem occurs in the XML when the first row of a table spans some or all 
of the columns in the table.  Simply remove this and the xml is exported
correctly. 

