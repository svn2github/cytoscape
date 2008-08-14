package ScoreModel::MultiOrganismSIF;

use ScoreModel::Object;

@ISA = qw(ScoreModel::Object);
                  
ScoreModel::MultiOrganismSIF->_generateAccessors(qw(org2genes org2interactions 
				    organisms name pub genes));
#					file organisms name pub genes));
sub new
{
    my ($caller, $dbh, $network_file_id, $organisms, $geneNameMapper, $edgeMapper) = @_;
    my $self = $caller->SUPER::new();

    $self->org2genes({});
    $self->genes({});
    #$self->file_id($network_file_id);
    $self->organisms($organisms);
    $self->org2interactions({});
    $self->name("");
    $self->pub("");
    
    $self->parse($dbh, $network_file_id, $geneNameMapper, $edgeMapper);
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
    my ($self, $dbh, $network_file_id, $geneNameMapper, $edgeMapper) = @_;
    
    my @orgs = @{ $self->organisms() }; 
    my $genes = $self->genes();
    my $org2genes = $self->org2genes();
    my $org2interactions = $self->org2interactions();

	# get the model name for this sif file
	my $get_model_sth = $dbh->prepare("select file_name from network_files where id = $network_file_id");
	$get_model_sth->execute();
	# Actually there is only one record
	while (my $ref = $get_model_sth->fetchrow_hashref()) {	
		$model_name = $ref->{'file_name'};
		my @tmpArray = split(".sif", $model_name); # remove .sif extension
		$model_name = $tmpArray[0];
		$self->name($model_name);		
	}

	#
	$get_model_sth = $dbh->prepare("select file_name, data from network_files where id = ?");
	$get_model_sth->bind_param(1, $network_file_id);
	$get_model_sth->execute();

	my $file_name;
	my $data;
	
	# actually there is only one record
	while (my $ref = $get_model_sth->fetchrow_hashref()) {	
		$file_name = $ref->{'file_name'};
		$data = $ref->{'data'};
	}
	
    my $tmp = "";

	@lines = split "\n", $data;
	
	foreach $_ (@lines) 
    {
		chomp; s/^\s+//g; s/\s+$//g;
		my (@line) = split(/\t+/); 
	
		if(scalar(@line) == 3) {
		   
			my $gene1 = uc($line[0]);
			my $type = lc($line[1]);
			my $gene2 = uc($line[2]);
			
			$genes->{$gene1}++;
			$genes->{$gene2}++;
			my @g1 = split(/\|/,$gene1);
			my @g2 = split(/\|/,$gene2);
			
			for my $i (0..$#orgs) 
			{
				if ($orgs[$i] eq "saccharomyces cerevisiae") {
					
					my @genes = ($geneNameMapper->mapName($g1[$i], $orgs[$i]),
							 $geneNameMapper->mapName($g2[$i], $orgs[$i]));
		
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
    }
}

1;
