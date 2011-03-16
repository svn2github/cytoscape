#!/usr/bin/perl -w

use strict;
use PPI_lib3::ORIG_math2;

use PPI_lib3::PPI_matrix1_clu1;
use PPI_lib3::PPI_matrix1_grf1;
use PPI_lib3::PPI_matrix1_fal1;
use PPI_lib3::PPI_matrix1_rnd1;
use PPI_lib3::PPI_matrix1_hclu1;

package PPI_matrix1;

#@ISA = qw(Exporter);
#@EXPORT = qw(new volume);
#@EXPORT_OK = qw($sally @listabob %harry func3);

sub new {

    my($class_name, $input) = @_;
    my($ppinet);
    my @ppi_list;
    my @ppi_list_nr;
    my %ppi_matrix;
    my @protein_set;

    if(!ref($input)){ @ppi_list = read_PPI_from_file($input); }
    elsif(ref($input) eq "ARRAY"){ @ppi_list = @$input; }
    elsif(ref($input) eq "HASH"){ 
	@ppi_list = make_ppi_list_from_matrix($input);
    }
    
    %ppi_matrix = construct_ppi_matrix_from_ppi_list(\@ppi_list);
    @ppi_list_nr = make_ppi_list_from_matrix(\%ppi_matrix);
    @protein_set = matrix_to_protein(\%ppi_matrix);

    $ppinet = {
	'ppi_list' => [ @ppi_list ],
	'ppi_list_nr' => [ @ppi_list_nr ],
	'ppi_matrix' => { %ppi_matrix },
	'protein_set' => [ @protein_set ],
    };

    bless $ppinet;
    return $ppinet;
}

sub join_ppi {

    my($object1, $object2) = @_;
    my @ppi_list = (@{$object1->{'ppi_list'}},
                    @{$object2->{'ppi_list'}});
    my $join_obj = new PPI_matrix1 \@ppi_list;
    return $join_obj;

}

sub print_info {

    my ($object) = @_;
    my ($protein1, $protein2);

    print "Protein interaction list\n";
    foreach(@{$object->{'ppi_list'}}){ print join("\t", @$_), "\n"; }
    print "\n";

    print "Non-redundant protein interaction list\n";
    foreach(@{$object->{'ppi_list_nr'}}){ print join("\t", @$_), "\n"; }
    print "\n";

    print "Protein set\n";
    foreach(@{$object->{'protein_set'}}){ print "$_\n"; }
    print "\n";

    print "Protein interaction matrix\n";
    foreach $protein1 (keys(%{$object->{'ppi_matrix'}})){
	foreach $protein2 (keys(%{$object->{'ppi_matrix'}->{$protein1}})){
	    print "$protein1\t$protein2\t";
	    print $object->{'ppi_matrix'}->{$protein1}->{$protein2};
	    print "\n";
	}
    }

}

sub convert_to_synonym($){

    my($object, $synonym_ref) = @_;
    my(@ppi, $interact_ref, @each_interaction, $protein1, $protein2);

    @ppi = ();
    foreach $interact_ref (@{$object->{'ppi_list'}}){
	@each_interaction = @$interact_ref;
	$protein1 = shift(@each_interaction);
	$protein2 = shift(@each_interaction);
	if(defined($synonym_ref->{ $protein1 })){
	    $protein1 = $synonym_ref->{ $protein1 };
	}
	if(defined($synonym_ref->{ $protein2 })){
	    $protein2 = $synonym_ref->{ $protein2 };
	}
	push(@ppi, [ $protein1, $protein2, @each_interaction ]);
    }

    return new PPI_matrix1 \@ppi;

}

sub read_PPI_from_file($){

    my($filename) = @_;
    local(*FH);
    my(@r, @ppi);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	if(!defined($r[2])){ $r[2] = ""; }
	push(@ppi, [ @r ]);
    }
    close FH;
    return @ppi;
}

sub create_PPI_from_redu_file($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(@r, %ppi);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	my($p1, $p2, $weight) = @r;
	if(defined($synonym_ref->{$p1})){ $p1 = $synonym_ref->{$p1}; }
	if(defined($synonym_ref->{$p2})){ $p2 = $synonym_ref->{$p2}; }
	if(!defined($weight) || $weight !~ /\d/){ $weight = 1; }
	$ppi{$p1}->{$p2} += $weight;
	if($p1 ne $p2){ $ppi{$p2}->{$p1} += $weight; }
    }
    close FH;
    return new PPI_matrix1 \%ppi;

}

sub create_spoke_PPI_from_complex_file($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(@r, %ppi);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	my($complex_name, @complex) = @r;
	my @complex_syn = ();
	foreach(@complex){
	    if(defined($synonym_ref->{$_})){ $_ = $synonym_ref->{$_}; }
	    push(@complex_syn, $_);
	    if(!/[a-zA-Z0-9]/){ print "Blank in $complex_name\n"; }
	}
	my($bait, @prey) = @complex_syn;
	foreach my $prey (@prey){
	    if(defined($ppi{ $bait }->{ $prey })){ 
		$ppi{ $bait }->{ $prey } ++;
		if($bait ne $prey){
		    $ppi{ $prey }->{ $bait } ++;
		}
	    }
	    else { 
		$ppi{ $bait }->{ $prey } = 1;
		$ppi{ $prey }->{ $bait } = 1;
	    }
	}
    }
    close FH;
    return new PPI_matrix1 \%ppi;

}


sub create_spoke_hash_one_way_from_complex_file($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(@r, %ppi);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	my($complex_name, @complex) = @r;
	my @complex_syn = ();
	foreach(@complex){
	    if(defined($synonym_ref->{$_})){ $_ = $synonym_ref->{$_}; }
	    push(@complex_syn, $_);
	    if(!/[a-zA-Z0-9]/){ print "Blank in $complex_name\n"; }
	}
	my($bait, @prey) = @complex_syn;
	foreach my $prey (@prey){
	    if(defined($ppi{ $bait }->{ $prey })){ 
		$ppi{ $bait }->{ $prey } ++;
	    }
	    else { 
		$ppi{ $bait }->{ $prey } = 1;
	    }
	}
    }
    close FH;
    return \%ppi;

}


# Self interaction (Homodimer ) not considered.
sub create_matrix_PPI_from_complex_file($$){

    my($filename, $synonym_ref) = @_;
    local(*FH);
    my(@r, %ppi);

    open(FH, $filename) || die "Cannot open \"$filename\":$!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	my($complex_name, @complex) = @r;
	my @complex_syn = ();
	foreach(@complex){
	    if(defined($synonym_ref->{$_})){ $_ = $synonym_ref->{$_}; }
	    push(@complex_syn, $_);
	    if(!/[a-zA-Z0-9]/){ print "Blank in $complex_name\n"; }
	}
	del_redu2(\@complex_syn);
	my($i, $j);
	for($i = 0;$i < $#complex_syn; $i ++){
	    for($j = $i + 1;$j <= $#complex_syn;$j ++){
		$ppi{ $complex_syn[$i] }->{ $complex_syn[$j] } ++;
		$ppi{ $complex_syn[$j] }->{ $complex_syn[$i] } ++;
	    }
	}
    }
    close FH;
    return new PPI_matrix1 \%ppi;

}


sub construct_ppi_matrix_from_ppi_list($){

    my($ppi_ref) = @_;
    my(%ppi_matrix);
    my($item1, $item2, $value);
    
    foreach(@$ppi_ref){
	($item1, $item2, $value) = @$_;
	if(!defined($value)){ $value = ""; }
	$ppi_matrix{ $item1 }->{ $item2 } = $value;
	$ppi_matrix{ $item2 }->{ $item1 } = $value;
    }

    return %ppi_matrix;

}

sub make_ppi_list_from_matrix($){

    my($matrix_ref) = @_;
    my($protein1, $protein2);
    my(%output);
    my(@ppi);

    foreach $protein1 (keys(%$matrix_ref)){
	foreach $protein2 (keys(%{$matrix_ref->{ $protein1 }})){
	    if(!defined($output{$protein1}->{$protein2}) &&
	       !defined($output{$protein2}->{$protein1})){
		$output{$protein1}->{$protein2} = 
		    $matrix_ref->{ $protein1 }->{ $protein2 };
	    }
	}
    }

    foreach $protein1 (keys(%output)){
	foreach $protein2 (keys(%{$output{ $protein1 }})){
	    push(@ppi, [ $protein1, $protein2, 
			 $matrix_ref->{ $protein1 }->{ $protein2 } ]);
	}
    }

    return @ppi;

}

sub matrix_to_protein($){
    my($matrix_ref) = @_;
    my($protein1, $protein2);
    my(%protein_rec);

    foreach $protein1 (keys(%$matrix_ref)){
	foreach $protein2 (keys(%{$matrix_ref->{ $protein1 }})){
	    $protein_rec{ $protein1 } = "";
	    $protein_rec{ $protein2 } = "";
	}
    }
    return keys(%protein_rec);
}

sub guilt_protein($$){
    
     my($object, $id) = @_;
     my @around_p = ();
     my @around_candidates = 
	 keys(%{$object->{'ppi_matrix'}->{ $id }});
     foreach my $around_cand (@around_candidates){
	 if($around_cand ne $id &&
	    defined($object->{'ppi_matrix'}->{$id}->{$around_cand})){
           # The above defined statement is necessary to check whether there
           # is really an interaction or just side effect of query.
	     push(@around_p, $around_cand);
	 }
     }
     return @around_p;

}

sub pick_both_dir($$$){

    my($object) = @_;
    my(@both_list);
    my(%matrix, %b_matrix);
    my($id1, $id2, $value, $ppi_ref);

    foreach $ppi_ref (@{$object->{'ppi_list'}}){
	($id1, $id2, $value) = @$ppi_ref;
	if(defined($matrix{ $id2 }->{ $id1 }) && $id1 ne $id2){
	    if(!defined($value)){ $value = ""; }
	    $b_matrix{ $id1 }->{ $id2 } = $value;
	    $b_matrix{ $id2 }->{ $id1 } = $value;
	}
	$matrix{ $id1 }->{ $id2 } = "";
    }

    return new PPI_matrix1 \%b_matrix;

}

sub num_conn($){
    my($object, $protein) = @_;
    my @partners = keys(%{$object->{'ppi_matrix'}->{ $protein }});
    my $partner;
    my $ct = 0;
    foreach $partner (@partners){
	if(defined($object->{'ppi_matrix'}->{ $protein }->{ $partner })){
	    $ct ++;
	}
    }
    return $ct;
}

sub del_redu2($){

   my($list_ref) = @_;
   my(%redu_check);

   undef(%redu_check);
   @redu_check{ @$list_ref } = "";
   @$list_ref = keys(%redu_check); 
  
}

sub matrix_copy($){
    my($ppi_matrix_ref) = @_;
    my %ppi_matrix;

    foreach my $p1 (keys(%$ppi_matrix_ref)){
	foreach my $p2 (keys(%{$ppi_matrix_ref->{$p1}})){
	    $ppi_matrix{$p1}->{$p2} = 
		$ppi_matrix_ref->{$p1}->{$p2};
	}
    }
    
    return %ppi_matrix;

}

sub isect($$){
 
    my($a_ref, $b_ref) = @_;
    my(%a);
    my @isect = ();
 
    foreach(@$a_ref){ $a{ $_ } = 1; }
    foreach(@$b_ref){ if(defined($a{ $_ })){ push(@isect, $_); } }
 
    return @isect;
 
}

1;


