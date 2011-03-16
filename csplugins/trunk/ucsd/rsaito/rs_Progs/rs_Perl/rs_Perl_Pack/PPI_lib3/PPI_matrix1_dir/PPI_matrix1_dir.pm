#!/usr/bin/perl -w

use strict;

package PPI_matrix1_dir;
use vars qw(@ISA);
use PPI_lib3::PPI_matrix1;
use PPI_lib3::PPI_matrix1_dir::PPI_matrix1_dir_clu1;

@ISA = qw(PPI_matrix1);

sub new {

    my($class_name, $input) = @_;
    my $ppi_dir = new PPI_matrix1 $input;

    my(@ppi_dir, @ppi_dir_nr);
    my %ppi_dir_matrix;
    my %ppi_dir_matrix_r;
   
    if(!ref($input)){ 
	@ppi_dir = read_PPI_dir_from_file($input);
    }
    elsif(ref($input) eq "ARRAY"){ @ppi_dir = @$input; } 
    elsif(ref($input) eq "HASH"){
	@ppi_dir = make_ppi_dir_list_from_matrix($input);
    }

    %ppi_dir_matrix = construct_ppi_dir_matrix_from_ppi_dir_list
	(\@ppi_dir);
    %ppi_dir_matrix_r = construct_ppi_dir_matrix_r_from_ppi_dir_list
	(\@ppi_dir);
    @ppi_dir_nr = make_ppi_dir_list_from_matrix(\%ppi_dir_matrix);
    
    $ppi_dir->{ 'ppi_dir_nr' } = [ @ppi_dir_nr ];
    $ppi_dir->{ 'ppi_dir_matrix' } = { %ppi_dir_matrix }; 
    $ppi_dir->{ 'ppi_dir_matrix_r' } = { %ppi_dir_matrix_r };
    $ppi_dir->{ 'edges' } = [ get_edges(\%ppi_dir_matrix) ];

    return bless $ppi_dir;

}

sub print_dir_info {

    my($obj) = shift;


    print "Raw list\n";
    foreach(@{$obj->{'ppi_list'}}){ print join("\t", @$_), "\n"; }
    print "\n";

    print "Non-redundant Directional list\n";
    foreach my $elem (@{$obj->{'ppi_dir_nr'}}){ 
	print join("\t", $elem->[0], $elem->[1]), "\t";
	print join(",", @{$elem->[2]});
	print "\n";
    }
    print "\n";

    print "Directional Matrix information:\n";
    foreach my $item1 (keys(%{$obj->{'ppi_dir_matrix'}})){
	foreach my $item2 (keys(%{$obj->{'ppi_dir_matrix'}->{$item1}})){
	    print "$item1\t$item2\t";
	    if($obj->{'ppi_dir_matrix'}
	       ->{ $item1 }->{ $item2 } ne ""){
		print join(",", @{$obj->{'ppi_dir_matrix'}
			   ->{ $item1 }->{ $item2 }});
	    }
	    print "\n";
	}
    }
    print "\n";

    print "Reverse Directional Matrix information:\n";
    foreach my $item1 (keys(%{$obj->{'ppi_dir_matrix_r'}})){
	foreach my $item2 (keys(%{$obj->{'ppi_dir_matrix_r'}->{$item1}})){
	    print "$item1\t$item2\t";
	    if($obj->{'ppi_dir_matrix_r'}
	       ->{ $item1 }->{ $item2 } ne ""){
		print join(",", @{$obj->{'ppi_dir_matrix_r'}
			   ->{ $item1 }->{ $item2 }});
	    }
	    print "\n";
	}
    }
    print "\n";

    print "Edges:\n";
    print join "\n", @{$obj->{'edges'}};
    print "\n";

}

sub read_PPI_dir_from_file($){

    my($filename) = @_;
    local(*FH);

    my @ppi_dir = ();

    open(FH, $filename) || die "Cannot open \"$filename\":$!";
    while(<FH>){
	chomp;
	my($p1, $p2, $vals) = split(/\t/);
	if(!defined($vals)){ $vals = ""; }
	my(@vals) = split(/,/, $vals);
	push(@ppi_dir, [ $p1, $p2, [ @vals ]]);
    }
    close FH;
    return @ppi_dir;

}

sub construct_ppi_dir_matrix_from_ppi_dir_list($){

    my($ppi_dir_ref) = @_;
    my(%ppi_dir_matrix);
    
    foreach my $ppi_dir (@$ppi_dir_ref){
	my($item1, $item2, $value) = @$ppi_dir;
	push(@{$ppi_dir_matrix{ $item1 }->{ $item2 }}, @$value);
    }
    
    foreach my $item1 (keys(%ppi_dir_matrix)){
	foreach my $item2 (keys(%{$ppi_dir_matrix{ $item1 }})){
	    if($ppi_dir_matrix{ $item1 }->{ $item2 } ne ""){
		PPI_matrix1::del_redu2($ppi_dir_matrix{ $item1 }->{ $item2 });
	      }
	}
    }
    
    return %ppi_dir_matrix;

}

sub construct_ppi_dir_matrix_r_from_ppi_dir_list($){

    my($ppi_dir_ref) = @_;
    my(%ppi_dir_matrix);
    
    foreach my $ppi_dir (@$ppi_dir_ref){
	my($item1, $item2, $value) = @$ppi_dir;
	push(@{$ppi_dir_matrix{ $item2 }->{ $item1 }}, @$value);
    }
    
    foreach my $item1 (keys(%ppi_dir_matrix)){
	foreach my $item2 (keys(%{$ppi_dir_matrix{ $item1 }})){
	    if($ppi_dir_matrix{ $item1 }->{ $item2 } ne ""){
		PPI_matrix1::del_redu2($ppi_dir_matrix{ $item1 }->{ $item2 });
	      }
	}
    }
    
    return %ppi_dir_matrix;

}

sub make_ppi_dir_list_from_matrix($){

    my($matrix_ref) = @_;
    my($protein1, $protein2);
    my(%output);
    my @ppi_dir = ();

    foreach $protein1 (keys(%$matrix_ref)){
	foreach $protein2 (keys(%{$matrix_ref->{ $protein1 }})){
	    push(@ppi_dir, [ $protein1, $protein2, 
			     [ @{$matrix_ref->
				 { $protein1 }->{ $protein2 }}] ]);
	}
    }

    return @ppi_dir;

}

sub get_edges($){

    my($matrix_ref) = @_;
    my %edges;

    foreach my $p1 (keys(%$matrix_ref)){
	foreach my $p2 (keys(%{$matrix_ref->{ $p1 }})){
	    foreach my $edge (@{$matrix_ref->{ $p1 }->{ $p2 }}){
		if($edge ne ""){ $edges{ $edge } = ""; }
	    }
	}
    }

    return keys %edges;

}

# Autoregulation NOT considered.
sub get_next_target($$){
    
     my($object, $id) = @_;
     my @around_p = ();
     my @around_candidates = 
	 keys(%{$object->{'ppi_dir_matrix'}->{ $id }});
     foreach my $around_cand (@around_candidates){
	 if($around_cand ne $id &&
	    defined($object->{'ppi_dir_matrix'}->{$id}->{$around_cand})){
           # The above defined statement is necessary to check whether there
           # is really an interaction or just side effect of query.
	     push(@around_p, $around_cand);
	 }
     }
     return @around_p;

}

# Autoregulation NOT considered.
sub get_previous_target($$){
    
     my($object, $id) = @_;
     my @around_p = ();
     my @around_candidates = 
	 keys(%{$object->{'ppi_dir_matrix_r'}->{ $id }});
     foreach my $around_cand (@around_candidates){
	 if($around_cand ne $id &&
	    defined($object->{'ppi_dir_matrix_r'}->{$id}->{$around_cand})){
           # The above defined statement is necessary to check whether there
           # is really an interaction or just side effect of query.
	     push(@around_p, $around_cand);
	 }
     }
     return @around_p;

}

sub convert_to_synonym {

    my($object, $synonym_ref) = @_;
    my(@pdi, $interact_ref, @each_interaction);

    @pdi = ();
    foreach $interact_ref (@{$object->{'ppi_list'}}){
	my($p1, $p2, $vals) = @$interact_ref;
	if(!defined($vals)){ $vals = ""; }
	my(@vals) = split(/,/, $vals);
	if(defined($synonym_ref->{ $p1 })){
	    $p1 = $synonym_ref->{ $p1 };
	}
	if(defined($synonym_ref->{ $p2 })){
	    $p2 = $synonym_ref->{ $p2 };
	}
	push(@pdi, [ $p1, $p2, [ @vals ]]);
    }

#    foreach(@pdi){ print join("\t", @$_), "\n"; }

    return new PPI_matrix1_dir \@pdi;

}

# Under construction .. Reversible reaction elimination
sub make_label_net($){
    
    my $obj = shift;
    my($node1, $node2, $node3);
    my(%output);
    my $matrix_ref = $obj->{ 'ppi_dir_matrix' };

    foreach $node1 (keys(%$matrix_ref)){
	foreach $node2 (keys(%{$matrix_ref->{ $node1 }})){
	    foreach $node3 (keys(%{$matrix_ref->{ $node2 }})){
		my @edges1 = @{$matrix_ref->{ $node1 }-> { $node2 }};
		my @edges2 = @{$matrix_ref->{ $node2 }-> { $node3 }};
		foreach my $each_edge1 (@edges1){
		    foreach my $each_edge2 (@edges2){
			if($each_edge1 ne "" && $each_edge2 ne ""){
			    push(@{$output{ $each_edge1 }->{ $each_edge2 }},
				 $node2);
			}
		    }
		}
	    }
	}
    }
    
    return new PPI_matrix1_dir \%output;

}


1;

