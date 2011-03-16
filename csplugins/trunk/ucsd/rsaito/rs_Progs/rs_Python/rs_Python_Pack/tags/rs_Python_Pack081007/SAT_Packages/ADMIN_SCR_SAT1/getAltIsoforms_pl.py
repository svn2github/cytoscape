#!/usr/bin/env python

print r"""#!/usr/bin/perl

use strict;
use Storable;
use Data::Dumper;

my $TEST = 0;
$TEST = 1 if( $ARGV[$#ARGV -1] =~ /test/);
#print STDERR "Test status: $TEST\n";

my $ap = retrieve($ARGV[$#ARGV]);


#main
my $total=0;
my $test1 = 0;
my $test2 = 0;
foreach my $chr ( sort keys %{$ap}){
    #print "chromosome[$chr], ",$ap->{$chr},"\n";
#    $test2++;
    my $n=0;
    foreach my $sap (@{$ap->{$chr}}){ ############# array
	$n++;
#	$test1++;
	foreach my $which ( 'a', 'b' ){

	    my $cluster = 'cluster_' . $which;
	    #my $id = 'id' . $which;
	    my $id = 'clusterID' . $which;
	    foreach my $est ( @{$sap->{$cluster}->{est_pointers}} ){
		print 
		    $chr, "\t", $sap->{$id}, "\t", $est->{id}, "\t", 
		    $est->{strand}, "\t",
		    join(',', @{$est->{start}} ), "\t",
		    join(',', @{$est->{end}} ), "\t",
		    $est->{file}, "\t", 
		    join(',', @{$est->{aln}} ), "\n";
	    }
	}
	
#	last if( $TEST == 1 ) ;
    }
#    print STDERR "chromosome $chr: $n SATs\n";
    $total += $n;
#    last if( $TEST == 1 && $test2 == 2 ) ;
}
#print STDERR "Total: $total SATs\n";
# ------------------------------------------ do not use following part



# obtain overlapping exons
# no diversity among isoforms is allowed within overlapping region 
# overlappin region is 3'- $ovst - $oved -5' (sense strand)
sub identicalExonEdges
{
    my $isoforms = shift @_;
    my $ovst = shift @_;
    my $oved = shift @_;
    my $minlen = shift @_;

    # check the identity among isoforms within the region of overlap
    my @firstEdges=();
    my $entrynum=0;
    foreach my $isoform (@{$isoforms}){
	print "an isoform in identicalExonEdges check---\n";
	$entrynum++;
	
	my @ends = @{$isoform->{end}};	
	my @starts = @{$isoform->{start}};
	print join(",",@starts)," : starts\n", join(",",@ends)," : ends\n"; 
	
	# store the internal edge points of exons to @edges
	my $ovexonnum = 0;
	my $n = 0;
	my @edges=(); # edge points of exons of current entry
	while(@ends){
	    my $end   = shift(@ends);
	    my $start = shift(@starts);
	    print "while array of exons\n"; print "start: $start\n"; print "end: $end\n";
	    # check the end point. if smaller, check next
	    next if ( $end <= $ovst );
	    # check the start point. if bigger, no more exon needs check
	    last if ( $oved <= $start );
	    # if both is OK, push current exon's st&end to @edges
	    ($start < $ovst) ? push(@edges, $ovst) : push(@edges, $start);
	    ($end > $oved) ? push(@edges, $oved) : push(@edges, $end);
	    $n++;
	}
	# check the identity between current isoform and the firs isoform

	return 0 if($n<1); # is this the first one suit for the criteria?
	if( $entrynum == 1 ){
	    @firstEdges = @edges;
	    print "firstedge: ",join(",",@firstEdges),"\n";
	}else{
	    print "first:    ", join(",",@firstEdges),"\n","current[$entrynum]: ",join(",",@edges),"\n";
	    #compare
	    return 0 if( join(",",@firstEdges) ne join(",",@edges) );
	}
    }
#    return @firstEdges;
    return join(",",@firstEdges);
}
sub overlapType02
{
    my $sap = shift @_;
    my $ovst = shift @_;
    my $oved = shift @_;
    my $minlen = shift @_;
    $minlen = ($minlen < 1) ? 1 : $minlen;

    print "type02: $ovst(ovst)--$oved(oved)\n";
=comment
    my @identicalEE_a = &identicalExonEdges($sap->{cluster_a}->{est_pointers}, $ovst, $oved, $minlen);
    my @identicalEE_b = &identicalExonEdges($sap->{cluster_b}->{est_pointers}, $ovst, $oved, $minlen);
    print "identical A: ",join(",",@identicalEE_a),"\n";
    print "identical B: ",join(",",@identicalEE_b),"\n";
=cut
    my $identicalEE_a = &identicalExonEdges($sap->{cluster_a}->{est_pointers}, $ovst, $oved, $minlen);
    my $identicalEE_b = &identicalExonEdges($sap->{cluster_b}->{est_pointers}, $ovst, $oved, $minlen);
    print "identical A: $identicalEE_a\n";
    print "identical B: $identicalEE_b\n";

    # check: longer than minimum length?
    if($identicalEE_a && $identicalEE_b){
	print "check: longer than minimum length?\n";

	my @oer=();

	my @exonA = split(",",$identicalEE_a);
	my @exonB = split(",",$identicalEE_b);

	my $sa = shift @exonA;
	my $ea = shift @exonA;
	my $sb = shift @exonB;
	my $eb = shift @exonB;
	my $i=0;
	while($sa && $sb && $ea && $eb){

	    ($sa < $sb) ? push(@oer, $sb) : push(@oer, $sa);
	    my $tmpe = ($ea < $eb) ? $ea : $eb;
	    push(@oer, $tmpe);
	    if($tmpe == $ea){
		$sa = shift @exonA || last;
		$ea = shift @exonA;
		print "new entry A: $sa - $ea\n";
#		last unless defined($sa) && defined($ea);
	    }
	    if($tmpe == $eb){	    
		$sb = shift @exonB || last;
		$eb = shift @exonB;
		print "new entry B: $sb and $eb\n";
#		last unless defined($sb) && defined($eb);
	    }
	}
	return join(",",@oer);
    }else{
	return 0;
    }

}


# Returns the start & end of overlapped region
sub overlapType01
{
    my $sap = shift @_;
    my $ovst = shift @_;
    my $oved = shift @_;
    my $minlen = shift @_;
    $minlen = ($minlen < 1) ? 1 : $minlen;

    # check the last exon's start and end of upstream cluster
    foreach my $isoform (@{$sap->{cluster_a}->{est_pointers}}){
	print "a:\n";
	my $last = $#{$isoform->{start}};
	my $start =  $isoform->{start}->[$last];
	my $end =  $isoform->{end}->[$last];
	
	if( $start <= $ovst &&  $end == $oved ){
	    print "$ovst -*- $oved OK\n";
	}else{
	    print "$start and $end NG----\n";
	    return 0;
	}
    }
    # check the first exon's start and end
    foreach my $isoform (@{$sap->{cluster_b}->{est_pointers}}){
	print "b:\n";
	my $start = $isoform->{start}->[0];
	my $end   = $isoform->{end}->[0];
	
	if( $start == $ovst &&  $oved <= $end ){
	    print "$ovst -*- $oved OK\n";
	}else{
	    print "$start and $end NG----\n";
	    return 0;
	}
    }

    # check: longer than minimum length?
    ($minlen <= ($oved - $ovst)) ?  return join(",", ($oved, $ovst)) : return 0;
    
}


# Returns the number of deleted hash entriy[s] 
#   from the array referred by 
sub deleteRedundantEntry
{
    my $isoform = shift @_;
    my @identical = ();
    my $n=0;

    print $#{$isoform},"\n";
    print join(",",@{$isoform}),"\n";
    for( my $i = 0; $i < $#{$isoform}; $i++ ){
	print "[$i]\n";
	for( my $j = $i+1; $j <= $#{$isoform}; $j++ ){
	    if( (join(', ', @{$isoform->[$i]->{start}}) eq
		 join(', ', @{$isoform->[$j]->{start}})    ) &&
		(join(', ', @{$isoform->[$i]->{end}}) eq
		 join(', ', @{$isoform->[$j]->{end}})    )       ){
		
		splice(@{$isoform}, $j, 1);
		$n++;

		print "$i and $j is identical\n";
		print "  ";
		print $#{$isoform},"\n";
		print "  ";
		print join(",",@{$isoform}),"\n";
	    }
	}
    }
    return $n;
}
"""

