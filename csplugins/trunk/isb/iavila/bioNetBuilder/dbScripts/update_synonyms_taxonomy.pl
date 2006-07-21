#!/usr/bin/perl

use DBI();
use Cwd;
use Net::FTP;

if(scalar @ARGV < 3){
	print "USAGE: perl update_synonyms_taxonomy.pl <dbuser> <dbpassword> <dbname>\n";
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];


# Prepare database connection
$dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE DATABASE IF NOT EXISTS $dbname");
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

# GenBank is no longer loaded because most databases used refer to GI numbers from RefSeq, not GenBank
# I'll leave the code here in case that GenBank is used in the future
#genBankTables();


taxonomyTables();

###########################################################################################
# Creates and populates genbank_* tables
###########################################################################################

sub genBankTables {

$dbh->do("CREATE TABLE IF NOT EXISTS genbank_accession".
" (nucacc VARCHAR(15), nucversion VARCHAR(15), nucgi INT, protacc VARCHAR(15) UNIQUE, protversion VARCHAR(15) UNIQUE, protgi INT UNIQUE,".
" KEY(protacc,protversion,protgi),INDEX(protgi),INDEX(protacc))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_genename (protgi INT, genename VARCHAR(50), KEY(protgi,genename), INDEX(genename), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_prodname (protgi INT, prodname VARCHAR(60), KEY(protgi,prodname), INDEX(protgi), INDEX(prodname))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_goa (protgi INT, goaid VARCHAR(20), KEY(protgi,goaid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_goid (protgi INT, goid INT, KEY(protgi,goid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_definition (nucgi INT, definition VARCHAR(200), KEY(nucgi), INDEX(nucgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_sprot (protgi INT, sprotid VARCHAR(12), KEY(protgi,sprotid), INDEX(sprotid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_trembl (protgi INT, tremblid VARCHAR(12), KEY(protgi, trembl), INDEX(tremblid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_pir (protgi INT, pirid VARCHAR(15), KEY(protgi,pirid), INDEX(pirid))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_interpro (protgi INT, interproid VARCHAR(9), KEY(protgi,interproid), INDEX(interproid), INDEX(protgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_pdb (protgi INT, pdbid VARCHAR(4), KEY(protgi,pdbid))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_ensembl (nucgi INT, ensemblid INT, KEY(nucgi,ensemblid), INDEX(nucgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_geneid (nucgi INT, geneid INT, KEY(nucgi,geneid), INDEX(nucgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_hgnc (nucgi INT, hgncid INT, KEY(nucgi,hgncid), INDEX(nucgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_locuslink (nucgi INT, locusid INT, KEY(nucgi, locusid), INDEX(nucgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_mim (nucgi INT, mimid INT, KEY(nucgi,mimid), INDEX(nucgi))");
$dbh->do("CREATE TABLE IF NOT EXISTS genbank_taxid (protgi INT KEY, taxid INT, INDEX(protgi), INDEX(taxid))");


$fullFilePath1 = getcwd."/genbank/genbank_accession.txt";
$sth = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath1}\' IGNORE INTO TABLE genbank_accession") or die "Error: $dbh->errstr";

$fullFilePath2 = getcwd."/genbank/genbank_genename.txt";
$sth2 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath2}\' IGNORE INTO TABLE genbank_genename") or die "Error $dbh->errstr";

$fullFilePath3 = getcwd."/genbank/genbank_prodname.txt";
$sth3 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath3}\' IGNORE INTO TABLE genbank_prodname") or die "Error $dbh->errstr";

$fullFilePath4 = getcwd."/genbank/genbank_goa.txt";
$sth4 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath4}\' IGNORE INTO TABLE genbank_goa") or die "Error $dbh->errstr";

$fullFilePath5 = getcwd."/genbank/genbank_definition.txt";
$sth5 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath5}\' IGNORE INTO TABLE genbank_definition") or die "Error $dbh->errstr";

$fullFilePath6 = getcwd."/genbank/genbank_sprot.txt";
$sth6 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath6}\' IGNORE INTO TABLE genbank_sprot") or die "Error $dbh->errstr";

$fullFilePath7 = getcwd."/genbank/genbank_trembl.txt";
$sth7 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath7}\' IGNORE INTO TABLE genbank_trembl") or die "Error $dbh->errstr";

$fullFilePath8 = getcwd."/genbank/genbank_pir.txt";
$sth8 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath8}\' IGNORE INTO TABLE genbank_pir") or die "Error $dbh->errstr";

$fullFilePath9 = getcwd."/genbank/genbank_goid.txt";
$sth9 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath9}\' IGNORE INTO TABLE genbank_goid") or die "Error $dbh->errstr";

$fullFilePath10 = getcwd."/genbank/genbank_interpro.txt";
$sth10 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath10}\' IGNORE INTO TABLE genbank_interpro") or die "Error $dbh->errstr";

$fullFilePath11 = getcwd."/genbank/genbank_pdb.txt";
$sth11 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath11}\' IGNORE INTO TABLE genbank_pdb") or die "Error $dbh->errstr";

$fullFilePath12 = getcwd."/genbank/genbank_ensembl.txt";
$sth12 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath12}\' IGNORE INTO TABLE genbank_ensembl") or die "Error $dbh->errstr";

$fullFilePath13 = getcwd."/genbank/genbank_geneid.txt";
$sth13 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath13}\' IGNORE INTO TABLE genbank_geneid") or die "Error $dbh->errstr";

$fullFilePath14 = getcwd."/genbank/genbank_hgnc.txt";
$sth14 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath14}\' IGNORE INTO TABLE genbank_hgnc") or die "Error $dbh->errstr";

$fullFilePath15 = getcwd."/genbank/genbank_locuslink.txt";
$sth15 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath15}\' IGNORE INTO TABLE genbank_locuslink") or die "Error $dbh->errstr";

$fullFilePath16 = getcwd."/genbank/genbank_mim.txt";
$sth16 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath16}\' IGNORE INTO TABLE genbank_mim") or die "Error $dbh->errstr";

$fullFilePath17 = getcwd."/genbank/genbank_taxid.txt";
$sth17 = $dbh->prepare_cached("LOAD DATA LOCAL INFILE \'${fullFilePath17}\' IGNORE INTO TABLE genbank_taxid") or die "Error $dbh->errstr";

# Get a list of all seq files 
$ftp = Net::FTP->new("bio-mirror.net", Debug => 0) or die "Cannot connect to bio-mirror.net: $@";
$ftp->login("anonymous",'-anonymous@') or die "Cannot login ", $ftp->message;
$ftp->cwd("/biomirror/genbank/") or die "Cannot change working directory ", $ftp->message;
@ls = $ftp->ls();
$ftp->quit;

system("rm -r genbank");
system("mkdir genbank");

foreach $file (@ls){

	if($file !~ /seq.gz$/){
		# not a sequence file, skip.
	 	next;
	}
	
	print "Downloading file $file...\n";
	system("wget ftp://bio-mirror.net/biomirror/genbank/${file} --directory-prefix=./genbank/") == 0 or die "\nError: $?\n";
	print "done. Uncompressing...\n";
	system("cd genbank; gunzip ${file}") == 0 or die "Could not decompress file $file, error $?\n";
	print "done.\n";
	
	$file =~ s/.gz//;
	
	open(IN, "genbank/${file}") or die "Could not open file $file\n";
	open(ACC, ">genbank/genbank_accession.txt") or die "Could not create file genbank_accession.txt\n";
	open(GN, ">genbank/genbank_genename.txt") or die "Could not create file genbank_genename.txt\n";
	open(PN, ">genbank/genbank_prodname.txt") or die "Could not create file genbank_prodname.txt\n";
	open(GOA, ">genbank/genbank_goa.txt") or die "Could not create file genbank_goa.txt\n";
	open(GOID, ">genbank/genbank_goid.txt") or die "Could not create file genbank_goid.txt\n";
	open(DEF, ">genbank/genbank_definition.txt") or die "Could not create file genbank_definition.txt\n";
	open(SPROT, ">genbank/genbank_sprot.txt") or die "Could not create file genbank_sprot.txt\n";
	open(TREMBL, ">genbank/genbank_trembl.txt") or die "Could not create file genbank_trembl.txt\n";
	open(PIR, ">genbank/genbank_pir.txt") or die "Could not create file genbank_pir.txt\n";
	open(INTERPRO, ">genbank/genbank_interpro.txt") or die "Could not create file genbank_interpro.txt\n";
	open(PDB, ">genbank/genbank_pdb.txt") or die "Could not create file genbank_pdb.txt\n";
	open(ENSEMBL, ">genbank/genbank_ensembl.txt") or die "Could not create file genbank_ensembl.txt\n"; # text
	open(GENEID, ">genbank/genbank_geneid.txt") or die "Could not create file genbank_geneid.txt\n"; # INT
	open(HGN, ">genbank/genbank_hgnc.txt") or die "Could not create file genbank_hgnc.txt\n"; # INT 
	open(LOCUSID, ">genbank/genbank_locuslink.txt") or die "Could not create file genbank_locuslink.txt\n"; # int
	open(MIM, ">genbank/genbank_mim.txt") or die "Could not create file genbank_mim.txt\n"; # int
	open(TAXID, ">genbank/genbank_taxid.txt") or die "Could not open file genbank_taxid.txt\n";
	
	$nucacc = "";
	$nucversion = "";
    $nucgi = "";
	$definition = "";
	$taxid = "";
	
	@pdbid = ();
	@ensemblid = ();
	@hgnid = ();
	@locusid = ();
	@mimid = ();
	@geneid = ();
	
	
	while($line = <IN>){
		
		if($line =~ /^\/\/$/){
			# end of a GenBank entry
			# see if we were able to find protein ids
			
			if($nucacc ne "" and $protacc eq ""){
				# ONLY STORE ENTRIES THAT ENCODE PROTEINS
				# If later we want to store nucleotide ids, create new table with
				# only nucgis to save space
				# print ACC "$nucacc\t$nucgi\t$protacc\t$protgi\n";
			}elsif($nucacc ne "" and $protacc ne ""){
			
				# only store data for protein-encoding nucgis
				print DEF "$nucgi\t$definition\n";
				
				foreach $e (@ensemblid){
					print ENSEMBL "$nucgi\t$e\n";
				}
				
				foreach $hg (@hgnid){
					print HGN "$nucgi\t$hg\n";
				}
				
				foreach $l (@locusid){
					print LOCUSID "$nucgi\t$l\n";
				}
				
				foreach $m (@mimid){
					print MIM "$nucgi\t$m\n";
				}
				
				foreach $g (@geneid){
					print GENEID "$nucgi\t$g\n";
				}
				
			}
			
		}elsif($line =~ /^VERSION/){
			chomp $line;
			@fields = split /\s+/, $line;
			$nucversion = $fields[1];
			$fields[1] =~ s/\.\d+//;
			$nucacc = $fields[1];
			$fields[2] =~ s/GI://;
			if($fields[2] =~ /\d+/){
				$nucgi = $fields[2];
			}
		}elsif($line =~ /^DEFINITION/){
			chomp $line;
			$line =~ s/DEFINITION\s+//;
			$definition = $line; 
			while($line = <IN>){
				if($line =~ /^\ACCESSION/){
					last;
				}else{
					chomp $line;
					$line =~ s/^\s+//;
					$definition = $definition." ".$line;
				}
			}# while
			
		}elsif($line =~ /^\s+\/db_xref="ENSEMBL:/){
			chomp $line;
			@fields = split /:/, $line;
			$fields[1] =~ s/"//g;
			push @ensemblid, $fields[1];
		}elsif($line =~ /^\s+\/db_xref="HGNC:/){
			chomp $line;
			@fields = split /:/, $line;
			$fields[1] =~ s/"//g;
			push @hgnid, $fields[1];
		}elsif($line =~ /^\s+\/db_xref="LocusID:/){
			chomp $line;
			@fields = split /:/, $line;
			$fields[1] =~ s/"//g;
			push @locusid, $fields[1];
		}elsif($line =~ /^\s+\/db_xref="MIM:/){
			chomp $line;
			@fields = split /:/, $line;
			$fields[1] =~ s/"//g;
			push @mimid, $fields[1];
		}elsif($line =~ /^\s+\/db_xref="GeneID:/){
			chomp $line;
			@fields = split /:/, $line;
			$fields[1] =~ s/"//g;
			push @geneid, $fields[1];
		}elsif($line =~ /^\s+\/db_xref="taxon:/){
			chomp $line;
			@fields = split /:/, $line;
			$fields[1] =~ s/"//g;
			$taxid = $fields[1];
		}elsif($line =~ /^\s+CDS/){
			# find protein IDs and names
			$protacc = "";
			$protversion = "";
			$protgi = "";
			@genename = ();
			@prodname = ();
			@goaid = ();
			@goid = ();
			@interproids= ();
			@sprotid = ();
			@tremblid = ();
			@pirid = ();
			@pdbid = ();
			
			while($line = <IN>){
				
				if($line =~ /^\s\s\s\s\s\w/ or $line =~ /^\w/){
					# we reached a new feature or a new sequence entry (end of CDS)	
					print ACC "$nucacc\t$nucversion\t$nucgi\t$protacc\t$protversion\t$protgi\n";
					
					foreach $g (@genename){
						print GN "$protgi\t$g\n";
					}
					foreach $p (@prodname){
						print PN "$protgi\t$p\n";
					}
					
					foreach $s (@sprotid){
						print SPROT	"$protgi\t$s\n";
					}
					
					foreach $t (@tremblid){
						print TREMBL	"$protgi\t$t\n";
					}
					
					foreach $p (@pirid){
						print PIR "$protgi\t$p\n";
					}
					
					foreach $g (@goaid){
						print GOA "$protgi\t$g\n";
					}
					
					foreach $g (@goid){
						print GOID "$protgi\t$g\n";
					}
					
					foreach $i (@interproids){	
						print INTERPRO "$protgi\t$i\n";
					}
					
					foreach $p (@pdbid){
						print PDB "$protgi\t$p\n";
					}
					
					if($taxid ne ""){
						print TAXID "$protgi\t$taxid\n";
					}			
					
					last; #skip the rest
				
				}elsif($line =~ /^\s+\/protein_id=/){
					chomp $line;
					@fields = split /=/,$line;
					$fields[1] =~ s/"//g; #replace quotes by the empty string
					$protversion = $fields[1];
					$fields[1] =~ s/\.\d+//; #replace the version section by the empty string, version looks like: .1 or .2 etc.
					$protacc = $fields[1];
				}elsif($line =~ /^\s+\/db_xref="GI:/){ #if it is a GI
					chomp $line;
					@fields = split /:/,$line;
					$fields[1] =~ s/"//g; #replace quotes by the empty string
					$protgi = $fields[1];	
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
				}elsif($line =~ /^\s+\/db_xref="UniProtKB\/Swiss-Prot:/){
					chomp $line;
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					push @sprotid, $fields[1];
				}elsif($line =~ /^\s+\/db_xref="UniProtKB\/TrEMBL:/){
					chomp $line;
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					push @tremblid, $fields[1];
				}elsif($line =~ /^\s+\/db_xref="GOA:/){
					chomp $line;
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					push @goaid, $fields[1];
				}elsif($line =~ /^\s+\/db_xref="GO:/){
					chomp $line;
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					push @goid, $fields[1];	
				}elsif($line =~ /^\s+\/db_xref="PIR:/){
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					push @pirid, $fields[1];
				}elsif($line =~ /^\s+\/db_xref="InterPro:/){
					chomp $line;
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					$interproid = $fields[1];
					push @interproids, $interproid;
				}elsif($line =~ /^\s+\/db_xref="PDB:/){
					chomp $line;
					@fields = split /:/, $line;
					$fields[1] =~ s/"//g;
					push @pdbid, $fields[1];
				}#end of if statements
				
			}# inner while (within a CDS seq entry)			
		}# if CDS
	}# outer while (look for VERSION and CDS entries in a file)
	
	close(IN);
	close(ACC);
	close(GN);
	close(PN);
	close(GO);
	close(GOA);
	close(DEF);
	close(SPROT);
	close(TREMBL);
	close(PIR);
	close(INTERPRO);
	close(PDB);
	close(ENSEMBL);
	close(LOCUSID);
	close(HGN);
	close(MIM);
	close(GENEID);
	close(TAXID);
	
	# Done reading a file, load its info into tables
	
	print "Loading entries into genbank_accession...\n";
	$sth->execute() or die "Error: $sth->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_genename...\n";
	$sth2->execute() or die "Error: $sth2->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_prodname...\n";
	$sth3->execute() or die "Error: $sth3->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_goa...\n";
	$sth4->execute() or die "Error: $sth4->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_definition...\n";
	$sth5->execute() or die "Error: $sth5->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_sprot...\n";
	$sth6->execute() or die "Error: $sth6->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_trembl...\n";
	$sth7->execute() or die "Error: $sth7->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_pir...\n";
	$sth8->execute() or die "Error: $sth8->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_goid...\n";
	$sth9->execute() or die "Error: $sth9->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_interpro...\n";
	$sth10->execute() or die "Error: $sth10->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_pdb...\n";
	$sth11->execute() or die "Error: $sth11->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_ensembl...\n";
	$sth12->execute() or die "Error: $sth12->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_geneid...\n";
	$sth13->execute() or die "Error: $sth13->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_hgnc...\n";
	$sth14->execute() or die "Error: $sth14->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_locuslink...\n";
	$sth15->execute() or die "Error: $sth15->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_mim...\n";
	$sth16->execute() or die "Error: $sth16->errstr";
	print "done.\n";
	
	print "Loading entries into genbank_taxid...\n";
	$sth17->execute() or die "Error: $sth17->errstr";
	print "done.\n";
	
	# Save space by deleting files
	system("rm genbank/*");

}# for each file

}# end of sub genBankTables

###########################################################################################
# Creates and populates  ncbi_taxid_species, nucgi_taxid, and protgi_taxid  tables
###########################################################################################

sub taxonomyTables {
# Now, take care of taxonomy...

system("rm -r taxonomy");
system("mkdir taxonomy");
print "Downloading taxdump.tar.gz...\n";
system("wget ftp://ftp.ncbi.nih.gov/pub/taxonomy/taxdump.tar.gz --directory-prefix=./taxonomy/") == 0 or die "\nError: $?\n";
print "done. Decompressing...";
system("cd taxonomy; gunzip < taxdump.tar.gz | tar xvf -");
print "done.\n";

# only include species and subspecies that have sequence data
open IN, "taxonomy/nodes.dmp" or die "Could not open file taxonomy/nodes.dmp\n";
my %species;
while($line = <IN>){
	@fields = split /\t\|\t/, $line;
    if($fields[2] =~ /^species$/ or $fields[2] =~ /^subspecies$/ ){
    		if($fields[11] == 0){
    			$species{$fields[0]} = 1;
    		}
 	 	
	}
}
close(IN);
print "Read ".scalar(%species)." species with sequence data.\n";
open IN,"taxonomy/names.dmp" or die "Could not open file taxonomy/names.dmp\n";
open OUT, ">taxonomy/taxid_speciesname.txt" or die "Could not create file taxonomy/taxid_speciesname.txt\n";
$num = 0;
$total = 0;
while($line = <IN>){
	@fields = split /\t\|\t/, $line;
    if($fields[$#fields] =~ /^scientific/ and exists $species{$fields[0]}){
    		print OUT $fields[0],"\t",$fields[1],"\n";
 	 	$num++;
	}
}

$dbh->do("DROP TABLE IF EXISTS ncbi_taxid_species");

$dbh->do("CREATE TABLE IF NOT EXISTS ncbi_taxid_species (taxid INT, name VARCHAR(100), INDEX(taxid), KEY(taxid))");

$fullFilePath = getcwd()."/taxonomy/taxid_speciesname.txt";
print "Loading data into ncbi_taxid_species...";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE ncbi_taxid_species");
print "done.\n";
	
print "Downloading and decompressing gi_taxid_nucl.dmp.gz...\n";	
system("wget ftp://ftp.ncbi.nih.gov/pub/taxonomy/gi_taxid_nucl.dmp.gz --directory-prefix=./taxonomy/") == 0 or die "\nError: $?\n";
system("cd taxonomy; gunzip gi_taxid_nucl.dmp.gz");
print "done. Downloading and decompressing gi_taxid_prot.dmp.gz...\n";
system("wget ftp://ftp.ncbi.nih.gov/pub/taxonomy/gi_taxid_prot.dmp.gz --directory-prefix=./taxonomy/") == 0 or die "\nError: $?\n";
system("cd taxonomy; gunzip gi_taxid_prot.dmp.gz");
print "done.\n";

# Not sure of whether I want this or not. I don't know what GI numbers are used here (from GenBank or RefSeq?).
$dbh->do("DROP TABLE IF EXISTS tax_nucgi_taxid");
$dbh->do("CREATE TABLE IF NOT EXISTS tax_nucgi_taxid (nucgi INT, taxid INT, INDEX(nucgi), INDEX(taxid), KEY(nucgi))");
$fullFilePath = getcwd()."/taxonomy/gi_taxid_nucl.dmp";
print "Loading data into nucgi_taxid...\n";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE  tax_nucgi_taxid");
print "done.\n";
$dbh->do("DROP TABLE IF EXISTS tax_protgi_taxid");
$dbh->do("CREATE TABLE IF NOT EXISTS tax_protgi_taxid (protgi INT, taxid INT, INDEX(protgi), INDEX(taxid), KEY(protgi))");
$fullFilePath = getcwd()."/taxonomy/gi_taxid_prot.dmp";
print "Loading data into protgi_taxid...\n";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE  tax_protgi_taxid");
print "done.\n";

} # end of sub taxonomyTables

# TODO gene2accession to complete genbank_accession???
