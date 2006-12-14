

"ant all"  creates all of the documentation based on the existing manual_raw.xml
and images files.

"ant create-help"
"ant create-html"
"ant create-pdf"

Each create the associated files.  PDF will only be created if XEP
(http://renderx.com) is installed on your system.  Edit the "xep.home"
property for your specific system.

"ant regenerate" re-downloads the manual and all images files from the wiki.

"ant clean" removes all generated files, but none of the downloaded files.

"ant clean-all" removes all generated AND downloaded files.

