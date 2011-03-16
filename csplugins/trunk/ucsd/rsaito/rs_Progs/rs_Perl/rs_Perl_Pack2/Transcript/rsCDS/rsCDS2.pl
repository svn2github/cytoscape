#!/usr/bin/perl -w

use strict;

*::CONFIG_FILE = \ "./rsCDS.cnf";

sub rsCDS_help {

    print <<EOF;

************************************ 
* rsCDS, relational search for CDS *
************************************
                     March 8, 2005

Program written by Rintaro Saito (rsaito\@gsc.riken.go.jp).

Usage:

./rsCDS1.pl [FASTA formatted file] [corresponding BLAST result file]

Second parameter can be omitted.

Related reference:
Furuno M, Kasukawa T, Saito R, Adachi J,
Suzuki H, Baldarelli R, Hayashizaki Y, Okazaki Y.
CDS annotation in full-length cDNA sequence.
Genome Res. 2003 Jun;13(6B):1478-87.  

EOF

}

sub file_exist($){

    my($filename) = @_;
    local(*FH);

    open(FH, $filename) || return 0;
    close FH;
    return 1;

}

sub rev_str {

    my($str1) = @_;
    my($rev_str1);
    my($length, $i);
    
    $length = length($str1);
    $rev_str1 = "";

    for($i = length($str1) - 1;$i >= 0;$i --){
	$rev_str1 .= substr($str1, $i, 1);
    }
    
    return $rev_str1;

}

sub get_metric {

    my($line, $metric_a) = @_;
    my($num_start, $num_end, $metric);
    my($i, $j);
    
    $i = 0;
    while($i < length($line)){

	for(; $i < length($line); $i ++){
	    if(substr($line, $i, 1) =~ /[0-9]/){ last; }
	}
	if($i >= length($line)){ return; }
#	print "$i ";
	$num_start = $i;
	for(; $i < length($line); $i ++){
	    if(substr($line, $i, 1) !~ /[0-9]/){ last; }
	}
#	print "$i\n";
	$num_end = $i;
	$metric = substr($line, $num_start, $num_end - $num_start);
	
#	print "$metric\n";

	$$metric_a{ $num_end - 1 } = $metric;
    }

}



sub get_fasta_line {

    my($FH, $first_line, $query_metric, $subject_metric) = @_;

    my($query_seq, $match, $subject_seq);
    my(%query_m, %subject_m);
    my($query_seq_tmp, $query_seq_start_col);
    my($subject_seq_tmp, $subject_seq_start_col);
    my($start_col);
    my($line);
    my($i, $j);


    $line = $first_line;

    &get_metric($line, \%query_m);

    $query_seq_tmp = <$FH>;
    chop($query_seq_tmp);
    
    for($i = 0;$i < length($query_seq_tmp);$i ++){
	if(substr($query_seq_tmp, $i, 1) eq ' '){ last; }
    }
    if($i >= length($query_seq_tmp)){ 
	die "Abnormal query sequence line.\n";
    }
    for(;$i < length($query_seq_tmp);$i ++){
	if(substr($query_seq_tmp, $i, 1) ne ' '){ last; }
    }

    if($i >= length($query_seq_tmp)){ 
	die "Abnormal query sequence line.\n";
    }

    $query_seq_start_col = $i;

    $match = <$FH>;
    chop($match);

    $subject_seq_tmp = <$FH>;
    chop($subject_seq_tmp);

    for($i = 0;$i < length($subject_seq_tmp);$i ++){
	if(substr($subject_seq_tmp, $i, 1) eq ' '){ last; }
    }
    if($i >= length($subject_seq_tmp)){ 
	die "Abnormal subject sequence line.\n";
    }
    for(;$i < length($subject_seq_tmp);$i ++){
	if(substr($subject_seq_tmp, $i, 1) ne ' '){ last; }
    }

    if($i >= length($subject_seq_tmp)){ 
	die "Abnormal subject sequence line.\n";
    }
    $subject_seq_start_col = $i;

    if($query_seq_start_col <= $subject_seq_start_col){
	$start_col = $query_seq_start_col;
    }
    else { $start_col = $subject_seq_start_col; }

    $query_seq = substr($query_seq_tmp, $start_col);
    $match = substr($match, $start_col);
    $subject_seq = substr($subject_seq_tmp, $start_col);

#    print "$query_seq|\n";
#    print "$match|\n";
#    print "$subject_seq|\n";


    $line = <$FH>;
    &get_metric($line, \%subject_m);

    foreach $i ( keys(%query_m) ){
	$$query_metric{ $i - $start_col } = $query_m{ $i };
#	print "$i\t$query_m{ $i }\n";

    }

#    print "\n";

    foreach $i ( keys(%subject_m) ){
	$$subject_metric{ $i - $start_col } = $subject_m{ $i };
#	print "$i\t$subject_m{ $i }\n";
    }

    return ($query_seq, $match, $subject_seq);

}


sub get_fasta {

    my($FH, $query_met, $subject_met) = @_;
    my($line);
    my(%query_met_frag, %subject_met_frag);
    my($query_frag, $subject_frag, $match_frag);
    my($query, $subject, $match);
    my(%query_met, %subject_met);
    my($total_length);
    my($result);
    my($i);
    
    $total_length = 0;
    $query = "";
    $subject = "";
    $match = "";
    
    while(<$FH>){
	
	$line = $_;
	chop($line);
	
	if($line =~ /identity.*overlap/){ $result = $line; next; }
	if($line =~ /residue/){ last; }
	if($line =~ /[a-zA-Z]/){ next; }
	if($line eq ""){ next; }
	
	
	%query_met_frag = ();
	%subject_met_frag = ();
	
	($query_frag, $match_frag, $subject_frag) = 
	    &get_fasta_line(*$FH, $line, \%query_met_frag, \%subject_met_frag);
	
#	print "$query_frag|\n";
#	print "$match_frag|\n";
#	print "$subject_frag|\n";

	$query .= $query_frag;
	$match .= $match_frag;
	$subject .= $subject_frag;

	
	foreach $i ( keys(%query_met_frag) ){
	    $$query_met{ $i + $total_length } = $query_met_frag{ $i };
	}
	
	foreach $i ( keys(%subject_met_frag) ){
	    $$subject_met{ $i + $total_length } = $subject_met_frag{ $i };
	}

	$total_length += length($match_frag); # Temporary

    }

#    foreach $i ( keys(%query_met) ){
#	print "$i\t$query_met{ $i }\n";
#    }
#    print "\n";
#    foreach $i ( keys(%subject_met) ){
#	print "$i\t$subject_met{ $i }\n";
#    }

    return ($result, $query, $match, $subject, $total_length); 

}


sub print_fasta_res {

    my($query, $match, $subject, $total_length, 
       $query_met, $subject_met) = @_;
    my($i);


    print "Total length: $total_length\n";

    for($i = 0;$i < $total_length; $i ++){
	if(defined $$query_met{ $i }){
	    print "$$query_met{ $i }";
	    $i += int(log(1.0 * $$query_met{ $i }) / log(10.0));
	}
	else { print "_"; }
    }

    print "\n";
    print "$query|\n";
    print "$match|\n";
    print "$subject|\n";

    for($i = 0;$i < $total_length; $i ++){
	if(defined $$subject_met{ $i }){
	    print "$$subject_met{ $i }";
	    $i += int(log(1.0 * $$subject_met{ $i }) / log(10.0));
	}
	else { print "_"; }
    }
    print "\n";


}

sub read_seq {

    my($seqfile) = @_;
    my($sequence, $fragm);
    local *EACH_SEQ_FILE;

    open(EACH_SEQ_FILE, "$seqfile") || die "Cannot open file \"$seqfile\": $
!\n";

    $sequence = "";
    while(<EACH_SEQ_FILE>){

        if($_ !~ /^>/){
            $fragm = $_;
            $fragm =~ s/[^a-zA-Z]//g;
            $sequence .= $fragm;
        }
    }

    close EACH_SEQ_FILE;
    
    $sequence =~ tr/A-Z/a-z/;

    return $sequence;

}


sub fasta_overlap {

    my($result) = @_;
    my($query_o_start, $query_o_end);
    my($subject_o_start, $subject_o_end);

    $query_o_start = $result; 
    $query_o_start =~ s/.*\(//g; $query_o_start =~ s/\)//g;
    $subject_o_start = $query_o_start;

    $query_o_start =~ s/:.*//g; $query_o_end = $query_o_start;
    $subject_o_start =~ s/.*://g; $subject_o_end = $subject_o_start;

    $query_o_start =~ s/-.*//g; $query_o_end =~ s/.*-//g;
    $subject_o_start =~ s/-.*//g; $subject_o_end =~ s/.*-//g;

    return ($query_o_start, $query_o_end, $subject_o_start, $subject_o_end);

}

sub fasta_identity_overlap {

    my($result) = @_;
    my($identity, $overlap);

    $identity = $result;
    $identity =~ s/% identity.*//g;
    $identity =~ s/.* //g;

    $overlap = $result;
    $overlap =~ s/ aa overlap.*//g;
    $overlap =~ s/.* //g;

    return ($identity, $overlap);

}


sub get_start_stop_codon {

    my($query_o_start, $query_o_end, $subject_o_start, 
       $sequence, $first_amino) = @_;
    my($start_codon, $stop_codon);
    my($start_pos_mod, $stop_pos_mod);
 
    my($seq_len);

    $seq_len = length($sequence);
    $start_pos_mod = 0;
    $stop_pos_mod = 0;

    if($query_o_start > $query_o_end){
	$sequence = &rev_str($sequence);
	$sequence =~ tr/atcg/tagc/;
	$query_o_start = $seq_len - $query_o_start + 1;
	$query_o_end = $seq_len - $query_o_end + 1;
    }

    if($query_o_start > 0 && $query_o_start + 2 <= $seq_len){
	$start_codon = substr($sequence, $query_o_start - 1, 3);
    }
    else { $start_codon = "???"; }

    if($query_o_end > 0 && $query_o_end + 3 <= $seq_len){
	$stop_codon  = substr($sequence, $query_o_end - 1 + 1, 3);
    }
    else { $stop_codon = "???"; }

    if($start_codon ne "atg" && $query_o_start - 3 > 0 &&
       $subject_o_start == 1 && # $first_amino ne "M" &&
       substr($sequence, $query_o_start - 1 - 3, 3) eq "atg"){
	$start_codon = "atg";
	$start_pos_mod = -3;
    }

    return ($start_codon, $stop_codon, $start_pos_mod, $stop_pos_mod);

}

sub get_start_stop_codon2($$$$) {

    my($query_o_start, $query_o_end, $subject_o_start, $sequence) = @_;

    my($start_codon, $stop_codon);
    my($predicted_start, $predicted_end);
    my($seq_len);

    my @stop_codons = ("taa", "tag", "tga");

    my($grep_res);
    my($triplet);
    my($i, $j, $k);

    $seq_len = length($sequence);

    if($query_o_start > $query_o_end){
	$sequence = reverse($sequence);
	$sequence =~ tr/atcg/tagc/;
	$query_o_start = $seq_len - $query_o_start + 1;
	$query_o_end = $seq_len - $query_o_end + 1;
    }


    if($query_o_start > 0 && $query_o_start + 2 <= $seq_len){
	$start_codon = substr($sequence, $query_o_start - 1, 3);
    }
    else { $start_codon = "???"; }

    if($query_o_end > 0 && $query_o_end + 3 <= $seq_len){
	$stop_codon  = substr($sequence, $query_o_end - 1 + 1, 3);
    }
    else { $stop_codon = "???"; }

    if($start_codon eq "atg" && $subject_o_start == 1){
	$predicted_start = $query_o_start;
    }
    elsif($start_codon ne "atg" && $query_o_start - 3 > 0 &&
       $subject_o_start == 1 && # $first_amino ne "M" &&
       substr($sequence, $query_o_start - 1 - 3, 3) eq "atg"){
	$start_codon = "atg";
	$predicted_start = $query_o_start - 3;
    }
    else {
	for($i = $query_o_start; $i >= 0;$i -= 3){
	    $triplet = substr($sequence, $i - 1, 3);
#	print "$triplet\n";
	    eval "\$grep_res = grep(/$triplet/, \@stop_codons)";
	    if($grep_res){ last; }
	}
	
	for($i += 3;$i < $query_o_start;$i += 3){
	    $triplet = substr($sequence, $i - 1, 3);
#	print "$triplet\n";
	    if($triplet eq "atg"){ last; }
	}

	if($i < $query_o_start){ $predicted_start = $i; }
	else { $predicted_start = "<$query_o_start"; }
    }

    for($i = $query_o_end + 1; $i + 2 <= $seq_len; $i += 3){
	$triplet = substr($sequence, $i - 1, 3);
	eval "\$grep_res = grep(/$triplet/, \@stop_codons)";
	if($grep_res){ last; }
    }
    if($i + 2 > $seq_len){ $predicted_end = ">$seq_len"; }
    else { $predicted_end = $i + 2; }

    return ($start_codon, $stop_codon, $predicted_start, $predicted_end);

}


sub find_nearest_pat {

    my($sequence, $pat, $from, $step) = @_;
    my($i, $j, $pat_len, $seq_len);
    my @near_list = ();

    $seq_len = length($sequence);
    $pat_len = length($pat);
    for($i = $from; 
	$i >= 0 && $i + $pat_len - 1 < $seq_len;
	$i += $step){

	if(substr($sequence, $i, $pat_len) eq $pat){
	    push(@near_list, $i - $from);
	}
    }

    return @near_list;

}

sub find_nearest_pats {

    my($sequence, $pat, $from, $step) = @_;
    my($i, $j, $pat_len, $seq_len);
    my @near_list = ();

    $seq_len = length($sequence);
    for($i = $from; 
	$i >= 0 && $i < $seq_len;
	$i += $step){
	
	foreach $j (@$pat){
	    $pat_len = length($j);
	    if($i + $pat_len - 1 < $seq_len &&
	       substr($sequence, $i, $pat_len) eq $j){
		push(@near_list, $i - $from);
		last;
	    }
	}
    }
    return @near_list;
}

sub find_nearest_pat_consider_stop_codon {

    my($sequence, $pat, $from, $step) = @_;
    my($i, $j, $pat_len, $seq_len, $up_range);
    my($triplet);
    my @near_list = ();

    if($step < 0){
	for($i = $from;$i >= 0;$i -= 3){
	    $triplet = substr($sequence, $i, 3);
	    if($triplet eq "taa" || $triplet eq "tag" || $triplet eq "tga"){
		last;
	    }
	}
	if($i >= 0){ $up_range = $i + 3; }
	else { $up_range = 0; }
    }
    else { $up_range = 0; }

#    print "up range: $up_range\n";

    $seq_len = length($sequence);
    $pat_len = length($pat);
    for($i = $from; 
	$i >= $up_range && $i + $pat_len - 1 < $seq_len;
	$i += $step){

	if(substr($sequence, $i, $pat_len) eq $pat){
	    push(@near_list, $i - $from);
	}
    }

    return @near_list;

}

sub g_fasta {

    my($seqfile, $fastafile) = @_;

    my(%query_met, %subject_met);
    my($result, $query, $match, $subject, $total_length);

    my($query_o_start, $query_o_end);
    my($subject_o_start, $subject_o_end);
    my($subject_length);
    my($sequence, $c_sequence);
    my($subject_title);
    my($identity, $identity_ungapped, $overlap);
    my($fshift_query, $fshift_subject);

    my($start_codon, $stop_codon);
    my($predicted_start, $predicted_stop);
    my($first_amino);

    my(@up_atg_pos, $up_atg_pos, @down_stop_pos, $down_stop_pos);
    my(@up_ag_pos, $up_ag_pos, @down_gt_pos, $down_gt_pos);

    my @stop_pat = ("taa", "tag", "tga");

    my %seq_info = ();

    my($i, $j);

    local *FASTA_FILE;

    open(FASTA_FILE, $fastafile) || 
	die "Cannot open file \"$fastafile\": $!\n";

    $subject_title = "";
    while(<FASTA_FILE>){
	chop;
	if($_ =~ /^>>/){ 
	    $subject_title = $_;
	    last; 
	}
    }
    if($subject_title eq ""){
	$seq_info{ "NO-MATCH" } = "No-FASTA-Match";
	return %seq_info;
    }

    $subject_title =~ /\((\d+) \w+\)$/;
    $subject_length = $1;

    %query_met = (); %subject_met = ();
    ($result, $query, $match, $subject, $total_length) 
	=  &get_fasta(*FASTA_FILE, \%query_met, \%subject_met);

#    print "Q: $query\n";
#    print "M: $match\n";
#    print "S: $subject\n";

    ($query_o_start, $query_o_end, $subject_o_start, $subject_o_end) 
	= &fasta_overlap($result);

    $result =~ /([\d\.]+)% identity \( *([\d\.]+)% ungapped\) in (\d+)/;
    $identity = $1;
    $identity_ungapped = $2;
    $overlap = $3;

    $sequence = &read_seq($seqfile);
    $c_sequence = &rev_str($sequence);
    $c_sequence =~ tr/atcg/tagc/;

    $fshift_query = $query;
    $fshift_query =~ s/[A-Z]//g;

    $fshift_subject = $subject;
    $fshift_subject =~ s/[A-Z]//g;
    
    ($start_codon, $stop_codon, $predicted_start, $predicted_stop) = 
	&get_start_stop_codon2($query_o_start, $query_o_end,
			       $subject_o_start, $sequence);

# Not applicable to reverse sequence   
    @up_atg_pos = 
	&find_nearest_pat_consider_stop_codon($sequence, "atg",
					      $query_o_start - 1,  -3);
    @up_ag_pos = 
	&find_nearest_pat_consider_stop_codon($sequence, "ag",
					      $query_o_start - 1, -1);

    @down_gt_pos = &find_nearest_pat($sequence, "gt",
				     $query_o_end + 1 - 1, +1);

    @down_stop_pos = &find_nearest_pats($sequence, \@stop_pat,
					$query_o_end + 1 - 1, +3);
    if($identity eq ""){
	warn "ERROR: seqfile: $seqfile fastafile: $fastafile\n";
	warn "ERROR: subject_title: $subject_title\n";
	warn "ERROR: result: $result\n";
	warn "ERROR: query: $query\n";
	warn "ERROR: subject : $subject\n";
	warn "ERROR: sequence : $sequence\n";
    }

    if($fshift_query eq ""){ $fshift_query = "clean"; }
    if($fshift_subject eq ""){ $fshift_subject = "clean"; }

    if(defined $up_atg_pos[0]){ $up_atg_pos = join(",", @up_atg_pos); }
    else { $up_atg_pos = "-"; }

    if(defined $down_stop_pos[0]){ $down_stop_pos = $down_stop_pos[0]; }
    else { $down_stop_pos = "-"; }
    
    if(defined $up_ag_pos[0]){ $up_ag_pos = $up_ag_pos[0]; }
    else { $up_ag_pos = "-"; }

    if(defined $down_gt_pos[0]){ $down_gt_pos = $down_gt_pos[0]; }
    else { $down_gt_pos = "-"; }

    close FASTA_FILE;

    %seq_info = ("query_alignment" => $query,
		 "subject_alignment" => $subject,
		 "alignment_result" => $match,
		 "query_len"       => length($sequence),
		 "subject_len"     => $subject_length,
                 "identity"        => $identity,
		 "identity_ungapped" => $identity_ungapped,
		 "overlap"         => $overlap,
		 "query_start"     => $query_o_start,
		 "query_end"       => $query_o_end,
		 "subject_start"   => $subject_o_start,
		 "subject_end"     => $subject_o_end,
		 "predicted_start" => $predicted_start,
		 "predicted_stop"  => $predicted_stop,
		 "start_codon"     => $start_codon,
		 "stop_codon"      => $stop_codon,
		 "start_amino"     => substr($subject, 0, 1),
		 "fshift_query"    => $fshift_query,
		 "fshift_subject"  => $fshift_subject,
		 "up_atg_pos"      => $up_atg_pos,
		 "down_stop_pos"   => $down_stop_pos,
		 "up_ag_pos"       => $up_ag_pos,
		 "down_gt_pos"     => $down_gt_pos
		 );

    return %seq_info;

}


sub read_blast_res2 {

    my($seqfile, $blast_res) = @_;
    my $seqid; ($seqid = $seqfile) =~ s/.*\///g; 
    my($dbid);
    my(%seq_info, %best_info);
    my($key);
    my(@res_line);
    my($i, $j);
    my($evalue);
    my($line_tmp);
    my($count);

    my $db = $::SUBJECT_DB;
    local *BLAST_FILE;
    my $count_limit = $::BLAST_HITLIMIT;

    my $tmptfa = "tmp.tfa$$";
    my $tmpfasta_res = "tmp.fasta$$";

    my($seq_status, $base_status);

    open(BLAST_FILE, $blast_res) || die "Cannot open file \"$blast_res\": $!\n";

    %best_info = ();

    while(<BLAST_FILE>){
      chomp;
      if(/^Query=/){ last; }
    }

    while(<BLAST_FILE>){
      if(/^Sequences producing significant alignments:/){ 
	$line_tmp = <BLAST_FILE>;
	last;
      } 
    }

    $count = 0;
    while(<BLAST_FILE>){
	chomp;
	my $line = $_;

	undef($dbid);
	undef($evalue);
	eval "\$line =~ $::BLAST_PATMATCH; (\$dbid, \$evalue) = (\$1, \$2);";

	if(defined($dbid) && defined($evalue)){

#	  if($evalue !~ /0.0+/ && $evalue !~ /e-[1-9]\d/){ last; }
	  
	  qx!$::FASTACMD_EXEC -d $db -s $dbid > $tmptfa!;
	  qx!$::FASTA_EXEC -AQHn -b 1 -d 1 $seqfile $tmptfa > $tmpfasta_res!;
	  %seq_info = &g_fasta("$seqfile", $tmpfasta_res);
	  
	  $seq_info{ "seqid" } = $seqid;
	  $seq_info{ "dbid" } = $dbid;
	  $seq_info{ "evalue" } = $evalue;
	  $seq_info{ "query_ngap" } = 
	    $seq_info{ "query_alignment" } =~ tr/-/-/;
	  $seq_info{ "subject_ngap" } = 
	    $seq_info{ "subject_alignment" } =~ tr/-/-/;

	    ($seq_status, $base_status) = get_status(%seq_info);

	    $seq_info{ "seq_status" } = $seq_status;
	    $seq_info{ "base_status" } = $base_status;

#	    foreach $key (keys(%seq_info)){
#		print "$key\t$seq_info{$key}\n";
#	    }


	    if($seq_info{"seq_status"} eq "No-FASTA-Match"){
		@res_line = (@seq_info{ "seqid", "dbid"}, "No-FASTA-Match");
	    }
	    else {
		@res_line =
		    @seq_info{ "seqid", "dbid", "seq_status", 
				"query_len", "subject_len",
				"identity",  "overlap",
				"query_start", "query_end",
				"subject_start", "subject_end",
				"predicted_start", "predicted_stop",
				"start_codon", "stop_codon", "start_amino",
				"fshift_query", "fshift_subject",
				"up_atg_pos", "down_stop_pos",
				"up_ag_pos", "down_gt_pos", "base_status",
				 "identity_ungapped", 
				 "query_ngap", "subject_ngap", "evalue"
			       };
	    }
	    
	  if($::BLAST_VERBOSE eq "T"){
	      print "EDEC$count\t", join("\t", @res_line), "\n";
	  }

#	    print $seq_info{ "query_alignment" }, "\n";
#	    print $seq_info{ "alignment_result" }, "\n";
#	    print $seq_info{ "subject_alignment" }, "\n";

	    if(!defined($best_info{"seqid"})){ %best_info = %seq_info; }
	    elsif($best_info{"seq_status"} eq "No-FASTA-MATCH"){
	      %best_info = %seq_info;
	      }
	    elsif($best_info{"seq_status"} ne "Complete-CDS" &&
		  $seq_info{"seq_status"} eq "Complete-CDS"){
	      %best_info = %seq_info;
	      }
	    elsif($best_info{"seq_status"} ne "Complete-CDS" &&
		  $best_info{"seq_status"} !~ "Truncated" &&
		  $seq_info{"seq_status"} =~ "Truncated"){
	      %best_info = %seq_info;
	    }
	  $count ++;
	  if($count > $count_limit){ last; }
	  if($::BLAST_CCDS_STOP eq "T" &&
	     $best_info{"seq_status"} eq "Complete-CDS"){ last; }

	}
	elsif($_ eq ""){ last; }
    }
    
    if(!defined($best_info{ "dbid" })){ 
      @res_line = ($seqid, "-", "No-BLAST-Match");
    }
    elsif($best_info{"seq_status"} eq "No-FASTA-Match"){
      @res_line = (@best_info{ "seqid", "dbid"}, "No-FASTA-Match");
    }
    else {
      @res_line =
	  @best_info{ "seqid", "dbid", "seq_status", 
		      "query_len", "subject_len",
		      "identity",  "overlap",
		      "query_start", "query_end",
		      "subject_start", "subject_end",
		      "predicted_start", "predicted_stop",
		      "start_codon", "stop_codon", "start_amino",
		      "fshift_query", "fshift_subject",
		      "up_atg_pos", "down_stop_pos",
		      "up_ag_pos", "down_gt_pos", "base_status",
		      "identity_ungapped", 
		      "query_ngap", "subject_ngap", "evalue" };
    }
    
    print join("\t", @res_line), "\n";
    
    close BLAST_FILE;

    qx!touch $tmptfa; rm $tmptfa!;
    qx!touch $tmpfasta_res; rm $tmpfasta_res!;
    
}

sub get_status {

    my(%seq_info) = @_;

    my(@info);
    my($seqid, $db_id);
    my($query_len, $subject_len);
    my($identity, $overlap);
    my($query_start, $query_end);
    my($subject_start, $subject_end);
    my($cds_start, $cds_end);
    my($start_codon, $stop_codon);
    my($start_amino);
    my($query_status, $subject_status);
    
    my($seq_status, $base_status);
    
    my($up_atg, $down_stop, $up_ag, $down_gt);

    if(defined($seq_info{ "NO-MATCH" }) && 
       $seq_info{ "NO-MATCH" } eq "No-FASTA-Match"){
	return ("No-FASTA-Match", "No-FASTA-Match");
    }

    
    ($seqid, $db_id, $query_len, $subject_len, $identity, $overlap,
     $query_start, $query_end, $subject_start, $subject_end,
     $cds_start, $cds_end, $start_codon, $stop_codon, $start_amino,
     $query_status, $subject_status, $up_atg, $down_stop, 
     $up_ag, $down_gt) 
	= @seq_info{ "seqid", "dbid", "query_len", "subject_len",
		     "identity", "overlap", "query_start", "query_end",
		     "subject_start", "subject_end",
		     "predicted_start", "predicted_stop",
		     "start_codon", "stop_codon", "start_amino",
		     "fshift_query", "fshift_subject",
		     "up_atg_pos", "down_stop_pos",
		     "up_ag_pos", "down_gt_pos"};

#    print "## $seqid $db_id :$query_start,$query_end ##\n";

    if($query_start >= $query_end){
	$seq_status = "Reverse";
    }
    elsif($query_start <= $query_end && #冗長
#       $query_start >= 3 && $query_end <= $query_len - 3 &&
	  $subject_start == 1 && $subject_end == $subject_len &&
#	  $start_amino eq "M" &&
	  $start_codon eq "atg" &&
	  ( $stop_codon eq "taa" || $stop_codon eq "tag"
	    || $stop_codon eq "tga")){
	$seq_status = "Complete-CDS";
    }
    elsif($query_start <= 3 && 
	  $subject_start > 1 && $subject_end == $subject_len &&
	  ( $stop_codon eq "taa" || $stop_codon eq "tag"
	    || $stop_codon eq "tga")){
	$seq_status = "5'-Truncated";
    }
    elsif(
#	  $query_start >= 3 &&
	  $query_end > $query_len - 3 &&
	  $subject_start == 1 && $subject_end < $subject_len &&
#	  $start_amino eq "M" &&
	  $start_codon eq "atg"){
	$seq_status = "3'-Truncated";
    }
    elsif($up_ag ne "-" && $up_ag >= -4 && $up_ag <= -2 &&
	  $subject_start >= 1 && $subject_end == $subject_len &&
	  ( $stop_codon eq "taa" || $stop_codon eq "tag"
	    || $stop_codon eq "tga")){
	$seq_status = "5'-Immature";
    }
    elsif($up_atg ne "-" &&
	  $subject_start >= 1 && $subject_end == $subject_len &&
	  ( $stop_codon eq "taa" || $stop_codon eq "tag"
	    || $stop_codon eq "tga")){
	$seq_status = "Alternative-N-terminal";
    }
    elsif(
#	  $query_start >= 3 &&
	  $down_gt ne "-" && $down_gt >= 0 && $down_gt <= 2 &&
	  $subject_start == 1 && $subject_end <= $subject_len &&
#	  $start_amino eq "M" &&
	  $start_codon eq "atg"){
	$seq_status = "3'-Immature";
    }
    elsif(
#	  $query_start >= 3 &&
	  $down_stop ne "-" &&
	  $subject_start == 1 && $subject_end <= $subject_len &&
#	  $start_amino eq "M" &&
	  $start_codon eq "atg"){
	$seq_status = "Alternative-C-terminal";
    }
    elsif($up_atg eq "-" && # 冗長
	  $subject_start >= 1 && $subject_end == $subject_len &&
	  ( $stop_codon eq "taa" || $stop_codon eq "tag"
	    || $stop_codon eq "tga")){
	$seq_status = "Coding-potential-with-predicted-stop-codon";
    }
    elsif(
#	  $query_start >= 3 &&
	  $down_stop eq "-" && # 冗長
	  $subject_start == 1 && $subject_end <= $subject_len &&
#	  $start_amino eq "M" &&
	  $start_codon eq "atg"){
	$seq_status = "Coding-potential-with-predicted-start-codon";
    }
    else {
	$seq_status =  "?";
    }
    
#   print "$seq_status ";
    
    $base_status = "";
    
    if($query_status =~ /\//){
	$base_status .= ":Deletion";
    }
    if($query_status =~ /\\/){
	$base_status .= ":Insertion";
    }
    if($query_status =~ /\*/){
	$base_status .= ":Unexp.stop";
    }
    
#    print "$base_status";
#    print "\n";

    return ($seq_status, $base_status);
    
}

sub read_rsconfig_global {
  local(*FH);
  my %cnf;

  if(open(FH, $::CONFIG_FILE)){
    while(<FH>){
      chomp;
      if(/^#/ || /^ / || $_ eq ""){ next; }
      my($att, $val) = split(/\t/); 
      if($att eq "SUBJECT_DB"){ $::SUBJECT_DB = $val; }
      elsif($att eq "FASTA_EXEC"){ $::FASTA_EXEC = $val; }
      elsif($att eq "FASTACMD_EXEC"){ $::FASTACMD_EXEC = $val; }
      elsif($att eq "BLAST_EXEC"){ $::BLAST_EXEC = $val; }
      elsif($att eq "BLAST_PATMATCH"){ $::BLAST_PATMATCH = $val; }
      elsif($att eq "BLAST_EVALUE"){ $::BLAST_EVALUE = $val; }
      elsif($att eq "BLAST_HITLIMIT"){ $::BLAST_HITLIMIT = $val; }
      elsif($att eq "BLAST_CCDS_STOP"){ $::BLAST_CCDS_STOP = $val; }
      elsif($att eq "BLAST_VERBOSE"){ $::BLAST_VERBOSE = $val; }
    }   
    close FH;
  }
  
  else {
    open(FH, "> $::CONFIG_FILE");
    print FH <<EOF;
# This is configuration file for rsCDS.
# Each line should be separated by <TAB>.

# Specify where the subject Database for BLAST search is located.
SUBJECT_DB	/pub/db/ncbi/blast/db/nr

# Specify locations of the executable files of BLAST and FASTA command.
FASTA_EXEC	/pub/software/fasta/fasty33
FASTACMD_EXEC	/pub/software/blast/fastacmd
BLAST_EXEC	/pub/software/blast/blastall

# First bracket should correspond to database ID.
# Second bracket should correspond to e-value.
BLAST_PATMATCH	/^[a-z]+\\|([^\\|]+)\\| .* ([e0-9.\\-]+) *\$/

BLAST_EVALUE	10.0
BLAST_HITLIMIT	1000
BLAST_CCDS_STOP	T
BLAST_VERBOSE	T

#

EOF
    close FH;
    die "Please edit $::CONFIG_FILE\n";
  }
 
}
  
my $seqfile = shift @ARGV;
my $blast_res = shift @ARGV;
my $blast_invoked = 0;

if(!defined($seqfile)){
    &rsCDS_help;
    exit;
}

&read_rsconfig_global;

if(!defined($blast_res)){
    $blast_invoked = 1;
    $blast_res = "blast_res.tmp$$";
    qx!$::BLAST_EXEC -p blastx -e $::BLAST_EVALUE -d $::SUBJECT_DB -i $seqfile > $blast_res!;
}

read_blast_res2($seqfile, $blast_res);

if($blast_invoked){ qx!rm $blast_res!; }
