#!/usr/bin/perl -w

use strict;

package PPI_matrix1_dir;

sub test1 {

    print "Hello!!!\n";

}

#
#                         $from-------
#                          /          \
#                         x            \
#                        /              \
#                     $target_partner ---$target
#
sub calc_min_dist_from_interaction($$$){

    my($object, $target_partner, $from, $target) = @_;
    my $pm_ref = $object->{'ppi_dir_matrix'};
    my %dist_h;
    my %tmp_m;

    if(defined($pm_ref->{ $target }->{ $target_partner })){
	$tmp_m{ $target }->{ $target_partner }
	= $pm_ref->{ $target }->{ $target_partner };
	delete($pm_ref->{ $target }->{ $target_partner });
    }
    if(defined($pm_ref->{ $target_partner }->{ $target })){
	$tmp_m{ $target_partner }->{ $target }
	= $pm_ref->{ $target_partner }->{ $target };
	delete($pm_ref->{ $target_partner }->{ $target });
    }
    if(defined($pm_ref->{ $from }->{ $target_partner })){
	$tmp_m{ $from }->{ $target_partner }
	= $pm_ref->{ $from }->{ $target_partner };
	delete($pm_ref->{ $from }->{ $target_partner });
    }
    if(defined($pm_ref->{ $target_partner }->{ $from })){
	$tmp_m{ $target_partner }->{ $from }
	= $pm_ref->{ $target_partner }->{ $from };
	delete($pm_ref->{ $target_partner }->{ $from });
    }

    # $pm_ref is altered. Do not do much calculation using $pm_ref
    # before restoring $pm_ref.
    PPI_matrix1::calc_min_dist_m($pm_ref, $from, \%dist_h, $target);

    foreach my $node1 (keys(%tmp_m)){
	foreach my $node2 (keys(%{$tmp_m{ $node1 }})){
	    $pm_ref->{ $node1 }->{ $node2 } = $tmp_m{ $node1 }->{ $node2 };
	}
    }

    if(defined($dist_h{ $target })){ return $dist_h{ $target }; }
    else { return undef; }

}

sub calc_min_dist_from_interaction_self($$$){

  my($object, $target_partner, $from, $target) = @_;
  my $pm_ref = $object->{'ppi_dir_matrix'};
  my %dist_h;
  my %tmp_m;
  
  if(defined($pm_ref->{ $target }->{ $target_partner })){
    $tmp_m{ $target }->{ $target_partner }
    = $pm_ref->{ $target }->{ $target_partner };
    delete($pm_ref->{ $target }->{ $target_partner });
  }
  if(defined($pm_ref->{ $target_partner }->{ $target })){
    $tmp_m{ $target_partner }->{ $target }
    = $pm_ref->{ $target_partner }->{ $target };
    delete($pm_ref->{ $target_partner }->{ $target });
  }
  
#  if(defined($pm_ref->{ $from }->{ $target_partner })){
#    $tmp_m{ $from }->{ $target_partner }
#    = $pm_ref->{ $from }->{ $target_partner };
#    delete($pm_ref->{ $from }->{ $target_partner });
#  }
#  if(defined($pm_ref->{ $target_partner }->{ $from })){
#    $tmp_m{ $target_partner }->{ $from }
#    = $pm_ref->{ $target_partner }->{ $from };
#    delete($pm_ref->{ $target_partner }->{ $from });
#  }
  
  # $pm_ref is altered. Do not do much calculation using $pm_ref
  # before restoring $pm_ref.
  PPI_matrix1::calc_min_dist_m($pm_ref, $from, \%dist_h, $target);
  
  foreach my $node1 (keys(%tmp_m)){
    foreach my $node2 (keys(%{$tmp_m{ $node1 }})){
      $pm_ref->{ $node1 }->{ $node2 } = $tmp_m{ $node1 }->{ $node2 };
    }
  }
  
  if(defined($dist_h{ $target_partner })){ 
    return $dist_h{ $target_partner };
  }
  else { return undef; }
  
}

sub calc_ext_generality3_dir {
   
    my($object, $id1, $id2, $step_limit_alt, $step_limit_loop) = @_;

    my($m_ref);
    my(@alt, @loop, $further, $deadend);
    my(@tmp, $node, @p_around, @p_around_around);

    my($min_dist, $min_dist_loop);
    my($i);

    for $i (0..$step_limit_alt - 1){ $alt[ $i ] = 0; }
    for $i (0..$step_limit_loop - 1){ $loop[ $i ] = 0; }

    $further = 0;
    $deadend = 0;

    $m_ref = $object->{'ppi_dir_matrix'};
#    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
#       !defined($m_ref->{ $id2 }->{ $id1 })){ return undef; }

    @tmp = ($object->get_next_target($id1)); # $object->get_next_target($id2)); 
  PPI_matrix1::del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    foreach $node (@p_around){
      	@p_around_around = $object->get_next_target($node);
	if(defined($m_ref->{ $id1 }->{ $node })){
	    $min_dist = $object->calc_min_dist_from_interaction
		($id1, $node, $id2);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id1, $node, $id2);
	}
	else { # Maybe this section is unnecessary...
	    $min_dist = $object->calc_min_dist_from_interaction
		($id2, $node, $id1);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id2, $node, $id1);
	}      # Maybe this section is unnecessary...
	if(defined($min_dist) && $min_dist <= $step_limit_alt){
	    $alt[ $min_dist - 1 ] ++;
	}
	elsif(defined($min_dist_loop) && $min_dist_loop <= $step_limit_loop){
	    $loop[ $min_dist_loop - 1 ] ++;
	}
	elsif($#p_around_around >= 0){ # Different from non-directional
	    $further ++;
	}
	else {
	    $deadend ++;
	}
    }

#      shift(@loop); 
    return (@alt, @loop, $further, $deadend);

}



1;
