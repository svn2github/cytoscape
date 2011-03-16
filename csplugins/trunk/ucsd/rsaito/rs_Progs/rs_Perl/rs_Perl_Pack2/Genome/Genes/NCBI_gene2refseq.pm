#!/usr/bin/env perl

use strict;
use warnings;

use General::Data_Struct::Hash2;
use General::Usefuls::rsConfig;

package NCBI_gene2refseq;

sub new {

    my $class = shift;
    my $obj = {};
  
    my %h = rsConfig::read_config("NCBI.cnf");
	local *FH;
	my $gene2refseq_file = $h{ gene2refseq };

	open(FH, $gene2refseq_file) || die "Cannot open \"$gene2refseq_file\": $!";
	while(<FH>){
		chomp;
		if(/^#Format/){ next; }
		my @r = split(/\t/);
		my $geneid = $r[1];
		my $refseq_nuc_ver = $r[3];
		my $refseq_nuc = $r[3];
		$refseq_nuc =~ s/\.[^.]*$//;
		# print "$geneid $refseq_nuc\n";
		$obj->{ refseq2gene }->{ $refseq_nuc } = $geneid;
	}
	close FH;
    
    return bless $obj;

}

sub get_geneid_from_refseq_nuc($$){
	
	my $obj        = shift;
	my $refseq_nuc = shift;
	
	$refseq_nuc =~ s/\.[^.]*$//;
	
	if(defined($obj->{ refseq2gene }->{ $refseq_nuc })){
		return $obj->{ refseq2gene }->{ $refseq_nuc };
	}
	else {
		return undef;
	}
}

unless(caller){
	
	my $gene2refseq = new NCBI_gene2refseq;
	print $gene2refseq->get_geneid_from_refseq_nuc("NM_001099.3"), "\n";
}


1;
