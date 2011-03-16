#!/usr/bin/perl -w

use strict;
use vars qw(@score_path_matrix $seq1 $seq2);

*::Gap_Penalty = \-5;
*::GAPM = \"-";

sub find_max_score($$); # Prototype declaration

sub print_board($$){
    my($m, $n) = @_;

    for(my $i = 0;$i <= $m;$i ++){
	for(my $j = 0;$j <= $n;$j ++){
	    printf("%2d ", $score_path_matrix[$i]->[$j]->{"score"});
	}
	print "\n";
    }
  
    print "\n";

    for(my $i = 0;$i <= $m;$i ++){
	for(my $j = 0;$j <= $n;$j ++){
	    my $d = $score_path_matrix[$i]->[$j]->{"direction"};
	    if($d eq "H"){ printf("%2s ", "-"); }
	    elsif($d eq "V"){ printf("%2s ", "|"); }
	    elsif($d eq "D"){ printf("%2s ", "\\"); }
	    elsif($d eq "S"){ printf("%2s ", "x"); }
	}
	print "\n";
    }
    print "\n";
}



sub score($$){
    my($a, $b) = @_;
    if($a eq $b){ return 10; }
    else { return -7; }
}

sub find_max_elem(%){
    my(%h) = @_;
    my $max_val = -99999;
    my $max_key;
    
    foreach my $key (keys(%h)){
	if($max_val < $h{ $key }){
	    $max_val = $h{ $key };
	    $max_key = $key;
	}
    }

  return $max_key;

}

sub find_max_score($$){
    my($i, $j) = @_;

    my %score_tmp;
    my $max_dir;
    my $max_score;

    if(defined($score_path_matrix[$i]->[$j]->{"score"})){
	return $score_path_matrix[$i]->[$j]->{"score"};
    }
    elsif($i == 0){ 
	$max_score = 0; $max_dir = "S";
    }
    elsif($j == 0){
	$max_score = 0; $max_dir = "S";
    }
    else {
	$score_tmp{"V"} = find_max_score($i - 1, $j) + $::Gap_Penalty;
	$score_tmp{"H"} = find_max_score($i, $j - 1) + $::Gap_Penalty;
	$score_tmp{"D"} = find_max_score($i - 1, $j - 1) + 
	    score(substr($seq1, $i-1, 1), substr($seq2, $j-1, 1));
 
	$max_dir = find_max_elem(%score_tmp);
	$max_score = $score_tmp{$max_dir};
	if($max_score <= 0){ $max_score = 0; $max_dir = "S"; }
	
   
    }

    $score_path_matrix[$i]->[$j]->{"score"} = $max_score;
    $score_path_matrix[$i]->[$j]->{"direction"} = $max_dir;
    return $max_score;

}

sub board_to_alignment($$){
    
    my($m, $n) = @_;
    my(@a_seq1, @a_seq2);
    my($i, $j, $p);
    my($tmp);
    my $max = -1;
    my($max_i, $max_j);

    for my $i (0..$m){
       for my $j (0..$n){
          if($max < $score_path_matrix[$i]->[$j]->{"score"}){
             $max = $score_path_matrix[$i]->[$j]->{"score"};
             $max_i = $i; $max_j = $j;
          }
       }
    }

    for($p = 0, $i = $max_i, $j = $max_j;
	$i != 0 || $j != 0;){
	my $d = $score_path_matrix[$i]->[$j]->{"direction"};
	if($d eq "V"){
	    $a_seq1[$p] = substr($seq1, --$i, 1);
	    $a_seq2[$p] = $::GAPM;
	    $p ++;
	}
	elsif($d eq "H"){
	    $a_seq1[$p] = $::GAPM;
	    $a_seq2[$p] = substr($seq2, --$j, 1);
	    $p ++;
	}
	elsif($d eq "D"){
	    $a_seq1[$p] = substr($seq1, --$i, 1);
	    $a_seq2[$p] = substr($seq2, --$j, 1);
	    $p ++;
	}
        else { last; }
    }

    @a_seq1 = reverse(@a_seq1);
    @a_seq2 = reverse(@a_seq2);

    return (join("", @a_seq1), join("", @a_seq2));
}

$seq1 = "cag";
$seq2 = "tagc";

my $seq1_len = length($seq1);
my $seq2_len = length($seq2);

for my $i (0..$seq1_len){
    for my $j (0..$seq2_len){
       if(!defined($score_path_matrix[$i]->[$j]->{"score"})){
          find_max_score($i, $j);
       }
    }
}



print_board($seq1_len, $seq2_len);

my($a_seq1, $a_seq2) = board_to_alignment($seq1_len, $seq2_len);
printf("%s\n%s\n", $a_seq1, $a_seq2);

