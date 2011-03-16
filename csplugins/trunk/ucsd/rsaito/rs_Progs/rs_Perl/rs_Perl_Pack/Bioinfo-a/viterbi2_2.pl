#!/usr/bin/perl -w

use strict;
use vars qw(@x @type @v @t @e %cton);

sub viterbi($$);

*::LOG0 = \ -1000.0;

@type = ( "TYPE_S", "TYPE_N", "TYPE_N", "TYPE_N", "TYPE_N", "TYPE_N" );


@t = ( 
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

%cton = ( 'a' => 0, 'c' => 1, 'g' => 2, 't' => 3 );


#   l = state number
#   i = sequence observation(1,2...i in array 0,1,..i-1)
sub viterbi($$){
  my($l, $i) = @_;
  my($k, $k_max, $p, $p_max);

  if($type[$l] eq "TYPE_S"){
    if($i <= 0){ return 0.0; }
    else { return $::LOG0; }
  }
  elsif($type[$l] eq "TYPE_N"){
    if($i <= 0){ return $::LOG0; }
    for($p_max = $::LOG0 - 1, $k = 0;$k < $#t+1;$k ++){
      if($t[$k]->[$l] > 0.0){ $p = viterbi($k, $i - 1) + log($t[$k]->[$l]); }
      else { $p = $::LOG0; }
      if($p > $p_max){ $p_max = $p; $k_max = $k; }
    }
    return $p_max + log($e[$l]->[ $cton{ $x[$i - 1] } ]);
  }
}

@x = split("", "atga");

print exp(viterbi(5, 4)), "\n";




