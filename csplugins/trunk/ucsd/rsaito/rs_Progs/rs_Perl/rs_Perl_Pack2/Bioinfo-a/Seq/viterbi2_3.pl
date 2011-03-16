#!/usr/bin/perl -w

use strict;
use vars qw(@x @path @type @v @ptr @tr @e %cton %ntoc);

sub viterbi($$);

*::K = \ 6; # Number of states
*::LOG0 = \ -1000.0;

@type = ( "TYPE_S", "TYPE_N", "TYPE_N", "TYPE_N", "TYPE_N", "TYPE_N" );

# static double v[K][L+1]; /* log transformed */
# static int ptr[K][L+1];
@tr = ( 
#  0    1    2    3    4    5
 [ 0.0, 1.0, 0.0, 0.0, 0.0, 0.0 ], # 0
 [ 0.0, 0.0, 0.8, 0.2, 0.0, 0.0 ], # 1
 [ 0.0, 0.0, 0.5, 0.0, 0.5, 0.0 ], # 2
 [ 0.0, 0.0, 0.0, 0.0, 0.7, 0.3 ], # 3
 [ 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 ], # 4
 [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ]  # 5
);

@e = ( 
#   a     c     g     t
 [ 0.00, 0.00, 0.00, 0.00 ], # 0
 [ 0.40, 0.10, 0.30, 0.20 ], # 1
 [ 0.10, 0.10, 0.10, 0.70 ], # 2
 [ 0.20, 0.20, 0.30, 0.30 ], # 3
 [ 0.10, 0.30, 0.50, 0.10 ], # 4
 [ 0.70, 0.10, 0.10, 0.10 ]  # 5
);

sub find_max(@){
  my $max;
  my($max_i);
  my @array = @_;
  
  if($#array < 0){ return -1; }
  $max = $array[0];
  $max_i = 0;

  for(my $i = 1;$i <= $#array;$i ++){
    if($max < $array[$i]){ $max = $array[$i]; $max_i = $i; }
  }
  
  return $max_i;

}

%cton = ( 'a' => 0, 'c' => 1, 'g' => 2, 't' => 3 );
%ntoc = ( 0 => 'a', 1 => 'c', 2 => 'g', 3 => 't' );

#   l = state number
#   i = sequence observation(1,2...i in array 0,1,..i-1)

sub viterbi($$){
  my($l, $i) = @_;
  my($k, $k_max, $p, $p_max);


  if(defined($v[$l]->[$i])){ 
    return $v[$l]->[$i];
  }

  if($type[$l] eq "TYPE_S"){
    if($i <= 0){ $v[$l]->[$i] = 0.0; return $v[$l]->[$i] = 0.0; }
    else { $v[$l]->[$i] = $::LOG0; return $v[$l]->[$i]; }
  }
  elsif($type[$l] eq "TYPE_N"){
    if($i <= 0){ $v[$l]->[$i] = $::LOG0; return $v[$l]->[$i]; }
    for($p_max = $::LOG0 - 1, $k = 0;$k < $::K;$k ++){
      if($tr[$k]->[$l] > 0.0){ $p = viterbi($k, $i - 1) + log($tr[$k]->[$l]); }
      else { $p = $::LOG0; }
      if($p > $p_max){ $p_max = $p; $k_max = $k; }
    }

    $v[$l]->[$i] = $p_max + log($e[$l]->[ $cton{ $x[$i - 1] }]);
    $ptr[$l]->[$i] = $k_max;
    return $v[$l]->[$i];
  }
}

sub ptr_to_path($$){
  my $l = shift;
  my $i = shift;
  
  my $n = 0;
  my $new_l;

  $path[$n ++] = $l;
  while($type[$l] ne "TYPE_S"){
    $new_l = $ptr[$l]->[$i];
    if($type[$l] eq "TYPE_N"){ $i --; }
    $l = $new_l;
    $path[$n ++] = $l;
  }
  return $n;

}

my($i, $j);
my $result;

my $npath;
my $l;

@x = split("", "atga");
$l = $#x + 1;

$result = viterbi($::K - 1, $l);
$npath = ptr_to_path($::K - 1, $l);
  
printf("Viterbi table:\n");
for($i = 0;$i < $::K; $i ++){
  for($j = 0;$j <= $l;$j ++){
    if(!defined($v[$i]->[$j])){
      printf("---\t");
    }
    else {
      printf("%.4lf\t", exp($v[$i]->[$j]));
    }
  }
  print "\n";
}
  
print "\n";
  

printf("Pointer to previous state\n");
for($i = 0;$i <$::K; $i ++){
  for($j = 0;$j <= $l;$j ++){
    if(!defined($ptr[$i]->[$j])){
      print "--- ";
    }
    else {
      printf("%2d ", $ptr[$i]->[$j]);
    }
  }
  print "\n";
}


printf("result = %lf\n", exp($result));

for($i = $npath - 1;$i >= 0;$i --){
  printf("%d ", $path[$i]);
}
print "\n";







