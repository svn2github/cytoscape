#!/usr/bin/perl -w

use strict;

package ORIG_math2;
require Exporter;
@::ISA = qw(Exporter);
#@EXPORT = qw(new volume);
#@EXPORT_OK = qw($sally @listabob %harry func3);

sub integral_trp($$$$@){

   my($func_ref, $lower_limit, $upper_limit, $ndiv, @param) = @_;

   my $sum = 0;
   my($h, $k);

   $h = 1.0 * ($upper_limit - $lower_limit) / $ndiv;
   for($k = 1;$k <= $ndiv - 1;$k ++){
      $sum += $h * &$func_ref($lower_limit + $h * $k, @param);
   }
   
   $sum += 0.5 * $h * &$func_ref($lower_limit, @param);
   $sum += 0.5 * $h * &$func_ref($upper_limit, @param);

   return $sum;

}

sub gamma_integer($){

   my($x) = @_;
   my($prod, $i);

   $prod = 1;
   for($i = $x;$i > 1;$i --){ $prod *= $i - 1; }

   if($i == 1){ $prod *= 1; }
   elsif($i == 0.5){ $prod *= sqrt(3.141592653589793238462643383279); }
   else { die "Gamma function error: Input was $x" . ".\n"; }

   return $prod;

}

sub t_func($$){

   my($x, $n) = @_;
   my $pi = 3.141592653589793238462643383279;
   my($numerator, $denominator);

   $numerator = gamma_integer(($n + 1)/2);
   $denominator = sqrt($pi * $n)*gamma_integer($n/2);

   return $numerator / $denominator * ($x ** 2 / $n + 1)**(-($n + 1)/2);

}

sub chi_func($$){

    my($x, $n) = @_;

    my $chi = 1.0/(2.0**($n/2.0)*gamma_integer($n/2))
	* $x**($n/2.0 - 1)*exp(-$x/2.0);
    return $chi;

}


sub mean($){
    
    my($item_ref) = @_;
    my($each, $total, $count);

    $total = 0;
    $count = 0;
    foreach $each (@$item_ref){
	if($each =~ /\d/){ 
	    $count ++;
	    $total += $each;
	}
    }

    return $total / $count;

}

sub var($){

    my($item_ref) = @_;
    my($mean);
    my($each, $total, $count);
    
    $mean = mean($item_ref);

    $total = 0;
    $count = 0;
    foreach $each (@$item_ref){
	if($each =~ /\d/){ 
	    $total += ($each - $mean) * ($each - $mean);
	    $count ++;
	}
    }

    return $total / $count;

}

sub corr($$){

    my($item1_ref, $item2_ref) = @_;
    my(@item1, @item2);
    my($each, $each1, $each2);

    my($mean1, $mean2, $sd1, $sd2);
    my($corr);

    my($i);

    @item1 = ();
    @item2 = ();
    for $i (0..$#$item1_ref){
	if(defined($item1_ref->[$i]) && defined($item2_ref->[$i]) &&
	    $item1_ref->[$i] =~ /\d/ && $item2_ref->[$i] =~ /\d/){
	    push(@item1, $item1_ref->[$i]);
	    push(@item2, $item2_ref->[$i]);
	}
    } 

    if($#item1 < 0 || $#item2 < 0){ return "NO-APPROP-DATA"; }

    $mean1 = mean(\@item1);
    $mean2 = mean(\@item2);

    $sd1 = sqrt(var(\@item1));
    $sd2 = sqrt(var(\@item2));

    $corr = 0;
    for $i (0..$#item1){
	$corr += ($item1[$i] - $mean1) / $sd1
	    * ($item2[$i] - $mean2) / $sd2;
    }
    $corr /= $#item1 + 1;

    return $corr;

}

sub mean_W($$){

   my($item_ref, $w_ref) = @_;
   my($each, $weight, $total, $count);
   my($i);

#   return 0; #!!!!!

   $total = 0;
   $count = 0;

   for $i (0..$#$item_ref){
      if(defined($item_ref->[$i]) && $item_ref->[$i] =~ /\d/ &&
         defined($w_ref->[$i]) && $w_ref->[$i] =~ /\d/){
         $each = $item_ref->[$i];
         $weight = $w_ref->[$i];
         $total += $weight * $each;
         $count += $weight;
      }
   }

  return $total / $count;

}

sub var_W($$){

    my($item_ref, $w_ref) = @_;
    my($mean);
    my($each, $weight, $total, $count);
    my($i);
    
    $mean = mean_W($item_ref, $w_ref);

    $total = 0;
    $count = 0;

    for $i (0..$#$item_ref){
      if(defined($item_ref->[$i]) && $item_ref->[$i] =~ /\d/ &&
         defined($w_ref->[$i]) && $w_ref->[$i] =~ /\d/){
         $each = $item_ref->[$i];
         $weight = $w_ref->[$i];
	 $total += ($each - $mean) * ($each - $mean) * $weight;
	 $count += $weight;
       }
    }
    return $total / $count;
}

sub cov_W($$$){

    my($item1_ref, $item2_ref, $w_ref) = @_;
    my(@item1, @item2, @w);
    my($each, $each1, $each2);

    my($mean1, $mean2, $sd1, $sd2);
    my($count, $cov);

    my($i);

    @item1 = ();
    @item2 = ();
    for $i (0..$#$w_ref){
	if(defined($item1_ref->[$i]) && defined($item2_ref->[$i]) &&
	   $item1_ref->[$i] =~ /\d/ && $item2_ref->[$i] =~ /\d/ &&
	   defined($w_ref->[$i])){
	    push(@item1, $item1_ref->[$i]);
	    push(@item2, $item2_ref->[$i]);
            push(@w, $w_ref->[$i]);
	}
    } 

    if($#w < 0){ return "NO-APPROP-DATA"; }

    $mean1 = mean_W(\@item1, \@w);
    $mean2 = mean_W(\@item2, \@w);

    $cov = 0;
    $count = 0;
    for $i (0..$#w){
	$cov += ($item1[$i] - $mean1) * ($item2[$i] - $mean2) * $w[ $i ];
        $count += $w[ $i ];
    }
    $cov /= $count;

    return $cov;

}

sub corr_W($$$){

    my($item1_ref, $item2_ref, $w_ref) = @_;
    my(@item1, @item2, @w);
    my($each, $each1, $each2);

    my($mean1, $mean2, $sd1, $sd2);
    my($count, $corr);

    my($i);

    @item1 = ();
    @item2 = ();
    for $i (0..$#$w_ref){
	if(defined($item1_ref->[$i]) && defined($item2_ref->[$i]) &&
	   $item1_ref->[$i] =~ /\d/ && $item2_ref->[$i] =~ /\d/ &&
	   defined($w_ref->[$i])){
	    push(@item1, $item1_ref->[$i]);
	    push(@item2, $item2_ref->[$i]);
            push(@w, $w_ref->[$i]);
	}
    } 

    if($#w < 0){ return "NO-APPROP-DATA"; }

    $mean1 = mean_W(\@item1, \@w);
    $mean2 = mean_W(\@item2, \@w);

    $sd1 = sqrt(var_W(\@item1, \@w));
    $sd2 = sqrt(var_W(\@item2, \@w));

    $corr = 0;
    $count = 0;
    for $i (0..$#w){
	$corr += ($item1[$i] - $mean1) / $sd1
	    * ($item2[$i] - $mean2) / $sd2
            * $w[ $i ];
        $count += $w[ $i ];
    }
    $corr /= $count;

    return $corr;

}

sub regres1($$$){

   my($x_ref, $y_ref, $w_ref) = @_;

   my($x_mean, $y_mean);
   my($alpha, $beta);

   my $beta_numerator = 0;
   my $beta_denominator = 0;

   my($i);

   $x_mean = mean_W($x_ref, $w_ref);
   $y_mean = mean_W($y_ref, $w_ref);

   for $i (0..$#$w_ref){
      if(defined($x_ref->[$i]) && $x_ref->[$i] =~ /\d/ &&
         defined($y_ref->[$i]) && $y_ref->[$i] =~ /\d/ &&
         defined($w_ref->[$i]) && $w_ref->[$i] =~ /\d/){
         $beta_numerator += $w_ref->[$i] 
             * ($x_ref->[$i] - $x_mean) * ($y_ref->[$i] - $y_mean);
         $beta_denominator += $w_ref->[$i]
             * ($x_ref->[$i] - $x_mean) * ($x_ref->[$i] - $x_mean);
      }
   }

   $beta = $beta_numerator / $beta_denominator;
   $alpha = $y_mean - $beta * $x_mean;

   return ($alpha, $beta);

}

sub select_signif1($$$){

  my($item_ref, $w_ref, $thres) = @_;
  my @w_copy;
  my($mean, $var, $sd, $z);
  my @flag = ();
  my $flag_change;
  my $n_valid;

  my($i);

  @w_copy = @$w_ref;
  undef_to_w($item_ref, \@w_copy);
  $n_valid = 0;
  map { $n_valid += ($_ > 0), push(@flag, 0) } @w_copy;

  if($n_valid <= 2){ @$w_ref = @w_copy; return; }

  do {
     $mean = mean_W($item_ref, \@w_copy);
     $var = var_W($item_ref, \@w_copy);
     $sd = sqrt($var);

     $flag_change = 0;
     for $i (0..$#w_copy){
        if($w_copy[$i] > 0){
           $z = ($item_ref->[$i] - $mean) / $sd;
#           printf("%+2.2lf ", $z);
           if(abs($z) >= $thres){ 
              $flag[ $i ] = $w_copy[ $i ];
              $w_copy[ $i ] = 0;
              $flag_change = 1;
              $n_valid --;
           }
        }
        else {
#           print "XXXXX ";
        }
     }
#     print "\n";
  } while ($flag_change == 1 && $n_valid >= 2);

  @$w_ref = @flag;

}

sub undef_to_w($$){

   my($item_ref, $w_ref) = @_;
   my($i);

   for $i (0..$#$w_ref){
     if(!defined($item_ref->[$i]) || $item_ref->[$i] !~ /\d/ ||
        !defined($w_ref->[$i]) || $w_ref->[$i] !~ /\d/){
        $w_ref->[$i] = 0;
     }
   }

}

sub log_plus($){

   my($x) = @_;
   my $minus = -1000;

   if($x > 0){ return log($x); }
   else { return $minus; }

}

sub add_to_hist {

    my($x, $l_limit, $u_limit, 
       $n_classes, $under_ref, $hist_ref, $over_ref) = @_;

    my($class);

    if($x < $l_limit, ){ $$under_ref ++; }
    elsif($x >= $u_limit){ $$over_ref ++; }
    else {
        $class = int($n_classes * ($x - $l_limit) / 
                     ($u_limit - $l_limit));
        $$hist_ref[ $class ] ++;
    }

}

sub on_ratio_w($$){

    my($bool_ref, $w) = @_;
    my $w_total = 0;
    my $i;
    my $ret = 0;

    map { $w_total += $_ } @$w;
    
    for $i (0..$#$w){
	if(defined($bool_ref->[$i])){ 
	    $ret += $bool_ref->[$i] * $w->[$i]; 
	}
    }
    
    $ret /= $w_total;
    return $ret;

}

sub two_term($$$){

    my($item1_ref, $item2_ref, $func_ref) = @_;
    my($i);
    my @ret = ();

    for $i (0..$#$item1_ref){
	if(defined($item1_ref->[$i]) && defined($item2_ref->[$i]) &&
	   $item1_ref->[$i] =~ /\d/ && $item2_ref->[$i] =~ /\d/){
	    $ret[$i] = &$func_ref($item1_ref->[$i], $item2_ref->[$i]);
	}
    }

    return @ret;

}

sub plog2p($){

    my($p) = @_;
    
    if($p == 0){ return 0; }
    return $p * log($p) / log(2.0);

}

sub entropy{

    my(@num) = @_;
    my $total = 0;
    my $ent = 0;

    if($#num < 0){ return "No-item"; }
    map { $total += $_ } @num;
    if($total == 0){ return 100.0; }
    map { $ent -= plog2p(1.0*$_/$total) } @num;

    return $ent;

}

sub v_diff($$){

    my($a_ref, $b_ref) = @_;
    my @ret = ();
    my $i;

    if($#$a_ref != $#$b_ref){ die "Vector length not identical.\n"; }
    for $i (0..$#$a_ref){ push(@ret, $a_ref->[$i] - $b_ref->[$i]); }
    return @ret;

}

sub v_i_prod($$){

    my($a_ref, $b_ref) = @_;
    my $inner_prod = 0;
    my($i);

    if($#$a_ref != $#$b_ref){ die "Vector length not identical.\n"; }
    for $i (0..$#$a_ref){ $inner_prod += $a_ref->[$i] * $b_ref->[$i]; }
    return $inner_prod;

}

sub max_2($$){ return $_[0] > $_[1] ? $_[0] : $_[1]; }

