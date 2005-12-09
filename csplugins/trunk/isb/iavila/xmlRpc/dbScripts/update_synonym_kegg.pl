#!/usr/bin/perl

##############################################################################
# Authors: Jung Park, Iliana Avila-Campillo
# Last modified: December 9, 2005 by Iliana
# Downloads information from ftp site at KEGG
# Populates xref_gi, xref_kegg, xref_ncbigene in synonyms db
##############################################################################

use DBI();

print "--------------------- update_synonym_kegg.pl ------------------\n";

if(scalar(@ARGV) < 4){
	print "USAGE: perl update_synonym_kegg.pl <db user> <db password> <kegg db name> <synonyms db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$keggname = $ARGV[2];
$synname = $ARGV[3];

$starttime = time;

$metah = DBI->connect("dbi:mysql:database=metainfo:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$keggh = DBI->connect("dbi:mysql:database=$keggname:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

$keggh->do("DROP TABLE IF EXISTS org_name") or die "Error: $dbh->errstr\n";
$keggh->do("CREATE TABLE org_name (org VARCHAR(5), filename VARCHAR(100), fullname VARCHAR(100), NCBI_name VARCHAR(100), EMBL_name VARCHAR(100))") or die "Error: $dbh->errstr\n";

print "Downloading genes.weekly.last.tar.Z...\n";
system('wget ftp://ftp.genome.jp/pub/kegg/tarfiles/genes.weekly.last.tar.Z --directory-prefix=./xref/kegg/genes') == 0 or die "Error: $?\n";
print "done downloading. Uncompressing...";
system('tar xzf ./xref/kegg/genes/genes.weekly.last.tar.Z -C ./xref/kegg/genes/') == 0 or die "Error: $?\n";
print "done.\n";
open (FH, "./xref/kegg/genes/all_species.tab") or die "Could not open ./xref/kegg/genes/all_species.db\n";

while (<FH>) {
	chomp;
	unless ($_ =~ /^\#/) {
		@infos = split(/\t/,$_);
		$keggh->do("INSERT INTO org_name VALUES(?, ?, ?, ?, ?)", undef, $infos[0], $infos[1], $infos[2], $infos[8], $infos[9]) or die "Error: $dbh->errstr\n";
	}
}

$metah->do("INSERT INTO update_log VALUES(?, CURRENT_TIMESTAMP(), ?)", undef, 'kegg', 'organism name abbreviation mapping information is imported.') or die "Error: $dbh->errstr\n";

close(FH);

$sth = $keggh->prepare("SELECT org FROM org_name") or die "Error: $dbh->errstr\n";
$sth->execute();

system('rm -r ./xref/kegg');
system('mkdir ./xref/kegg');
$cmd1 = 'wget ftp://ftp.genome.jp/pub/kegg/genomes/';
$cmd2 = '_xrefall.list -q --directory-prefix=./xref/kegg';

$syndb = DBI->connect("dbi:mysql:database=$synname:host=localhost", $dbuser, $dbpwd) or die "Error: $dbh->errstr\n";

$syndb->do("DELETE FROM xref_kegg") or die "Error: $dbh->errstr\n";
$syndb->do("DELETE FROM xref_gi") or die "Error: $dbh->errstr\n";
$syndb->do("DELETE FROM xref_ncbigeneid") or die "Error: $dbh->errstr\n";

$sprotoidret = $syndb->prepare("SELECT oid FROM prot_sprot WHERE sprotac = ?") or die "Error: $dbh->errstr\n";
$trembloidret = $syndb->prepare("SELECT oid FROM prot_trembl WHERE tremblac = ?") or die "Error: $dbh->errstr\n";

while ($ref = $sth->fetchrow_hashref()) {
	$orgname = $ref->{'org'};
	$cmd = $cmd1.$orgname.'/'.$orgname.$cmd2.' -q';
	print "Download: ".$cmd."\n";
	system($cmd) == 0 or die "Error:$?\n";
	print "done.\n";
	
	open (FH, './xref/kegg/'.$orgname.'_xrefall.list') or die "Could not open ./xref/kegg\n.";

	print "Loading ./xref/kegg/${orgname}_xrefall.list... ";
	while (<FH>) {
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

		if ($upid ne '') {
			$sprotoidret->execute($upid) or die "Error: $dbh->errstr\n";
			while ($gotoid = $sprotoidret->fetchrow_hashref()) {
				$syndb->do("INSERT IGNORE INTO keydb VALUES(?, ?, ?)", undef, $gotoid->{'oid'}, 'kegg', $keggid) or die "Error: $dbh->errstr\n";
				$syndb->do("INSERT IGNORE INTO hasit VALUES(?, ?)", undef, $gotoid->{'oid'}, 'xref_kegg') or die "Error: $dbh->errstr\n";
				$syndb->do("INSERT IGNORE INTO xref_kegg VALUES(?, ?)", undef, $gotoid->{'oid'}, $keggid) or die "Error: $dbh->errstr\n";
				if ($ncbigi ne '') {
					$syndb->do("INSERT IGNORE INTO hasit VALUES(?, ?)", undef, $gotoid->{'oid'}, 'xref_ncbigi') or die "Error: $dbh->errstr\n";
					$syndb->do("INSERT IGNORE INTO xref_gi VALUES(?, ?)", undef, $gotoid->{'oid'}, $ncbigi) if ($ncbigi ne '') or die "Error: $dbh->errstr\n";
				}
				if ($ncbigeneid ne '') {
					$syndb->do("INSERT IGNORE INTO hasit VALUES(?, ?)", undef, $gotoid->{'oid'}, 'xref_ncbigeneid') or die "Error: $dbh->errstr\n";
					$syndb->do("INSERT IGNORE INTO xref_ncbigeneid VALUES(?, ?)", undef, $gotoid->{'oid'}, $ncbigeneid) if ($ncbigeneid ne '') or die "Error: $dbh->errstr\n";
				}
			}
			$trembloidret->execute($upid) or die "Error: $dbh->errstr\n";
			while ($gotoid = $trembloidret->fetchrow_hashref()) {
				$syndb->do("INSERT IGNORE INTO keydb VALUES(?, ?, ?)", undef, $gotoid->{'oid'}, 'kegg', $keggid) or die "Error: $dbh->errstr\n";
				$syndb->do("INSERT IGNORE INTO hasit VALUES(?, ?)", undef, $gotoid->{'oid'}, 'xref_kegg') or die "Error: $dbh->errstr\n";
				$syndb->do("INSERT IGNORE INTO xref_kegg VALUES(?, ?)", undef, $gotoid->{'oid'}, $keggid) or die "Error: $dbh->errstr\n";
				if ($ncbigi ne '') {
					$syndb->do("INSERT IGNORE INTO hasit VALUES(?, ?)", undef, $gotoid->{'oid'}, 'xref_ncbigi') or die "Error: $dbh->errstr\n";
					$syndb->do("INSERT IGNORE INTO xref_gi VALUES(?, ?)", undef, $gotoid->{'oid'}, $ncbigi) if ($ncbigi ne '') or die "Error: $dbh->errstr\n";
				}
				if ($ncbigeneid ne '') {
					$syndb->do("INSERT IGNORE INTO hasit VALUES(?, ?)", undef, $gotoid->{'oid'}, 'xref_ncbigeneid') or die "Error: $dbh->errstr\n";
					$syndb->do("INSERT IGNORE INTO xref_ncbigeneid VALUES(?, ?)", undef, $gotoid->{'oid'}, $ncbigeneid) if ($ncbigeneid ne '') or die "Error: $dbh->errstr\n";
				}
			}
		}
	}
	print "done.\n";
	close(FH);
}

$syndb->disconnect();
$keggh->disconnect();
$metah->disconnect();

$endtime = time;

print "\nKEGG-Synonym elapsed time: ".$endtime - $startime."\n";
