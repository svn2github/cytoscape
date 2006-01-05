#!/usr/bin/perl

##############################################################################
# Authors: Iliana Avila-Campillo, Jung Park
# Last modified: December 9, 2005 by Iliana
# Downloads information from ftp site at KEGG
##############################################################################

use DBI();
use Cwd;

print "--------------------- update_synonym_kegg.pl ------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_synonym_kegg.pl <db user> <db password> <synonyms db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$synname = $ARGV[2];

$starttime = time;

$metah = DBI->connect("dbi:mysql:database=metainfo:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$sth = $metah->prepare_cached("SELECT dbname FROM db_name WHERE db=?") or die "Error: $dbh->errstr";
$sth->execute("kegg") or die "Error: $dbh->errstr";
@row = $sth->fetchrow_array;
$keggname = $row[0];
$keggh = DBI->connect("dbi:mysql:database=$keggname:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

$keggh->do("DROP TABLE IF EXISTS org_name") or die "Error: $dbh->errstr\n";
$keggh->do("CREATE TABLE org_name (org VARCHAR(5), filename VARCHAR(100), fullname VARCHAR(100), NCBI_name VARCHAR(100), EMBL_name VARCHAR(100))") or die "Error: $dbh->errstr\n";

system(rm -r kegg);
system(mkdir kegg);

print "Downloading genes.weekly.last.tar.Z...\n";
system('wget ftp://ftp.genome.jp/pub/kegg/tarfiles/genes.weekly.last.tar.Z --directory-prefix=./kegg') == 0 or die "Error: $?\n";
print "done downloading. Uncompressing...";
system('tar xzf ./kegg/genes.weekly.last.tar.Z -C ./kegg/') == 0 or die "Error: $?\n";
print "done.\n";

open (FH, "./kegg/all_species.tab") or die "Could not open ./kegg/all_species.db\n";
open (OUT, "> ./kegg/org_name.txt") or die "Could not create ./kegg/org_name.txt\n";

while (<FH>) {
	chomp;
	unless ($_ =~ /^\#/) {
		@infos = split(/\t/,$_);
		print OUT "$infos[0]\t$infos[1]\t$infos[2]\t$infos[8]\t$infos[9]\n";
	}
}

$fullFilePath = getcwd()."/kegg/org_name.txt"; 
$keggh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE org_name") or die "Error: $keggh->errstr";

close(FH);
close(OUT);

$metah->do("INSERT INTO when_updated VALUES(?, CURRENT_TIMESTAMP())", undef, 'kegg') or die "Error: $dbh->errstr\n";

$sth = $keggh->prepare("SELECT org FROM org_name") or die "Error: $dbh->errstr\n";
$sth->execute();

system('rm -r ./kegg');
system('mkdir kegg');
$cmd1 = 'wget ftp://ftp.genome.jp/pub/kegg/genomes/';
$cmd2 = '_xrefall.list --directory-prefix=./kegg';

$syndb = DBI->connect("dbi:mysql:database=$synname:host=localhost", $dbuser, $dbpwd) or die "Error: $dbh->errstr\n";

###### OLD ##################################################################
#$syndb->do("DELETE FROM xref_kegg") or die "Error: $dbh->errstr\n";
#$syndb->do("DELETE FROM xref_gi") or die "Error: $dbh->errstr\n";
#$syndb->do("DELETE FROM xref_ncbigeneid") or die "Error: $dbh->errstr\n";
#############################################################################

$syndb->do("CREATE TABLE IF NOT EXISTS kegg_gi (uid INT, keggid VARCHAR(20), gi INT, INDEX(uid), INDEX(gi))");
$syndb->do("CREATE TABLE IF NOT EXISTS kegg_up (uid INT, keggid VARCHAR(20), up VARCHAR(11), INDEX(uid), INDEX(up))");
$syndb->do("CREATE TABLE IF NOT EXISTS kegg_ncbigeneid (uid INT, keggid VARCHAR(20), ncbi_geneid VARCHAR(25), INDEX(uid), INDEX(keggid))");

######## MATCH uids LATER!!!!! #################################
#$sprotoidret = $syndb->prepare_cached("SELECT oid FROM prot_sprot WHERE sprotac = ?") or die "Error: $dbh->errstr\n";
#$trembloidret = $syndb->prepare_cached("SELECT oid FROM prot_trembl WHERE tremblac = ?") or die "Error: $dbh->errstr\n";
################################################################

open (KGI, ">./kegg/kegg_gi.txt") or die "Could not create file ./kegg/kegg_gi.txt\n";
open (KUP, ">./kegg/kegg_up.txt") or die "Could not create file ./kegg/kegg_up.txt\n";
open (NGN, ">./kegg/kegg_ncbigeneid.txt") or die "Could not create file ./kegg/kegg_ncbigeneid.txt\n";

######### OLD #######################################################################################
#open(KDB, "> ./xref/kegg/keydb.txt") or die "Could not create file ./xref/kegg/keydb.txt\n";
#open(HDB, "> ./xref/kegg/hasit.txt") or die "Could not create file ./xref/kegg/hasit.txt\n";
#open(XKDB, "> ./xref/kegg/xkegg.txt") or die "Could not create file ./xref/kegg/xkegg.txt\n";
#open(XGDB, "> ./xref/kegg/xgi.txt") or die "Could not create file ./xref/kegg/xgi.txt\n";
#open(XNDB, "> ./xref/kegg/xncbi.txt") or die "Could not create file ./xref/kegg/xncbi.txt\n";
#####################################################################################################

while ($ref = $sth->fetchrow_hashref()) {
	$orgname = $ref->{'org'};
	$cmd = $cmd1.$orgname.'/'.$orgname.$cmd2;
	
	print "Download: ".$cmd."\n";
	system($cmd) == 0 or die "Error:$?\n";
	print "done downloading.\n";
	
	open (FH, './kegg/'.$orgname.'_xrefall.list') or die "Could not open ./kegg\n.";

	print "Loading ./kegg/${orgname}_xrefall.list...\n";
	$line = 0;
	while (<FH>) {
		$line++;
		if($line % 100 == 0) {
			print "Alive $line.\n";
		}
	
		chomp;
		@ids = split(/\t/, $_);
		$keggid = $ids[0];
		
		$ids[1] =~ /ncbi\-gi\:(.+)$/;
		$ncbigi = $1;
		
		$ids[2] =~ /ncbi\-geneid\:(.+)$/;
		$ncbigeneid = $1;
		
		$ids[3] =~ /up\:(.+)$/;
		$upid = $1;
		
		$ecnum = $ids[4];

		$koid = $ids[5];
		
		if($ncbigi ne ""){
			print KGI "$keggid\t$ncbigi\n";
		}
		if($ncbigeneid ne ""){
			print NGN "$keggid\t$ncbigeneid\n"; 
		}
		if($upid ne ""){
			print KUP "$keggid\t$upid\n";
		}


		########################## OLD SLOW CODE ##########################################
		#if ($upid ne '') {
		#	$sprotoidret->execute($upid) or die "Error: $dbh->errstr\n";
		#	while ($gotoid = $sprotoidret->fetchrow_hashref()) {
		#		
		#		print KDB "$gotoid->{'oid'}\tkegg\t$keggid";
		#		print HDB "$gotoid->{'oid'}\txref_kegg";
		#		print XKDB "$gotoid->{'oid'}\t$keggid\n";
		#		
		#		if ($ncbigi ne '') {
		#			print HDB "$gotoid->{'oid'}\txref_ncbi\n";
		#			print XGDB "$gotoid->{'oid'}\t$ncbigi\t\t\n";
		#		}
		#		if ($ncbigeneid ne '') {
		#			print HDB "$gotoid->{'oid'}\txref_ncbigeneid\n";
		#			print XNDB "$gotoid->{'oid'}\t$ncbigeneid\n";
		#		}
		#	}
		#	
		#	
		#	$trembloidret->execute($upid) or die "Error: $dbh->errstr\n";
		#	while ($gotoid = $trembloidret->fetchrow_hashref()) {
		#		print KDB "$gotoid->{'oid'}\tkegg\t$keggid";
		#		print HDB "$gotoid->{'oid'}\txref_kegg";
		#		print XKDB "$gotoid->{'oid'}\t$keggid\n";
		#		
		#		if ($ncbigi ne '') {
		#			
		#			print HDB "$gotoid->{'oid'}\txref_ncbi\n";
		#			print XGDB "$gotoid->{'oid'}\t$ncbigi\t\t\n";
		#		}
		#		if ($ncbigeneid ne '') {
		#			print HDB "$gotoid->{'oid'}\txref_ncbigeneid\n";
		#			print XNDB "$gotoid->{'oid'}\t$ncbigeneid\n";
		#		}
		#	}
		#	
		#	}
		#}
		#################################################################################
	}# inner loop
	print "done reading file ./kegg/${orgname}_xrefall.list.\n";
	close(FH);
}# outer while loop

print "Loading data into kegg_gi...\n";
$fullFilePath = getcwd()."/kegg/kegg_gi.txt";
$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE kegg_gi (keggid, gi)") or die "Error: $synh->errstr";
print "done.\n";

print "Loading data into kegg_up...\n";
$fullFilePath = getcwd()."/kegg/kegg_up.txt";
$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE kegg_up (keggid, up)") or die "Error: $synh->errstr";
print "done.\n";

print "Loading data into kegg_ncbigene...\n";
$fullFilePath = getcwd()."/kegg/kegg_ncbigeneid.txt";
$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' REPLACE INTO TABLE kegg_ncbigeneid (keggid, ncbi_geneid)") or die "Error: $synh->errstr";
print "done.\n";


################################ OLD CODE ###################################
#print "Loading data into keydb...\n";
#$fullFilePath = getcwd()."/xref/kegg/keydb.txt";
#$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE keydb") or die "Error: $synh->errstr";
#print "done.\n";
#
#print "Loading data into hasit...\n";
#$fullFilePath = getcwd()."/xref/kegg/hasit.txt";
#$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE hasit") or die "Error: $synh->errstr";
#print "done.\n";
#
#print "Loading data into xref_kegg...\n";
#$fullFilePath = getcwd()."/xref/kegg/xkegg.txt";
#$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE xref_kegg") or die "Error: $synh->errstr";
#print "done.\n";
#
#print "Loading data into xreg_gi..\n";
#$fullFilePath = getcwd()."/xref/kegg/xgi.txt";
#$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE xref_gi") or die "Error: $synh->errstr";
#print "done.\n";
#
#print "Loading data into xref_ncbi...\n";
#$fullFilePath = getcwd()."/xref/kegg/xncbi.txt";
#$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE xref_ncbi") or die "Error: $synh->errstr";
#print "done.\n";
#
#close(KDB);
#close(HDB);
#close(XKDB);
#close(XGDB);
#close(XNDB);
###############################################################################

$syndb->disconnect();
$keggh->disconnect();
$metah->disconnect();

$endtime = time;

print "\nKEGG-Synonym elapsed time: ".$endtime - $startime."\n";
