#!/usr/bin/perl

# Junghwan Park, Iliana Avila-Campillo
# Calls iproclass_parser to parse XML file from iProClass Database
# Calls update_synonym_kegg and update_synonym_prolinks to get additional information on synonyms
# TODO: tables gi_taxonomy and ncbiTaxNames

use DBI();
print "--------------------- update_synonyms.pl --------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_synonyms.pl <db user> <db password> <db name>\n";
 	die;
}

$| = 1;
$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

&create_db_structure($dbh, $dbname);
&downloadfiles;
&ipcparse($dbh, $dbname);
&loadparsed($dbh, $dbname);

print "Calling update_synonym_kegg.pl...\n";
$dbh->do("use metainfo") or die "Error: $dbh->errstr\n";
$sth = $dbh->prepare_cached("SELECT db_name WHERE db=?") or die "Error: $dbh->errstr";
$sth->execute('kegg') or die "Error: $dbh->errstr";
@row = $sth->fetchrow_array;
$keggname = $row[0];
system("update_synonym_kegg.pl $dbuser $dbpwd $keggname $dbname");

$sth = $dbh->prepare_cached("SELECT db_name WHERE db=?") or die "Error: $dbh->errstr";
$sth->execute('prolinks') or die "Error: $dbh->errstr";
@row = $sth->fetchrow_array;
$plName = $row[0];
system("update_synonym_prolinks.pl $dbuser $dbpwd $plname $dbname");

$dbh->disconnect();
print "------------------- Leaving update_synonyms.pl ----------------------\n";

########################################
sub create_db_structure {
	($dbh, $dbname) = @_;

	print "Creating DB Structure.\n";

	print "   Dropping database $dbname if it exists...\n";
	$dbh->do("DROP DATABASE IF EXISTS $dbname") or die "Error: $dbh->errstr";
	print "   Creating database $dbname and tables...\n";
	$dbh->do("CREATE DATABASE $dbname") or die "Error: $dbh->errstr";
	$dbh->do("USE $dbname") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE keydb (oid INT NOT NULL AUTO_INCREMENT, db VARCHAR(20), id VARCHAR(30), PRIMARY KEY(oid, db, id))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE hasit (oid INT, db VARCHAR(20), PRIMARY KEY(oid, db))") or die "Error: $dbh->errstr";

	# from what I see, keydb and hasit is all we need, so maybe we will remove these: ???
	
	# ILIANA'S NOTES ON SQL:
	# primary keys are a constraint that forces the tables to not have tuples with equal values for all attributes in the key
	# this is different from an index, which is a data structure to improve performance

	# --------- PROTEIN INFORMATION --------------- #

	$dbh->do("CREATE TABLE prot_pir (oid INT, ipcid VARCHAR(30), csqid VARCHAR(30), PRIMARY KEY(oid, ipcid, csqid))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE prot_sprot (oid INT, sprotid VARCHAR(30), sprotname TEXT, sprotac VARCHAR(30), PRIMARY KEY (oid, sprotid, sprotac))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE prot_trembl (oid INT, tremblid VARCHAR(30), tremblname TEXT, tremblac VARCHAR(30), PRIMARY KEY (oid, tremblid, tremblac))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE prot_refseq (oid INT, refseqname TEXT, refseqac VARCHAR(30), PRIMARY KEY(oid, refseqac))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE prot_genpeptac (oid INT, genpeptac VARCHAR(30), PRIMARY KEY(oid, genpeptac))") or die "Error: $dbh->errstr";

	# ---------- TAXONOMY INFORMATION ------------- #
	$dbh->do("CREATE TABLE tx_taxonomy (oid INT, sourceorg VARCHAR(255), taxongroup VARCHAR(255), taxonid INT, lineage TEXT, PRIMARY KEY(oid, taxonid))") or die "Error: $dbh->errstr";
	
	#----------- GENE INFORMATION ----------------- #
	$dbh->do("CREATE TABLE gn_genename (oid INT, genename VARCHAR(30), PRIMARY KEY(oid, genename))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE gn_oln (oid INT, oln VARCHAR(30), PRIMARY KEY(oid, oln))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE gn_orf (oid INT, orf VARCHAR(30), PRIMARY KEY(oid, orf))") or die "Error: $dbh->errstr";

	#----------- CROSS REFERENCE IDS -------------- #
	# these three are taken care of in update_synonym_kegg:
	$dbh->do("CREATE TABLE xref_kegg (oid INT, kegg_id VARCHAR(30), PRIMARY KEY(oid, kegg_id))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE xref_gi (oid INT, ngi INT, pgi INT (oid, ngi, pgi))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE xref_prolinks (oid INT, prolinks INT PRIMARY KEY(oid,prolinks))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE xref_ncbigeneid (oid INT, ncbigeneid VARCHAR(30), PRIMARY KEY (oid, ncbigeneid))") or die "Error: $dbh->errstr";
	
	#$dbh->do("CREATE TABLE xref_biblio (oid BIGINT, pmid VARCHAR(30), KEY (oid), UNIQUE(oid, pmid))") or die "Error: $dbh->errstr";
	
	$dbh->do("CREATE TABLE xref_dnaseq (oid INT, genbankac VARCHAR(30), PRIMARY KEY(oid, genbankac))") or die "Error: $dbh->errstr";
	
	#$dbh->do("CREATE TABLE xref_genomegene_tigr (oid INT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_genomegene_uwgp (oid BIGINT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_genomegene_sgd (oid BIGINT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_genomegene_fly (oid BIGINT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_genomegene_mgi (oid BIGINT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_genomegene_gdb (oid BIGINT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_genomegene_omim (oid BIGINT, id VARCHAR(30), KEY (oid), KEY(id), UNIQUE(oid, id))");
	#$dbh->do("CREATE TABLE xref_locus	(oid BIGINT, locusid VARCHAR(30), locusname VARCHAR(255), KEY (oid), KEY (locusid), KEY (locusname), UNIQUE(oid, locusid))");

	$dbh->do("CREATE TABLE xref_ontology (oid INT, goid VARCHAR(30), goexp TEXT, PRIMARY KEY(oid, goid))") or die "Error: $dbh->errstr";
	
	#$dbh->do("CREATE TABLE xref_pdb	(oid BIGINT, pdbid VARCHAR(30), chain VARCHAR(2), detail VARCHAR(255), KEY (oid), KEY (pdbid), UNIQUE(oid, pdbid))");
	#$dbh->do("CREATE TABLE xref_scop	(oid BIGINT, scopid VARCHAR(30), KEY (oid), KEY (scopid), UNIQUE(oid, scopid))");
	
	$dbh->do("CREATE TABLE xref_dip	(oid INT, dipid VARCHAR(30), KEY (oid), KEY (dipid), UNIQUE(oid, dipid))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE xref_bind (oid INT, bindid VARCHAR(30), KEY (oid), KEY (bindid), UNIQUE(oid, bindid))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE xref_prolinks (oid INT, prolinksid VARCHAR(30), KEY (oid), KEY (prolinksid), UNIQUE(oid, prolinksid))") or die "Error: $dbh->errstr";

	print "Database structure creation completed.\n";
}
##########################################################################
sub downloadfiles {
	
	print "Downloading iproclass.xml.gz...";
	system("rm xref -Rf");
	system("mkdir xref");
	system("mkdir xref/ipc");
	system("wget ftp://ftp.pir.georgetown.edu/pir_databases/iproclass/iproclass.xml.gz -q --directory-prefix=xref/ipc/");
	print "done\n";
	print "Decompressing it...";
	system("gunzip xref/ipc/iproclass.xml.gz");
	print "done\n";

	# OLD CODE
	# which of these files are actually used????
	
	#my @cmd = (
	#	'rm xref -Rf',
	#	'mkdir xref',
	#	'mkdir xref/ipc',
	#	'mkdir xref/kegg',
	#	'mkdir xref/kegg/genes', #?????
	#	'mkdir xref/prolinks',
	#	'mkdir xref/taxonomy',
	#	'wget ftp://ftp.pir.georgetown.edu/pir_databases/iproclass/iproclass.xml.gz -q --directory-prefix=xref/ipc/',
	#	'gunzip xref/ipc/iproclass.xml.gz',
	#	'wget http://mysql5.mbi.ucla.edu/public/reference_files/geneIDS_to_GInum.txt -q --directory-prefix=xref/prolinks/',
	#	'wget http://mysql5.mbi.ucla.edu/public/reference_files/GeneID_Genename.txt -q --directory-prefix=xref/prolinks/'
	#	'wget ftp://ftp.ncbi.nih.gov/pub/taxonomy/gi_taxid_nucl.dmp.gz -q --directory-prefix=xref/taxonomy/',
	#	'gunzip xref/taxonomy/gi_taxid_nucl.dmp.gz',
	#	'gunzip xref/taxonomy/gi_taxid_prot.dmp.gz',
	#	'wget ftp://ftp.ncbi.nih.gov/pub/taxonomy/taxdump.tar.gz -q --directory-prefix=xref/taxonomy/'
	#	'gunzip < xref/taxonomy/taxdump.tar.gz | tar xvf-', # not sure this works
	#	'rm xref/taxonomy/delnodes.dmp', # need to write code for this.
    #           'rm xref/taxonomy/division.dmp',
    #          'rm xref/taxonomy/gencode.dmp',
    #         'rm xref/taxonomy/merged.dmp',
    #'rm xref/taxonomy/nodes.dmp',
    #'rm xref/taxonomy/readme.txt'	
	#);
	
	# this is wrong. Should connect to current KEGG. Also, Kegg may not exist:	
	#$keggdbh = DBI->connect("dbi:mysql:database=kegg:host=localhost", "cytouser", "bioNetBuilder") or die "Can't make database connect: $DBI::errstr\n";

	#$sth = $keggdbh->prepare("SELECT filename FROM org_name");
	#$sth->execute();

	#$keggcmd1 = 'wget ftp://ftp.genome.jp/pub/kegg/genomes/';
	#$keggcmd2 = '_xrefall.list -q --directory-prefix=xref/kegg/';

	#while ($ref = $sth->fetchrow_hashref()) {
		push(@cmd, $keggcmd1.lc($ref->{'filename'}).'/'.lc($ref->{'filename'}).$keggcmd2);
	#}
	#$keggdbh->disconnect();
	
	#while ($cmd = shift(@cmd)) {
	#	print "Executing $cmd..."
	#	system($cmd);
	#	print "done\n";
	#}
}
#######################################################################
sub ipcparse {
	($dbh, $dbname) = @_;

	$dbh->do("USE $dbname") or die "Error: $dbh->errstr";
	print "Parsing iproclass.xml...\n";
	system('./iproclass_parser.pl xref/ipc/iproclass.xml');
	print "Done parsing iproclass.xml.\n";

}

#######################################################################
sub loadparsed {
	($dbh, $dbname) = @_;
	
	# directory where all the parsed tab delimited files are located
	$tdfDir = system('pwd')."xref/parsed";

	print "Loading start...\n";
	print  "   table keydb...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.keydb' IGNORE INTO TABLE keydb") or die "Error: $dbh->errstr\n"; 
	
	print  "   table hasit...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.hasit' IGNORE INTO TABLE hasit") or die "Error: $dbh->errstr\n"; 
	
	print  "   table prot_pir...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.prot_pir' IGNORE INTO TABLE prot_pir") or die "Error: $dbh->errstr\n"; 
	
	print  "   table prot_sprot...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.prot_sprot' IGNORE INTO TABLE prot_sprot") or die "Error: $dbh->errstr\n"; 
	
	print  "   table prot_trembl...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.prot_trembl' IGNORE INTO TABLE prot_trembl") or die "Error: $dbh->errstr\n"; 
	
	print  "   table prot_refseq...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.prot_refseq' IGNORE INTO TABLE prot_refseq") or die "Error: $dbh->errstr\n"; 
	
	print  "   table prot_genpeptac...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.prot_genpeptac' IGNORE INTO TABLE prot_genpeptac") or die "Error: $dbh->errstr\n"; 
	
	print  "   table tx_taxonomy...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.tx_taxonomy' IGNORE INTO TABLE tx_taxonomy") or die "Error: $dbh->errstr\n"; 
	
	print  "   table gn_genename...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.gn_genename' IGNORE INTO TABLE gn_genename") or die "Error: $dbh->errstr\n"; 
	
	print  "   table gn_oln...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.gn_oln' IGNORE INTO TABLE gn_oln") or die "Error: $dbh->errstr\n"; 
	
	print  "   table gn_orf...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.gn_orf' IGNORE INTO TABLE gn_orf") or die "Error: $dbh->errstr\n"; 
	
	# this loads nothing
	print  "   table xref_kegg...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_kegg' IGNORE INTO TABLE xref_kegg") or die "Error: $dbh->errstr\n"; 
	
	# this loads nothing
	print  "   table xref_gi...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_gi' IGNORE INTO TABLE xref_gi") or die "Error: $dbh->errstr\n"; 
	
	#this loads nothing
	print  "   table xref_ncbigeneid...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_ncbigeneid' IGNORE INTO TABLE xref_ncbigeneid") or die "Error: $dbh->errstr\n"; 
	
	#print  "   table xref_biblio...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_biblio' IGNORE INTO TABLE xref_biblio") or die "Error: $dbh->errstr\n"; 
	
	print  "   table xref_dnaseq...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_dnaseq' IGNORE INTO TABLE xref_dnaseq") or die "Error: $dbh->errstr\n"; 
	
	#print  "   table xref_genomegene_tigr...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_tigr' IGNORE INTO TABLE xref_genomegene_tigr"); 
	
	#print  "   table xref_genomegene_uwgp...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_uwgp' IGNORE INTO TABLE xref_genomegene_uwgp"); 
	
	#print  "   table xref_genomegene_sgd...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_sgd' IGNORE INTO TABLE xref_genomegene_sgd"); 
	
	#print  "   table xref_genomegene_fly...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_fly' IGNORE INTO TABLE xref_genomegene_fly"); 
	
	#print  "   table xref_genomegene_mgi...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_mgi' IGNORE INTO TABLE xref_genomegene_mgi"); 
	
	#print  "   table xref_genomegene_gdb...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_gdb' IGNORE INTO TABLE xref_genomegene_gdb"); 
	
	#print  "   table xref_genomegene_omim...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_genomegene_omim' IGNORE INTO TABLE xref_genomegene_omim"); 
	
	#print  "   table xref_locus...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_locus' IGNORE INTO TABLE xref_locus"); 
	
	print  "   table xref_ontology...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_ontology' IGNORE INTO TABLE xref_ontology") or die "Error: $dbh->errstr\n"; 
	
	#print  "   table xref_pdb...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_pdb' IGNORE INTO TABLE xref_pdb") or die "Error: $dbh->errstr\n"; 
	
	#print  "   table xref_scop...\n";
	#$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_scop' IGNORE INTO TABLE xref_scop") or die "Error: $dbh->errstr\n"; 
	
	print  "   table xref_dip...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_dip' IGNORE INTO TABLE xref_dip") or die "Error: $dbh->errstr\n"; 
	
	print  "   table xref_bind...\n";
	$dbh->do("LOAD DATA INFILE '$tdfDir/tdf.xref_bind' IGNORE INTO TABLE xref_bind") or die "Error: $dbh->errstr\n"; 

	print "Loading done.\n";

	print "Deleting parsed files...\n";
	system ('rm ./xref/parsed -R');
	print "Deleting completed.\n";
}	
