#!/usr/bin/perl

use DBI();
use Cwd;


if(scalar @ARGV < 3){
	print "USAGE: perl update_synonyms_genbank.pl <dbuser> <dbpassword> <dbname> optional: <update>\n";
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];
$update = false;
if(scalar @ARGV == 4){
	if($ARGV[3] =~ /^update/){
		$update = true;
	}
}

# Prepare database connection
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

#################################### Create tables #######################################################################################

$dbh->do("CREATE TABLE IF NOT EXISTS ipc_pir (uid INT, ipcid VARCHAR(15), pirid VARCHAR(15), pirac VARCHAR(15), INDEX(uid) )");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_sprot (uid INT, ipcid VARCHAR(15), sprotid VARCHAR(11), sprotac VARCHAR(6), INDEX(uid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_trembl (uid INT,ipcid VARCHAR(15),  tremblid VARCHAR(11), tremblac VARCHAR (6), INDEX(uid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_genbankac (uid INT, ipcid VARCHAR(15), genbankac VARCHAR(100), INDEX (uid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_refseqac (uid INT, ipcid VARCHAR(15), refseqac VARCHAR(18), refseqname VARCHAR(30), INDEX(uid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_genpeptac (uid INT, ipcid VARCHAR(15), genpeptac VARCHAR(15), INDEX(uid))"); # contains accessions for proteins
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_ontology (uid INT, ipcid VARCHAR(15), goid VARCHAR (20) )");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_function (uid INT, ipcid VARCHAR(15), function VARCHAR(200))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_pathway (uid INT, ipcid VARCHAR(15), keggid VARCHAR(100), INDEX(uid), INDEX(ipcid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_keywords (uid INT, ipcid VARCHAR(15), keywords VARCHAR(50), INDEX(uid), INDEX(ipcid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_pdb (uid INT, ipcid VARCHAR(15), pdbid VARCHAR(4), INDEX(uid), INDEX(ipcid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_interpro (uid INT, ipcid VARCHAR(15), interproid VARCHAR(9), INDEX(uid), INDEX(ipcid))");
#################################### Parse iProClass.xml #################################################################################

#system("rm -r ipc");
#system("mkdir ipc");
#print "Downloading iproclass.xml.gz...\n";
#system("wget ftp://ftp.pir.georgetown.edu/pir_databases/iproclass/iproclass.xml.gz --directory-prefix=ipc/") == 0 or die "Error: $?\n";
#print "\ndone downloading iproclass.xml.gz\n";
#print "Decompressing... ";
#system("gunzip ipc/iproclass.xml.gz") == 0 or die "$?\n";
#print "done.\n";

system("perl iproclass_xmlparser.pl ipc/iproclass.xml");

################################# Load parsed data into tables ###########################################################################


print "Loading data into ipc_pir...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_pir.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_pir (ipcid, pirid, pirac)");

print "Loading data into ipc_sprot...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_sprot.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_sprot (ipcid, sprotid, sprotac)");

print "Loading data into ipc_trembl...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_trembl.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_trembl (ipcid, tremblid, tremblac)");

print "Loading data into ipc_genbankac...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_genbankac.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_genbankac (ipcid, genbankac)");

print "Loading data into ipc_refseqac...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_refseqac.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_refseqac (ipcid, refseqac, refseqname)");

print "Loading data into ipc_genpeptac...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_genpeptac.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_genpeptac (ipcid, genpeptac)");

print "Loading data into ipc_ontology...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_ontology.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_ontology (ipcid, goid)");

print "Loading data into ipc_function...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_function.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_function (ipcid, function)");

print "Loading data into ipc_pathway...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_pathway.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_pathway (ipcid, keggid)");

print "Loading data into ipc_keywords...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_keywords.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_keywords (ipcid, keywords)");

print "Loading data into ipc_pdb...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_pdb.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_pdb (ipcid, pdbid)");

print "Loading data into ipc_interpro...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_interpro.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE ipc_interpro (ipcid, interproid)");

print "done loading data into tables from iProClass.\n";