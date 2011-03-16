#!/usr/bin/perl -w

use strict;

package PPI_matrix1;

sub vcg_out($){

    my($object) = @_;
    my($matrix_ref, $protein_set_ref)
	= ($object->{'ppi_matrix'}, $object->{'protein_set'});
    my($protein1, $protein2);
    my(%output);
    my($node_color);

    foreach $protein1 (keys(%$matrix_ref)){
	foreach $protein2 (keys(%{$matrix_ref->{ $protein1 }})){
	    if(!defined($output{$protein1}->{$protein2}) &&
	       !defined($output{$protein2}->{$protein1})){
		$output{$protein1}->{$protein2} = "";
	    }
	}
    }

    print "graph: {\n";
    print "title: \"PPI_cluster\"\n";

    foreach(@$protein_set_ref){
	if(defined($output{$_}->{$_})){ $node_color = "red"; }
	else { $node_color = "white"; }
	print "   node: { title: \"$_\" label:\"$_\" color: $node_color }\n";
    }

    foreach $protein1 (keys(%output)){
	foreach $protein2 (keys(%{$output{ $protein1 }})){
	    if($protein1 eq $protein2){ next; }
	    print "edge: { sourcename:\"$protein1\" targetname:\"$protein2\" arrowsize:0 linestyle : continuous color: black }\n";
	}
    }

    print "}\n";
	
	
}

sub java_simple_out($){

    my($object) = @_;
    my($matrix_ref, $protein_set_ref)
	= ($object->{'ppi_matrix'}, $object->{'protein_set'});

    my($protein1, $protein2);
    my(%output);
    my(@protein_func, @interaction);
    my($node_color);

    foreach $protein1 (keys(%$matrix_ref)){
	foreach $protein2 (keys(%{$matrix_ref->{ $protein1 }})){
	    if(!defined($output{$protein1}->{$protein2}) &&
	       !defined($output{$protein2}->{$protein1})){
		$output{$protein1}->{$protein2} = "";
	    }
	}
    }
    
    print <<EOF;
<html>
<head>
<title>Example</title>
</head>
<body>
<h2>Example</h2>
<hr>
<applet code="GraphInteraction.class" width=800 height=600>
EOF
    print "<param name=functionlist value=\"";

    foreach(@$protein_set_ref){
	push(@protein_func, $_ . "#fu1");
    }
    print join(",", @protein_func);
    print "\">\n";
    print "<param name=interaction value=\"";

    foreach $protein1 (keys(%output)){
	foreach $protein2 (keys(%{$output{ $protein1 }})){
            push(@interaction, "$protein1-$protein2");
	}
    }

    print join(",", @interaction);
    print "\">\n";
    print "<param name=functionkey value=\"fu1#Function1\">\n";

    print <<EOF;
alt="Your browser understands the &lt;APPLET&gt; tag but isn't running the applet, for some reason."
Your browser is completely ignoring the &lt;APPLET&gt; tag!

</applet>
<hr>
</body>
</html>
EOF

}

sub java_simple_out2($$){

    my($object, $ypd_class_ref) = @_;
    my($ppi_list_nr, $protein_set_ref)
	= ($object->{'ppi_list_nr'}, $object->{'protein_set'});
    my(%class_list, @class_list);
    my $protein;

    foreach $protein (keys(%$ypd_class_ref)){
	foreach(@{ $ypd_class_ref->{ $protein } }){
	    $class_list{ $_ } = "";
	}
    }
    @class_list = keys(%class_list);

    print <<EOF;
<html>
<head>
<title>Example</title>
</head>
<body>
<h2>Example</h2>
<hr>
<applet code="GraphInteraction.class" width=800 height=600>
EOF
    print "<param name=functionlist value=\"";

    my @protein_func = ();
    foreach $protein (@$protein_set_ref){
	my $function;
	if(defined($ypd_class_ref->{ $protein })){
	    $function = join("/", @{$ypd_class_ref->{ $protein }});
	}
	else { $function = "Unknown"; }
	push(@protein_func, $protein . "#$function");
    }
    print join(",", @protein_func);
    print "\">\n";
    print "<param name=interaction value=\"";

    my $ppi_ref;
    my @interaction;
    foreach $ppi_ref (@$ppi_list_nr){
	push(@interaction, $ppi_ref->[0] . "-" . $ppi_ref->[1]);
    }

    print join(",", @interaction);
    print "\">\n";

    
    print "<param name=functionkey value=\"";
    my @function_key;
    foreach(@class_list){
	push(@function_key, "$_#$_");
    }
    print join(",", @function_key);
    print "\">\n";
	
    print <<EOF;
alt="Your browser understands the &lt;APPLET&gt; tag but isn't running the applet, for some reason."
Your browser is completely ignoring the &lt;APPLET&gt; tag!

</applet>
<hr>
</body>
</html>
EOF

}

# Reduce errors
sub java_simple_out2_ef($$){

    my($object_raw, $ypd_class_raw_ref) = @_;

    my(@ppi_list_nr_raw) = @{ $object_raw->{'ppi_list_nr'} };
    my($ppi_list_nr);
    my $ypd_class_ref;

    foreach my $pair_ref (@ppi_list_nr_raw){
	my($p1, $p2, $val) = @$pair_ref;
	$p1 =~ s/[^a-zA-Z0-9\-]/_/g;
	$p2 =~ s/[^a-zA-Z0-9\-]/_/g;
	push(@$ppi_list_nr, [$p1, $p2, $val]);

	my $f1 = $ypd_class_raw_ref->{$p1};
	my $f2 = $ypd_class_raw_ref->{$p2};
	
	if(defined($f1)){
	    foreach(@$f1){
		if(/[a-zA-Z0-9]/){
		    s/[^a-zA-Z0-9_\-]/_/g;
		    s/^_+//; s/_+$//;
		    push(@{$ypd_class_ref->{$p1}}, $_);
		    del_redu2($ypd_class_ref->{$p1});
		}
	    }
	}
	
	if(defined($f2)){
	    foreach(@$f2){
		if(/[a-zA-Z0-9]/){
		    s/[^a-zA-Z0-9_\-]/_/g;
		    s/^_+//; s/_+$//;
		    push(@{$ypd_class_ref->{$p2}}, $_);
		    del_redu2($ypd_class_ref->{$p2});
		}
	    }
	}
    }

    my $object = new PPI_matrix1 $ppi_list_nr;

    my $protein_set_ref = $object->{'protein_set'};
    my(%class_list, @class_list);
    my $protein;

    foreach $protein (keys(%$ypd_class_ref)){
	foreach(@{ $ypd_class_ref->{ $protein } }){
	    $class_list{ $_ } = "";
	}
    }
    $class_list{ "Unknown" } = "";
    @class_list = keys(%class_list);


#    foreach(@{$object->{'protein_set'}}){
#	print "<$_>\n";
#    }
#    foreach(@class_list){
#	print "<$_>\n";
#    }
#
#    exit;



    print <<EOF;

<applet code="GraphInteraction.class" width=800 height=600>
EOF
    print "<param name=functionlist value=\"";

    my @protein_func = ();
    foreach $protein (@$protein_set_ref){
	my $function;
	if(defined($ypd_class_ref->{ $protein })){
	    $function = join("/", @{$ypd_class_ref->{ $protein }});
	}
	else { $function = "Unknown"; }
	push(@protein_func, $protein . "#$function");
    }
    print join(",", @protein_func);
    print "\">\n";
    print "<param name=interaction value=\"";

    my $ppi_ref;
    my @interaction;
    foreach $ppi_ref (@$ppi_list_nr){
	push(@interaction, $ppi_ref->[0] . "-" . $ppi_ref->[1]);
    }

    print join(",", @interaction);
    print "\">\n";

    
    print "<param name=functionkey value=\"";
    my @function_key;
    foreach(@class_list){
	push(@function_key, "$_#$_");
    }
    print join(",", @function_key);
    print "\">\n";
	
    print <<EOF;
alt="Your browser understands the &lt;APPLET&gt; tag but isn't running the applet, for some reason."
Your browser is completely ignoring the &lt;APPLET&gt; tag!

</applet>

EOF

}

sub java_simple_out3($$$){

    my($object, $ypd_class_ref, $prot_info_ref) = @_;
    my($ppi_list_nr, $protein_set_ref)
	= ($object->{'ppi_list_nr'}, $object->{'protein_set'});
    my(%class_list, @class_list);
    my $protein;

    foreach $protein (keys(%$ypd_class_ref)){
	foreach(@{ $ypd_class_ref->{ $protein } }){
	    $class_list{ $_ } = "";
	}
    }
    @class_list = keys(%class_list);

    print <<EOF;
<html>
<head>
<title>Example</title>
</head>
<body>
<h2>Example</h2>
<hr>
<applet code="GraphInteraction.class" width=800 height=600>
EOF
    print "<param name=functionlist value=\"";

    my @protein_func = ();
    foreach $protein (@$protein_set_ref){
	my $function;
	if(defined($ypd_class_ref->{ $protein })){
	    $function = join("/", @{$ypd_class_ref->{ $protein }});
	}
	else { $function = "Unknown"; }
	my $ph;
	if(defined($prot_info_ref->{ $protein })){
	    $ph = substr($prot_info_ref->{ $protein }, 0, 1);
	}
	else { $ph = ""; }
	push(@protein_func, "$protein" . "_" . "$ph" . "#$function");
    }
    print join(",", @protein_func);
    print "\">\n";
    print "<param name=interaction value=\"";

    my $ppi_ref;
    my @interaction;
    foreach $ppi_ref (@$ppi_list_nr){
	my($p1, $p2) = @$ppi_ref;
	my($ph1, $ph2);
	if(defined($prot_info_ref->{ $p1 })){
	    $ph1 = substr($prot_info_ref->{ $p1 }, 0, 1);
	}
	else { $ph1 = ""; }
	if(defined($prot_info_ref->{ $p2 })){
	    $ph2 = substr($prot_info_ref->{ $p2 }, 0, 1);
	}
	else { $ph2 = ""; }
	push(@interaction, 
	     $p1 . "_" . $ph1 . "-" .
	     $p2 . "_" . $ph2);
	     
    }

    print join(",", @interaction);
    print "\">\n";

    
    print "<param name=functionkey value=\"";
    my @function_key;
    foreach(@class_list){
	push(@function_key, "$_#$_");
    }
    print join(",", @function_key);
    print "\">\n";
	
    print <<EOF;
alt="Your browser understands the &lt;APPLET&gt; tag but isn't running the applet, for some reason."
Your browser is completely ignoring the &lt;APPLET&gt; tag!

</applet>
<hr>
</body>
</html>
EOF

}


1;

