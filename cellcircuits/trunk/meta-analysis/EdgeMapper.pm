package EdgeMapper;

@ISA = qw(Object);

sub new
{
    my ($caller) = @_;
    my $self = $caller->SUPER::new();

    return $self;
}

sub isDirected
{
    my ($self, $type, $organism) = @_;

    return ($type eq "pd");
}

sub mapType
{
    my ($self, $type, $organism, $organismIndex) = @_;

    if($type eq "mips"){ return 'sl'; }
    if($type eq "sga"){ return 'sl'; }
    if($type eq "ss"){ return 'sl'; }
    
    ## if type is cross species, i.e. conserved pp type ...
    ## 0 ignore
    ## 1 is direct pp edge
    ## 2 is indirect pp edge (inferred b/c the homologs interact)
    ## 3 ignore
    if($type =~ /^[0123]{2,}$/){
	my $edge_dist = substr($type,$organismIndex,1);
	
	if($edge_dist == 1) {return "pp";}
	if($edge_dist == 2) {return "ppPredicted";}

	return undef;
    }
    
    return $type;
}

1;
