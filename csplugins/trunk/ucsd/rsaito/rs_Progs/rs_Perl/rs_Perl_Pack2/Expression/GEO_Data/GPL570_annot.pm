#!/usr/bin/env perl

use strict;
use warnings;

use General::Usefuls::rsConfig;

package GPL570_annot;


sub new {

    my $class = shift;
    my $obj = {};
    bless $obj;

	$obj->read_annot();
    
    return $obj;

}

sub read_annot($){

	my $obj      = shift;

    my %h = rsConfig::read_config("ExpData.cnf");
	my $annot_file   = $h{"GPL570.annot"};

	local *FH;
	open(FH, $annot_file) or die "Cannot open \"$annot_file\":$!";
	
	while(<FH>){
		if(/^!platform_table_begin/){
			last;
		}
	}
	
	my $header = <FH>;
	chomp($header);
	my @header = split(/\t/, $header);
	my %hlabel_to_colnum;
	foreach my $i (0..$#header){
		$hlabel_to_colnum{ $header[ $i ]} = $i;
	}
	
	my $col_probe_ID = $hlabel_to_colnum{ "ID" };
	my $col_Gene_ID  = $hlabel_to_colnum{ "Gene ID" };
	
	while(<FH>){
		chomp;
		if(/^!platform_table_end/){
			last;
		}
		my @r = split(/\t/);

		if(!defined($r[$col_Gene_ID])){
			next;
		}

		my $probeid = $r[$col_probe_ID];
		my $geneid  = $r[$col_Gene_ID];

		$obj->{ probeid2geneid }->{ $probeid } = $geneid;
		push(@{$obj->{ geneid2probeids }->{ $geneid }}, $probeid);
	}

	close FH;
	
}

sub get_geneid_from_probeid($$){
	my $obj     = shift;
	my $probeid = shift;
	return $obj->{ probeid2geneid }->{ $probeid };
}

sub get_probeids_from_geneid($$){
	my $obj     = shift;
	my $geneid = shift;	
	if(defined($obj->{ geneid2probeids }->{ $geneid })){
	    return @{$obj->{ geneid2probeids }->{ $geneid }};
	}
	else {
	    return ();
	}
}

unless (caller) {
 	my $gpl570_annot = new GPL570_annot;
 	print $gpl570_annot->get_geneid_from_probeid("1552272_a_at"), "\n";
 	print join(",", $gpl570_annot->get_probeids_from_geneid("113277")), "\n";	
}

1;
