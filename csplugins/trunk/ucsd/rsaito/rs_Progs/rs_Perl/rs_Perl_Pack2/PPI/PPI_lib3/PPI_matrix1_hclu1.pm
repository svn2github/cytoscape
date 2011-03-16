#!/usr/bin/perl -w

use strict;

package PPI_matrix1;

sub igen_sort { $a->[2] <=> $b->[2]; }
sub igen_sort_rev { $b->[2] <=> $a->[2]; }

# Prototype declaration
sub depth_first_disp(@);

sub pairwise_score_f($$$){

    my($a_ref, $b_ref, $matrix_ref) = @_;
    my($i, $j);
    my($best);

#    print "Finding best from\n";
#    print "a: ", join(",", @{$a_ref->[3]}), "\n";
#    print "b: ", join(",", @{$b_ref->[3]}), "\n";

    $best = 10000;
    for $i (0..$#{$a_ref->[3]}){
	for $j (0..$#{$b_ref->[3]}){
	    if(!defined($matrix_ref->
			{$a_ref->[3]->[$i]}->{$b_ref->[3]->[$j]})){
		next;
	    }
	    if($matrix_ref->{$a_ref->[3]->[$i]}->{$b_ref->[3]->[$j]} < $best){
		$best = $matrix_ref->{$a_ref->[3]->[$i]}->{$b_ref->[3]->[$j]};
	    }
	}
    }

#    print "Best: $best\n";
    return $best;

}


sub ppi_h_clust4 {
    
    my($ppi_obj) = @_;

    my(@branch_set);
    my(@left, @right);
    my($integration_ref, $new_cluster_ref);
    my $cluster_id;

    my($score, $best_score, $best_i, $best_j);
    my($i, $j);
    my(%clust);
    my @clust_set = ();

    my($join_pair, @join_pair, @join_pair_sort);

    my $initial_set_ref = $ppi_obj->{'protein_set'};

### Structure of each node ###    
#  ( cluster ID,
#    reference to the left node(or one atom),
#    reference to the right node(or one atom),
#    all atoms that belong to this branch,
#    score for this node )


### Initialization ###

#    print "Initialization\n";

    $cluster_id = 0;
    foreach(@$initial_set_ref){
	$clust{ $_ } = [ $cluster_id, $_, undef, [ $_ ], undef];
	push(@branch_set, $clust{ $_ });
#	print "Cluster #$cluster_id assigned\n";
	$cluster_id ++;
    }

######################

    @join_pair = ();
    foreach(@{ $ppi_obj->{'ppi_list_nr'} }){
	if($_->[0] ne $_->[1]){ push(@join_pair, $_); }
    }

    @join_pair_sort = sort igen_sort_rev @join_pair; 
    # Change sorting function as necessary.

# !!!!! Random shuffling !!!!!
#    my $rdm;
#    @join_pair_sort = ();
#    while(@join_pair){
#	$rdm = int(rand($#join_pair + 1));
#	push(@join_pair_sort, $join_pair[$rdm]);
#	splice(@join_pair, $rdm, 1);
#    }

    foreach $join_pair (@join_pair_sort){
	if($clust{ $join_pair->[0] } == $clust{ $join_pair->[1] }){
#	    print "Identical group\n";
	    next;
	} 
#	print "Processing cluster #$cluster_id...\n";
#	print join("\t", @$join_pair), "\n";
	$new_cluster_ref = [ $cluster_id,
			     $clust{ $join_pair->[0] },
			     $clust{ $join_pair->[1] },
			     [ @{$clust{ $join_pair->[0] }->[3]},
			       @{$clust{ $join_pair->[1] }->[3]} ],
			     $join_pair->[2]
			     ];
#	print "Old elements 1: ";
#	print join(",", @{$clust{ $join_pair->[0] }->[3]}), "\n";
#	print "Old elements 2: ";
#	print join(",", @{$clust{ $join_pair->[1] }->[3]}), "\n";

#	print "Number of elements: ";
#	print $#{$new_cluster_ref->[3]};
#	print "\t", $new_cluster_ref->[4];
#	print "\t", join(",", @{$new_cluster_ref->[3]});
#	print "\n";
#	print "\n";
#	$clust{ $join_pair->[0] }->[3] = undef;
#	$clust{ $join_pair->[1] }->[3] = undef;

#	print "Replacing cluster reference...\n";
	foreach(@{$new_cluster_ref->[3]}){
	    $clust{ $_ } = $new_cluster_ref;
	}
	$clust{ $join_pair->[0] } = $new_cluster_ref;
	$clust{ $join_pair->[1] } = $new_cluster_ref;
	
	push(@clust_set, $new_cluster_ref);

	$cluster_id ++;

    }

    return(\%clust, \@clust_set);

}

sub depth_first_disp(@){

    my($cluster) = @_;

    if(!ref($cluster->[1])){
	print "Cluster #$cluster->[0]: This node has atom $cluster->[1]\n";
    }
    else {
	depth_first_disp($cluster->[1]);
	depth_first_disp($cluster->[2]);
	print "Cluster #$cluster->[0]: Cluster #$cluster->[1]->[0] and #$cluster->[2]->[0] connected -> ", join(",", @{$cluster->[3]}), "\n";
	print "Best score: $cluster->[4]\n";
    }

}

1;
