#!/usr/bin/perl

############################################################################
# Authors: Junghwan Park, Iliana Avila-Campillo
# Last modified: December 7, 2005 by Iliana
# Data is downloaded from ftp site into ./kgml directory
# Requires Perl module XML::Simple, download and install from CPAN.
############################################################################
use XML::Simple;
use DBI();
use Cwd;

print "------------------- update_kegg.pl --------------------\n";
if(scalar(@ARGV) < 3){
	print "USAGE perl update_kegg.pl <db name> <db user> <db pwd>\n";
	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

$dbid = "kegg";

# download current KEGG KGML
system('rm -r kgml');
system('mkdir kgml');
print "Getting kgml.tar.gz... ";
system('wget ftp://ftp.genome.jp/pub/kegg/xml/kgml.tar.gz --directory-prefix=kgml') == 0 or die "$?\n";
print "done\n";
print "Uncompressing... ";
system('cd kgml;gunzip < kgml.tar.gz | tar xvf -') == 0 or die "$?\n";
print "done\n";
system('cd kgml;rm list.lst');
system('cd kgml;ls -R1 > list.lst'); # recursive and print one entry per line of output

open (LIST, "kgml/list.lst") or die "Could not open kgml/list.lst\n";

$| = 1;

my @filename = ();
print "Storing xml file names into array...\n";
while (<LIST>) {
	chomp;

	if ($_ =~ /^\.\/([A-Za-z0-9]*)\:/) {
		$org = $1; #directory name
	}
	
	if ($_ =~ /([A-Za-z0-9]+)\.xml$/) {
		# xml file
		$filename = './'.$org.'/'.$1.'.xml';
		#print "\t$filename\n";
		push(@filename, $filename);
	}
}

print "done\n";

print "Creating database and tables...";

$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("DROP DATABASE IF EXISTS $dbname");
$dbh->do("CREATE DATABASE $dbname") or die "Error: $dbh->errstr";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";


# org_name table is created and populated in update_synonyms_kegg
# TODO: Move it to here?
#$dbh->do("CREATE TABLE org_name 	(org VARCHAR(5), 	name VARCHAR(100), 	UNIQUE(org, 	name))") or die "Error: $dbh->errstr";

$dbh->do("CREATE TABLE path_name 	(path VARCHAR(20),	name VARCHAR(100), 	UNIQUE(path, 	name))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE gene_name 	(gene VARCHAR(20), 	name VARCHAR(20),	UNIQUE(gene, 	name))") or die "Error: $dbh->errstr";

$dbh->do("CREATE TABLE xml_obj 		(id VARCHAR(20), 	type VARCHAR(20), 	UNIQUE(id,	type))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE org_gene 	(org VARCHAR(5), 	gene VARCHAR(20),	UNIQUE(org,	gene))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE org_path 	(org VARCHAR(5), 	path VARCHAR(20),	UNIQUE(org,	path))") or die "Error: $dbh->errstr";

$dbh->do("CREATE TABLE path_gene 	(path VARCHAR(20), 	gene VARCHAR(20),	UNIQUE(path,	gene))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE path_cpd 	(path VARCHAR(20), 	cpd VARCHAR(20),	UNIQUE(path,	cpd))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE path_enz 	(path VARCHAR(20), 	enz VARCHAR(20),	UNIQUE(path,	enz))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE path_path 	(path1 VARCHAR(20), 	path2 VARCHAR(20),	UNIQUE(path1,	path2))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE path_rel 	(path VARCHAR(20), 	entry1 VARCHAR(20), entry2 VARCHAR(20), UNIQUE(path,entry1,entry2))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE path_rxn 	(path VARCHAR(20), 	rxn VARCHAR(20),	UNIQUE(path,	rxn))") or die "Error: $dbh->errstr";

$dbh->do("CREATE TABLE gene_rxn (gene VARCHAR(20), rxn VARCHAR(20), UNIQUE(gene, rxn))") or die "Error: $dbh->errstr";
$dbh->do("CREATE TABLE gene_map (gene VARCHAR(20), map VARCHAR(20), UNIQUE(gene, map))") or die "Error: $dbh->errstr";

$dbh->do("CREATE TABLE enz_map (enz VARCHAR(20), map VARCHAR(20), UNIQUE(enz, map))") or die "Error: $dbh->errstr";

$dbh->do("CREATE TABLE rxn_cpd (rxn VARCHAR(20), cpd VARCHAR(20), UNIQUE(rxn, cpd))") or die "Error: $dbh->errstr";


print "done\n";

$total = $#filename+1;
$cur = 0;
my @sqls = ();
print "KEGG KGML file DB Importing Progress:\n";
$ttt = 100;

$starttime = time;

foreach $filename (@filename) {
	$nowtime = time;	
	if ($cur == 0) {
		$totaltime = 0;
	} else {
		$totaltime = $total * ($nowtime-$starttime)/$cur;
	}
	$lefttime = $totaltime - ($nowtime - $starttime);
	
	$totalhr  = int($totaltime/3600);
	$totalmin = int(($totaltime-$totalhr*3600)/60);
	$totalsec = int($totaltime-$totalhr*3600-$totalmin*60);

	$lefthr   = int($lefttime/3600);
	$leftmin  = int(($lefttime-$lefthr*3600)/60);
	$leftsec  = int($lefttime-$lefthr*3600-$leftmin*60);
	
	printf("\r$filename - progress: %5.2f\%, total: %2d:%2d:%2d, left: %2d:%2d:%2d",($cur*100.0/$total), $totalhr, $totalmin, $totalsec, $lefthr, $leftmin, $leftsec);
	my $file = 'kgml/'.$filename;
	$filename =~ /\.\/([a-z]+)\/([a-z0-9]+)\.xml/;
	
	my $xs1 = XML::Simple->new();
	my $doc = $xs1->XMLin($file);

	$orgname = $doc->{'org'};
	$pathname = $doc->{'name'};

	$dbh->do("INSERT IGNORE INTO org_path VALUES(?, ?)", undef, $orgname, $pathname) or die "Error: $dbh->errstr";
	
	$entry = $doc->{'entry'};
	
	foreach $entry_name (keys(%$entry)) {
		$dbh->do("INSERT IGNORE INTO xml_obj VALUES(?, ?)", undef, $pathname.":".$entry->{$entry_name}->{"id"}, $entry->{$entry_name}->{"type"}) or die "Error: $dbh->errstr";
		
		if ($entry->{$entry_name}->{"type"} eq "gene") {
			$genenames = $entry_name;
			@genenames = split(/\s/, $genenames);

			foreach $genename (@genenames) {
				$dbh->do("INSERT IGNORE INTO org_gene VALUES(?, ?)", undef, $orgname, $genename) or die "Error: $dbh->errstr";
				$dbh->do("INSERT IGNORE INTO path_gene VALUES(?, ?)", undef, $pathname, $genename) or die "Error: $dbh->errstr";
				
				if (exists($entry->{$entry_name}->{"map"})) {
					$dbh->do("INSERT IGNORE INTO gene_map VALUES(?, ?)", undef, $genename, $pathname.":".$entry->{$entry_name}->{"map"}) or die "Error: $dbh->errstr";
				} else {
					@cgenenames = split(/,\s+/,$entry->{$entry_name}->{"graphics"}->{"name"});
					foreach $cgenename (@cgenenames) {
						$dbh->do("INSERT IGNORE INTO gene_name VALUES(?, ?)", undef, $genename, $cgenename) or die "Error: $dbh->errstr";
					}
					@rxnnames = split(/\s+/,$entry->{$entry_name}->{"reaction"});
					foreach $rxnname (@rxnnames) {
						$dbh->do("INSERT IGNORE INTO gene_rxn VALUES(?, ?)", undef, $genename, $rxnname) or die "Error: $dbh->errstr";
					}
				}
			}
		}
		if ($entry->{$entry_name}->{"type"} eq "compound") {
			$dbh->do("INSERT IGNORE INTO path_cpd VALUES(?, ?)", undef, $pathname, $entry_name) or die "Error: $dbh->errstr";
		}
		if ($entry->{$entry_name}->{"type"} eq "enzyme") {
			$dbh->do("INSERT IGNORE INTO path_enz VALUES(?, ?)", undef, $pathname, $entry_name) or die "Error: $dbh->errstr";

			if (exists($entry->{$entry_name}->{"map"})) {
				$dbh->do("INSERT IGNORE INTO enz_map VALUES(?, ?)", undef, $entry_name, $pathname.":".$entry->{$entry_name}->{"map"}) or die "Error: $dbh->errstr";
			}
		}
		if ($entry->{$entry_name}->{"type"} eq "map") {
			$dbh->do("INSERT IGNORE INTO path_path VALUES(?, ?)", undef, $pathname, $entry_name) or die "Error: $dbh->errstr"; 
			
			$fullname = $entry->{$entry_name}->{"graphics"}->{"name"};
			if ($fullname =~ /^TITLE\:([A-Za-z0-9\s\:_-]+)/) {
				$fullname = $1;
			}
			$dbh->do("INSERT IGNORE INTO path_name VALUES(?, ?)", undef, $entry_name, $fullname) or die "Error: $dbh->errstr";
		}
	}
	
	$rxns = $doc->{"reaction"};

	foreach $rxnid (keys(%$rxns)) {
		$dbh->do("INSERT IGNORE INTO path_rxn VALUES(?, ?)", undef, $pathname, $rxnid) or die "Error: $dbh->errstr";
		
		$rxn = $rxns->{$rxnid};
		if (exists($rxn->{"product"})) {
			unless (exists($rxn->{"product"}->{'name'})) {
				foreach $product (keys(%{$rxn->{"product"}})) {
					$dbh->do("INSERT IGNORE INTO rxn_cpd VALUES(?, ?)", undef, $rxnid, $product) or die "Error: $dbh->errstr";
				}
			} else {
				$dbh->do("INSERT IGNORE INTO rxn_cpd VALUES(?, ?)", undef, $rxnid, $rxns->{$rxnid}->{"product"}->{"name"}) or die "Error: $dbh->errstr";
			}
		}

		if (exists($rxn->{"substrate"})) {
			unless (exists($rxn->{"substrate"}->{'name'})) {
				foreach $substrate (keys(%{$rxn->{"substrate"}})) {
					$dbh->do("INSERT IGNORE INTO rxn_cpd VALUES(?, ?)", undef, $rxnid, $substrate) or die "Error: $dbh->errstr";
				}
			} else {
				$dbh->do("INSERT IGNORE INTO rxn_cpd VALUES(?, ?)", undef, $rxnid, $rxns->{$rxnid}->{"substrate"}->{"name"}) or die "Error: $dbh->errstr";
			}
		}
	}

	@rels = $doc->{"relation"};

	foreach $rel (@rels) {
		if (ref($rel) eq 'HASH') {
			$entry1 = $entry2 = $subtype = $value = $type = "";
			$entry1 = $rel2->{"entry1"};
			$entry2 = $rel2->{"entry2"};
			$subtype = $rel2->{"subtype"};
			$value = $pathname.":".$subtype->{"value"};
			$type = $rel2->{"type"} if (exists $rel2->{"type"});
			$dbh->do("INSERT IGNORE INTO path_rel VALUES(?, ?, ?)", undef, $pathname, $entry1, $entry2) or die "Error: $dbh->errstr";
			$dbh->do("INSERT IGNORE INTO path_rel VALUES(?, ?, ?)", undef, $pathname, $entry2, $entry1) or die "Error: $dbh->errstr";
		} else {
			@rels2 = @$rel;
			foreach $rel2 (@rels2) {
				$entry1 = $entry2 = $subtype = $value = $type = "";
				$entry1 = $rel2->{"entry1"};
				$entry2 = $rel2->{"entry2"};
				$subtype = $rel2->{"subtype"};
				$value = $pathname.":".$subtype->{"value"};
				$type = $rel2->{"type"} if (exists $rel2->{"type"});
				$dbh->do("INSERT IGNORE INTO path_rel VALUES(?, ?, ?)", undef, $pathname, $entry1, $entry2) or die "Error: $dbh->errstr";
				$dbh->do("INSERT IGNORE INTO path_rel VALUES(?, ?, ?)", undef, $pathname, $entry2, $entry1) or die "Error: $dbh->errstr";
			}
		}
	}
	$cur++;
}

$starttime = time;
print "CREATE TABLE gene_cpd\n";
$dbh->do("CREATE TABLE gene_cpd (gene VARCHAR(20), cpd VARCHAR(20), UNIQUE (gene, cpd))") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "INSERT INTO gene_cpd\n";
$dbh->do("INSERT IGNORE INTO gene_cpd select gene_rxn.gene, rxn_cpd.cpd from gene_rxn, rxn_cpd where gene_rxn.rxn = rxn_cpd.rxn") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "Add org col\n";
$dbh->do("ALTER TABLE gene_cpd ADD org VARCHAR(5)") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "Update org col\n";
$dbh->do("UPDATE gene_cpd, org_gene SET gene_cpd.org = org_gene.org WHERE gene_cpd.gene = org_gene.gene") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "Create Table cpd_score\n";
$dbh->do("CREATE TABLE cpd_score SELECT cpd, count(*) AS score FROM gene_cpd GROUP BY cpd") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "add index gene\n";
$dbh->do("ALTER TABLE gene_cpd ADD INDEX (gene)") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "add index cpd\n";
$dbh->do("ALTER TABLE gene_cpd ADD INDEX (cpd)") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "add index gene, cpd, org\n";
$dbh->do("ALTER TABLE gene_cpd ADD INDEX (gene, cpd, org)") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "add index cpd\n";
$dbh->do("ALTER TABLE cpd_score ADD INDEX (cpd)") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

# gene_cpd_gene_score contains "interactions" between genes that share a compound and are of the same organism
# the score reflects the total number of genes (regardless of organism) that are connected to the compound
$starttime = time;
print "create table gene_cpd_gene_score\n";
$dbh->do("CREATE TABLE gene_cpd_gene_score SELECT g1.gene AS gene1, g1.cpd AS cpd, g2.gene AS gene2, cs.score AS score, g1.org AS org FROM gene_cpd as g1, gene_cpd AS g2, cpd_score AS cs WHERE g1.cpd = g2.cpd AND g1.org = g2.org AND g1.gene != g2.gene AND cs.cpd = g1.cpd") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "add index gene1, gene2\n";
$dbh->do("ALTER TABLE gene_cpd_gene_score ADD INDEX (gene1, gene2)") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

# gene-gene interactions with a minimum score obtained from the minimum score of all compounds that mediate the "interaction"
# this could be used if all we are interested in is connecting genes in a network, and we don't care of the specific compounds that mediate the interactions
# using this table to create interactions minimizes multiple edges between nodes that share many compounds
$starttime = time;
print "create table gene_gene_score\n";
$dbh->do("CREATE TABLE gene_gene_score SELECT gcgs.gene1 AS gene1, gcgs.gene2 AS gene2, MIN(gcgs.score) as score, gcgs.org as org FROM gene_cpd_gene_score AS gcgs GROUP BY gcgs.gene1, gcgs.gene2") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";

$starttime = time;
print "create table cpd_name\n";
$dbh->do("CREATE TABLE cpd_name (cpd VARCHAR(20), name VARCHAR(50), UNIQUE(cpd,name))") or die "Error: $dbh->errstr";
print "Getting COMPOUND...";
system('wget ftp://ftp.genome.jp/pub/kegg/ligand/compound --directory-prefix=kgml') == 0 or die "$?\n";
print "done.\n";
open (COMPOUND, "kgml/compound") or die "Could not open kgml/compound\n";
open (CP_OUT, ">kgml/compound_name.txt") or die "Could not create file kgml/compound_name.txt\n";
while($line = <COMPOUND>){
	if($line =~ /^ENTRY/){
		@fields = split(/\s+/,$line);
		print CP_OUT "cpd:"."$fields[1]\t";
	}elsif ($line =~ /^NAME/){
		@fields = split(/\s+/,$line);
		chop($fields[1]); # take out the ";"
		print CP_OUT "$fields[1]\n";
	}
}
$fullFilePath = getcwd()."/kgml/compound_name.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE cpd_name") or die "Error: $dbh->errstr";
$nowtime = time;
print "time elapsed: ".($nowtime-$starttime)."\n";
close(CP_OUT);
close(COMPOUND);
$dbh->disconnect();

print "\n--------------------- Leaving update_kegg.pl ----------------------\n";

