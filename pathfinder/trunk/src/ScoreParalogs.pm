package ScoreParalogs;

#
# Fuctions to score subclassifications for gene families
#
use POSIX;

require Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(scoreComponent
		 printComponentScore
		 disorder
		 ); #symbols to export by default
our @EXPORT_OK = qw(); #symbols to export on request
our $VERSION = 1.00;

my $C_THRESH = 0.5;

sub scoreComponent
{
    my ($genes, $classTable, $graph) = @_;

    my $G = classifyGenes($genes, $classTable);
    
    printf "### G = %s\n", classificationToString($G);
    if(scalar(keys %{$G}) < 2)
    {
	printf "### Fewer than 2 groups.  Skipping component\n";
	return { 'tfs' => [], 'scores' => [], 'classMatrix' => []};
    }

    my $T = getIncidentGenes($genes, $graph);
    printf "### T = %s\n", join(",", @{$T}) if $DEBUG;

    my @scores = map { score($G, $_, $graph) } @{$T};

    my @classMatrix;
    my ($scoreI, $scoreJ);
    my $n = scalar(@{$T}) - 1;
    foreach my $i (0..$n)
    {
	$scoreI = $scores[$i];
	
	foreach my $j (($i+1)..$n)
	{
	    $scoreJ = $scores[$j];
	    $classMatrix[$i][$j] = classify($scoreI, $scoreJ);
	    # later break into type + entropy value
	}
    }

    return { 'tfs' => $T, 'scores' => \@scores, 'classMatrix' => \@classMatrix};
}

sub classify
{
    my ($scoreI, $scoreJ) = @_;

    my $total = $scoreI->[1] + $scoreJ->[1];
    return ["Pair", $total] if(isS($scoreI) && isS($scoreJ));
    return ["Comb", $total] if(isC($scoreI) && isS($scoreJ));
    return ["Comb", $total] if(isS($scoreI) && isC($scoreJ));
    return ["Para", $total] if(isC($scoreI) && isC($scoreJ) && 
			       ($scoreI->[0] ne $scoreJ->[0]));
    return ["Pair", $total] if(isC($scoreI) && isC($scoreJ));
    return ["Conf", $total] if(isC($scoreI) || isC($scoreJ));
    return ["Span", $total] if(isS($scoreI) || isS($scoreJ));
    
    # Stub
    return ["None", $total];
}

sub isC
{
    my ($score) = @_;
    return $score->[0] =~ /^C/;
}

sub isS
{
    my ($score) = @_;
    return $score->[0] =~ /^S/;
}

# disorder
sub disorder
{
    my ($counts) = @_;

    my $B = scalar(@{$counts});

    my @N; # marginal sums; N[b] = total number of items in branch b
    my $T = 0; # Total number of items
    foreach my $b (0..($B - 1))
    {
	$N[$b] = sum(@{$counts->[$b]});
	$T += $N[$b];

	#printf "%s\n", join(",", @{$counts->[$b]});
    }

    my $D = 0; # Average disorder
    my ($k, $p); #intermediate tmp values
    foreach my $b (0..($B-1))
    {
	$p = 0;
	next if $N[$b] == 0;
	foreach my $c (0..(scalar(@{$counts->[$b]}) - 1))
	{
	    $k = $counts->[$b][$c] / $N[$b];
	    next if $k==0;
	    $p += ($k * log2($k));
	}
	$D += ($N[$b]/$T) * $p;
    }
 
    return -1*$D;

    #Simple formula for 2 classes
    #return 0 if($Nbc == 0 || $Nbc == $Nb);
    #return(-($Nbc/t)*log2(x/t) - ((t-x)/t)*log2((t-x)/t))
}

sub log2
{
    my $n = shift;
    return log($n)/log(2);
}

sub sum
{
    my $sum = 0;
    map { $sum += $_ } @_;
    return $sum;
}

#
# Calculate the average disorder of the grouping
sub score
{
    my ($G, $source, $graph) = @_;

    my @groups = sort keys %{$G};
    my @groupScores;
    my @counts;
    foreach my $i (0..(scalar(@groups)-1))
    {
	my $edge = 0;
	my $noEdge = 0;
	my @group = @{$G->{$groups[$i]}}; 
	foreach my $gene (@group)
	{
	    if($graph->edgeExists($source, $gene))
	    {
		$edge += 1;
	    }
	    else { $noEdge += 1;}
	}
	$counts[0][$i] = $edge;
	$counts[1][$i] = $noEdge;
	if(scalar(@group) > 0)
	{
	    $groupScores{$groups[$i]} = $edge/scalar(@group);
	}
	else
	{
	    $groupScores{$groups[$i]} = 0;
	}
    }
    my $D = (1-$groupScores{'subtelomeric'}) + disorder(\@counts);
    printf("### $source [D=%.3f] groupScores = (%s)\n", 
	   $D, 
	   join("\t", map { sprintf "%s=%.2f", $_, $groupScores{$_}} keys %groupScores));


    my $S = 0;
    foreach my $key (keys %groupScores)
    {
	$S += 1 if($groupScores{$key} > $C_THRESH);
    }
    
    if($S == scalar(@groups))
    {
	return ["S", $D, $source];
    }
    elsif($S > 0)
    {
	my $val = "";
	foreach my $key (keys %groupScores)
	{
	    if($groupScores{$key} > $C_THRESH)
	    {
		$val .= "C." . $key;
	    }
	}
	return [$val, $D, $source];
    }
    return ["N", $D, $source];
}

# Old method... not used anymore
# NOT VALID for hash $groupsv
# This method expects $groups to be an array ref
sub scoreOld
{
    my ($groups, $source, $graph) = @_;

    my @groupScores;
 
    foreach my $group (@{$groups})
    {
	my $C = 0;
	foreach my $gene (@{$group})
	{
	    if($graph->edgeExists($source, $gene))
	    {
		$C += 1;
	    }
	}
	push @groupScores, $C/scalar(@{$group});
    }
    printf "### $source groupScores = (%s)\n", join(",", @groupScores) if $DEBUG;

    my $S = 0;
    map { $S += 1 if($_ > $C_THRESH) } @groupScores;
    
    my $sum = 0;
    map { $sum += $_ } @groupScores;
    
    my $score = $sum + 10*$S;

    if($S == scalar(@{$groups}))
    {
	return ["S", $score];
    }
    elsif($S > 0)
    {
	my $val = "";
	foreach my $i (0..$#groupScores)
	{
	    if($groupScores[$i] > $C_THRESH)
	    {
		$val .= "C$i";
	    }
	}
	return [$val, $score];
    }
    return ["N", $score];
}

sub getIncidentGenes
{
    my ($genes, $graph) = @_;
    
    my %nodes;

    foreach my $g (@{$genes})
    {
	next if(! $graph->containsNode($g));
	my @I = $graph->getIncidentNeighbors($g);

	printf "### I[$g] = %s\n", join(",", @I) if $DEBUG;
	map {$nodes{$_}++} @I;
    }
    return [keys %nodes];
}

#
# Classify genes based in their values 
# in the classTable hash.
#
# Do this by inverting the hash.
# Genes that are not keys in the table are placed in a group
# called "__NONE__"
# 
# i.e. classTable = { Gx => ClassA, Gy => ClassB, Gz=>ClassA }
# return [[Gx, Gz], [Gy]]
#
# TODO: return a hash instead of an array of arrays
sub classifyGenes
{
    my ($genes, $classTable) = @_;

    my %c;
    foreach my $v (values(%{$classTable}))
    {
	$c{$v} = [];
    }
    
    foreach my $g (@{$genes})
    {
	if(exists($classTable->{$g}))
	{
	    push @{$c{$classTable->{$g}}}, $g;
	}
	else
	{
	    push @{$c{"__NONE__"}}, $g;
	}
    }
    return \%c;
    #return [values(%c)];
    # stub code: divide $genes in half.  IMPLEMENT LATER.
    #my $N = scalar(@{$genes});
    #my $x = POSIX::floor($N/2);
    #my @g1 = @{$genes}[0..$x];
    #my @g2 = @{$genes}[($x+1)..($N-1)];
    #return [\@g1, \@g2];
}

sub score2string
{
    my ($score) = @_;
    return sprintf("%s, [%s,%.3f]", $score->[2], $score->[0], $score->[1]);
}

sub printComponentScore
{
    my ($score, $componentId) = @_;

    my @tfs = @{$score->{'tfs'}};
    my $Nt = scalar(@tfs) - 1;
    my $Ns = scalar(@{$score->{'scores'}}) - 1;

    if($Nt != $Ns) { die "ERROR: Ntfs [$Nt] != Nscores [$Ns]\n"};

    print "### Single scores\n" ;
    map {  
	printf("    (%s)\n", 
	       score2string($_))
    } sort { $a->[1] <=> $b->[1]} @{$score->{'scores'}};
    
    print "### Pairwise classification\n";
    my @flattened = flattenScoreMatrix($score);

    if(scalar(@flattened) > 0)
    {
	my @flatSorted = sort {$a->[1] <=> $b->[1]} @flattened;
	#foreach my $s ($flatSorted[0])
	#{
	my $s = $flatSorted[0];
	#next if($s->[1] < 10);
	printf "*** %s %.2f [%s]\n", $componentId, $s->[1], join(",", @{$s}[0,2,3]);
	#}
    }
}

#    print join("\t", "", @tfs) . "\n" if (scalar(@tfs) > 1);
#   foreach my $i (0..($Nt-1))
#    {
#	print(join("\t", 
#		   $tfs[$i], 
#		   map { defined($_) ? score2string($_) : "" } @{$score->{'classMatrix'}->[$i]}),
#	      "\n");
#    }


sub flattenScoreMatrix
{
    my ($score) = @_;
    my @tfs = @{$score->{'tfs'}};
    my $Nt = scalar(@tfs) - 1;
    my (@flattened, $s);
    foreach my $i (0..($Nt-1))
    {
	foreach my $j (($i+1)..$Nt)
	{
	    $s = $score->{'classMatrix'}->[$i][$j];
	    next if ! defined($s);
	    push(@{$s}, $tfs[$i], $tfs[$j]);
	    push @flattened, $s;
	}
    }
    return @flattened;
}


sub classificationToString
{
    my ($c) = @_;
    
    my @s;
    foreach my $group (sort keys %{$c})
    {
	push @s, sprintf("%s=(%s)", $group, join(",", @{$c->{$group}}));
    }
    return "[" . join(", ", @s) . "]";
}

1;
