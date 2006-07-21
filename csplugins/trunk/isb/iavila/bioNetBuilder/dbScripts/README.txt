== Instructions to create or update databases using Perl scripts ==
Iliana Avila-Campillo, July 17th, 2006

= Requirements to run scripts =

wget software http://www.gnu.org/software/wget/wget.html
Perl module XML::Simple, download and install from CPAN.
Perl module XML::Parser::Expat, download and intall from CPAN

= Instructions =

This directory contains the Perl scripts we wrote to create and populate all of the mySQL databases BioNetBuilder uses.
You can run any of these scripts to create or update your own mySQL databases (if  you are managing your own BioNetBuilder server).
You can also use these scripts as a guide to write your own Perl scripts to create and populate new databases you are adding to BioNetBuilder.

Use the update.pl script to update a set of databases. The script takes these arguments:

update.pl -u=db user -p=db password  [synonyms=synonyms db name] [prolinks=prolinks db name] [kegg=kegg db name] [bind=bind db name] [dip=dip db name] [hprd=hprd db name][go=go db name] 

For each database that you wish to update, you need its name (one of synonyms, prolinks, kegg, bind, dip, hprd, and go) and the actual name of the mySQL database that will hold its information.
You also need a mySQL user and password with permissions to create, drop, and update databases and tables, insert into tables, etc.
For example, if you want to update bind, do the following:
perl update.pl -u=myuser -p=mypassword bind=bind1

For the following databases, you have to manually download their flat-files (since you will need to register):

DIP: Download must be obtained before hand and copied to "dip" directory at the same level as this document.
The download can be obtained from DIP's website (http://dip.doe-mbi.ucla.edu/, free registration for academic users), and must be in MIF format.
Get the "FULL" data set.

HPRD: Must download HPRD PSIMI file to "hprd" directory located in the same directory as this document.
The file (psimi_single_final.xml) can be downloaded for free for academic registered users from HPRD's site: hprd.org

This directory also contains Perl scripts for each individual database:
update_prolinks.pl
update_bind.pl
update_hprd.pl

Which you can also run. If for some reason, you are using these scripts instead of update.pl, and, you are updating synonyms and another database, make sure you ALWAYS first update synonyms.

Most of the scripts (except the ones for DIP and HPRD) automatically download the database flat-files from a public FTP site provided by the corresponding institution. The location of this FTP site may change.
If you get errors related to not being able to find an FTP URL or file at an FTP site, go to the database's website to see what the new FTP URL is, and then edit the scripts to use the new FTP site.
