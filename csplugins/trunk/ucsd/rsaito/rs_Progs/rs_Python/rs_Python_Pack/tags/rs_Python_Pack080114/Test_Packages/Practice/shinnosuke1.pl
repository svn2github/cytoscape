#!/usr/bin/perl

use strict;

my $cds_cou = 0;
my $cds;
my $cds1;
my $cds2;
my $cds3;
my $cds4;
my $cds5;
my $cds6;
my $cds7;
my $cds_start;
my $cds_stop;
my $cds_cal;
my $cds_cal_a;
my $aaa = 0;
my $bbb = 0;
my $ccc = 0;
my $cds_a;
my $cds_b;
my $m = 0;

open(FILE0, $ARGV[0]) || die "Cannot open \"$ARGV[0]\": $!\n";


while(<FILE0>){
chomp;
if($_ =~ /^ CDS (.*)/){
$cds_cou = $cds_cou +1;
$cds_a = $1;
($cds_b,$cds) = split(/\(/,$cds_a);
($cds1,$cds2,$cds3,$cds4,$cds5,$cds6,$cds7) = split(/,/,$cds);
($cds_start,$cds_stop) = split(/\.\./,$cds2);
if($cds_start < $cds_stop){
$cds_cal_a = $cds_stop - $cds_start +1;
}
if($cds_start > $cds_stop){
$cds_cal_a = $cds_start - $cds_stop +1;
}
$cds_cal = $cds_cal_a % 3;
if($cds_cal == 0){
$aaa = $aaa +1;
}
if($cds_cal == 1){
$bbb = $bbb +1;
}
if($cds_cal == 2){
$ccc = $ccc +1;
}
if($cds_cal_a < 0){
$m = $m +1;
}
}
}
print "cds = ",$cds_cou,"\n";
print "3n = ",$aaa,"\n";
print "3n+1= ",$bbb,"\n";
print "3n+2= ",$ccc,"\n";



__END__
