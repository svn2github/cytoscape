#!/usr/bin/perl

# Jung Park, Iliana Avila-Campillo
# Downloads txt files from http site, adds rows to tables in the synonyms db

use DBI();
use Cwd;

print "--------------------- update_synonyms_prolinks.pl ------------------\n";

my $pwd = cwd(); # get current directory

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_synonym_prolinks.pl <db user> <db password> <synonyms db name>\n";
 	die;
}

my $dbuser = $ARGV[0];
my $dbpwd = $ARGV[1];
my $dbname = $ARGV[2];

system("rm -r prolinks/syn");
system("mkdir prolinks/syn");

my $dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("USE $dbname");

#findGis();
taxids();

sub findGis {
print "Getting geneIDS_to_GInum.txt and GeneID_Genename.txt...";
system('wget http://mysql5.mbi.ucla.edu/public/reference_files/geneIDS_to_GInum.txt --directory-prefix=prolinks/syn');
system('wget http://mysql5.mbi.ucla.edu/public/reference_files/GeneID_Genename.txt --directory-prefix=prolinks/syn');
print "done\n";

print "start: ".(time())."\n";
print "Creating DB tables...\n";

$dbh->do("CREATE TABLE IF NOT EXISTS prolinks_protgi (prolinksid INT, protgi INT, KEY(prolinksid,protgi),INDEX(prolinksid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS prolinks_gi_genename (protgi INT, genename VARCHAR(20), KEY(protgi,genename), INDEX(protgi))");

print "Loading data into prolinks_gi...\n";
$fullFilePath = getcwd()."/prolinks/syn/geneIDS_to_GInum.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE prolinks_protgi") or die "Error: $synh->errstr";
print "done.\n";

print "Loading data into prolinks_gi_genename...\n";
$fullFilePath = getcwd()."/prolinks/syn/GeneID_Genename.txt";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE prolinks_gi_genename") or die "Error: $synh->errstr";
print "done.\n";
}

sub taxids {

$metah = DBI->connect("dbi:mysql:database=metainfo:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$sth = $metah->prepare_cached("SELECT dbname FROM db_name WHERE db=?") or die "Error: $dbh->errstr";
$sth->execute("prolinks") or die "Error: $dbh->errstr";
@row = $sth->fetchrow_array;
$prolinksname = $row[0];
$plh = DBI->connect("dbi:mysql:database=$prolinksname:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

$sth = $plh->prepare("SHOW TABLES") or die "Error: $plh->errstr\n";
$sth->execute();

$sth2 = $dbh->prepare_cached("SELECT protgi FROM prolinks_protgi WHERE prolinksid = ?") or die "Error: $dbh->errstr\n";
$sth3 = $dbh->prepare_cached("SELECT taxid FROM refseq_taxid WHERE protgi = ?") or die "Error: $dbh->errstr\n";

system("rm prolinks/syn/taxid.txt");
open (OUT, ">prolinks/syn/taxid.txt") or die "Could not create file prolinks/syn/taxid.txt\n";
$numTables = 0;
$numTaxids = 0;

while( @row = $sth->fetchrow_array ){
	
	$table = $row[0];
	
	if($table =~ /species/ or $table =~ /interaction_types/){next;} 
	
	if($table =~ /gn$/ or $table =~ /low$/ or $table =~ /pp$/ or $table =~ /rs$/ or $table =~ /gc$/){ next;}
	
	$numTables++;
	
	$sth4=$plh->prepare( "SELECT gene_id_a FROM $table" );	
	$sth4->execute();
	
	$foundTaxid = 0;
	while(@row = $sth4->fetchrow_array){
		
		$gene = $row[0];
		$sth2->execute($gene);
		@gi = $sth2->fetchrow_array;
		
		if($gi[0] ne ""){
			
			$sth3->execute($gi[0]);
			@taxid = $sth3->fetchrow_array;
			
			if($taxid[0] ne "" and $taxid[0] != 0){
				$foundTaxid = 1;
				$numTaxids++;
				print OUT "$taxid[0]\t$table\n";
				last;
			}
		}
	}#gene
	
	if($foundTaxid == 0){
		print "Did not find a taxid for $table\n";
	}
	
}#table

print "Found $numTaxids taxids for $numTables tables.\n";

$plh->do("DROP TABLE species");
$plh->do("CREATE TABLE species (taxid INT, tablename VARCHAR(100), KEY(taxid,tablename))");
$fullFilePath = $pwd."/prolinks/syn/taxid.txt";
$plh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE species");

}

print "---------------- Leaving update_synonym_prolinks.pl -----------------\n";