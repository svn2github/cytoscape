# ggms.pl -- GGMS: align short and long sequences with SOAP and BLAT

# usage, with writing standard error into 'err':
# perl ggms.pl input1 input2 2> err

# input1	a multifasta with the sequences which are to be aligned
# input2	a multifasta with the database (genome) to search against

# author: Anton Kratz <anton.kratz@gmail.com>
# created:			12.11.2009
# last change:		24.12.2009

# if a sequence is shorter than SHORTLONG it will be aligned by SOAP,
# otherwise by BLAT; you can change this value to anything between 25 and 50,
# depending on your application
$SHORTLONG = 50;


# location of SOAP. try to get the location of SOAP from .ggms_config
open CONFIG, ".ggms_config" or die "ioerror: $!";
$path_to_soap = <CONFIG>;
chomp $path_to_soap;
$path_to_blat = <CONFIG>;
chomp $path_to_blat;
close CONFIG;


# check if the file actually exists. if not, start a search in the current
# users home directory. I assume locate is not working so a find across the
# entire disc would take very long, therefore the search is only in the home
# directory. If a full search is needed, replace ~ with / in commandstring.
# if there was no correct path specified in .ggms_config, but a correct path
# has been found, then .ggms_config is changed.
$full_soap = $path_to_soap . "/soap";
if (! -e $full_soap) {
	$commandstring = "find ~ -name soap 2> /dev/null";
	# print "COMMAND: ", $commandstring , "\n";
	open PH, "$commandstring |";
	$path_to_soap = <PH>;
	chomp $path_to_soap;
	$last_slash = rindex($path_to_soap, "/");
	$path_to_soap = substr($path_to_soap, 0, $last_slash);
	print "SOAP is not in the path, I will be using: [$path_to_soap]\n";
	close PH;
}

# same for BLAT
$full_blat = $path_to_blat . "/blat";
if (! -e $full_blat) {
	$commandstring = "find ~ -name blat 2> /dev/null";
	# print "COMMAND: ", $commandstring , "\n";
	open PH, "$commandstring |";
	$path_to_blat = <PH>;
	chomp $path_to_blat;
	$last_slash = rindex($path_to_blat, "/");
	$path_to_blat = substr($path_to_blat, 0, $last_slash);
	print "BLAT is not in the path, I will be using: [$path_to_blat]\n";
	close PH;
}

# overwrite the config file with working paths
if ((! -e $full_blat) or (! -e $full_soap)) {
	open CONFIG, ">.ggms_config" or die "ioerror: $!";
	print CONFIG "$path_to_soap\n";
	print CONFIG "$path_to_blat\n";
	close CONFIG;
}

# careful! end-of-line is redefined to > because I am dealing with FASTA files
$/ = ">";


# build directory structure for temporary files. All temporary files and directories
# are stored in
# /tmp/'PROCESS-ID OF GGMS.PL'_ggms
# this allows to run multiple instances of GGMS without the temporary files
# getting into conflict.

$tmpdir = "/tmp/" . $$ . "_ggms";

# create a new, empty directory under /tmp for temporary files
if (-d "$tmpdir") {
	system("rm -rf $tmpdir");
}
system("mkdir $tmpdir");				# storing the alignments and split sequences
system("mkdir $tmpdir/db");				# storing the split database
system("mkdir $tmpdir/tempDir");		# temp workspace for pslSort

 
# split input1 into short (<SHORTLONG nt) and long (>=SHORTLONG nt) sequences
# also determine the length of the sequence
open SHORT, ">$tmpdir/short.fa" or die "ioerror";
open LONG,  ">$tmpdir/long.fa"  or die "ioerror";

open FH, $ARGV[0] or die "ioerror: $!";
$foo = <FH>; # ignore first "virtual" entry
while ($foo = <FH>) {

	chop $foo;
	
	@entry = split /\n/, $foo;
	$header = $entry[0];
	
	$sequence = "";
	$tmp_sequence = "";
	
	shift @entry;

	$sequence = join "\n", @entry;
	$tmp_sequence = join "", @entry;

	$curr_length = length($tmp_sequence);
	$tmp_sequence = "";
	
	if ($curr_length < $SHORTLONG) {
		print SHORT ">" . $header . "\n";
		print SHORT $sequence . "\n";
	} else {
		print LONG ">" . $header . "\n";
		print LONG $sequence . "\n";
	}

	
}
close FH;

close SHORT;
close LONG;


# split input2 into infividual entries
# again, determine the sizes of all input files - this is necessary when I later
# need to convert SOAP output into PSL format
%sizehash = ();
open FH, $ARGV[1] or die "ioerror: $!";
$foo = <FH>; # ignore first "virtual" entry
while ($foo = <FH>) {

	chop $foo;
	
	@entry = split /\n/, $foo;
	$header = $entry[0];
	
	$sequence = "";

	shift @entry;

	$sequence = join "\n", @entry;
	
	$tmp_sequence = join "", @entry;
	# $tmp_sequence =~ tr/N//d;
	# $tmp_sequence =~ tr/n//d;

	$curr_length = length($tmp_sequence);
	$tmp_sequence = "";
	
	# the current FASTA entry is now saved in '$header' and '$sequence', respectively

	# store the length
	$sizehash{$header} = $curr_length;
	
	open(F, ">$tmpdir/db/$header.fa") or die "ioerror: $!";
	print F ">" . $header . "\n";
	print F $sequence . "\n";
	close F;
	
}
close FH;


# ALIGNMENT
	
# BLAT long.fa against each file
@files = <$tmpdir/db/*.fa>;
foreach $f (@files) {
	@s = split /\//, $f;
	$last = pop @s;
	($name, $extension) = split /\./, $last;
	
	$commandstring = "$path_to_blat/blat -noHead $f $tmpdir/long.fa $tmpdir/out_$name.psl";
	print "COMMAND: ", $commandstring , "\n";
	system($commandstring);
	
}

# SOAP short.fa against each file
foreach $f (@files) {
	@s = split /\//, $f;
	$last = pop @s;
	($name, $extension) = split /\./, $last;
	
	$commandstring = "$path_to_soap/soap -a $tmpdir/short.fa -d $f -o $tmpdir/out_$name.soap -s 6";
	print "COMMAND: ", $commandstring , "\n";
	system($commandstring);
	
}


# CONVERSION, FINDING BEST HITS AND CLEANUP

# convert soap outfiles into PSL format
$/ = "\n";
@files = <$tmpdir/*.soap>;
open SOAPOUT, ">$tmpdir/soapout.psl" or die "ioerror: $!";
foreach $f (@files) {
	open SOAP, $f or die "ioerror: $!";
	while ($soap = <SOAP>) {
		chomp $soap;
		@col = split /\t/, $soap;
		if ($col[9] eq "0") { # perfect match
			if ($col[3] eq "1") { # number of equal best hits is one (e.g. unique hit: convert to PSL!
		
				# SOAP column										# BLAT meaning
				
				print SOAPOUT "$col[5]\t";							# Number of bases that match that aren't repeats
				print SOAPOUT "0\t";								# Number of bases that don't match
				print SOAPOUT "0\t";								# Number of bases that match but are part of repeats
				print SOAPOUT "0\t";								# Number of 'N' bases
				print SOAPOUT "0\t";								# Number of inserts in query
				print SOAPOUT "0\t";								# Number of bases inserted in query
				print SOAPOUT "0\t";								# Number of inserts in target
				print SOAPOUT "0\t";								# Number of bases inserted in target
				print SOAPOUT "$col[6]\t";							# + or - for query strand, optionally followed by + or ? for target strand
				print SOAPOUT "$col[0]\t";							# Query sequence name
				print SOAPOUT "$col[5]\t";							# Query sequence size
				print SOAPOUT "0\t";								# Alignment start position in query
				print SOAPOUT "$col[5]\t";							# Alignment end position in query
				print SOAPOUT "$col[7]\t";							# Target sequence name
				print SOAPOUT "" . $sizehash{$col[7]} . "\t";		# Target sequence size
				print SOAPOUT "" . ($col[8] - 1) . "\t";			# Alignment start position in target
				print SOAPOUT "" . ($col[8] - 1 + $col[5]) . "\t";	# Alignment end position in target
				print SOAPOUT "" . "1\t";							# Number of blocks in alignment. A block contains no gaps.
				print SOAPOUT "$col[5],\t";							# Size of each block in a comma separated list
				print SOAPOUT "0,\t";								# Start of each block in query in a comma separated list
				print SOAPOUT "" . ($col[8] - 1) . ",\n";			# Start of each block in target in a comma separated list
			}
		}
	
	} # while reading a line
	close SOAP;

} # for each file
close SOAPOUT;


# sort (this implicitly concatenates all PSL files) and filter for unique entries
	# pslSort -nohead dirs outFile.psl foo/ ggms/
	# pslReps -nohead -ignoreSize -singleHit outFile.psl final.psl final.psr
system ("$path_to_blat/pslSort -nohead dirs outFile.psl $tmpdir/tempDir $tmpdir/");
system ("$path_to_blat/pslReps -nohead -ignoreSize -singleHit outFile.psl ./final.psl final.psr");

# cleanup
$commandstring = "rm outFile.psl";
print "COMMAND: ", $commandstring , "\n";
system($commandstring);

# comment this if you want to keep repeat info
$commandstring = "rm final.psr";
print "COMMAND: ", $commandstring , "\n";
system($commandstring);

$commandstring = "rm -rf $tmpdir";
print "COMMAND: ", $commandstring , "\n";
system($commandstring);

