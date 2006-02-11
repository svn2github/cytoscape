#!/usr/bin/perl

use DBI();
use Cwd;

if(scalar @ARGV < 3){
	print "USAGE: perl update_synonyms_uniprot.pl <dbuser> <dbpassword> <dbname> optional: <update>\n";
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

# Prepare database connection
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

# create a gi to uniprot ids table
$dbh->do("DROP TABLE IF EXISTS gi2trembl");
$dbh->do("DROP TABLE IF EXISTS gi2sprot");
$dbh->do("CREATE TABLE gi2trembl (protgi INT, tremblac VARCHAR(6), KEY (protgi,tremblac), INDEX(protgi))");
$dbh->do("CREATE TABLE gi2sprot (protgi INT, sprotac VARCHAR(6), KEY (protgi,sprotac), INDEX(protgi))");

print "Populating gi2tremblac...";
$dbh->do("INSERT IGNORE INTO gi2trembl SELECT refseq.protgi, ipct.tremblac FROM refseq_accession AS refseq, ipc_refseqac AS ipc, ipc_trembl AS ipct".
" WHERE refseq.accession = ipc.refseqac AND ipc.ipcid = ipct.ipcid");
print "done.\n";

print "Populating gi2sprot...";
$dbh->do("INSERT IGNORE INTO gi2sprot SELECT refseq.protgi, ipcs.sprotac FROM refseq_accession AS refseq, ipc_refseqac AS ipc, ipc_sprot AS ipcs".
" WHERE refseq.accession = ipc.refseqac AND ipc.ipcid = ipcs.ipcid");
print "done.\n";