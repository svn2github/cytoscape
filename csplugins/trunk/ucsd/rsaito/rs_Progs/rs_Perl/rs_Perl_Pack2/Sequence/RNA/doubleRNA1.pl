#!/usr/bin/perl -w

use strict;
use vars qw(@seq1 @seq2 @e_matrix @d_matrix @connect);

sub alpha($$){
  my($n1, $n2) = @_;

  if(($n1 eq 'a' && $n2 eq 't') || ($n1 eq 't' && $n2 eq 'a')){ return -2; }
  if(($n1 eq 'c' && $n2 eq 'g') || ($n1 eq 'g' && $n2 eq 'c')){ return -3; }
  return +1000.0;
}

sub e($$$$){

  my($i, $j, $k1, $k2) = @_;
  my($ri, $rj, $rk1, $rk2) =
    ($seq1[$i-1], $seq2[$j-1], $seq1[$k1-1], $seq2[$k2-1]);

  if($i == 0 && $j == 0){ return 0; }
  elsif($k1 - $i == 1 && $k2 - $j == 1){
      return alpha($ri, $rj) + alpha($rk1, $rk2);
  }
  else {
      if(alpha($ri, $rj) < 1000.0 && alpha($rk1, $rk2) < 1000.0){
	  return $k1 - $i - 1 + $k2 - $j - 1;
      }
      else { return 1000.0; }
  }  
}

sub E($$);

sub E($$){
  # Base index in sequences are 1,2,...$#seq + 1
  # They are stored in $seq[0], $seq[1], ... $seq[ $#seq ]

  my($i, $j) = @_;
  my $len_seq1 = $#seq1 + 1;
  my $len_seq2 = $#seq2 + 1;
  
  if($i >= $len_seq1 || $j >= $len_seq2){
    return 0;
  }

  if(defined($e_matrix[$i]->[$j])){
    return $e_matrix[$i]->[$j];
  }

  my $min_fe = 0;
  my $min_k1 = 0;
  my $min_k2 = 0;
  
  for my $k1 ($i+1..$len_seq1){
    for my $k2 ($j+1..$len_seq2){
	my $E_current = e($i, $j, $k1, $k2) + E($k1, $k2);
	if($E_current < $min_fe){
	    $min_fe = $E_current;
	    $min_k1 = $k1;
	    $min_k2 = $k2;
	}
#      if($i == 4 && $j == 3){
#        print "$min_fe: ($min_k1, $min_k2)\n";
#        print "$E_current: ",e($i, $j, $k1, $k2), "-", E($k1, $k2), " ($k1, $k2)\n";
#      }
	
    }
}
  
  $e_matrix[ $i ]->[ $j ] = $min_fe;
  $d_matrix[ $i ]->[ $j ] = [ $min_k1, $min_k2 ];
  
#  print "$i $j called --- $min_fe.\n";
  
  return $min_fe;
  
}


sub d_to_c($$){
  my($i, $j) = @_;
  my($k1, $k2) = ($i, $j);

  while(1){
    if(!defined($d_matrix[$k1]->[$k2])){ last; }
    ($k1, $k2) = @{$d_matrix[$k1]->[$k2]};
    if($k1 == 0 || $k2 == 0){ last; }
    push(@connect, [$k1, $k2]);
    print "$k1, $k2 connected.\n";
  }
}

sub partial_align {

    my $seq_frag1 = shift;
    my $seq_frag2 = shift;
    my $seq_con1 = shift;
    my $seq_con2 = shift;
    my $right_flag = shift;

    my @align_frag1 = ();
    my @align_frag2 = ();
    my @align = ();

#    print "Fragment given:\n";
#    print join("", @$seq_frag1), "\n";
#    print join("", @$seq_frag2), "\n";

    if(!defined($right_flag)){
	push(@align_frag1, @$seq_frag1);
	push(@align_frag2, @$seq_frag2);
    }

    if($#$seq_frag1 < $#$seq_frag2){
	for my $i (1..($#$seq_frag2 - $#$seq_frag1)){
	    push(@align_frag1, "-");
	}
	for my $i (0..$#$seq_frag2){ 
	    push(@align, "O");
	}
    }
    else {
	for my $i (1..($#$seq_frag1 - $#$seq_frag2)){
	    push(@align_frag2, "-");
	}
	for my $i (0..$#$seq_frag1){
	    push(@align, "O");
	}
    }


    if(defined($right_flag)){
	push(@align_frag1, @$seq_frag1);
	push(@align_frag2, @$seq_frag2);
    }

    if(defined($seq_con1) && defined($seq_con2)){
	push(@align_frag1, $seq_con1);
	push(@align_frag2, $seq_con2);
	push(@align, "*");
    }

    return (\@align_frag1, \@align_frag2, \@align);

}


sub c_to_aln {
    my $i = 0;
    my $j = 0;
    my $p = 0;
    
    my(@a_seq1, @a_seq2, @align);

    while(defined($connect[$p])){
	my($next_i, $next_j) = @{$connect[$p]};
	my $len1 = $next_i - $i;
	my $len2 = $next_j - $j;

	my @non_align1 = @seq1[$i..$next_i-2];
	my @non_align2 = @seq2[$j..$next_j-2];
	my $con1 = $seq1[$next_i-1];
	my $con2 = $seq2[$next_j-1];
	my($a_frag1, $a_frag2, $a_frag);
	if($p == 0){
	    ($a_frag1, $a_frag2, $a_frag) = 
		partial_align(\@non_align1, \@non_align2, $con1, $con2, 1);
	}
	else {
	    ($a_frag1, $a_frag2, $a_frag) = 
		partial_align(\@non_align1, \@non_align2, $con1, $con2);
	}
	push(@a_seq1, @$a_frag1);
	push(@a_seq2, @$a_frag2);
	push(@align, @$a_frag);

	$p += 1;
	$i = $next_i;
	$j = $next_j;
    }

    my @non_align1 = ();
    my @non_align2 = ();
    if($i+1 <= $#seq1+1){ push(@non_align1, @seq1[$i..$#seq1]); }
    if($j+1 <= $#seq2+1){ push(@non_align2, @seq2[$j..$#seq2]); }
    my($a_frag1, $a_frag2, $a_frag) =
	partial_align(\@non_align1, \@non_align2, undef, undef);

    push(@a_seq1, @$a_frag1);
    push(@a_seq2, @$a_frag2);
    push(@align, @$a_frag);

    return(join(" ", @a_seq1), join(" ", @a_seq2), join(" ", @align));

}  

sub display_e_matrix{

    my @out = ("", 0..$#seq2+1);
    print join("\t", @out), "\n";
    
    for my $i (0..$#seq1+1){
	@out = ($i);
	for my $j (0..$#seq2+1){
	    if(defined($e_matrix[$i]->[$j])){
		push(@out, $e_matrix[$i]->[$j]);
	    }
	    else {
		push(@out, "-");
	    }
	}
	print join("\t", @out), "\n";
    }

}

sub display_d_matrix{

    my @out = ("", 0..$#seq2+1);
    print join("\t", @out), "\n";
    
    for my $i (0..$#seq1+1){
	@out = ($i);
	for my $j (0..$#seq2+1){
	    if(defined($d_matrix[$i]->[$j])){
		push(@out, join(",", @{$d_matrix[$i]->[$j]}));
	    }
	    else {
		push(@out, "-");
	    }
	}
	print join("\t", @out), "\n";
    }

}

@seq1 = split("", "acgtaccc");
@seq2 = split("", "gcatcc");

# print e(4,3,5,4), "\n";

print E(0, 0), "\n";
d_to_c(0,0);
my($seq1, $seq2, $aln) = c_to_aln();

print "$seq1\n";
print "$seq2\n";
print "$aln\n";
print "\n";

print "* E matrix *\n";
display_e_matrix();
print "\n";


print "* D matrix *\n";
display_d_matrix();
print "\n";

