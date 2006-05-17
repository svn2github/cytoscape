#!/usr/bin/perl

use DBI();
use Cwd;
use Net::FTP;

if(scalar @ARGV < 3){
	print "USAGE: perl update_synonyms_refseq.pl <dbuser> <dbpassword> <dbname> optional: <update>\n";
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];
$update = false;
if(scalar @ARGV == 4){
	if($ARGV[3] =~ /^update/){
		$update = true;
	}
}

# Prepare database connection
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE DATABASE IF NOT EXISTS $dbname");
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

createProteinTables();
populateProteinTables();

################################################################################################################
sub createProteinTables {
	print "Droping tables...\n";
	$dbh->do("DROP TABLE IF EXISTS refseq_accession");
	$dbh->do("DROP TABLE IF EXISTS refseq_definition");
	$dbh->do("DROP TABLE IF EXISTS refseq_taxid");
	$dbh->do("DROP TABLE IF EXISTS refseq_codedby");
	$dbh->do("DROP TABLE IF EXISTS refseq_prodname");
	$dbh->do("DROP TABLE IF EXISTS refseq_genename");
	$dbh->do("DROP TABLE IF EXISTS refseq_geneid");
	$dbh->do("DROP TABLE IF EXISTS refseq_hgnc");
	$dbh->do("DROP TABLE IF EXISTS refseq_hprd");
	$dbh->do("DROP TABLE IF EXISTS refseq_locustag");
	print "done.\n";

	print "Creating RefSeq tables...\n";
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_accession".
		" (protgi INT UNIQUE, accession VARCHAR(15) UNIQUE, version VARCHAR(15) UNIQUE, KEY(protgi), INDEX(protgi), INDEX(accession))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_definition (protgi INT, definition VARCHAR(300), KEY(protgi), INDEX(protgi))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_taxid (protgi INT UNIQUE, taxid INT, KEY(protgi), INDEX(protgi),INDEX(taxid))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_codedby (protgi INT UNIQUE, codedby VARCHAR(20), KEY(protgi,codedby), INDEX(protgi))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_prodname (protgi INT, prodname VARCHAR(60), KEY(protgi,prodname), INDEX(protgi), INDEX(prodname))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_genename (protgi INT, genename VARCHAR(60), KEY(protgi,genename), INDEX(protgi), INDEX(genename))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_geneid (protgi INT, geneid INT, KEY(protgi,geneid), INDEX(protgi), INDEX(geneid))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_hgnc (protgi INT, hgncid INT, KEY(protgi,hgncid), INDEX(protgi))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_hprd(protgi INT, hprdid VARCHAR(10), KEY(protgi,hprdid), INDEX(protgi))");
	$dbh->do("CREATE TABLE IF NOT EXISTS refseq_locustag (protgi INT, locustag VARCHAR(25), KEY(protgi,locustag), INDEX(protgi))");
	print "done.\n";
}
#################################################################################################################
sub populateProteinTables(){
	
	print "Preparing loading statements...\n";
	$fullFilePath1 = getcwd."/refseq/refseq_accession.txt";
	$sth = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath1}\' IGNORE INTO TABLE refseq_accession") or die "Error: $dbh->errstr";

	$fullFilePath2 = getcwd."/refseq/refseq_genename.txt";
	$sth2 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath2}\' IGNORE INTO TABLE refseq_genename") or die "Error $dbh->errstr";

	$fullFilePath3 = getcwd."/refseq/refseq_prodname.txt";
	$sth3 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath3}\' IGNORE INTO TABLE refseq_prodname") or die "Error $dbh->errstr";

	$fullFilePath4 = getcwd."/refseq/refseq_definition.txt";
	$sth4 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath4}\' IGNORE INTO TABLE refseq_definition") or die "Error $dbh->errstr";

	$fullFilePath5 = getcwd."/refseq/refseq_geneid.txt";
	$sth5 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath5}\' IGNORE INTO TABLE refseq_geneid") or die "Error $dbh->errstr";

	$fullFilePath6 = getcwd."/refseq/refseq_hgnc.txt";
	$sth6 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath6}\' IGNORE INTO TABLE refseq_hgnc") or die "Error $dbh->errstr";

	$fullFilePath7 = getcwd."/refseq/refseq_taxid.txt";
	$sth7 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath7}\' IGNORE INTO TABLE refseq_taxid") or die "Error $dbh->errstr";
	
	$fullFilePath8 = getcwd."/refseq/refseq_hprd.txt";
	$sth8 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath8}\' IGNORE INTO TABLE refseq_hprd") or die "Error $dbh->errstr";
	
	$fullFilePath9 = getcwd."/refseq/refseq_codedby.txt";
	$sth9 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath9}\' IGNORE INTO TABLE refseq_codedby") or die "Error $dbh->errstr";
	print "done.\n";
	
	$fullFilePath10 = getcwd."/refseq/refseq_locustag.txt";
	$sth10 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath10}\' IGNORE INTO TABLE refseq_locustag") or die "Error $dbh->errstr";
	print "done.\n";
	
	# Get a list of all seq files
	$ftp = Net::FTP->new("ftp.ncbi.nih.gov", Debug => 0) or die "Cannot connect to ftp.ncbi.nih.gov: $@";
	$ftp->login("anonymous",'-anonymous@') or die "Cannot login ", $ftp->message;
	$ftp->cwd("/refseq/release/complete") or die "Cannot change working directory ", $ftp->message;
	@ls = $ftp->ls();
	$ftp->quit;

	system("rm -r refseq");
	system("mkdir refseq");
	
	foreach $file (@ls){

		if($file !~ /protein.gpff.gz$/){
			# not a protein file, skip.
	 		next;
		}
	
		print "Downloading file $file...\n";
		system("wget ftp://ftp.ncbi.nih.gov/refseq/release/complete/${file} --directory-prefix=./refseq/") == 0 or die "\nError: $?\n";
		print "done. Uncompressing...\n";
		system("cd refseq; gunzip ${file}") == 0 or die "Could not decompress file $file, error $?\n";
		print "done.\n";
	
		$file =~ s/.gz//;
	
		open(IN, "refseq/${file}") or die "Could not open file $file\n";
		
		open(ACC, ">refseq/refseq_accession.txt") or die "Could not create file refseq_accession.txt\n"; 
		open(GN, ">refseq/refseq_genename.txt") or die "Could not create file refseq_genename.txt\n";
		open(PN, ">refseq/refseq_prodname.txt") or die "Could not create file refseq_prodname.txt\n";
		open(DEF, ">refseq/refseq_definition.txt") or die "Could not create file refseq_definition.txt\n";
		open(GENEID, ">refseq/refseq_geneid.txt") or die "Could not create file refseq_geneid.txt\n";
		open(HGN, ">refseq/refseq_hgnc.txt") or die "Could not create file refseq_hgnc.txt\n";
		open(TAXID, ">refseq/refseq_taxid.txt") or die "Could not open file refseq_taxid.txt\n";
		open(CODEDBY, ">refseq/refseq_codedby.txt") or die "Could not open file refseq_codedby.txt\n";
		open(HPRD, ">refseq/refseq_hprd.txt") or die "Could not open file refseq_hprd.txt\n";
		open(LT, ">refseq/refseq_locustag.txt") or die "Could not open file refseq_locustag.txt\n";
		
		while($line = <IN>){
		
			if($line =~ /^\/\/$/){
				# end of a RefSeq entry
				# print to files
				
				print ACC "$gi\t$accession\t$version\n";
				print DEF "$gi\t$definition\n";
				
				foreach $gn(@genename){
					print GN "$gi\t$gn\n";
				}
				foreach $pn(@prodname){
					print PN "$gi\t$pn\n";
				}
				foreach $gid(@geneid){
					print GENEID "$gi\t$gid\n";
				}
				foreach $hid (@hgncid){
					print HGN "$gi\t$hid\n";
				}
				foreach $cb (@codedby){
					print CODEDBY "$gi\t$cb\n";
				}
				if($taxid ne ""){
					print TAXID "$gi\t$taxid\n";
				}
				foreach $hpid(@hprdid){
					print HPRD "$gi\t$hpid\n";
				}
				foreach $lt(@locustag){
					print LT "$gi\t$lt\n";
				}
				# reset variables
				$version = "";
				$gi = "";
				$definition = "";
				$taxid = "";
				$accession = "";
				@genename = ();
				@prodname = ();
				@geneid = ();
				@hgncid = ();
				@codedby = ();
				@hprdid = ();
				@locustag = ();
				
			}elsif($line =~ /^VERSION/){
				chomp $line;
				@fields = split /\s+/, $line;
				$version = $fields[1];
				$fields[1] =~ s/\.\d+//;
				$accession = $fields[1];
				$fields[2] =~ s/GI://;
				if($fields[2] =~ /\d+/){
					$gi = $fields[2];
				}
			}elsif($line =~ /^DEFINITION/){
				chomp $line;
				$line =~ s/DEFINITION\s+//;
				$definition = $line; 
				while($line = <IN>){
					if($line =~ /^ACCESSION/){
						last;
					}else{
						chomp $line;
						$line =~ s/^\s+//;
						$definition = $definition." ".$line;
					}
				}# while
			
			}elsif($line =~ /^\s+\/db_xref="taxon:/){
				chomp $line;
				@fields = split /:/, $line;
				$fields[1] =~ s/"//g;
				$taxid = $fields[1];
			}elsif($line =~ /^\s+\/db_xref="GeneID:/){
				chomp $line;
				@fields = split /:/, $line;
				$fields[1] =~ s/"//g;
				push @geneid, $fields[1];
			}elsif($line =~ /^\s+\/coded_by=/){
				
				#/coded_by="NC_001911.1:5718..7118"
				#/coded_by="complement(NC_004988.1:5906..6484)"
				#/coded_by="join(NC_234122.1:432..4321)"
				#/coded_by="join(complement(NC_423234.2:3432..1234)"
				#/coded_by="complement(join..."
				
				if($line =~ m/\(/){
					# contains at least one "("
					chomp $line;
					@fields = split /\(/, $line;
					# the actual ID will be in positions 1 or 2
					if($fields[1] =~ m/_/){
						# get rid of the version and what comes after the version
						$fields[1] =~ s/\.[0-9]+:.+$//g;
						push @codedby,$fields[1];
					}elsif($fields[2] =~ m/_/){
						# get rid of the version and what comes after the version
						$fields[2] =~ s/\.[0-9]+:.+$//g;
						push @codedby,$fields[2];
					}
										
				}else{
					chomp $line;
					@fields = split /=/,$line;
					@fields2 = split /:/,$fields[1];
					$fields2[0] =~ s/"//g;
					# get rid of the version
					$fields2[0] =~ s/\.[0-9]+$//g;
					push @codedby, $fields2[0];
				}
			}elsif($line =~ /^\s+\/gene=/){
			   	chomp $line;
				@fields = split /=/, $line;
				$fields[1] =~ s/"//g;
				push @genename, $fields[1];
			}elsif($line =~ /^\s+\/product=/){
				chomp $line;
				@fields = split /=/, $line;
				$fields[1] =~ s/"//g;
				push @prodname, $fields[1];								
			}elsif($line =~ /^\s+\/db_xref="HGNC:/){
				chomp $line;
				@fields = split /:/, $line;
				$fields[1] =~ s/"//g;
				push @hgncid, $fields[1];
			}elsif($line =~ /^\s+\/db_xref="HPRD:/){
				chomp $line;
				@fields = split /:/, $line;
				$fields[1] =~ s/"//g;
				push @hprdid,$fields[1];
			}elsif($line =~ /^\s+\/locus_tag="/){
				chomp $line;
				@fields = split /"/,$line;
				$fields[1] =~ s/"//;
				push @locustag, $fields[1];
			}
		
		}#while IN
		
		close(IN);
		close(ACC);
		close(GN);
		close(PN);
		close(DEF);
		close(HGN);
		close(GENEID);
		close(TAXID);
		close(CODEDBY);
		close(HPRD);
		close(LT);
		
		# load into tables
		print "Loading refseq_accession...\n";
		$sth->execute() or die "Error: $sth->errstr";
		
		print "Loading refseq_genename...\n";
		$sth2->execute() or die "Error: $sth2->errstr";
		
		print "Loading refseq_prodname...\n";
		$sth3->execute() or die "Error: $sth3->errstr";
		
		print "Loading refseq_definition...\n";
		$sth4->execute() or die "Error: $sth4->errstr";
		
		print "Loading refseq_geneid...\n";
		$sth5->execute() or die "Error: $sth5->errstr";
		
		print "Loading refseq_hgnc...\n";
		$sth6->execute() or die "Error: $sth6->errstr";
		
		print "Loading refseq_taxid...\n";
		$sth7->execute() or die "Error: $sth7->errstr";
		
		print "Loading refseq_hprd...\n";
		$sth8->execute() or die "Error: $sth8->errstr";
		
		print "Loading refseq_codedby...\n";
		$sth9->execute() or die "Error: $sth9->errstr";
		
		print "Loading refseq_locustag...\n";
		$sth10->execute() or die "Error: $sth10->errstr";
		
		# Save space by deleting files
		system("rm refseq/*");
		
	}# foreach file
	
}