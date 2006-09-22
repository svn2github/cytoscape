package PerlFile;

use File::Spec;
use Object;

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
    
    open(IN, $file) || die "Can't open $file\n";

    my @contents;

    while(<IN>)
    {
	if(/^package\s+(\w+)/)
	{
	    $self->packageName($1);

	}
	push @contents, $_;

	#if(/^sub\s+(\w+)/)
	#{
	#    push @{$self->methods()}, $1;
	#}
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
	    #printf "  method: %s\n", join(",", @params); 
	}
	if(defined($alias))
	{
	    #print "  found scalar\n";
	    push @{$self->scalars()}, $varName;
	}
	if(defined(@alias))
	{
	    #print "  found array\n";
	    my @value;
	    if($varName eq "ISA") {@value = @alias};
	    $self->arrays()->{$varName} = \@value;
	}
	if(defined(%alias))
	{
	    #print "  found hash\n";
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

    $text .= generateTable("Methods", $self->methods()) . "<br>";
    $text .= generateTable("Scalars", $self->scalars()) . "<br>";
    $text .= generateTable("Arrays", $self->arrays()) . "<br>";
    $text .= generateTable("Hashes", $self->hashes()) . "<br>";

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

    my $text .= "<b>$title</b>\n";
    $text .= "<table><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>\n";
    $text .= "<table cellpadding=5 cellspacing=5>\n";
    
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

    return $text;
}

sub basename
{
    my ($s) = @_;
    
    my ($volume,$directories,$file) = File::Spec->splitpath( $s );
    my @name = split(/\./, $file);
    pop @name;
    my $basename = join(".", @name);
    return $basename;
}



1;
