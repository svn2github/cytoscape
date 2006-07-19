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

If you want to create and populate ALL databases (DIP, BIND, GO, KEGG, Prolinks, and synonyms) then do this:

1. Update initiate.props to include the user name and password for your mySQL server.
Make sure that this user has permissions to insert, update, create, delete, and drop tables and databases.

2. For the following databases, you have to manually download their flat-files:

DIP: Download must be obtained before hand and copied to "dip" directory at the same level as this document.
The download can be obtained from DIP's website (http://dip.doe-mbi.ucla.edu/, free registration for academic users), and must be in MIF format.
Get the "FULL" data set.

HPRD: Must download HPRD PSIMI file to "hprd" directory located in the same directory as this document.
The file (psimi_single_final.xml) can be downloaded for free for academic users from HPRD's site: hprd.org

3. Run:
perl initiate.pl
This creates and populates DIP, BIND, GO, KEGG, Prolinks, and synonyms, so it will take a while!

4. Run (for HPRD):
perl update_hprd.pl <db user> <db password> <hprd db name>


If you want to update one of the databases, there are scripts that update each:

update_synonyms.pl
update_dip.pl
update_bind.pl
etc.

Make sure you update synonyms LAST (after you created/updated everything else).

Each takes as arguments the user name, password of the mySQL server where the databases will be created and populated, and, the name of the database you wish to create (in that order).
For example, to create and populate BIND call this script (using your own mySQL db user and password:
perl update_bind.pl root rootpass bind1

Most of the scripts (except DIP and HPRD) automatically download the database flat-files from a public FTP site provided by the corresponding institution. Ths location of this FTP site may change.
If you get errors related to not being able to find an FTP URL or file at an FTP site, go to the database's website to see what the new FTP URL is, and then edit the scripts to use the new FTP site.
