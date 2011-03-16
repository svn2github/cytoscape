#!/usr/bin/perl -w

use strict;

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

sub mfasta_read($$$){

    my $filename = shift;
    my $seq = shift;
    my $header_count = shift;;


    local(*FH);
    
    open(FH, $filename) || die "Cannot open \"$filename\": $!";

    my $header;

    while(<FH>){
	chomp;
	if(/^>(.*)/){ 
	    $header = $1;
	    $header =~ /^(\S+)/;
	    my $id = $1;
	    $header_count->{ $id } ++;
	    if(defined($seq->{ $header })){
		delete $seq->{ $header };
	    }
	}
	elsif(defined($header)){
	    if(!defined($seq->{ $header })){
		$seq->{ $header } = $_;
	    }
	    else { $seq->{ $header } .= $_; }
	}
    }
    close FH;
    return($seq, $header_count);

}

local(*FH, *WH, *WH_sh);
my %seq;
my %header_count;

foreach my $fasta_file (@ARGV){
    mfasta_read($fasta_file, \%seq, \%header_count);
}

my @headers = keys %seq;
print "Number of sequences: ", $#headers + 1, "\n";

for my $header (@headers){
    if(length($seq{ $header }) < 9){
	print "Short sequence (", length($seq{ $header }),
	") : ", $header, " : ", $seq{ $header }, "\n";
	delete $seq{ $header };
    }
}

@headers = keys %seq;
print "Number of remained sequences: ", $#headers + 1, "\n";
print "How many FASTA sub-files do you want to make? : ";

my $nfile = <STDIN>;
chomp $nfile;
my $nfile_each_std = int(($#headers+1)/$nfile);

print "Where is BLAST executable? (ex. /usr/local/bin/blastall) : ";
my $blast_exec = <STDIN>;
chomp $blast_exec;

print "Where is subject DB? : ";
my $subjdb = <STDIN>;
chomp $subjdb;

my $nheader = 0;
while($nheader <= $#headers){

    my $wfasta_file = join("_", @ARGV) . int($nheader / $nfile_each_std);

    open(WH, "> $wfasta_file");
        
    my $nfile_each;
    if($#headers - $nheader + 1 >= $nfile_each_std * 2){
	$nfile_each = $nfile_each_std;
    }
    else {
	$nfile_each = $#headers - $nheader + 1;
    }

    for(my $neach = 0;
	$neach < $nfile_each && $nheader + $neach <= $#headers;
	$neach ++){

	my $header = $headers[ $nheader+$neach ];
	my $seq = $seq{ $header };

	print WH ">$header\n";
	print WH embed_char($seq, "\n", 70), "\n";
	print WH "\n";

    }

    close WH;
    print "Created FASTA file $wfasta_file with $nfile_each sequences.\n";

    $subjdb =~ /\/([^\/]+)$/;
    my $subjdb_base = $1;

    my $output_shell = "${wfasta_file}_${subjdb_base}.sh";

    open(WH_sh, "> $output_shell");
    print WH_sh <<EOF;
# This shell script is automatically generated.
$blast_exec -p blastx -m7 -S 1 -F F -e 1.0e-3 -d $subjdb -i $wfasta_file > ${output_shell}_res

EOF
    close WH_sh;
    print "Generated $output_shell...\n";

    $nheader += $nfile_each;
    
}

foreach my $key (keys %header_count){

    if($header_count{ $key } > 1){
	print "Redundant ID ... $key ($header_count{$key})\n";
    }

}
