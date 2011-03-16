#!/usr/bin/perl -w

use strict;
use Storable qw(store);

# Gets all the sub-directories under the specified directory
# Either human or mouse directory must be specified.
sub get_dirs($){

    my $dir = shift;

    local(*DIR);
    my @dirs;
   
    opendir(DIR, $dir) || die "Cannot open \"$dir\": $!";
    
    my @files_or_dirs = readdir(DIR);
    
    foreach(@files_or_dirs){
	if(-d "$dir/$_" && $_ !~ /^\./){ 
	    push(@dirs, $_); 
	}
    }
    
    closedir(DIR);
    return @dirs;

}

# Reads PPI information in "info.txt" and returns bait, preys and errors.
# Preys are returned as a reference to hash in which there are pairs of
# Locus IDs and Gene names.
sub read_IVV_PPI($){
    my $ivv_file = shift;
    my %preys;
    local(*FH);
    open(FH, $ivv_file) || die "Cannot open \"$ivv_file:\": $!";


    my $condition = <FH>; chomp $condition;
    my $bait = <FH>; chomp $bait;
    my $d1 = <FH>;
    my $d2 = <FH>;
    my $d3 = <FH>;
    my $dummy1 = <FH>;

    my @errors = ();

    while(<FH>){
	chomp;
	if(!/^[0-9]+\t/){ push(@errors, "$ivv_file [ $_ ]"); }
        else {
	    my @r = split(/\t/);
	    $preys{ $r[0] } = $r[1];
	}
    }
    close(FH);
    return($condition, $bait, \%preys, [@errors]);

}

sub embed_char($$$){

    my($str, $char, $nchar) = @_;
    my $result = "";

    for(my $i = 0;$i < length($str);$i ++){
	$result .= substr($str, $i, 1);
	if(($i+1) % $nchar == 0 && $i < length($str) - 1){
	    $result .= $char;
	}
    }
    return $result;
}


# Reads sequences from the specified multi FASTA file and
# returns sequences as hash in which pairs of headers and
# sequences are stored.
sub mfasta_read_IVV($){

    my $filename = shift;
    local(*FH);
    
    open(FH, $filename) || die "Cannot open \"$filename\": $!";

    my $header;
    my %seq;
    while(<FH>){
	chomp;
	if(/^>(.*)/){ 
	    $header = $1; 
	    $header =~ s/^lcl\|//g;
	    if(defined($seq{ $header })){ delete $seq{ $header }; }
	}
	elsif(defined($header) && 
	      ($header =~ /RefSeq/ || $header =~ /seq_cDNA/)){
	    if(!defined($seq{ $header })){
		$seq{ $header } = $_;
	    }
	    else { $seq{ $header } .= $_; }
	}
    }
    close FH;
    return %seq;

}


# Reads sequences from the specified multi FASTA file and
# returns sequences as hash in which pairs of headers and
# sequences are stored.
# RefSeq and IVV not checked.
sub mfasta_read_simple($){

    my $filename = shift;
    local(*FH);
    
    open(FH, $filename) || die "Cannot open \"$filename\": $!";

    my $header;
    my %seq;
    while(<FH>){
	chomp;
	if(/^>(.*)/){ 
	    $header = $1; 
	    if(defined($seq{ $header })){ delete $seq{ $header }; }
	}
	elsif(defined($header)){
	    if(!defined($seq{ $header })){
		$seq{ $header } = $_;
	    }
	    else { $seq{ $header } .= $_; }
	}
    }
    close FH;
    return %seq;

}


sub get_iwas_refseq1($){

    my $filename = shift;
    local *FH;

    open(FH, $filename) || die "Cannot open \"$filename\": $!";
    my @header = split(/[>\|]/, <FH>);
    shift @header;
    my($refseqid, $geneid, $symbol) = @header;
    my $sequence = "";

#    print join("\t", $refseqid, $geneid, $symbol), "\n";

    while(1){

	my $num = <FH>;
	my $seq = <FH>;
	my $f1 = <FH>;
	my $f2 = <FH>;
	my $f3 = <FH>;
	my $sp = <FH>;

	if($num =~ /[^\d\s]/){ last; }

	chomp $seq;
	$sequence .= $seq;

    }

    close FH;

    return($sequence, $refseqid, $geneid, $symbol);

}

sub get_iwas_ivv1($){
    
    my $filename = shift;
    local *FH;
    
    open(FH, $filename) || die "Cannot open \"$filename\": $!";
    my $header = <FH>;
    $header =~ /^>(\S+)/;
    my $seqid = $1;

    my $sequence = "";
    while(<FH>){
	chomp;
	if(! /\S/){ last; }
	s/\s//g;
	$sequence .= $_;
    }

    close FH;

    return($sequence, $seqid);

}


sub get_bait_info($){

    my $bait_info_file = shift;
    local(*FH);
    my %bait_info;

    open(FH, $bait_info_file) || die "Cannot open \"$bait_info_file:\": $!";
    while(<FH>){
	chomp;
	if($_ !~ /\S/){ next; }
	my($bait, $baitid, $seqfile) = split(/\t/);
	$bait_info{"BaitID"}->{ $bait } = $baitid;

	my %seq = mfasta_read_simple($seqfile);
	my @headers = keys %seq;
	
	$bait_info{"Sequence"}->{ $baitid } = $seq{ $headers[0] };
	$bait_info{"Header"}->{ $baitid } = $headers[0];
    }
    
    close FH;

    return %bait_info;
}


# Reads quality file of sequences, i.e., "iwas_status.tab"
sub read_seq_info1($){

    my $filename = shift;
    my $seq_info;
    my $seq_info_header;

    local *FH;
    open(FH, $filename) || die "Cannot open \"$filename\": $!";
    my $first_line = <FH>;
    chomp $first_line;
    
    @$seq_info_header = split(/\t/, $first_line);

    while(<FH>){
	chomp;
	my @r = split(/\t/);
	my $seqid;

	foreach my $i (0..$#$seq_info_header){
	    if($seq_info_header->[ $i ] eq "seqno"){
		$seqid = $r[$i]; # There was "_cDNA.fan"
		last;
	    }
	}

	foreach my $i (0..$#$seq_info_header){
	    my $colname = $seq_info_header->[ $i ];
	    if(defined($r[ $i ])){
		$seq_info->{ $seqid }->{ $colname } = $r[ $i ];
#		print join("\t", $seqid, $colname, $r[$i]), "\n";
	    }
	}
    }

    close FH;

    return($seq_info, $seq_info_header);

}

sub record_quality_info($$$$){
    my $dir_name = shift;
    my $seq_info = shift;
    my $seq_info_each = shift;
    my $seq_info_header = shift;

    my %refseq_header_bait;

    $seq_info->{ "Exp_quality_header" }->{ $dir_name }
    = [ @$seq_info_header ];

    foreach my $seqid (keys %$seq_info_each){

	my $expno = $seq_info_each->{$seqid}->{"exp_no"};
	my $entrez_ID = $seq_info_each->{$seqid}->{"hit_locusid"};
	my $gene_symbol = $seq_info_each->{$seqid}->{"gene_symbol"};
	my $bait_ID = dir_name_to_bait_ID($expno);
	my $bait_symbol = $seq_info_each->{$seqid}->{ "BaitSymbol" };
	my $refseqid = $seq_info_each->{$seqid}->{"hit_refseqid"};
	$expno =~ /^([^_]+)/;
	my $library = $1;

	$bait_ID =~ /^([^_]+)/;
	my $bait_entrez_ID = $1;
	$seq_info->{ "Bait_Entrez_ID" }->{ $bait_ID } = $bait_entrez_ID;
	$seq_info->{ "Bait_symbol" }->{ $bait_ID } = $bait_symbol;

	$seq_info->{ "Prey_Seq_header_bait" }->{ $seqid } = $bait_ID;
	$seq_info->{ "Prey_Seq_header_exp" }->{ $seqid } = $expno;
	$seq_info->{ "Prey_Seq_header_lib" }->{ $seqid } = $library;

	if(defined($entrez_ID) && $entrez_ID ne ""){
	    $seq_info->{ "Prey_Seq_header_prey" }->{ $seqid }
	    = $entrez_ID;
	    $seq_info->{ "Entrez_ID_to_Symbol" }->{ $entrez_ID }
	    = $gene_symbol;
	}

	if(defined($refseqid) && $refseqid ne ""){
	    $seq_info->{ "Prey_Seq_header_RefSeq" }->{ $seqid }
	    = $refseqid;
	    $seq_info->{ "RefSeq_Entrez_ID" }->{ $refseqid } = $entrez_ID;
#	    print join("\t", $seqid, $expno, $refseqid, $entrez_ID), "\n";
	    $refseq_header_bait{ $refseqid }->{ $bait_ID } = "";
	}

	foreach my $colname (@$seq_info_header){
	    my $info
		= (defined($seq_info_each->{ $seqid }->{ $colname }) ?
		   $seq_info_each->{ $seqid }->{ $colname } : "");

	    push(@{$seq_info->{"Prey_Seq_quality"}->{ $seqid }},
		 $info);

#	    print join("\t", $seqid, $colname, $info), "\n";

	}

    }

    foreach my $refseqid (keys %refseq_header_bait){
	my @baits = (keys %{$refseq_header_bait{ $refseqid }});
	$seq_info->{ "RefSeq_header_bait" }->{ $refseqid } = [ @baits ];
    }

}


sub dir_name_to_bait_ID($){

    my $dir_name = shift;

    my @ids = split(/_/, $dir_name);
    my $bait_ID = "$ids[1]_$ids[2]";

    return $bait_ID;

}

sub ivv_info_file_to_hash($){
    my $w_seq_info_file = shift;
    my %seq_info;

    open(FH, $w_seq_info_file) || 
	die "Cannot open \"$w_seq_info_file\": $!";
    while(<FH>){
	chomp;
	my @r = split(/\t/);
	if($r[0] eq "[ Prey Info ]"){
	    my($type, $seq_header, $entrez_ID, $gene_symbol,
	       $bait_ID, $experiment, $refseqid, $library,
	       @quality) = @r;
	    
	    if(defined($entrez_ID) && $entrez_ID ne ""){
		push(@{$seq_info{ "Entrez_ID_to_Seq_Header" }
		       ->{ $entrez_ID }}, $seq_header);
		$seq_info{ "Entrez_ID_to_Symbol" }
		->{ $entrez_ID } = $gene_symbol;
		$seq_info{ "Prey_Seq_header_prey" }
		->{ $seq_header } = $entrez_ID;
	    }
	    if(defined($refseqid) && $refseqid ne ""){
		$seq_info{ "Prey_Seq_header_RefSeq" }
		->{ $seq_header } = $refseqid;
	    }

	    $seq_info{ "Prey_Seq_header_bait" }->{ $seq_header } = $bait_ID;
	    $seq_info{ "Prey_Seq_quality" }->{ $seq_header } = [ @quality ];
	    $seq_info{ "Prey_Seq_header_exp" }->{ $seq_header } = $experiment;
	    $seq_info{ "Prey_Seq_header_lib" }->{ $seq_header } = $library;
	}

	elsif($r[0] eq "[ Exp quality header ]"){
	    my($type, $experiment, @quality_header) = @r;
	    $seq_info{ "Exp_quality_header" }->{ $experiment }
	    = [ @quality_header ];
	}

	elsif($r[0] eq "[ Exp cond ]"){
	    my($type, $experiment, $condition) = @r;
	    $seq_info{ "Exp_condition" }->{ $experiment } = $condition;
	}

	elsif($r[0] eq "[ RefSeq Info ]"){
	    my($type, $seq_header, $entrez_ID, $gene_symbol, $bait_ID)= @r;
	    push(@{$seq_info{ "Entrez_ID_to_Seq_Header" }->{ $entrez_ID }},
		 $seq_header);
	    $seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID } = $gene_symbol;
	    $seq_info{ "RefSeq_Entrez_ID" }->{ $seq_header } = $entrez_ID;
	    push(@{$seq_info{ "RefSeq_header_bait" }->{ $seq_header }},
		 $bait_ID);
	}
	elsif($r[0] eq "[ Error ]" && $r[1] eq "No prey sequence"){
	    my($type, $message, $bait_ID, $prey_num, $dir_name)
		= @r;
	    $seq_info{ "Prey_Seq_header_No_Seq" }
	    ->{ $bait_ID }->{ $prey_num } = $dir_name;
	}
	elsif($r[0] eq "[ Bait Info ]"){
	    my($type, $bait_ID, $entrez_ID, $bait_symb) = @r;
	    $seq_info{ "Bait_Entrez_ID" }->{ $bait_ID } = $entrez_ID;
	    $seq_info{ "Bait_symbol" }->{ $bait_ID } = $bait_symb;
	    @{$seq_info{ "Entrez_ID_to_Seq_Header" }->{ $bait_ID }}
	    = ($bait_ID);
	}
	
    }

    close FH;
    return \%seq_info;

}

sub store_ivv_info($$$$){
    my $w_seq_info_file = shift;
    my $w_seq_file = shift;
    my $w_seq_info_file_bin = shift;
    my $w_seq_file_bin = shift;

    my $seq_info = ivv_info_file_to_hash($w_seq_info_file);
    store($seq_info, $w_seq_info_file_bin);
    
    my %stored_ivv_seq = mfasta_read_simple($w_seq_file);
    my %simple_ivv_seq;
    foreach my $header (keys %stored_ivv_seq){
	$header =~ /lcl\|([^ ]+)/; my $id = $1;
	$simple_ivv_seq{ $id } = $stored_ivv_seq{ $header };
    }

    store(\%simple_ivv_seq, $w_seq_file_bin);


}


################################################
# MAIN ROUTINE
################################################


my $ivv_dir = shift @ARGV;

my %seq_info;
my %seq_ivv;
my(%seq_quality, @seq_quality_header);

local *WH;

my $test_counter_max = 1000000;

# ---------------------------------------------
# Names for output file
#

my $output_base_fn = $ivv_dir;
$output_base_fn =~ s/\/+$//g;
$output_base_fn =~ s/[^\/]*\///g;
my $w_seq_info_file = "ivv_${output_base_fn}_info";
my $w_seq_file = "ivv_${output_base_fn}.tfa";
my $w_seq_prey_file = "ivv_${output_base_fn}_prey.tfa";
my $w_seq_info_file_bin = "ivv_${output_base_fn}_info.bin";
my $w_seq_file_bin = "ivv_${output_base_fn}_seq.bin";

# ---------------------------------------------


# ---------------------------------------------
# If output files already exist, just make binary files
#

if(-e $w_seq_info_file && -e $w_seq_file){
    print "Files \"$w_seq_info_file\" and \"$w_seq_file\" already exist.\n";
    print "Just making binary files.\n";
    store_ivv_info($w_seq_info_file, $w_seq_file,
		   $w_seq_info_file_bin, $w_seq_file_bin);
    exit;
}

# ---------------------------------------------
# Gets information on baits and their sequences.
#

my @ivv_dirs = get_dirs($ivv_dir);

my $test_counter = 0;

print "Reading bait information ...\n";
foreach my $dir_name (@ivv_dirs){

    print "Looking directory $dir_name for bait information ...\n";
#    if($dir_name =~ /Initial/){
#	print "This directory is ignored.\n";
#	next;
#    }

    my @ids = split(/_/, $dir_name);
    my $bait_ID = "$ids[1]_$ids[2]";
    # This will be an bait ID as well as a part of filename

    my $bait_seq_file = "${ivv_dir}/${dir_name}/ivv/bait/${bait_ID}.fa";

    if(-r $bait_seq_file){
	my %seq = mfasta_read_simple($bait_seq_file);
	my($header) = (keys %seq);
	$seq_ivv{"Bait"}->{ $bait_ID } = $seq{ $header };
    }
    if(++ $test_counter >= $test_counter_max){ last; }
}

# ---------------------------------------------

# ---------------------------------------------
# Gets information on experimental condition from gminer
#

$test_counter = 0;

print "Reading experimental condition  ...\n";
foreach my $dir_name (@ivv_dirs){

    print "Looking directory $dir_name for experimental condition ...\n";
#    if($dir_name =~ /Initial/){
#	print "This directory is ignored.\n";
#	next;
#    }

    my $bait_ID = dir_name_to_bait_ID($dir_name);

    my($condition, $bait, $preys, $errors)
	= read_IVV_PPI("${ivv_dir}/${dir_name}/gminer/info.txt");
    $seq_info{ "Exp_condition" }->{ $dir_name } = $condition;

    if(++ $test_counter >= $test_counter_max){ last; }
}

# ---------------------------------------------

# ---------------------------------------------
# Gets information on sequences qualities
#

$test_counter = 0;

print "Reading sequence quality information ...\n";

foreach my $dir_name (@ivv_dirs){

    print "Looking directory $dir_name for sequence quality information ...\n";
#    if($dir_name =~ /Initial/){
#	print "This directory is ignored.\n";
#	next;
#    }

    my $bait_ID = dir_name_to_bait_ID($dir_name);

    my($seq_info_each, $seq_info_header) = 
	read_seq_info1("${ivv_dir}/${dir_name}/ivv/iwas_status.tab");

    record_quality_info($dir_name, \%seq_info, 
			$seq_info_each, $seq_info_header);
    

    if(++ $test_counter >= $test_counter_max){ last; }
}

# ---------------------------------------------

# ---------------------------------------------
# Gets prey sequences according to quality information
#

foreach my $seqid (keys %{$seq_info{ "Prey_Seq_header_exp" }}){

    my $expno = $seq_info{ "Prey_Seq_header_exp" }->{ $seqid };
    my $ivv_seq_file
	= "${ivv_dir}/${expno}/ivv/fasta/${seqid}_ivv.txt";
#    my $cDNA_seq_file
#	= "${ivv_dir}/${expno}/ivv/fasta/${seqid}_cDNA.txt";
    my $refseq_file
	= "${ivv_dir}/${expno}/ivv/fasta/${seqid}_refseq_fs.txt";
    
    my($ivv_seq, $seqid_ivv) = get_iwas_ivv1($ivv_seq_file);
#    my($cDNA_seq,$seqid_cDNA) = get_iwas_ivv1($cDNA_seq_file);

    my $bait_ID = dir_name_to_bait_ID($expno);

    if(defined($seqid_ivv)){ 
	$seq_ivv{ "Prey" }->{ $seqid_ivv } = $ivv_seq;
    }
    else {
	$seq_info{ "Prey_Seq_header_No_Seq" }->{ $seqid } = $expno;
    }

    if(defined($seqid_ivv) &&
       defined($seq_info{ "Prey_Seq_header_RefSeq" }->{ $seqid_ivv }) &&
       -e $refseq_file){
	my($refseq, $refseqid, $entrez_ID, $symb)
	    = get_iwas_refseq1($refseq_file);
	$seq_ivv{ "RefSeq" }->{ $refseqid } = $refseq;
    }
    
}

# ---------------------------------------------

# ---------------------------------------------
# Outputs information on sequences.
#

open(WH, "> $w_seq_info_file");

foreach my $bait_ID (keys %{$seq_info{ "Bait_symbol" }}){
    my $entrez_ID = $seq_info{ "Bait_Entrez_ID" }->{ $bait_ID };
    my $bait_symb = $seq_info{ "Bait_symbol" }->{ $bait_ID };

    print WH join("\t", "[ Bait Info ]",
		  $bait_ID, $entrez_ID, $bait_symb), "\n";
}


foreach my $seq_header (keys %{$seq_info{ "Prey_Seq_quality" }}){

    my $entrez_ID = 
	(defined($seq_info{ "Prey_Seq_header_prey" }->{ $seq_header })
	 ? $seq_info{ "Prey_Seq_header_prey" }->{ $seq_header }
	 : "");

    my $gene_symbol =
	(defined($seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID })
	 ? $seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID }
	 : "");

    my $bait = 
	(defined($seq_info{ "Prey_Seq_header_bait" }->{ $seq_header })
	 ? $seq_info{ "Prey_Seq_header_bait" }->{ $seq_header }
	 : "");

    my $experiment =
	(defined($seq_info{ "Prey_Seq_header_exp" }
		 ->{ $seq_header })
	 ? $seq_info{ "Prey_Seq_header_exp" }->{ $seq_header }
	 : "");

    my $refseqid = 
	(defined($seq_info{ "Prey_Seq_header_RefSeq" }
		 ->{ $seq_header })
	 ? $seq_info{ "Prey_Seq_header_RefSeq" }->{ $seq_header }
	 : "");

    my $library = 
	(defined($seq_info{ "Prey_Seq_header_lib" }
		 ->{ $seq_header })
	 ? $seq_info{ "Prey_Seq_header_lib" }->{ $seq_header }
	 : "");

    my @quality = 
	(defined($seq_info{ "Prey_Seq_quality" }->{ $seq_header }) ?
	 @{$seq_info{ "Prey_Seq_quality" }->{ $seq_header }} : "");

    print WH join("\t", "[ Prey Info ]",
		  $seq_header, $entrez_ID, $gene_symbol,
		  $bait, $experiment, $refseqid, $library,
		  @quality), "\n";
}

foreach my $expno (keys %{$seq_info{ "Exp_quality_header" }}){

    my @quality_header = 
	(defined($seq_info{ "Exp_quality_header" }->{ $expno }) ?
	 @{$seq_info{ "Exp_quality_header" }->{ $expno }} : "");

    print WH join("\t", "[ Exp quality header ]", $expno,
		  @quality_header), "\n";

}

foreach my $expno (keys %{$seq_info{ "Exp_condition" }}){
    
    my $cond = $seq_info{ "Exp_condition" }->{ $expno };

    print WH join("\t", "[ Exp cond ]", $expno, $cond), "\n";

}


foreach my $seq_header (keys %{$seq_info{ "RefSeq_header_bait" }}){
    foreach my $bait
	(@{$seq_info{ "RefSeq_header_bait" }->{ $seq_header }}){
	    my $entrez_ID 
		= $seq_info{ "RefSeq_Entrez_ID" }->{ $seq_header };
	    my $gene_symbol
		= $seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID };

	    print WH join("\t", "[ RefSeq Info ]",
			  $seq_header, $entrez_ID, $gene_symbol, $bait), "\n";
	}
}

foreach my $seq_header (keys %{$seq_info{ "Prey_Seq_header_No_Seq" }}){
    my $dir_name = 
	$seq_info{ "Prey_Seq_header_No_Seq" }->{ $seq_header };
    my $bait_ID = $seq_info{ "Prey_Seq_header_bait" }->{ $seq_header };
    
    print WH join("\t", "[ Error ]", "No prey sequence",
		  $bait_ID, $seq_header, $dir_name), "\n";

}

close WH;

# ---------------------------------------------


# ---------------------------------------------
# Outputs sequences
#

open(WH, "> $w_seq_file");

foreach my $id (keys %{$seq_ivv{ "Bait" }}){
    my $entrez_ID = $seq_info{ "Bait_Entrez_ID" }-> { $id };
    my $gene_symbol = $seq_info{ "Bait_symbol" }->{ $id };

    print WH ">lcl|$id $entrez_ID $gene_symbol Bait sequence\n";
#    print ">lcl|$id $entrez_ID $gene_symbol Bait sequence\n";
    print WH embed_char($seq_ivv{ "Bait" }->{ $id }, "\n", 70), "\n";
    print WH "\n";
}

foreach my $id (keys %{$seq_ivv{ "Prey" }}){
    my $entrez_ID = $seq_info{ "Prey_Seq_header_prey" }->{ $id };
    if(!defined($entrez_ID)){ $entrez_ID = "-"; }
    my $gene_symbol = $seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID };
    if(!defined($gene_symbol)){ $gene_symbol = "-"; }
    my $bait_ID = $seq_info{ "Prey_Seq_header_bait" }->{ $id };
    my $dir_name = $seq_info{ "Prey_Seq_header_exp" }->{ $id };
    my $refseqid = $seq_info{ "Prey_Seq_header_RefSeq" }->{ $id };
    if(!defined($refseqid)){ $refseqid = "-"; }

    print WH ">lcl|$id $entrez_ID $gene_symbol $bait_ID $dir_name $refseqid\n";
    print WH embed_char($seq_ivv{ "Prey" }->{ $id }, "\n", 70), "\n";
    print WH "\n";
}

foreach my $id (keys %{$seq_ivv{ "RefSeq" }}){
    my $entrez_ID = $seq_info{ "RefSeq_Entrez_ID" }->{ $id };

    if(!defined($entrez_ID)){ print "$id undefined...\n"; }

    my $gene_symbol = $seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID };
    my $bait_ID = "(RefSeq)";
    my $dir_name = "-";

    print WH ">lcl|$id $entrez_ID $gene_symbol $bait_ID $dir_name\n";
    print WH embed_char($seq_ivv{ "RefSeq" }->{ $id }, "\n", 70), "\n";
    print WH "\n";
}

close WH;

open(WH, "> $w_seq_prey_file");

foreach my $id (keys %{$seq_ivv{ "Prey" }}){
    my $entrez_ID = $seq_info{ "Prey_Seq_header_prey" }->{ $id };
    if(!defined($entrez_ID)){ $entrez_ID = "-"; }
    my $gene_symbol = $seq_info{ "Entrez_ID_to_Symbol" }->{ $entrez_ID };
    if(!defined($gene_symbol)){ $gene_symbol = "-"; }
    my $bait_ID = $seq_info{ "Prey_Seq_header_bait" }->{ $id };
    my $dir_name = $seq_info{ "Prey_Seq_header_exp" }->{ $id };
    my $refseqid = $seq_info{ "Prey_Seq_header_RefSeq" }->{ $id };
    if(!defined($refseqid)){ $refseqid = "-"; }

    print WH ">lcl|$id $entrez_ID $gene_symbol $bait_ID $dir_name $refseqid\n";
    print WH embed_char($seq_ivv{ "Prey" }->{ $id }, "\n", 70), "\n";
    print WH "\n";
}

close WH;

# ---------------------------------------------


store_ivv_info($w_seq_info_file, $w_seq_file,
	       $w_seq_info_file_bin, $w_seq_file_bin);


print "Process completed.\n";

exit;
