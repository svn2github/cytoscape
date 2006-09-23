package PerlFile;

use File::Spec;
use Object;

my $DEBUG = 0;

@ISA = qw(Object);

PerlFile->_generateAccessors(qw(packageName methods scalars arrays hashes file));

sub new
{
    my ($caller, $file) = @_;
    my $self = $caller->SUPER::new();

    $self->file($file);
    $self->packageName("");
    $self->methods({});
    $self->scalars([]);
    $self->arrays({});
    $self->hashes([]);

    die "Can't read $file\n" if(! -r $file);

    # Read a file and save its contents;
    my @contents;

    open(IN, $file) || die "Can't open $file\n";
    while(<IN>)
    {
	if(/^package\s+(\w+)/)
	{
	    $self->packageName($1);

	}
	push @contents, $_;
    }
    close IN;

    if($self->packageName() ne "")
    {
	$self->introspectPackage($self->packageName(), \@contents);
    }

    return $self;
}

sub introspectPackage
{
    my ($self, $packageName, $fileContents) = @_;

    eval("use $packageName");
    local (*alias);
    *stash = *{"${packageName}::"};
    while (my ($varName, $globValue) = each %stash)
    {
	#print "introspect: $varName\n";
	*alias = $globValue;
	if(defined(&alias))
	{
	    my @params = parseParams($varName, $fileContents);
	    $self->methods()->{$varName} = \@params;
	    printf "  method $varName: %s\n", join(",", @params) if $DEBUG; 
	}
	if(defined($alias))
	{
	    print "  scalar $varName\n" if $DEBUG;
	    push @{$self->scalars()}, $varName;
	}
	if(defined(@alias))
	{
	    print "  array $varName\n" if $DEBUG;
	    my @value;
	    if($varName eq "ISA") {@value = @alias};
	    $self->arrays()->{$varName} = \@value;
	}
	if(defined(%alias))
	{
	    print "  hash $varName\n" if $DEBUG;
	    push @{$self->hashes()}, $varName;
	}
    }
}

sub parseParams
{
    my ($methodName, $fileContents) = @_;

    my $inMethod = 0;
    foreach (@{$fileContents})
    {
	if($inMethod)
	{
	    # match: my ($x, $y, $z) = @_;
	    if(/^\s* my \s+ \( (.+) \) \s* = \s* @\_;/x)
	    {
		return split(/,\s*/, $1);;
	    }
	}
	else
	{
	    if(/^sub\s+$methodName/) {$inMethod = 1};
	}
    }
    return();
}

sub writeHTML
{
    my ($self, $outDir) = @_;

    my $file = $self->file();
    my $pkg = $self->packageName();
    my $text = qq(<html>
		  <head><title>Documentation for $file</title></head>
		  <body>
		  <h1>$pkg</h1>
		  );

    $text .= generateTable("Methods", $self->methods());
    $text .= generateTable("Scalars", $self->scalars());
    $text .= generateTable("Arrays", $self->arrays());
    $text .= generateTable("Hashes", $self->hashes());

    $text .= qq(</body>
		</html>
		);

    my $outName = basename($self->file) . ".html";
    my $outfile = File::Spec->catfile($outDir, $outName); 
    open(OUT, ">$outfile") || die "Can't open $outfile\n";
    print OUT $text;
    close OUT;

    return $outName;
}

sub generateTable
{
    my ($title, $data) = @_;

    my $text = "";
    if((ref($data) eq "ARRAY") && scalar(@{$data}) > 0 ||
       (ref($data) eq "HASH") && scalar(keys %{$data}) > 0)
    {
	$text .= "<i><b>$title</b></i>\n";
	$text .= "<table><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>\n";
	$text .= "<table cellpadding=3 cellspacing=5>\n";
	
	if(ref($data) eq "ARRAY")
	{
	    for my $m (sort @{$data})
	    {
		$text .= "<tr><td>$m</td></tr>\n";
	    }
	}
	if(ref($data) eq "HASH")
	{
	    #$text .= "<tr><th>Name</th><th>Params</th></tr>\n";
	    for my $m (keys %{$data})
	    {
		my $params = join(", ", @{$data->{$m}});
		$text .= "<tr><td><b>$m</b></td><td>$params</td></tr>\n";
	    }
	}
	
	$text .= "</table>\n";
	$text .= "</td></tr></table>\n";
    }
    return $text;
}

sub basename
{
    my ($absoluteFileName) = @_;
    
    my ($volume,$directories,$file) = File::Spec->splitpath( $absoluteFileName );
    my @name = split(/\./, $file);
    pop @name;
    my $basename = join(".", @name);
    return $basename;
}



1;
