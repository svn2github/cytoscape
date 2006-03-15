#!/usr/bin/perl

use IO::File;
use Net::FTP;
use DBI();
use Cwd;

if(scalar @ARGV < 3){
	print "USAGE: perl update_synonyms_genbank.pl <dbuser> <dbpassword> <dbname> optional: <update>\n";
}

my $dbuser = $ARGV[0];
my $dbpwd = $ARGV[1];
my $dbname = $ARGV[2];

system("rm -r bind");
system("mkdir bind");

my $taxonFile, $refsFile, $intsFile;
my %taxons = ();
my %pmids = (), %methods = ();

my $dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE DATABASE IF NOT EXISTS $dbname");
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

downloadFiles();
#readTaxonFile(); # not needed anymore
#readRefsFile(); # not used
readInteractionsFile();

#################### Download files ##################################################
sub downloadFiles {
# Get a list of all

# TODO: Ftp site changed. Now it needs a user name and a password.
# new url: ftp.bind.ca/pub/BIND/
# user: iavila@systemsbiology.org pass: iliana123

$ftp = Net::FTP->new("ftp.bind.ca", Debug => 0) or die "Cannot connect to ftp.bind.ca: $@";
$ftp->login('iavila@systemsbiology.org','iliana123') or die $ftp->message;
$ftp->cwd("/pub/BIND/current/bindflatfiles/bindindex") or die "Cannot change working directory ", $ftp->message;
@ls = $ftp->ls();

foreach $file (@ls){

	chomp($file);
	#if($file =~ /\.taxon\.txt$/){
	#	$taxonFile = $file; 
	#	print $taxonFile;
	#}
	
	#if($file =~ /\.refs\.txt/){
	#	$refsFile = $file;
	#	print $refsFile;
	#}
	
	if($file =~ /\.ints\.txt$/){
		$intsFile = $file;
		$ftp->get($file,"./bind/$file");
		print $intsFile;
	}
	
}

$ftp->quit;
#system("wget ftp://ftp.blueprint.org/pub/BIND/current/bindflatfiles/bindindex/${taxonFile} --directory-prefix=./bind/") == 0 or die "\nError: $?\n";
#system("wget ftp://ftp.blueprint.org/pub/BIND/current/bindflatfiles/bindindex/${refsFile} --directory-prefix=./bind/") == 0 or die "\nError: $?\n";
#system("wget ftp://ftp.blueprint.org/pub/BIND/current/bindflatfiles/bindindex/${intsFile} --directory-prefix=./bind/") == 0 or die "\nError: $?\n";

}
########################################################################################

################### readTaxonFile ######################################################
sub readTaxonFile {

print "Reading taxon file...\n";
open X, "bind/${taxonFile}" or die "Could not open file bind/${taxonFile}\n";
$dbh->do("DROP TABLE taxid_species");
$dbh->do("CREATE TABLE taxid_species (taxid INT, species VARCHAR(80), KEY(taxid))");
$fileName = getcwd."/bind/${taxonFile}";
print "Loading data into taxid_species table...\n";
$dbh->do("LOAD DATA LOCAL INFILE \'${fileName}\' IGNORE INTO TABLE taxid_species")  or die "Error: $dbh->errstr";
print "done.\n";
while( <X> ) {
    print;
    chop;
    my ( $tax, $species ) = split /\t/;
    $taxons{$tax} = $species; 
}
close X;
print "done reading taxonFile.\n";
}
########################################################################################

################### readRefsFile ######################################################
sub readRefsFile {

open X, "bind/${refsFile}";

while( <X> ) {
    chop;
    my ( $rgid, $bid, $pmid, $method ) = split /\t/;
    #print "$rgid $bid $pmid $method\n";
    $pmids{$bid} = $pmid;
    $methods{$bid} = $method;
}
close X;
}
########################################################################################

################### readInteractionsFile ######################################################

sub readInteractionsFile {

open X, "bind/${intsFile}";

my $i = 1;
my %loaded = ();


# TODO: Add a BINDid and a method columns
$dbh->do("DROP TABLE interactions");
$dbh->do("CREATE TABLE interactions (id VARCHAR(25),i1 VARCHAR(15), interactionType VARCHAR(2), i2 VARCHAR(15), taxid1 INT, taxid2 INT, KEY(i1,interactionType,i2), INDEX(taxid1), INDEX(taxid2))") or die "Error: $dbh->errstr\n";

$fileName = "bindInteractions.txt";
system("rm bind/bindInteractions.txt");
open( INT, ">bind/${fileName}" );

while( <X> ) {
    chomp;
    my ( $rgid, $bid, $typ1, $db1, $i1, $gb1, $tax, $typ2, $db2, $i2, $gb2, $tax2 ) = split /\t/;
	   
	next if $typ1 eq 'complex' || $typ1 eq 'small-molecule' || 
	   $typ2 eq 'complex' || $typ2 eq 'small-molecule';
    
    next if $i1 eq 'NA' || $i2 eq 'NA';
    next if $i1 eq "" || $i2 eq "";
    
    # recognize the type of id
    if($i1 =~ /^[OPQ][0-9]\w\w\w[0-9]$/){
    		# uniprot AC
    		$i1 = "UniProt:".$i1;
    		
    }
    
     # recognize the type of id
    if($i2 =~ /^[OPQ][0-9]\w\w\w[0-9]$/){
    		# uniprot AC
    		$i2 = "UniProt:".$i2;
    		
    }
    
    if($i1 =~ /^[A-Z][A-Z]\d\d\d\d\d\d$/ or $i1 =~ /^[A-NR-Z]\d\d\d\d\d$/){
    		# EMBL AC
    		$i1 = "EMBL:".$i1;
    }
    
    if($i2 =~ /^[A-Z][A-Z]\d\d\d\d\d\d$/ or $i2 =~ /^[A-NR-Z]\d\d\d\d\d$/){
    		# EMBL AC
    		$i2 = "EMBL:".$i2;
    }
    
    if($i1 =~ /\w+\|\w+/){
    		# PDB
    		$i1 = "PDB:".$i1;
    }
    
    if($i2 =~ /\w+\|\w+/){
    		# PDB
    		$i2 = "PDB:".$i2;
    }
    
	
	$itype = 'pp';
    
    $itype = 'pd' if ( $typ1 eq 'DNA' && $typ2 eq 'protein' || 
		     ( $typ2 eq 'DNA' && $typ1 eq 'protein' ) );
    $itype = 'pr' if ( $typ1 eq 'RNA' && $typ2 eq 'protein' || 
		     ( $typ2 eq 'RNA' && $typ1 eq 'protein' ) );

    # BINDid interactor1 interactionType interactor2 taxonomy taxonomy2
  	print INT "$bid\t$i1\t$itype\t$i2\t$tax\t$tax2\n" if ! $loaded{"$i1,$itype,$i2"};
    $loaded{"$i1,$itype,$i2"} = 1;
    $i ++;
    print "$i\n" if $i % 1000 == 0;
}# while

# done with this species, add to table
$fullFilePath = getcwd."/bind/${fileName}";
print "Loading interactions into interactions table...\n";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE interactions") or die "Error: $dbh->errstr";
print "done.\n";

close X;

}
