#!/usr/bin/perl -w

use strict;
require Exporter;

use kmeans_func qw(determine_cluster calc_gravity_center initialize);
use Plot_field_KM;

*::CLUSTERS = \ 3;

##### Function Call Tests #####

my @ref_position;
my @points;

my $test_exp_file = shift @ARGV;

my($POINTS, $DIM, $gene) = 
  initialize($::CLUSTERS, \@ref_position, \@points, $test_exp_file);

print "***** Read positions from file $ARGV[0] *****\n\n";

for(my $i = 0;$i < $POINTS;$i ++){
  print "Point #$i\t", $gene->{$i}->{ "name" }," - ";
  print $gene->{$i}->{ "func" },"\t";
  print join("\t", @{$points[$i]}), "\n";
}
print "\n";

print "***** Generated reference positions *****\n\n";

for(my $i = 0;$i < $::CLUSTERS;$i ++){
  print "Cluster #$i\t";
  for(my $j = 0;$j < $DIM;$j ++){
    printf("%+.3lf ", $ref_position[$i]->[$j]);
  }
  print "\n";
}

print "\n";

my @points_belong;

determine_cluster(\@points_belong, \@ref_position, \@points);

print "***** Belongings of each points  *****\n\n";
for(my $i = 0;$i < $::CLUSTERS;$i ++){
  print "Cluster #$i: ";
  for(my $j = 0;$j < $POINTS;$j ++){
    printf("%d ", $points_belong[$i]->[$j]);
  }
  print "\n";
}
print "\n";

my @target_position;

calc_gravity_center(\@points_belong, \@target_position,
                    \@ref_position, \@points);


print "***** Gravity center positions  *****\n\n";
for(my $i = 0;$i < $::CLUSTERS;$i ++){
  print "Cluster #$i: ";
  for(my $j = 0;$j < $DIM;$j ++){
    if(defined($target_position[$i]->[$j])){
      printf("%+.3lf ", $target_position[$i]->[$j]);
    }
    else { print "------ "; }
  }
  print "\n";
}

print "\n";

my $fld = new Plot_field_KM -10, -10, 10, 10;

$fld->plot_kmeans(\@points_belong, \@target_position,
                  \@ref_position, \@points, $::CLUSTERS);

