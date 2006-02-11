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
$syndb = DBI->connect("dbi:mysql:database=$synname:host=localhost", $dbuser, $dbpwd) or die "Error: $dbh->errstr\n";

createOrgNameTable();
createTables();

##################################################################################
sub createOrgNameTable {

$keggh->do("DROP TABLE IF EXISTS org_taxid") or die "Error: $dbh->errstr\n";
$keggh->do("CREATE TABLE org_taxid (org VARCHAR(5), taxid INT, KEY(org), INDEX(org))") or die "Error: $dbh->errstr\n";

system("rm -r kegg");
system("mkdir kegg");

print "Downloading genes.weekly.last.tar.Z...\n";
system('wget ftp://ftp.genome.jp/pub/kegg/tarfiles/genes.weekly.last.tar.Z --directory-prefix=./kegg') == 0 or die "Error: $?\n";
print "done downloading. Uncompressing...";
system('tar xzf ./kegg/genes.weekly.last.tar.Z -C ./kegg/') == 0 or die "Error: $?\n";
print "done.\n";

open (FH, "./kegg/all_species.tab") or die "Could not open ./kegg/all_species.db\n";
open (OUT, "> ./kegg/org_taxid.txt") or die "Could not create ./kegg/org_taxid.txt\n";

$sth = $syndb->prepare_cached("SELECT taxid FROM ncbi_taxid_species WHERE name like ?") or die "Error: $dbh->errstr\n";

$numOrgs = 0;
$numTaxids = 0;
while (<FH>) {
	chomp;
	unless ($_ =~ /^\#/) {
	
		@infos = split(/\t/,$_);
		
		# header of this file is: 
		# 0:Abbr 1:FileName 2:FullName 3:inKEGG 4:Category 5:Annotation 6:Complete 7:Completed_year 8:NCBI 9:EMBL
		# 0:hsa	1:H.sapiens	2:Homo sapiens	3:yes	4:Animals	5:yes	6:no	 7: 8: 9:H_sapiens
		
		$sth->execute($infos[2]) or die "Error: $sth->errstr\n";
		@row = $sth->fetchrow_array;
		$taxid = $row[0];
		
		if($taxid eq ""){
			$sth->execute($infos[8]);
			@row = $sth->fetchrow_array;
			$taxid = $row[0];
		}
		
		if($taxid eq ""){
			$sth->execute($infos[9]);
			
			@row = $sth->fetchrow_array;
			$taxid = $row[0];
		}
		
		if($taxid eq ""){
			print "Did not find taxid for $infos[2], KEGG contains this organism: $infos[3]\n";
		}else{
			$numTaxids++;
		}
		$numOrgs++;
		print OUT "$infos[0]\t$taxid\n";
	}
}


print "Found $numTaxids taxids for $numOrgs organisms.\n";

close(FH);
close(OUT);

$fullFilePath = getcwd()."/kegg/org_taxid.txt";
print "Loading data into org_taxid...\n"; 
$keggh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE org_taxid") or die "Error: $keggh->errstr";
print "...done.\n";
$metah->do("INSERT INTO when_updated VALUES(?, CURRENT_TIMESTAMP())", undef, 'kegg') or die "Error: $dbh->errstr\n";

}

##################################################################################
sub createTables () {

$sth = $keggh->prepare("SELECT org FROM org_taxid") or die "Error: $dbh->errstr\n";
$sth->execute();

system('rm -r ./kegg');
system('mkdir kegg');
$cmd1 = 'wget ftp://ftp.genome.jp/pub/kegg/genomes/';
$cmd2 = '_xrefall.list --directory-prefix=./kegg';

$syndb->do("DROP TABLE IF EXISTS kegg_gi");
$syndb->do("DROP TABLE IF EXISTS kegg_up");
$syndb->do("DROP TABLE IF EXISTS kegg_ncbigeneid");
$syndb->do("CREATE TABLE IF NOT EXISTS kegg_gi (keggid VARCHAR(20), gi INT, KEY(keggid,gi), INDEX(gi),INDEX(keggid))");
$syndb->do("CREATE TABLE IF NOT EXISTS kegg_up (keggid VARCHAR(20), up VARCHAR(11), KEY(keggid,up), INDEX(keggid),INDEX(up))");
$syndb->do("CREATE TABLE IF NOT EXISTS kegg_ncbigeneid (keggid VARCHAR(20), ncbi_geneid VARCHAR(25), KEY(keggid,ncbi_geneid), INDEX(keggid))");

open (KGI, ">./kegg/kegg_gi.txt") or die "Could not create file ./kegg/kegg_gi.txt\n";
open (KUP, ">./kegg/kegg_up.txt") or die "Could not create file ./kegg/kegg_up.txt\n";
open (NGN, ">./kegg/kegg_ncbigeneid.txt") or die "Could not create file ./kegg/kegg_ncbigeneid.txt\n";

$sth1 = $keggh->prepare_cached("SELECT taxid FROM org_taxid  WHERE org = ?");
$sth2 = $syndb->prepare_cached("SELECT taxid FROM refseq_taxid WHERE protgi = ?") or die "Error: $dbh->errstr\n";
$numFoundTaxids = 0;

while ($ref = $sth->fetchrow_hashref()) {
	$orgname = $ref->{'org'};
	$cmd = $cmd1.$orgname.'/'.$orgname.$cmd2;
	
	print "Download: ".$cmd."\n";
	system($cmd) == 0 or die "Error:$?\n";
	print "done downloading.\n";
	
	open (FH, './kegg/'.$orgname.'_xrefall.list') or die "Could not open ./kegg\n.";

	# see if we already found a taxid for this organism
	$sth1->execute($orgname);
	@row = $sth1->fetchrow_array;
	$taxid = $row[0];

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
			
			if($taxid eq ""){
				# see if we can find the taxid using the GI number and refseq_taxid
				$sth2->execute($ncbigi);
				@row = $sth2->fetchrow_array;
				$taxid = $row[0];
				
				if($taxid ne ""){
					# found the taxid
					$keggh->do("REPLACE INTO org_taxid VALUES ($orgname,$taxid)") or die "Error: $keggh->errstr\n";
					$numFoundTaxids++;
				}
			}
		}
		if($ncbigeneid ne ""){
			print NGN "$keggid\t$ncbigeneid\n"; 
		}
		if($upid ne ""){
			print KUP "$keggid\t$upid\n";
		}
	}# inner loop
	print "done reading file ./kegg/${orgname}_xrefall.list.\n";
	close(FH);
}# outer while loop

print "Found $numFoundTaxids taxids using GI numbers and refseq_taxid table.\n";

print "Loading data into kegg_gi...\n";
$fullFilePath = getcwd()."/kegg/kegg_gi.txt";
$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE kegg_gi") or die "Error: $synh->errstr";
print "done.\n";

print "Loading data into kegg_up...\n";
$fullFilePath = getcwd()."/kegg/kegg_up.txt";
$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE kegg_up") or die "Error: $synh->errstr";
print "done.\n";

print "Loading data into kegg_ncbigene...\n";
$fullFilePath = getcwd()."/kegg/kegg_ncbigeneid.txt";
$syndb->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE kegg_ncbigeneid") or die "Error: $synh->errstr";
print "done.\n";

$syndb->disconnect();
$keggh->disconnect();
$metah->disconnect();

$endtime = time;
print "\nKEGG-Synonym elapsed time: ".$endtime - $startime."\n";

}


