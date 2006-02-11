#!/usr/bin/perl

##########################################################################################################
# Authors: Iliana Avila-Campillo
# Last date modified: December 8, 2005 by Iliana
# Files are downloaded from ftp site, into ./go_sql, ready to load into db, creates additional tables
# TODO: Table gi2go (maybe do it in synonyms???)
# TODO: Maybe protecting the * with double quotes will work instead of doing loops.
##########################################################################################################

print "------------------------- update_go.pl --------------------------\n";

use DBI();
use Cwd;

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_go.pl <db user> <db password> <db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];
$dbkind = 'go';

print "Creating database... ";
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("DROP DATABASE IF EXISTS $dbname");
$dbh->do("CREATE DATABASE IF NOT EXISTS $dbname") or die "Error: $dbh->errstr";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";
print "done.\n";

createMySqlTables();
createTermChildrenTable();
createGi2Go();
print "------------------- Leaving update_go.pl --------------------\n";


##############################################################################
# Creates MySql tables from dumps that GO makes available in their FTP site
##############################################################################
sub createMysqlTables (){

print "Downloading GO sql files to go_sql directory... ";
system('rm -r ./go_sql');
system('mkdir go_sql');

# seqdb contains all the GO db information
system("wget ftp://ftp.geneontology.org/pub/go/godatabase/archive/latest/go_*-seqdb-tables.tar.gz --directory-prefix=./go_sql/") == 0 or die "\nError: $?\n";
print "done.\n";

# The following messy mess of LIST loops is my way of getting around the shell not expanding wild cards! -iliana (I tried EVERYTHING, wasted a day!)

system('cd go_sql;ls -1 > list.lst'); #print one entry per line of output
open (LIST, "go_sql/list.lst") or die "Could not open go_sql/list.lst\n";
while ($file = <LIST>) {
	chomp $file;
	if($file =~ /gz$/){
		$directory = "./go_sql/".substr($file,0,-7);
		system("cd go_sql; gunzip < ${file} | tar xvf -");		
	}
}
close(LIST);
print "\n$directory\n";

print "Creating tables...\n";
system("cd ${directory};ls -1 > list.lst"); #print one entry per line of output
open (LIST, "${directory}/list.lst") or die "Could not open go_sql/list.lst\n";
while ($file = <LIST>) {
	chomp $file;
	if($file =~ /sql$/){
		print "\tLoading ${directory}/${file}...";
		system("mysql $dbname < ${directory}/${file} -u $dbuser -p${dbpwd}") == 0 or die "\nError: $?\n";
		print "done.\n";
	}
}
print "done.\n";
close(LIST);

print "Populating tables...\n";
system("rm ${directory}/list.lst");
system("cd ${directory};ls -1 > list.lst"); #print one entry per line of output
open (LIST, "${directory}/list.lst") or die "Could not open go_sql/list.lst\n";
while ($file = <LIST>) {
	chomp $file;
	if($file =~ /txt$/){
		print "\tLoading ${directory}/${file}: ";
		system("mysqlimport -u $dbuser -p${dbpwd} -L $dbname ${directory}/${file}") == 0 or die "\nError: $?\n";
	}
}
close(LIST);
print "done.\n";

}

############ CREATE term_children TABLE ##########################

sub createTermChildrenTable (){


$sth = $dbh->prepare("SELECT id FROM term WHERE term_type = ?") or die "Error: $dbh->errstr";
$sth->execute("universal") or die "Error: $dbh->errstr";

while(@row = $sth->fetchrow_array()){
	$rootID = $row[0]; 
}

print "Root term id = $rootID\n";

# this table contains a term's immediate children
print "Creating term_children table... ";
$dbh->do("CREATE TABLE term_children ( term_id INT, children LONGTEXT)") or die "\nError: $dbh->errstr";
my %hash = ();
insertChildren($rootID,%hash);
print "done.\n";
$dbh->disconnect();

}



#################################################################
# Recursively inserts rows into table term_children, 
# starts with the given id and then inserts rows for the children
#################################################################
sub insertChildren {
 	
 	my ($rootID,%hash) = @_;
 	
 	if( exists $hash{$rootID} ){return;}
        
     my @children = getChildrenIDs($rootID);
     if(scalar(@children) == 0){ return;}
     $childrenList = $children[0];
     for($i = 1; $i < scalar(@children); $i++){
   		$childrenList = $childrenList.",".$children[$i];
     }
     my $sth = $dbh->prepare_cached( "INSERT INTO term_children VALUES (?,?)" ) or die "\nError: $dbh->errstr";
     $sth->execute($rootID,$childrenList) or die "\nError: $dbh->errstr";
     $hash{$rootID}=1;
     
     #recursive call
     foreach $childID (@children){
     	insertChildren($childID,%hash);
     }
     
}


###########################################################
# Returns the children IDs of the given term
###########################################################
sub getChildrenIDs {
        
       my ($termID) = @_;
        my $sth = $dbh->prepare_cached("SELECT child.id  FROM term AS parent, term2term, term AS child ".
        "WHERE parent.id = term2term.term1_id AND parent.id = ? AND child.id  = term2term.term2_id AND parent.id != child.id ".
        "AND child.is_obsolete = 0") or die "\nError: $dbh->errstr\n";
        $sth->execute($termID) or die "\nError: $dbh->errstr\n";
        
        my @children;
        while(@row = $sth->fetchrow_array){
        		push(@children, $row[0]);
        }
        	  
        	return @children;
}

###########################################################
# Creates a gi2go table
###########################################################

sub createGi2Go{
	
	system("rm -r go");
	system("mkdir go");
	
	# download gi2refseq
	print "Downloading gene2refseq.gz...\n";
	system("wget  ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/gene2refseq.gz --directory-prefix=./go/") == 0 or die "\nError: $?\n";
	print "done. Uncompressing...\n";
	system("cd go; gzip -dv gene2refseq.gz");
	print "done.\n";
	
	# download gene2go
	print "Downloading gene2go.gz...\n";
	system("wget  ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/gene2go.gz --directory-prefix=./go/") == 0 or die "\nError: $?\n";
	print "done. Uncompressing...\n";
	system("cd go; gzip -dv gene2go.gz");
	print "done.\n";
	
	# make a table from gi to geneID ids using gene2refseq
	open (IN, "go/gene2refseq") or die "Could not open file go/gene2refseq\n";
	open (OUT, ">go/gi2geneid") or die "Could not create file go/gi2refseq\n";
	
	# columns are:
	# 0:tax_id 1:geneID 2:status 3:RNA accession.version 4:RNA nuc gi 5:protein accession.version 6:protein gi
	# 7:genomic nuc accession.version 8:genomic nuc gi 9:start position 10:end position 11: orientation
	while($line = <IN>){
		chomp $line;
		@cols = split /\s+/, $line;
		$geneID = $cols[1];
		
		if($cols[6] ne '-'){
			# protein gi
			print OUT "$cols[6]\t$geneID\n";
		}
		
		if($cols[4] ne '-'){
			# RNA gi
			print OUT "$cols[4]\t$geneID\n";
		}
		
		if($cols[8] ne '-'){
			# genomic gi
			print OUT "$cols[8]\t$geneID\n";
		}
		
	}#while
	
	close(IN);
	close(OUT);
	
	$dbh->do("CREATE TABLE IF NOT EXISTS gi2geneid (gi INT, geneid INT, INDEX(geneid), KEY(gi,geneid))") or die "Error: $dbh->errstr";
	$file = getcwd."/go/gi2geneid";
	print "Populating table gi2geneid...\n";
	$dbh->do("LOAD DATA LOCAL INFILE \'${file}\' REPLACE INTO TABLE gi2geneid") or die "Error: $dbh->errstr";
	print "done.\n";
	
	open (IN, "go/gene2go") or die "Could not open file go/gene2go\n";
	open (OUT, ">go/gene2goTable") or die "Could not open file go/gene2goTable\n";
	
	# 0:taxid 1:geneID 2:goID 3:evidence 4:go_qualifier 5:go_description 6:pipe separated pumed list
	while($line = <IN>){
		
		chomp($line);
		@cols = split /\s+/, $line;
		
		$taxid = $cols[0];
		$geneID = $cols[1];
		
		$goID = $cols[2];
		$evidence = $cols[3];
		$godesc = $cols[5];
		
		print OUT "$geneID\t$taxid\t$goID\t$evidence\t$godesc\n";
		
	}#while
	
	close(IN);
	close(OUT);
	
	$dbh->do("CREATE TABLE IF NOT EXISTS gene2go ".
	"(geneid INT, taxid INT, goid VARCHAR(255), evidence VARCHAR(3), godesc VARCHAR(100),INDEX(geneid), INDEX(taxid), INDEX(goid), KEY(geneid,goid,evidence))") or die "Error: $dbh->errstr";
	$file = getcwd."/go/gene2goTable";
	print "Populating table gene2go...\n";
	$dbh->do("LOAD DATA LOCAL INFILE \'${file}\' REPLACE INTO TABLE gene2go") or die "Error: $dbh->errstr";
	print "done.\n";
	
	# create gi2go table
	
	print "Creating and populating table gi2go...\n";
	$dbh->do("CREATE TABLE gi2go ".
		"SELECT gi2geneid.gi AS gi, gene2go.taxid AS taxid, gene2go.goid AS goid, gene2go.evidence AS evidence, gene2go.godesc AS godesc ".
		"FROM gi2geneid, gene2go ".
		"WHERE gi2geneid.geneid = gene2go.geneid");
	print "done.\n";
}