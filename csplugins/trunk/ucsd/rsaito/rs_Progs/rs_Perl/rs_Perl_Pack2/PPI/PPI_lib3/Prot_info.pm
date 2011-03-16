#!/usr/bin/perl -w

use strict;

package Prot_info;

@Prot_info::ISA = qw(Exporter);
@Prot_info::EXPORT = qw(get_protein_info get_protein_info2 convert_pinfo_to_synonym); 

sub get_protein_info($){

    my $filename = shift;
    my %prot_info;
    local *FH;
    my @r;

    open(FH, $filename) || die "Cannot open \"$filename\": $!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	$prot_info{ $r[0] } = $r[1];
    }
    close FH;

    return %prot_info;

}

sub get_protein_info2($){

    my $filename = shift;
    my %prot_info;
    local *FH;
    my @r;

    open(FH, $filename) || die "Cannot open \"$filename\": $!";
    while(<FH>){
	chomp;
	@r = split(/\t/);
	my $prot_name = shift @r;
	$prot_info{ $prot_name } = join("\t", @r);
    }
    close FH;

    return %prot_info;

}

sub convert_pinfo_to_synonym($$){

    my($synonym_ref, $prot_info_raw_ref) = @_;
    my(%prot_info);

    foreach(keys(%$prot_info_raw_ref)){ 
	if(defined($synonym_ref->{ $_ })){
	    $prot_info{ $synonym_ref->{ $_ } } = $prot_info_raw_ref->{ $_ };
	}
	else { $prot_info{ $_ } = $prot_info_raw_ref->{ $_ }; }
    }
    return %prot_info;

}

1;
