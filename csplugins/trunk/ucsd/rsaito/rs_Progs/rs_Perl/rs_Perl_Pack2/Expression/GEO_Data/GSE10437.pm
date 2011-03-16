#!/usr/bin/env perl

use strict;
use warnings;

use List::Util; # Util is not a package? but List is?
use General::Usefuls::rsConfig; # rsConfig is a package.

package GSE10437;
use Expression::GEO_Data::GPL570_annot;

sub new {

    my $class = shift;
    my $obj = {};
    bless $obj;
    
	$obj->read_exp_data;
    $obj->{ annot } = new GPL570_annot;
    
    return $obj;

}

sub read_exp_data($){

	my $obj = shift;
    my %h = rsConfig::read_config("ExpData.cnf");
	my $exp_file   = $h{"GSE10437"};

	local *FH;
	open(FH, $exp_file) or die "Cannot open \"$exp_file\":$!";
	
	while(<FH>){
		if(/^!series_matrix_table_begin/){
			last;
		}
	}
	
	my $header = <FH>;
	chomp($header);
	$header =~ s/"//g;
	my @header = split(/\t/, $header);
	if($header[0] ne "ID_REF"){
		die "Unexpected format of expression file: $header[0]\n";
	}
	shift @header;
	
	while(<FH>){
		chomp;
		if(/^!series_matrix_table_end/){
			last;
		}
		my @r = split(/\t/);
		my $id = shift(@r);
		$id =~ s/^"//g;
		$id =~ s/"$//g;
		foreach my $exp_label (@header){
			my $exp = shift @r;
			push(@{$obj->{ exp }->{ $exp_label }->{ $id }},
				 $exp);
			# print "$id\t$exp_label\t$exp\n";	 
		}
	}

	close FH;
	
}

sub read_annot($){
	my $obj = shift;
	
	my %h = read_config("ExpData.cnf");
	my $annot_file = $h{"GPL570.annot"};
	
	local *FH;
	open(FH, $annot_file) or die "Cannot open \"$annot_file\":$!";
	
	while(<FH>){
		if(/^!platform_table_begin/){
			last;
		}
	}
	
}

sub get_exp($$$){

    my $obj   = shift;
    my $label = shift;
    my $id    = shift;

    
    if(defined($obj->{ exp }->{ $label }->{ $id })){
	    return @{$obj->{ exp }->{ $label }->{ $id }}; 
    }  
	else {
		return ();
	}
}  

sub get_av_exp($$$){
	
	my $obj   = shift;
    my $label = shift;
    my $id    = shift;
	
	my @exp = $obj->get_exp($label, $id);
	
	if($#exp == -1){ return 0.0; }
	else {
		return List::Util::sum(@exp)/($#exp+1);		
	}
	
}

sub get_av_exp_from_geneid($$$){
	
    my $obj    = shift;
    my $label  = shift;
    my $geneid = shift;

	my $sum = 0;
	my $num = 0;
	
	foreach my $probeid ($obj->{ annot }->get_probeids_from_geneid($geneid)){
		# print "$probeid ", $obj->get_av_exp($label, $probeid), "\n";
		$sum += $obj->get_av_exp($label, $probeid);
		$num += 1;
	}

	return $sum / $num;

}

unless (caller) {
 	my $gse10437 = new GSE10437;
 	print "Let's go!\n";
	my @exp = $gse10437->get_exp("GSM263934", "121_at");
	print join(",", @exp), "\n";
	my @exp2 = $gse10437->get_exp("GSM263934", "121_atXX", );
	print join(",", @exp2), "\n";	
	
	print $gse10437->get_av_exp_from_geneid("GSM263934", "113277"), "\n";
	print "Hello---\n";
}


1;
