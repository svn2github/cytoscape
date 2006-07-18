#!/usr/bin/perl

####################################################################################################
# Authors: Junghwan Park, Iliana Avila-Campillo
# Last modified: December 9, 2005 by Iliana
####################################################################################################

use DBI();
use Cwd;
print "--------------------- update_synonyms.pl --------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_synonyms.pl <db user> <db password> <db name>\n";
 	die;
}

$| = 1;
$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];


############ 1 #####################################################
print "Calling update_synonyms_taxonomy.pl...\n";
system("perl update_synonyms_taxonomy.pl $dbuser $dbpwd $dbname");
print "done.\n";

############ 2 #####################################################
print "Calling update_synonyms_refseq.pl...\n";
system("perl update_synonyms_refseq.pl $dbuser $dbpwd $dbname"); 
print "done.\n";

############ 3 #####################################################
print "Calling update_synonyms_iproclass.pl...\n";
system("perl update_synonyms_iproclass.pl $dbuser $dbpwd $dbname");
print "done.\n";

############ 4 #####################################################
print "Calling update_synonyms_prolinks.pl...\n";
system("perl update_synonyms_prolinks.pl $dbuser $dbpwd $dbname"); 
print "done.\n";

############ 5 #####################################################
print "Calling update_synonyms_kegg.pl...\n";
system("perl update_synonyms_kegg.pl $dbuser $dbpwd $dbname"); 
print "done.\n";

############ 6 #####################################################
print "Calling update_synonyms_uniprot.pl...\n";
system("perl update_synonyms_uniprot.pl $dbuser $dbpwd $dbname"); 
print "done.\n";

print "--------------------- leaving update_synonyms.pl --------------------\n";