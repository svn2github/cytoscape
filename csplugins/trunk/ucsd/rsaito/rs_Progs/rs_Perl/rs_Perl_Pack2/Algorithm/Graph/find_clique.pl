#!/usr/bin/perl -w

use strict;
use Data::Dumper;

my $sif = shift @ARGV;


my $strct;

open(SIF,$sif);
while(<SIF>){
  chomp;
  if($_ =~ /^(.+) pp (.+)$/){

    my @array = ($1,$2);
    push(@$strct,\@array);
  }
}
close SIF;


my $clique_strct = &FindQlique($strct);
print Dumper $clique_strct;



sub FindQlique(){

  my $strct = shift;

  my $exist;
  my $clique;
  
  {
    for my $v1 (@$strct){
      my ($i,$j) = @$v1;
      
      $exist->{$i}->{$j} = 1;
      $exist->{$j}->{$i} = 1;
      
      $clique->{2}->{$i}->{$j} = 1;
      $clique->{2}->{$j}->{$i} = 1;
    }
    
    my $sig  = 1;
    my $size = 2;
    while(1){
      last unless($sig);
      ($sig,$clique) = &q_($clique,$size,$exist);
      $size++;
    }
  }
  

  my $set_mem;

  while(my($size,$value1) = each %$clique){
    while(my($root,$value2) = each %$value1){
      while(my($tail,$sig) = each %$value2){
	if($sig){
	  my @root_array = split(/\:/,$root);
	  my $mem_line = join ':', sort{$a cmp $b}(($tail),@root_array);
	  $set_mem->{$mem_line} = $size;
	}
      }
    }
  }

  my $clique_strct;
  while(my($set,$size) = each %$set_mem){
    my @array = split(/\:/,$set);
    push(@{$clique_strct->{$size}},\@array);
  }

  return $clique_strct;
}


sub q_(){

  my $clique = shift;
  my $size   = shift;
  my $exist  = shift;

  my $mem;
  while(my($n_i,$v_i) = each %{$clique->{$size}}){
    my @array = keys %$v_i;
    
    for(my $j1=0;$j1<=$#array;$j1++){
      for(my $j2=$j1+1;$j2<=$#array;$j2++){
	my $n_j1 = $array[$j1];
	my $n_j2 = $array[$j2];
	
	if(exists $exist->{$n_j1}->{$n_j2}){
	  
	  $clique->{$size}->{$n_i}->{$n_j1} = 0;
	  $clique->{$size}->{$n_i}->{$n_j2} = 0;

	  my @tmp_i = split(/\:/,$n_i);
	  my $pair_key = join ':', sort{$a cmp $b}(@tmp_i,($n_j1,$n_j2));
	  $mem->{$pair_key}++;
	  
	}else{
	  delete $exist->{$n_j1}->{$n_j2};
	}
      }
    }
  }

  $size++;


  my $sig = 0;

  for my $pair_key (keys %$mem){

    $sig++;

    my @array = split(/\:/,$pair_key);

    for my $tail (@array){
      my @root = ();
      for my $r (@array){
	push(@root,$r) if($r ne $tail);
      }
      my $root_set = join ':', sort{$a cmp $b}@root;
      $clique->{$size}->{$root_set}->{$tail} = 1;
    }
  }


  return ($sig,$clique);
}

