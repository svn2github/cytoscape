#!/usr/bin/perl -w

use strict;

package PPI_matrix1;

# Cluster nodes according to given graph in the object.
# The result will be like the followings.
# ([node1, node2, node3], [node4, node5], [node6])

# Prototype declarations
sub pick_cluster_from_node_matrix_sub($$$);
sub pick_cluster_from_node_matrix_sub_ct($$$$);

sub ppi_protein_clustering($){

    my($object) = @_;
    my $ppi_matrix_ref = $object->{'ppi_matrix'};
    my $protein_set_ref = $object->{'protein_set'};
    my($protein, $protein1, $protein2);
    my(%protein_rec);
    my(%one_cluster);
    my(@cluster, @cluster_set);
    
    @protein_rec{ @$protein_set_ref } = "";

    while(keys(%protein_rec)){
	($protein) = keys(%protein_rec);
	undef(%one_cluster);
	pick_cluster_from_node_matrix_sub
	    ($protein, $ppi_matrix_ref, \%one_cluster);
	push(@cluster_set, [ keys(%one_cluster) ]);
	foreach(keys(%one_cluster)){ 
	    delete($protein_rec{$_});
	}
    }

    return @cluster_set;

}

# Clusters interactions
# Result will be like follows.
# ([[node1, node2, value1_2], [node3, node4, value3_4]], 
#  [[node5, node6, value5_6]])

sub ppi_interaction_clustering($){

    my($object) = @_;
    my($ppi_matrix_ref, %ppi_matrix_sub);
    my(@cluster_set, $cluster_ref);
    my(@ppi_cluster);
    my(@ppi_sub);

    $ppi_matrix_ref = $object->{'ppi_matrix'};
    @cluster_set = $object->ppi_protein_clustering;

    foreach $cluster_ref (@cluster_set){
	undef(%ppi_matrix_sub);
	@ppi_sub = pick_subgraph_from_id_set
	    ($cluster_ref, $ppi_matrix_ref, \%ppi_matrix_sub);
	push(@ppi_cluster, [ @ppi_sub ]);
    }

    return @ppi_cluster;

}


# Collects nodes that are directly or indirectly connected to $id
# Graph of %$ppi_matrix_ref is used.
# Result is stored in %$cluster_ref

sub pick_cluster_from_node_matrix_sub($$$){

    my($id, $ppi_matrix_ref, $cluster_ref) = @_;
    my($connected_id);

    foreach $connected_id (keys(%{$ppi_matrix_ref->{$id}})){
	if(defined($ppi_matrix_ref->{$id}->{$connected_id}) &&
	   !defined($cluster_ref->{ $connected_id })){
	    $cluster_ref->{ $connected_id } = "";
	    pick_cluster_from_node_matrix_sub
		($connected_id, $ppi_matrix_ref, $cluster_ref);
	}
    }
}

# Collects nodes that are directly or indirectly connected to $id
# Graph of %$ppi_matrix_ref is used.
# Result is expressed as list of nodes.

sub pick_cluster_from_node_matrix($$){

    my($id, $ppi_matrix_ref) = @_;
    my %cluster = ( $id => "" ); 

    pick_cluster_from_node_matrix_sub($id, $ppi_matrix_ref, \%cluster);
    return keys(%cluster);

}

# Calculates $count - "minimal step from node id" - 1 for each node
# in the same cluster. Nodes with values less than 0 is not dealt with.
# In other words, keys defined in %$cluster_ref is within $count steps
# from $id

sub pick_cluster_from_node_matrix_sub_ct($$$$){

    my($id, $ppi_matrix_ref, $cluster_ref, $count) = @_;
    my($connected_id);

    if($count == 0){ return; }
    $count --;
    foreach $connected_id (keys(%{$ppi_matrix_ref->{$id}})){
	if(!defined($cluster_ref->{ $connected_id }) ||
	   $count > $cluster_ref->{ $connected_id }){
#	    print "$count: $id $connected_id\n";
	    $cluster_ref->{ $connected_id } = $count;
	    pick_cluster_from_node_matrix_sub_ct
		($connected_id, $ppi_matrix_ref, $cluster_ref, $count);
	}
    }
}

# Returns list of nodes within $count steps from $id
# in the same cluster.

sub pick_cluster_from_node_matrix_ct($$$){

    my($id, $ppi_matrix_ref, $count) = @_;
    my %cluster = ( $id => $count ); 

    pick_cluster_from_node_matrix_sub_ct
	($id, $ppi_matrix_ref, \%cluster, $count);
    return keys(%cluster);

}

# Returns hash of nodes within $count steps from $id
# in the same cluster.
# Information of distance ($count - $distance) is attached.

sub pick_cluster_from_node_matrix_ct_descr($$$){

    my($id, $ppi_matrix_ref, $count) = @_;
    my %cluster = ( $id => $count ); 

    pick_cluster_from_node_matrix_sub_ct
	($id, $ppi_matrix_ref, \%cluster, $count);
    return %cluster;

}


# Picks list of connections within given @id_set_ref
# Result will be like follows.
# ([node1, node2, value1_2], [node3, node4, value3_4])

sub pick_subgraph_from_id_set($$$){

    my($id_set_ref, $ppi_matrix_ref, $ppi_sub_ref) = @_;
    my($id1, $id2);
    my(%id_set);
    my(@ppi_sub);

    foreach(@$id_set_ref){ $id_set{ $_ } = ""; }

    foreach $id1 (@$id_set_ref){
	foreach $id2 (keys(%{$ppi_matrix_ref->{$id1}})){
	    if(defined($id_set{ $id2 }) && 
	       !defined($ppi_sub_ref->{$id1}->{$id2})){
		$ppi_sub_ref->{$id1}->{$id2} = 
		    $ppi_matrix_ref->{$id1}->{$id2};
		$ppi_sub_ref->{$id2}->{$id1} = 
		    $ppi_matrix_ref->{$id2}->{$id1};
		push(@ppi_sub, 
		     [ $id1, $id2,  $ppi_matrix_ref->{$id1}->{$id2} ]);
	    }
	}
    }
    return @ppi_sub;
}


# Collects nodes that are connected to $id within $count steps.
# New graph object will be returned.

sub pick_cluster_from_node_ct($$){

    my($object, $id, $count) = @_;
    my(@id_set);
    my(@ppi_sub, %ppi_matrix_sub);

    @id_set = pick_cluster_from_node_matrix_ct
	($id, $object->{'ppi_matrix'}, $count);
    
    undef(%ppi_matrix_sub);
    @ppi_sub =
	pick_subgraph_from_id_set(\@id_set, 
				  $object->{'ppi_matrix'},
				  \%ppi_matrix_sub);
    return new PPI_matrix1 \@ppi_sub;
    
}

# Collects nodes that are connected to either $id1 or $id2
# within $count steps.
# New graph object will be returned.

sub pick_cluster_from_node_ct2($$$){

    my($object, $id1, $id2, $count) = @_;
    my(@id_set1, @id_set2);
    my(@ppi_sub1, @ppi_sub2, %ppi_matrix_sub1, %ppi_matrix_sub2);
    my(@ppi_sub);

    @id_set1 = pick_cluster_from_node_matrix_ct
	($id1, $object->{'ppi_matrix'}, $count);
    undef(%ppi_matrix_sub1);
    @ppi_sub1 =
	pick_subgraph_from_id_set(\@id_set1, 
				  $object->{'ppi_matrix'},
				  \%ppi_matrix_sub1);

    @id_set2 = pick_cluster_from_node_matrix_ct
	($id2, $object->{'ppi_matrix'}, $count);
    undef(%ppi_matrix_sub2);
    @ppi_sub2 =
	pick_subgraph_from_id_set(\@id_set2, 
				  $object->{'ppi_matrix'},
				  \%ppi_matrix_sub2);

    @ppi_sub = (@ppi_sub1, @ppi_sub2);
    return new PPI_matrix1 \@ppi_sub;
    
}

# Picks set of proteins connected directly/indirectly to
# a set of given ID's.
sub pick_related_from_idset($$$){

    my($id_set_ref, $ppi_matrix_ref, $count_limit) = @_;

    my(%p_list, %q_list);
    foreach(@$id_set_ref){
	$p_list{ $_ } = "";
	$q_list{ $_ } = "";
    }

    my $count = 0;
    while(keys(%q_list) && $count < $count_limit){

#	print "$count P_LIST\t", join(",", keys(%p_list)), "\n";
#	print "$count Q_LIST\t", join(",", keys(%q_list)), "\n";

	my @n_list = ();
	my($p1, $p2);
	foreach $p1 (keys(%q_list)){
	    foreach $p2 (keys(%{$ppi_matrix_ref->{$p1}})){
		if(defined($ppi_matrix_ref->{$p1}->{$p2}) &&
		   !defined($p_list{ $p2 })){ push(@n_list, $p2); }
	    }
	}

	undef(%q_list);
	foreach(@n_list){
	    $p_list{ $_ } = "";
	    $q_list{ $_ } = "";
	}

	$count ++;
    }

    return keys(%p_list);

}


sub calc_min_dist {

    my $object = shift;
    my $id = shift;
    my $solved_ref = shift;
    my $target_id = shift;

    my $ppi_matrix_ref = $object->{'ppi_matrix'};
    my(%current_query, %next_query, $query_node);
    my %min_dist;
    my $connected_node;
    my $step = 0;

    $current_query{ $id } = "";
    $solved_ref->{ $id } = $step;
    $step ++;

    while(keys(%current_query)){
#	print "Current query: ", join(",", keys(%current_query)), "\n";
	undef(%next_query);
	foreach $query_node (keys(%current_query)){
	    foreach $connected_node
		(keys(%{$ppi_matrix_ref->{$query_node}})){
		    if(!defined($solved_ref->{$connected_node})){
			$solved_ref->{ $connected_node } = $step;
			$next_query{ $connected_node } = "";
		    }
	    }
	}
	%current_query = %next_query;
	$step ++;
	if(defined($target_id) && 
	   defined($solved_ref->{ $target_id })){ last; }
    }

}

# This function is applicable to directed interactions.
sub calc_min_dist_m($$$$){

    my $ppi_matrix_ref = shift;
    my $id = shift;
    my $solved_ref = shift;
    my $target_id = shift;

    my(%current_query, %next_query, $query_node);
    my %min_dist;
    my $connected_node;
    my $step = 0;

    $current_query{ $id } = "";
    $solved_ref->{ $id } = $step;
    $step ++;

    while(keys(%current_query)){
#	print "Current query: ", join(",", keys(%current_query)), "\n";
	undef(%next_query);
	foreach $query_node (keys(%current_query)){
	    foreach $connected_node
		(keys(%{$ppi_matrix_ref->{$query_node}})){
		    if(!defined($solved_ref->{$connected_node})){
			$solved_ref->{ $connected_node } = $step;
			$next_query{ $connected_node } = "";
		    }
	    }
	}
	%current_query = %next_query;
	$step ++;
	if(defined($target_id) && 
	   defined($solved_ref->{ $target_id })){ last; }
    }

}

sub calc_min_dist_from_interaction($$$){

    my($object, $target_partner, $from, $target) = @_;
    my $pm_ref = $object->{'ppi_matrix'};
    my %dist_h;

    my %m_copy;
    my($node1, $node2);
    foreach $node1 (keys(%$pm_ref)){
	foreach $node2 (keys(%{$pm_ref->{ $node1 }})){
	    $m_copy{ $node1 }->{ $node2 } = $pm_ref->{ $node1 }->{ $node2 };
	}
    }

    if(defined($m_copy{ $target }->{ $target_partner })){
	delete($m_copy{ $target }->{ $target_partner });
    }
    if(defined($m_copy{ $target_partner }->{ $target })){
	delete($m_copy{ $target_partner }->{ $target });
    }
    if(defined($m_copy{ $from }->{ $target_partner })){
	delete($m_copy{ $from }->{ $target_partner });
    }
    if(defined($m_copy{ $target_partner }->{ $from })){
	delete($m_copy{ $target_partner }->{ $from });
    }

    calc_min_dist_m(\%m_copy, $from, \%dist_h, $target);
    
    if(defined($dist_h{ $target })){ return $dist_h{ $target }; }
    else { return undef; }

}

sub calc_min_dist_from_interaction_self($$$){

    my($object, $target_partner, $from, $target) = @_;
    my $pm_ref = $object->{'ppi_matrix'};

    my %dist_h;

    my %m_copy;
    my($node1, $node2);
    foreach $node1 (keys(%$pm_ref)){
	foreach $node2 (keys(%{$pm_ref->{ $node1 }})){
	    $m_copy{ $node1 }->{ $node2 } = $pm_ref->{ $node1 }->{ $node2 };
	}
    }

    if(defined($m_copy{ $target }->{ $target_partner })){
	delete($m_copy{ $target }->{ $target_partner });
    }
    if(defined($m_copy{ $target_partner }->{ $target })){
	delete($m_copy{ $target_partner }->{ $target });
    }
    if(defined($m_copy{ $from }->{ $target_partner })){
	delete($m_copy{ $from }->{ $target_partner });
    }
    if(defined($m_copy{ $target_partner }->{ $from })){
	delete($m_copy{ $target_partner }->{ $from });
    }
    
    calc_min_dist_m(\%m_copy, $from, \%dist_h, $target);

    if(defined($dist_h{ $target_partner })){ 
	return $dist_h{ $target_partner };
    }
    else { return undef; }

}

sub interacting_partners($$){
    my($object, $id) = @_;
    
    return(keys(%{$object->{'ppi_matrix'}->{$id}}));
    # This will automatically define $object->{'ppi_matrix'}->{$id}.
    
}

sub k_core_network($$){

    my($object, $k) = @_;
    my %ppi_matrix;

    %ppi_matrix = matrix_copy($object->{'ppi_matrix'});

    my $delete_count = 0;
    my $step = 0;
    do {
	my $ppi = new PPI_matrix1 \%ppi_matrix;
	$delete_count = 0;
#	print "***** Step $step *****\n";
	foreach my $p (@{$ppi->{'protein_set'}}){
	    my @interact_p = $ppi->interacting_partners($p);
#	    print "Checking protein $p...", $#interact_p+1, "\n";
	    if(!(@interact_p) || $#interact_p + 1 < $k){
		foreach my $partner (@interact_p){
		    delete $ppi_matrix{$p}->{$partner};
		    delete $ppi_matrix{$partner}->{$p};
		} # list all connected nodes and delete!
		delete $ppi_matrix{$p};
#		print "Step $step: Protein $p deleted.\n";
		$delete_count ++;
	    }
	}
	$step ++;
    }while($delete_count > 0);

    return new PPI_matrix1 \%ppi_matrix;

}

# This function may include some bugs.
# Watch for the usage of "references".
sub calc_ext_generality1($$$){
   
    my($object, $id1, $id2, $step_limit) = @_;

    my($m_ref);
    my @gen_array;
    my(@tmp, $node, @p_around, @p_around_around);

    my($dead_end, $further);
    my($min_dist);
    my($i);

    for $i (0..$step_limit - 1){ $gen_array[ $i ] = 0; }
    $dead_end = 0;
    $further = 0;

    $m_ref = $object->{'ppi_matrix'};
    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
       !defined($m_ref->{ $id2 }->{ $id1 })){ return undef; }

    @tmp = (keys(%{$m_ref->{ $id1 }}), keys(%{$m_ref->{ $id2 }}));
    del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    foreach $node (@p_around){
	if(defined($m_ref->{ $node }->{ $id1 })){
	    $min_dist = $object->calc_min_dist_from_interaction
		($id1, $node, $id2);
	}
	else {
	    $min_dist = $object->calc_min_dist_from_interaction
		($id2, $node, $id1);
	}
	if(defined($min_dist) && $min_dist <= $step_limit){
	    $gen_array[ $min_dist - 1 ] ++;
	}
	else {
	    @p_around_around = keys(%{$m_ref->{ $node }});
	    if($#p_around_around > 0){ 
		$further ++;
	    }
	    else {
		$dead_end ++;
	    }
	}
    }
    
    push(@gen_array, $further);
    push(@gen_array, $dead_end);
    return @gen_array;
}


# This function may include some bugs.
# Watch for the usage of "references".
sub calc_ext_generality2($$$){
   
    my($object, $id1, $id2, $step_limit) = @_;

    my($m_ref);
    my(@alt, @loop, $further, $deadend);
    my(@tmp, $node, @p_around, @p_around_around);

    my($min_dist, $min_dist_loop);
    my($i);

    for $i (0..$step_limit - 1){ 
	$alt[ $i ] = 0;
	$loop[ $i ] = 0;
    }
    $further = 0;
    $deadend = 0;

    $m_ref = $object->{'ppi_matrix'};
    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
       !defined($m_ref->{ $id2 }->{ $id1 })){ return undef; }

    @tmp = (keys(%{$m_ref->{ $id1 }}), keys(%{$m_ref->{ $id2 }}));
    del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    foreach $node (@p_around){
	@p_around_around = keys(%{$m_ref->{ $node }});
	if(defined($m_ref->{ $node }->{ $id1 })){
	    $min_dist = $object->calc_min_dist_from_interaction
		($id1, $node, $id2);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id1, $node, $id2);
	}
	else {
	    $min_dist = $object->calc_min_dist_from_interaction
		($id2, $node, $id1);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id2, $node, $id1);
	}
	if(defined($min_dist) && $min_dist <= $step_limit){
	    $alt[ $min_dist - 1 ] ++;
	}
	elsif(defined($min_dist_loop) && $min_dist_loop <= $step_limit){
	    $loop[ $min_dist_loop - 1 ] ++;
	}
	elsif($#p_around_around > 0){ 
	    $further ++;
	}
	else {
	    $deadend ++;
	}
    }
    
    return (@alt, @loop, $further, $deadend);

}

sub calc_ext_generality3 {
   
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

    $m_ref = $object->{'ppi_matrix'};
#    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
#       !defined($m_ref->{ $id2 }->{ $id1 })){ return undef; }

    @tmp = ($object->guilt_protein($id1), $object->guilt_protein($id2)); 
    del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    foreach $node (@p_around){
	@p_around_around = $object->guilt_protein($node);
	if(defined($m_ref->{ $node }->{ $id1 })){
	    $min_dist = $object->calc_min_dist_from_interaction
		($id1, $node, $id2);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id1, $node, $id2);
	}
	else {
	    $min_dist = $object->calc_min_dist_from_interaction
		($id2, $node, $id1);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id2, $node, $id1);
	}
	if(defined($min_dist) && $min_dist <= $step_limit_alt){
	    $alt[ $min_dist - 1 ] ++;
	}
	elsif(defined($min_dist_loop) && $min_dist_loop <= $step_limit_loop){
	    $loop[ $min_dist_loop - 1 ] ++;
	}
	elsif($#p_around_around > 0){ 
	    $further ++;
	}
	else {
	    $deadend ++;
	}
    }
    
    shift(@loop); # This is because no case where $min_dist_loop == 1
    return (@alt, @loop, $further, $deadend);

}


sub calc_ext_generality3_val {
   
    my($object, $id1, $id2, $step_limit_alt, $step_limit_loop,
       $vector_ref, $correction) = @_;
    my(@i_values);
    my($gen);

    @i_values = calc_ext_generality3($object, $id1, $id2, 
				     $step_limit_alt, $step_limit_loop);

    $gen = ORIG_math2::v_i_prod(\@i_values, $vector_ref) + $correction;
    return $gen;

}

sub calc_ext_generality4 {
   
    my($object, $id1, $id2, $step_limit_alt, $step_limit_loop,
       $component_combi_ref) = @_;

    my($m_ref);
    my(@alt, @loop, $further, $deadend);
    my(@alt_c, @loop_c, $further_c, $deadend_c);
    my($alt_ref, $loop_ref, $further_ref, $deadend_ref);

    my(@tmp, $node, @p_around, @p_around_around);

    my($min_dist, $min_dist_loop);
    my($i);

    for $i (0..$step_limit_alt - 1){ $alt[ $i ] = 0; }
    for $i (0..$step_limit_loop - 1){ $loop[ $i ] = 0; }
    for $i (0..$step_limit_alt - 1){ $alt_c[ $i ] = 0; }
    for $i (0..$step_limit_loop - 1){ $loop_c[ $i ] = 0; }

    $further = 0;
    $deadend = 0;
    $further_c = 0;
    $deadend_c = 0;

    $m_ref = $object->{'ppi_matrix'};
#    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
#       !defined($m_ref->{ $id2 }->{ $id1 })){ return undef; }

    @tmp = ($object->guilt_protein($id1), $object->guilt_protein($id2)); 
    del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    foreach $node (@p_around){
	@p_around_around = $object->guilt_protein($node);
	if(defined($component_combi_ref->{$id1}->{$node}) &&
	   defined($component_combi_ref->{$id2}->{$node})){
	    $alt_ref = \@alt_c;
	    $loop_ref = \@loop_c;
	    $further_ref = \$further_c;
	    $deadend_ref = \$deadend_c;
	}
	else {
	    $alt_ref = \@alt;
	    $loop_ref = \@loop;
	    $further_ref = \$further;
	    $deadend_ref = \$deadend;
	}

	if(defined($m_ref->{ $node }->{ $id1 })){
	    $min_dist = $object->calc_min_dist_from_interaction
		($id1, $node, $id2);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id1, $node, $id2);
	}
	else {
	    $min_dist = $object->calc_min_dist_from_interaction
		($id2, $node, $id1);
	    $min_dist_loop = $object->calc_min_dist_from_interaction_self
		($id2, $node, $id1);
	}
	if(defined($min_dist) && $min_dist <= $step_limit_alt){
	    $alt_ref->[ $min_dist - 1 ] ++;
	}
	elsif(defined($min_dist_loop) && $min_dist_loop <= $step_limit_loop){
	    $loop_ref->[ $min_dist_loop - 1 ] ++;
	}
	elsif($#p_around_around > 0){ 
	    $$further_ref ++;
	}
	else {
	    $$deadend_ref ++;
	}
    }
    
    shift(@loop); # This is because no case where $min_dist_loop == 1
    shift(@loop_c); # This is because no case where $min_dist_loop == 1
    return (@alt, @loop, $further, $deadend,
	    @alt_c, @loop_c, $further_c, $deadend_c);

}


sub calc_ppi_spec($$$){

    my($object, $id1, $id2, $mode) = @_;
    my($m_ref);
    my $spec = 0;
    my(@tmp, $protein, @p_around, @p_around_around);

    $m_ref = $object->{'ppi_matrix'};
    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
       !defined($m_ref->{ $id2 }->{ $id1 })){ return -1; }

    @tmp = (keys(%{$m_ref->{ $id1 }}), keys(%{$m_ref->{ $id2 }}));
    del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    if($mode == 1){ 
	$spec = $#p_around + 1 + 1;
    }
    elsif($mode == 2){
	foreach $protein (@p_around){
	    @p_around_around = keys(%{$m_ref->{ $protein }});
	    if($#p_around_around == 0){ $spec ++; } # Only one protein
	}
	$spec += 1;
    }
    else { die "Illegal interaction-spec calculation mode: $mode\n"; }
    
    return $spec;

}

sub count_triangles {
   
    my($object, $id1, $id2) = @_;

    my($m_ref);
    my(@tmp, $node, @p_around);
    my $count = 0;

    $m_ref = $object->{'ppi_matrix'};

#    if(!defined($m_ref->{ $id1 }->{ $id2 }) &&
#       !defined($m_ref->{ $id2 }->{ $id1 })){ return undef; }

    @tmp = ($object->guilt_protein($id1), $object->guilt_protein($id2)); 
    del_redu2(\@tmp);
    foreach(@tmp){ if($_ ne $id1 && $_ ne $id2){ push(@p_around, $_); }}

    foreach $node (@p_around){
	if(defined($m_ref->{ $node }->{ $id1 }) &&
	   defined($m_ref->{ $node }->{ $id2 })){ $count ++; }
    }
    
    return $count;

}


sub auto_delete_false($$$){

    my($object, $thres, $mode) = @_;
    my($spec);
    my($ppi);
    my @ppi_soph = ();
    my($protein1, $protein2, $value);

    foreach $ppi (@{$object->{'ppi_list'}}){
	($protein1, $protein2, $value) = @$ppi;
	$spec = $object->calc_ppi_spec($protein1, $protein2, $mode);
	if($spec < $thres){ 
	    push(@ppi_soph, [ $protein1, $protein2, $value ]);
	}
    }

    return new PPI_matrix1 \@ppi_soph;

}

sub auto_delete_false3($$$){

    my($object, $thres, $mode) = @_;
    my($spec);
    my($ppi);
    my @ppi_soph = ();
    my @ppi_cut = ();
    my($ppi_soph, $ppi_cut);
    my($protein1, $protein2, $value);

    foreach $ppi (@{$object->{'ppi_list'}}){
        ($protein1, $protein2, $value) = @$ppi;
        $spec = $object->calc_ppi_spec($protein1, $protein2, $mode);
        if($spec < $thres){ 
            push(@ppi_soph, [ $protein1, $protein2, $value ]);
        }
        else {
            push(@ppi_cut, [ $protein1, $protein2, $value ]);
        }
    }

    $ppi_soph = new PPI_matrix1 \@ppi_soph;
    $ppi_cut = new PPI_matrix1 \@ppi_cut;
    return ($ppi_soph, $ppi_cut);

}

sub auto_delete_false_ext($$$$$){

    my($object, $thres,
       $step_limit_alt, $step_limit_loop, 
       $rotation_ref, $correction) = @_;

    my(@gen_array, $gen);
    my($ppi);
    my @ppi_soph = ();
    my($protein1, $protein2, $value);

    foreach $ppi (@{$object->{'ppi_list'}}){
	($protein1, $protein2, $value) = @$ppi;
	$gen = $object->calc_ext_generality3_val
	    ($protein1, $protein2,
	     $step_limit_alt, $step_limit_loop,
	     $rotation_ref, $correction);
	if($gen < $thres){ 
	    push(@ppi_soph, [ $protein1, $protein2, $value ]);
	}
    }

    return new PPI_matrix1 \@ppi_soph;

}


sub construct_ppi_gen3 {

    my($object, $step_limit_alt, $step_limit_loop,
       $vector_ref, $correction) = @_;

    my @ppi_list = ();
    my $gen;
    my $ppi_pair_ref;

    foreach $ppi_pair_ref (@{$object->{'ppi_list_nr'}}){
	$gen = calc_ext_generality3_val($object,
					$ppi_pair_ref->[0],
					$ppi_pair_ref->[1],
					$step_limit_alt,
					$step_limit_loop,
					$vector_ref, $correction);
	push(@ppi_list, [ $ppi_pair_ref->[0], $ppi_pair_ref->[1], $gen ]);
    }

    return new PPI_matrix1 \@ppi_list;

}


sub best_ig($$){

    my($object, $from, $target) = @_;
    
    my $g_ref = $object->{'ppi_matrix'};

    my @search_set = ($from);
    my @search_set_ig = (-9999);
    my @search_set_path = ([$from]);
    my @passed = ({ $from => "" });

    my $current_best_ig = 10000;
    my $current_best_step = 0;
    my @current_best_path = ();

    my $lower_bound_ig;
    
    my @new_search_set;
    my @new_search_set_ig;
    my @new_search_set_path;
    my @new_passed;

    my $cur_ig;
    my $step;
    my($prt, $ig, @stp, %pss);

    my($i);
    my $surround_p;

    $lower_bound_ig = 10000;
    foreach(keys(%{$g_ref->{ $target }})){
	if($lower_bound_ig > $g_ref->{ $target }->{ $_ }){
	    $lower_bound_ig = $g_ref->{ $target }->{ $_ };
	}
    }

    $step = 1;
    while(@search_set && $current_best_ig > $lower_bound_ig){
	print "\n";
	print "***** Step $step *****\n";
	print "Lower bound ig   : $lower_bound_ig\n";
	print "Current best ig  : $current_best_ig\n";
	print "Current best step: $current_best_step\n";
	print "Current best path: ", join("->", @current_best_path), "\n";
	print "\n";

	print "Remaining candidate path: ", $#search_set + 1, "\n";
#	for $i (0..$#search_set){

#	    print "$search_set[$i] ($search_set_ig[$i])\t";
#	    print "Path: ", join("->", @{$search_set_path[$i]}), "\t";
#	    print join(",", keys(%{$passed[$i]})), "\n";

#	}
#	print "\n";

	@new_search_set = ();
	@new_search_set_ig = ();
	@new_search_set_path = ();
	@new_passed = ();
	
	for $i (0..$#search_set){
	    
	    $prt = $search_set[$i];
	    $ig = $search_set_ig[$i];
	    
	    foreach $surround_p (keys(%{$g_ref->{ $prt }})){

		@stp = @{$search_set_path[$i]};
		%pss = %{$passed[$i]};

#		print "$prt : Checking attached node $surround_p...\n";
		$cur_ig = ORIG_math2::max_2($ig, 
					    $g_ref->{ $prt }->{ $surround_p });
#		print "Current ig: $cur_ig\n";
#		print "Passed: ", join(",", keys(%pss)), "\n";
		if(!defined($pss{$surround_p}) 
		   && $cur_ig < $current_best_ig){
		    if($surround_p eq $target){
			print "Reached the target!\n";
			$current_best_ig = $cur_ig;
			$current_best_step = $step;
			@current_best_path = @stp;
			push(@current_best_path, $target);
		    }
		    else {
			push(@new_search_set, $surround_p);
			push(@new_search_set_ig, $cur_ig);
			push(@stp, $surround_p);
			push(@new_search_set_path, [ @stp ]);
			$pss{ $surround_p } = "";
			push(@new_passed, { %pss });
		    }
		}
		else { 
#		    print "$surround_p not appropriate.\n";
		}
#		print "\n";
	    }
	}

	@search_set = @new_search_set;
	@search_set_ig = @new_search_set_ig;
	@search_set_path = @new_search_set_path;
	@passed = @new_passed;
	$step ++;

#	sleep 1;

    }
    
    return($current_best_ig, $current_best_step, \@current_best_path);

}



1;
