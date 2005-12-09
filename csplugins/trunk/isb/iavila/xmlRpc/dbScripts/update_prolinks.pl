#!/usr/bin/perl

######################################################################################################
# Authors:  Iliana Avila-Campillo, Junghwan Park
# Last date modified: December 7, 2005, by Iliana
# Files are downloaded and uncompressed into new directory ./prolinks, ready to load into mysql db
# Removes redundant interactions from Prolinks
# Requires wget software http://www.gnu.org/software/wget/wget.html
######################################################################################################

use DBI;
use Cwd;

print "------------------ update_prolinks.pl ---------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: update_prolinks.pl  <prolinks dbname> <db user> <db pwd>";
	die;
}

$dbname = $ARGV[0];
$dbuser = $ARGV[1];
$dbpwd = $ARGV[2];

###### Parameter Setting #############################################
my $dbid = "prolinks";
my @big, @methods;
my $pwd, $dbname;
my %thre, %methodNames;

@methods = ('PP', 'GN', 'RS', 'GC');

$pwd = getcwd;

$thre{'PP'} = 3e-24;
$thre{'GN'} = 4e-15;
$thre{'RS'} = 0.1;
$thre{'GC'} = 0.1;
$methodNames{'PP'} = "phylogenetic profiles";
$methodNames{'GN'} = "gene neighbor";
$methodNames{'RS'} = "rosetta stone";
$methodNames{'GC'} = "gene cluster";

#######################################################################

###### Main Program ###################################################
$source = &getSource;
&download($source);
&make_file_list;

$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("DROP DATABASE IF EXISTS $dbname") or die "Error: $dbh->errstr";
$dbh->do("CREATE DATABASE $dbname") or die "Error: $dbh->errstr";
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

&create_tables($dbh);
&import_data($dbh, \@methods, \%thre, \%methodNames, $pwd);

$dbh->disconnect();
print "------------------ Leaving update_prolinks.pl ---------------------\n";

#######################################################################
###### Sub Functions ##################################################

sub getSource {
	# for now
	return "http://mysql5.mbi.ucla.edu/public/Genomes";
	
	#$dbh = DBI->connect("dbi:mysql:database=metainfo:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
	#$sth = $dbh->prepare("SELECT source FROM db_info WHERE db=?") or die $dbh->errstr;
	#$sth->execute($dbid) or die $sth->errstr;
	
	#while ($row = $sth->fetchrow_hashref()) {
	#	$source = $row->{'source'};
	#}
	#$dbh->disconnect();
	#print "Prolinks data source: $source\n";
	#return $source;
}

#######################################################################
# NAME download
# ROLE download all the files from Prolinks website. and does other file jobs
# CALLS
# CALLED_BY main

sub download {
	
	my $source = shift;
	print "URL = $source\n";
	# get the source to download
	system("wget -r --accept=.txt --reject=.html --level=1 -np ${source}");
	
	# hard coded location:
	#system('wget -r --accept=.txt --reject=.html --level=1 -np http://mysql5.mbi.ucla.edu/public/Genomes');
	
	system('rm -r ./prolinks');
	system('mkdir ./prolinks') == 0 or die "mkdir ./prolinks failed : $?\n";
	system('mv mysql5.mbi.ucla.edu/public/Genomes/* ./prolinks') == 0 or die "mv mysql5.mbi.ucla.edu/public/Genomes/* ./prolinks failed : $?\n";
	system('rm -r mysql5.mbi.ucla.edu');
	print "Downloaded prolinks source.\n";

}


#######################################################################
# NAME make_file_list
# ROLE make file list of datafile which doesn't include the gi related files
# CALLS
# CALLED_BY main

sub make_file_list {
	system('ls ./prolinks/*.txt > ./prolinks/list.lst') == 0 or die "ls ./prolinks/*.txt > ./prolinks/list.lst failed: $?\n";
	open (FH, './prolinks/list.lst') or die "Could not open ./prolinks/list.lst\n";
	open (FH2, '>./prolinks/newlist.lst') or die "Could not create ./prolinks/newlist.lst\n";
	my $line;
	while ($line = <FH>) {
		chomp $line;
		unless ($line =~ /^[Gg]eneID/) {
			if($line =~ /(.*)\.txt$/){ #any number of characters followed by .txt
				print FH2 $line."\n";
			}
		}
	}
	
	close(FH);
	close(FH2);
	system('rm ./prolinks/list.lst');
}

#######################################################################
# NAME create_tables
# ROLE create tables for general species
# CALLS
# CALLED_BY main

sub create_tables {
	print "Creating SPECIES, and method_threshold tables...";
	my $dbh;
	$dbh = shift;
	$dbh->do("CREATE TABLE species (species VARCHAR(100), tablename VARCHAR(100), INDEX (species))") or die "Error: $dbh->errstr";
	$dbh->do("CREATE TABLE method_threshold (method CHAR(2), threshold DOUBLE, description VARCHAR(25))") or die "Error: $dbh->errstr";
	print "done\n";
}

#######################################################################
# NAME import_data
# ROLE read the file list, and import all the data into specific database
# CALLS divide_table
# CALLED_BY main

sub import_data {

	print "Importing data into prolinks tables...\n";
	
	my $dbh, $rmethods, $pwd;
	
	($dbh, $rmethods, $rthre, $rnames, $pwd) = @_;
	
	foreach $method (keys(%$rthre)) {
		$dbh->do("INSERT INTO method_threshold VALUES(?, ?, ?)", undef, $method, $rthre->{$method}, $rnames->{$method}) or die "Error: $dbh->errstr";
	}

	open (LIST, './prolinks/newlist.lst') or die "Could not open file ./prolinks/newlist.lst\n";

	$id = 0;
	my $row;
	
	while ($row = <LIST>) {
		chomp $row;
		$filepath = $row;
		
		@fe = split(/\//, $row);
		$fullspeciesname = $fe[2]; #contains .txt ending
		$fullspeciesname = substr($fullspeciesname,0,-4); # leave out .txt ending
		$originalname = $fullspeciesname;
		$fullspeciesname =~ s/:/_/g; # replace : by _
		my $shortspeciesname = '';
		if (length($fullspeciesname)>55) {
			$nspn = substr($fullspeciesname, length($fullspeciesname)-55, 55);
			$shortspeciesname = $nspn;
		} else {
			$shortspeciesname = $fullspeciesname;
		}
		
		print "\npath: $filepath, tablename: $shortspeciesname\n";
		
		$dbh->do("CREATE table $shortspeciesname (gene_id_a INT, gene_id_b INT, p FLOAT, confidence FLOAT, method CHAR(2), 
			INDEX (gene_id_a, p, method), INDEX (p, method), INDEX(gene_id_a, gene_id_b, p, method))") or die "Error: $dbh->errstr";
			
		$noDuplicatesFile = &remove_duplicates("${filepath}");
		$fullFilePath = $pwd.substr($noDuplicatesFile,1);
		print "Reduced file: $fullFilePath\n";
		$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' INTO TABLE ${shortspeciesname} IGNORE 1 LINES") or die "Error: $dbh->errstr";

		&divide_table($shortspeciesname, $rmethods, $rthre);
		
		$dbh->do("INSERT INTO species VALUES (?, ?)", undef, $originalname, $shortspeciesname) or die "Error: $dbh->errstr";
	}
	
	close(LIST);
}


#######################################################################
# NAME removeDuplicates
# ROLE interactions in Prolinks files contain duplicates: a interacts with b, b interactis with a
# CALLS
# CALLED import_data

sub remove_duplicates {
    print "Removing duplicates from Prolinks file...";
	my $pFile = shift;
	open (IN, "<", $pFile) or die "Can't open file $pFile\n";
	my $newFile = $pFile."_reduced.txt";
	open (OUT, ">", $newFile) or die "Can't create file $pFile"."_reduced.txt\n";
	# a unique interaction is defined by the interactors and the method
	my %seenInteractions;
	my $numRemoved = 0;
	my $line;
	while($line = <IN>){
		chomp $line;
		@fields = split(/\s/, $line);
		
		# order is: gene_id_a	 gene_id_b p confidence method
		
		$geneA = $fields[0];
		$geneB = $fields[1];
		$method = $fields[4];
		$key = $geneA.$geneB.$method;
		
		if( !exists( $seenInteractions{$key} ) ){
			# not seen before
			print OUT $line, "\n";
			$reverseKey=$geneB.$geneA.$method;
			# so that the next interaction we see is not printed to the file:
			$seenInteractions{$reverseKey} = 1;
		}else{
			$numRemoved++;
		}
		
	}#while
	print "num entries removed = $numRemoved\n";
	close(IN);
	close(OUT);
	return $newFile;
}

#######################################################################
# NAME divide_table
# ROLE divide each table into 8 small tables along the methods and p values
# CALLS
# CALLED import_data

sub divide_table {
    
	my $tablename, $rmethods, $rthre;

	($tablename, $rmethods, $rthre) = @_;
	
	print"Dividing table $tablename...\n";
	
	foreach $method (@$rmethods) {
		$dbh->do("CREATE TABLE ".$tablename.'_'.lc($method).' SELECT * FROM '.$tablename." WHERE method=\'".$method."\'") or die "Error: $dbh->errstr";
		print "\t".$tablename.'_'.lc($method)."\n";
		$dbh->do("CREATE TABLE ".$tablename.'_'.lc($method).'_low SELECT * FROM '.$tablename." WHERE p<".$rthre->{$method}." AND method=\'".$method."\'") 
			or die "Error: $dbh->errstr";
		print "\t".$tablename.'_'.lc($method)."_low\n";
	}
}

