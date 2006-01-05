#!/usr/bin/perl

# Jung Park, Iliana Avila-Campillo
# Downloads txt files from http site, adds rows to tables in the synonyms db

use DBI();
use Cwd;

print "--------------------- update_synonyms_prolinks.pl ------------------\n";

$pwd = cwd(); # get current directory

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_synonym_prolinks.pl <db user> <db password> <synonyms db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

system("rm -r prolinks/syn");
system("mkdir prolinks/syn");

print "Getting geneIDS_to_GInum.txt and GeneID_Genename.txt...";
system('wget http://mysql5.mbi.ucla.edu/public/reference_files/geneIDS_to_GInum.txt --directory-prefix=prolinks/syn');
system('wget http://mysql5.mbi.ucla.edu/public/reference_files/GeneID_Genename.txt --directory-prefix=prolinks/syn');
print "done\n";

print "start: ".(time())."\n";

$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("USE $dbname");

print "Creating DB tables...\n";

$dbh->do("CREATE TABLE IF NOT EXISTS prolinks_protgi (uid INT, prolinksid INT, protgi INT, INDEX(uid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS prolinks_gi_genename (uid INT, protgi INT, genename VARCHAR(20), INDEX(uid), INDEX(protgi))");

print "Loading data into prolinks_gi...\n";
$fullFilePath = getcwd()."/prolinks/syn/geneIDS_to_GInum.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE prolinks_protgi (prolinksid, protgi)") or die "Error: $synh->errstr";
print "done.\n";

print "Loading data into prolinks_gi_genename...\n";
$fullFilePath = getcwd()."/prolinks/syn/GeneID_Genename.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE prolinks_gi_genename (protgi, genename)") or die "Error: $synh->errstr";
print "done.\n";



##################################### OLD CODE ########################################################################
#print "   creating temp tables...\n";
#$dbh->do("CREATE TABLE IF NOT EXISTS prolinks_temp1 (uid INT DEFAULT -1, prolinksid INT, genename VARCHAR(255), gi INT, INDEX(uid), INDEX(prolinksid), INDEX (gi))") or die "Error: $dbh->errstr\n";
#$dbh->do("CREATE TABLE prolinks_temp2 (prolinksid INT, gi INT, INDEX (prolinksid), INDEX (gi), KEY(prolinksid, gi))") or die "Error: $dbh->errstr\n";
#
#print "Loading Up Datafiles...\n";
#print "   prolinksid - genename\n";
#$dbh->do("LOAD DATA LOCAL INFILE \'${pwd}/prolinks/GeneID_Genename.txt\' INTO TABLE prolinks_temp1 IGNORE 1 LINES (prolinksid, genename)") or die "Error: $dbh->errstr\n";
#print "   prolinksid - gi number\n";
#$dbh->do("LOAD DATA LOCAL INFILE \'${pwd}/prolinks/geneIDS_to_GInum.txt\' INTO TABLE prolinks_temp2 IGNORE 1 LINES") or die "Error: $dbh->errstr\n";
#
#print "Reorganizing tables\n";
#$commacontaining = $dbh->prepare_cached("SELECT * FROM prolinks_temp1 WHERE genename LIKE \'\%,\%\'") or die "Error: $dbh->errstr\n";
#$commacontaining->execute() or die "Error: $dbh->errstr\n";
#
# Cached statements for improved performance
#$sth1 = $dbh->prepare_cached("DELETE FROM prolinks_temp1 WHERE prolinksid = ? AND genename = ?");
#$sth2 = $dbh->prepare_cached("INSERT INTO prolinks_temp1 (prolinksid, genename) VALUES(?, ?)");
#
#while($commacontainingref = $commacontaining->fetchrow_hashref()) {
#	$prolinksid = $commacontainingref->{'prolinksid'};
#	$genename = $commacontainingref->{'genename'};
#
#	$sth1->execute($prolinksid, $genename) or die "Error: $dbh->errstr\n";
#
#	@genenames = split(/\,/,$genename);
#	$motherind = 0;
#	for ($i=0;$i<=$#genenames;$i++) {
#		if ($genenames[$i] ne '') {
#			if (length($genenames[$i])>2) {
#				$motherind = $i;
#			}
#			if ($genenames[$i] =~ /^[0-9]+$/) {
#				$genenames[$motherind] =~ /^(.*[^0-9])([0-9]+)$/;
#				$mothername = $1;
#				$genenames[$i] = $mothername.$genenames[$i];
#			}
#		}
#	}
#	
#	for ($i=0;$i<=$#genenames;$i++) {
#		if ($genenames[$i] ne '') {
#			if (length($genenames[$i])<3) {
#				$genenames[$i] = substr($genenames[$motherind],0,length($genenames[$motherid])-length($genenames[$i])).$genenames[$i];
#			}
#		}
#	}
#
#	for ($i=0;$i<=$#genenames;$i++) {
#		$sth2->execute($prolinksid, $genename) or die "Error: $dbh->errstr\n";
#	}
#}
#	
#print "Merging tables...\n";
#
#print "   creating prolinksid-genename-gi table...\n";
#$dbh->do("UPDATE prolinks_temp1, prolinks_temp2 SET prolinks_temp1.gi = prolinks_temp2.gi WHERE prolinks_temp1.prolinksid = prolinks_temp2.prolinksid") or die "Error: $dbh->errstr\n";
#
#print "   creating oid-prolinksid-genename-gi table by gi...\n";
#$dbh->do("UPDATE prolinks_temp1, xref_gi SET prolinks_temp1.oid = xref_gi.oid WHERE prolinks_temp1.gi = xref_gi.gi AND prolinks_temp1.gi != 0") or die "Error: $dbh->errstr\n";
#
#print "   creating oid-prolinksid-genename-gi table by genename...\n";
#$dbh->do("UPDATE prolinks_temp1, gn_genename SET prolinks_temp1.oid = gn_genename.oid WHERE prolinks_temp1.genename = gn_genename.genename AND prolinks_temp1.gi = 0") or die "Error: $dbh->errstr\n";
#$dbh->do("UPDATE prolinks_temp1, gn_oln SET prolinks_temp1.oid = gn_oln.oid WHERE prolinks_temp1.genename = gn_oln.oln AND prolinks_temp1.gi = 0") or die "Error: $dbh->errstr\n";
#$dbh->do("UPDATE prolinks_temp1, gn_orf SET prolinks_temp1.oid = gn_orf.oid WHERE prolinks_temp1.genename = gn_orf.orf AND prolinks_temp1.gi = 0") or die "Error: $dbh->errstr\n";

#print "Reorganizing tables...\n";
#$dbh->do("INSERT IGNORE INTO keydb (db, id) SELECT 'prolinks', prolinksid FROM prolinks_temp1 WHERE oid = -1") or die "Error: $dbh->errstr\n";
#$dbh->do("INSERT IGNORE INTO keydb (oid, db, id) SELECT oid, 'prolinks', prolinksid FROM prolinks_temp1 WHERE oid!=-1") or die "Error: $dbh->errstr\n";
#$dbh->do("UPDATE prolinks_temp1, keydb SET prolinks_temp1.oid = keydb.oid WHERE prolinks_temp1.prolinksid = keydb.id AND keydb.db = 'prolinks' AND prolinks_temp1.oid = -1") or die "Error: $dbh->errstr\n";

#$dbh->do("INSERT IGNORE INTO hasit (oid, db) SELECT oid, 'prolinks' FROM prolinks_temp1") or die "Error: $dbh->errstr\n";
#$dbh->do("INSERT IGNORE INTO xref_prolinks (oid, prolinksid) SELECT oid, prolinksid FROM prolinks_temp1") or die "Error: $dbh->errstr\n";
#$dbh->do("ALTER TABLE prolinks_temp1 RENAME TO key_prolinks") or die "Error: $dbh->errstr\n";

#$dbh->do("DROP TABLE prolinks_temp2") or die "Error: $dbh->errstr\n";
#$dbh->disconnect();

#print "Done.\n";

#print "end: ".(time())."\n";

print "---------------- Leaving update_synonym_prolinks.pl -----------------\n";