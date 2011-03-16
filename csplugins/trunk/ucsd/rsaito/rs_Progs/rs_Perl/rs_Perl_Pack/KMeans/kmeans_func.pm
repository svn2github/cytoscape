#!/usr/bin/perl -w

use strict;
require Exporter;

package kmeans_func;
use vars qw(@ISA @EXPORT_OK);

@ISA = qw(Exporter); # Current package, not main package
@EXPORT_OK = qw(determine_cluster calc_gravity_center initialize);

sub determine_cluster($$$){
  my $next_points_belong = shift;
  my $ref_position = shift;
  my $points = shift;

  my $min;
  my $ref_min;

  my $euc_dist;

  for(my $i = 0;$i < $#{$ref_position} + 1;$i ++){
    for(my $j = 0;$j < $#$points + 1;$j ++){
      $next_points_belong->[$i]->[$j] = 0;
    }
  }
  
  for(my $j = 0;$j < $#$points + 1;$j ++){
    $min = 9999999.0;
    $ref_min = 0;
    for(my $i = 0;$i < $#{$ref_position} + 1;$i ++){
      $euc_dist = 0.0;
      my $num_points = 0;
      for(my $k = 0;$k < $#{$points->[0]} + 1; $k ++){
        if(defined($points->[$j]->[$k]) && $points->[$j]->[$k] =~ /\d/){
          $euc_dist += 
            ($points->[$j]->[$k] - $ref_position->[$i]->[$k])
              * ($points->[$j]->[$k] - $ref_position->[$i]->[$k]);
          $num_points ++;
        }
      }

      $euc_dist *= ($#{$points->[0]} + 1) / $num_points;
      
      if($euc_dist < $min){
        $min = $euc_dist;
        $ref_min = $i;
      }
    }
    $next_points_belong->[$ref_min]->[$j] = 1;
  }
  
}

sub calc_gravity_center($$$$){
  my $points_belong = shift;
  my $target_position = shift;
  my $ref_position = shift;
  my $points = shift;

  my @total;
  my @num;

  for(my $i = 0;$i < $#{$ref_position} + 1;$i ++){
    for(my $k = 0;$k < $#{$points->[0]} + 1;$k ++){
      $total[$i]->[$k] = 0.0;
      $num[$i]->[$k] = 0;
    }
  }
  
  for(my $i = 0;$i < $#{$ref_position}+1;$i ++){ # No. of ref. points
    for(my $j = 0;$j < $#$points + 1;$j ++){ # No. of points
#      $num[$i] += $points_belong->[$i]->[$j];
      for(my $k = 0;$k < $#{$points->[0]} + 1;$k ++){ # No. of dimensions
        if(defined($points->[$j]->[$k]) && $points->[$j]->[$k] =~ /\d/ &&
           $points_belong->[$i]->[$j] == 1){
          $total[$i]->[$k] += $points->[$j]->[$k] * $points_belong->[$i]->[$j];
          $num[$i]->[$k] ++;
        }
      }
    }
  }
  
  for(my $i = 0;$i < $#{$ref_position} + 1;$i ++){
    for(my $k = 0;$k < $#{$points->[0]} + 1;$k ++){
      if($num[$i]->[$k] > 0){
        $target_position->[$i]->[$k] = $total[$i]->[$k] / $num[$i]->[$k];
      }
      else { undef $target_position->[$i]->[$k]; }
    }
  }

}

sub initialize($$$$){
  my $clusters = shift;
  my $ref_position = shift;
  my $points = shift;
  my $filename = shift;

  local *FH;

  my %gene;
  
  open(FH, $filename) || die "Cannot open \"$filename\": $!";
  my $p_num = 0;
  while(<FH>){
    my @r = split(/\t/);
    chomp $r[ $#r ];
    my $gene_name = shift @r;
    my $gene_func = shift @r;
    $gene{ $p_num }->{ "name" } = $gene_name;
    $gene{ $p_num }->{ "func" } = $gene_func;
    $points->[$p_num] = [ @r ];
    $p_num ++;
  }
  close FH;

  for(my $i = 0;$i < $clusters;$i ++){
    for(my $j = 0;$j < $#{$points->[0]} + 1;$j ++){
      $ref_position->[$i]->[$j] = rand() - 0.5;
    }
  }

  return($p_num, $#{$points->[0]} + 1, { %gene });
  
}


1;
