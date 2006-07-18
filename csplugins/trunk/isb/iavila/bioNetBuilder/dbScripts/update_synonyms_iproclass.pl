#!/usr/bin/perl

use DBI();
use Cwd;


if(scalar @ARGV < 3){
	print "USAGE: perl update_synonyms_iproclass.pl <dbuser> <dbpassword> <dbname>\n";
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

# Prepare database connection
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

createTables();
parse();
populateTables();

createGi2Pir();

#################################### Create tables #######################################################################################

sub createTables {

$dbh->do("DROP TABLE IF EXISTS ipc_pir"); 
$dbh->do("DROP TABLE IF EXISTS ipc_sprot");
$dbh->do("DROP TABLE IF EXISTS ipc_trembl");
$dbh->do("DROP TABLE IF EXISTS ipc_genbankac");
$dbh->do("DROP TABLE IF EXISTS ipc_refseqac");
$dbh->do("DROP TABLE IF EXISTS ipc_genpeptac");
$dbh->do("DROP TABLE IF EXISTS ipc_ontology");
$dbh->do("DROP TABLE IF EXISTS ipc_function");
$dbh->do("DROP TABLE IF EXISTS ipc_pathway");
$dbh->do("DROP TABLE IF EXISTS ipc_keywords");
$dbh->do("DROP TABLE IF EXISTS ipc_pdb");
$dbh->do("DROP TABLE IF EXISTS ipc_interpro"); 

$dbh->do("CREATE TABLE IF NOT EXISTS ipc_pir (ipcid VARCHAR(15), pirid VARCHAR(15), pirac VARCHAR(15), KEY(ipcid,pirid) )");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_sprot (ipcid VARCHAR(15) , sprotid VARCHAR(11), sprotac VARCHAR(6), KEY(ipcid,sprotid,sprotac), INDEX(sprotac))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_trembl (ipcid VARCHAR(15) KEY,  tremblid VARCHAR(11), tremblac VARCHAR (6), KEY(ipcid,tremblid,tremblac),INDEX(tremblac))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_genbankac (ipcid VARCHAR(15), genbankac VARCHAR(100),KEY(ipcid,genbankac), INDEX (genbankac))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_refseqac (ipcid VARCHAR(15), refseqac VARCHAR(18), KEY(ipcid,refseqac),INDEX(refseqac))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_genpeptac (ipcid VARCHAR(15), genpeptac VARCHAR(15), KEY(ipcid,genpeptac))"); # contains accessions for proteins
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_ontology (ipcid VARCHAR(15), goid VARCHAR (20), KEY(ipcid,goid) )");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_function (ipcid VARCHAR(15) KEY, function VARCHAR(200), INDEX(ipcid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_pathway (ipcid VARCHAR(15), keggid VARCHAR(100), KEY(ipcid,keggid), INDEX(keggid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_keywords (ipcid VARCHAR(15) KEY, keywords VARCHAR(50), INDEX(keywords), INDEX(ipcid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_pdb (ipcid VARCHAR(15), pdbid VARCHAR(4), KEY(ipcid,pdbid), INDEX(ipcid))");
$dbh->do("CREATE TABLE IF NOT EXISTS ipc_interpro (ipcid VARCHAR(15), interproid VARCHAR(9), KEY(ipcid,interproid),INDEX(interproid), INDEX(ipcid))");
}
#################################### Parse iProClass.xml #################################################################################
sub parse {
system("rm -r ipc");
system("mkdir ipc");
print "Downloading iproclass.xml.gz...\n";
system("wget ftp://ftp.pir.georgetown.edu/pir_databases/iproclass/iproclass.xml.gz --directory-prefix=ipc/") == 0 or die "Error: $?\n";
print "\ndone downloading iproclass.xml.gz\n";
print "Decompressing... ";
system("gunzip ipc/iproclass.xml.gz") == 0 or die "$?\n";
print "done.\n";

system("perl iproclass_xmlparser.pl ipc/iproclass.xml");
}
################################# Load parsed data into tables ###########################################################################

sub populateTables {
print "Loading data into ipc_pir...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_pir.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_pir");

print "Loading data into ipc_sprot...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_sprot.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_sprot");

print "Loading data into ipc_trembl...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_trembl.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_trembl");

print "Loading data into ipc_genbankac...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_genbankac.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_genbankac");

print "Loading data into ipc_refseqac...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_refseqac.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_refseqac");

print "Loading data into ipc_genpeptac...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_genpeptac.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_genpeptac");

print "Loading data into ipc_ontology...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_ontology.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_ontology");

print "Loading data into ipc_function...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_function.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_function");

print "Loading data into ipc_pathway...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_pathway.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_pathway");

print "Loading data into ipc_keywords...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_keywords.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_keywords");

print "Loading data into ipc_pdb...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_pdb.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_pdb");

print "Loading data into ipc_interpro...\n";
$fullFilePath = getcwd()."/ipc/parsed/ipc_interpro.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ipc_interpro");

print "done loading data into tables from iProClass.\n";
}

############################## Create gi2pir table ####################################################################################
sub createGi2Pir {

print "Creating gi2pir table...\n";
$dbh->do("DROP TABLE IF EXISTS gi2pir");
$dbh->do("CREATE TABLE gi2pir (protgi INT, pirid VARCHAR(15), KEY(protgi,pirid), INDEX(pirid), INDEX(protgi))");
$dbh->do("INSERT IGNORE INTO gi2pir SELECT refseq.protgi, ipc.pirid ".
								  "FROM 	refseq_accession AS refseq, ipc_pir AS ipc, ipc_refseqac AS ipcr ".
								  "WHERE refseq.accession = ipcr.refseqac AND ipc.ipcid = ipcr.ipcid");
print "done.\n";
}

