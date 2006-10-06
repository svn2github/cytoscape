package MultiOrganismSIF;

use Object;

@ISA = qw(Object);

MultiOrganismSIF->_generateAccessors(qw(org2genes org2interactions 
					file organisms));

sub new
{
    my ($caller, $file, $organisms, $geneNameFunction, $edgeMapper) = @_;
    my $self = $caller->SUPER::new();

    $self->org2genes({});
    $self->file($file);
    $self->organisms($organisms);
    $self->org2interactions({});
    
    $self->parse($geneNameFunction, $edgeMapper);
    return $self;
}

## Print a summary string about this SIF file
sub printSummary
{
    my ($self) = @_;
    
    my $geneSummary .= join(",", 
			    map {
				sprintf("%s:%d", 
					substr($_, 0, 4),
					scalar(keys(%{$self->org2genes()->{$_}})))
				} 
			    @{$self->organisms()}
			    );
    
    my $intSummary .= join(",", 
			   map {
			       sprintf("%s:%d", 
				       substr($_, 0, 4),
				       scalar(keys(%{$self->org2interactions()->{$_}})))
			       } 
			   @{$self->organisms()}
			   );

    return(sprintf("orgs=[%s] genes=[%s] intx=[%s]", 
		   join(", ", map {substr($_, 0, 4)} @{$self->organisms()}),
		   $geneSummary, $intSummary));
}

## Print a more verbose description of this sif file
## Use $verbose = 1 to list all genes and interactions
sub print
{
    my ($self, $verbose) = @_;

    my $str = "";
    $str .= "FILE: " . $self->file() . "\n";
    $str .= "ORGS: " . join(", ", sort @{$self->organisms()}) . "\n";

    ## Print organism - genes
    $str .= "GENES:\n";
    my $extra = "";
    foreach my $o (keys %{$self->org2genes()})
    {
	if($verbose)
	{
	    $extra = join(", ", sort keys %{$self->org2genes()->{$o}});
	}
	$str .= sprintf("  %s: [%d] %s\n", 
			$o, 
			scalar(keys %{$self->org2genes()->{$o}}),
			$extra);
    }

    ## Print organism - interaction mapping
    $str .= "ORG_2_INT:\n";
    foreach $o (keys %{$self->org2interactions()})
    {
	if($verbose)
	{
	    $extra = sprintf("    %s\n" , join("\n    ", 
			  sort keys %{$self->org2interactions()->{$o}}));
	}
	$str .= sprintf("  %s: [%d]\n%s", 
			$o, 
			scalar(keys %{$self->org2interactions()->{$o}}),
			$extra);
    }
    return $str;
}

sub parse
{
    my ($self, $geneNameFunction, $edgeMapper) = @_;
    
    my @orgs = @{ $self->organisms() }; 
    my $org2genes = $self->org2genes();
    my $org2interactions = $self->org2interactions();

    open(SIF, $self->file()) or die "Cannot open " . $self->file() . ": $!\n";
    my $tmp = "";
    while(<SIF>)
    {
	chomp; s/^\s+//g; s/\s+$//g;
	my (@line) = split(/\s+/);

	if(scalar(@line) == 3) {
	   
	    my $gene1 = uc($line[0]);
	    my $type = lc($line[1]);
	    my $gene2 = uc($line[2]);
	    
	    my @g1 = split(/\|/,$gene1);
	    my @g2 = split(/\|/,$gene2);
	    
	    for my $i (0..$#orgs) 
	    {
		my @genes = ($geneNameFunction->($g1[$i], $orgs[$i]),
			     $geneNameFunction->( $g2[$i], $orgs[$i]));

		map {$org2genes->{$orgs[$i]}{$_}++} @genes;

		my $mappedType = $edgeMapper->mapType($type, $orgs[$i], $i);

		if(defined($mappedType))
		{
		    if(!$edgeMapper->isDirected($type)){
			@genes = sort @genes;
		    }
		    my $intxn = join("::", $mappedType, @genes);
		    $org2interactions->{$orgs[$i]}{$intxn}++;
		}
	    }
	}
    }
    close(SIF);
    
}

1;
