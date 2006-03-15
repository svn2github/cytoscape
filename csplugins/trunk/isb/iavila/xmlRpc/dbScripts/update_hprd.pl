#!/usr/bin/perl

#use warnings;
#use strict;
# NP_620116,pp,NP_612815,0.0,PubMedID=9194558|dipID=1E|ref1=NP_620116|gi1=539664|swp1=Q07812|pir1=A47538|long1=bcl-2-associated protein x alpha splice form|dipID1=232N|ref2=NP_612815|gi2=2118487|swp2=Q07817|pir2=B47537|long2=apoptosis regulator bcl-xL|dipID2=328N

use XML::XPath;
use Data::Dumper;
use DBI();
use Cwd;

print "--------------------- update_hprd.pl ------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_hprd.pl <db user> <db password> <db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

$starttime = time;

# connect to the HPRD database
my $dbh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$dbh->do("CREATE DATABASE IF NOT EXISTS $dbname");
$dbh->do("USE $dbname") or die "Error: $dbh->errstr";

# connect to the synonyms database
$metah = DBI->connect("dbi:mysql:database=bionetbuilder_info:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$sth = $metah->prepare_cached("SELECT dbname FROM db_name WHERE db=?") or die "Error: $dbh->errstr";
$sth->execute("synonyms") or die "Error: $dbh->errstr";
@row = $sth->fetchrow_array;
$synname = $row[0];
my $synh = DBI->connect("dbi:mysql:database=$synname:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

# todo: how do I get this from an ftp site? I need to login...

system("rm hprd/interactions/*");
system("mkdir hprd/interactions");

open DATA, "hprd/psimi_single_final.xml";
my $line1;
while( <DATA> ) {
    $line1 = $_;
    last if /\<proteinInteractor id/;
}

my %ids = ();
my %specs = ();
my %infos = ();

my @lines = ( $line1 );

# this while loop accumulates interactors and their information (id, alternate ids, etc).

while( <DATA> ) {
    
    push @lines, $_;
    
    if ( /\<\/proteinInteractor\>/ ) {
	
	my $xp = XML::XPath->new( join( '', @lines ) );
	my @records = $xp->findnodes( '/proteinInteractor' );
	
	my $line = '';
	$numInteractors = 0;
	$numInteractorsNoTaxid = 0;	
	foreach my $record ( @records ) {
	    
	    ##my @fields = $xp->find( './child::*', $record )->get_nodelist();
	    
	    my @fields = $xp->find( '.', $record )->get_nodelist();
	    
	    foreach my $fld ( @fields ) {
		
		my @children = &getChildren( $fld );
		
		my ( $id, $ref, $spec, $taxid, $swp, $pir, $gi, $locuslink, $unigene, $full, $dbid );
		
		for my $i ( 0..$#children ) {
			$numInteractors++;
		    $_ = $children[ $i ];
		    #print $_,"\n";
		    $id = $1 if /^proteinInteractor\.id=(.*)/;
		    $spec = $1 if /^proteinInteractor\.proteinInteractor\.organism\.organism\.names\.fullName\.=(.*)/;
		    $taxid = $1 if /^proteinInteractor\.proteinInteractor\.organism\.ncbiTaxId=(.*)/;
		    $full = $1 if /^proteinInteractor\.proteinInteractor\.names\.fullName\.=(.*)/;
		    $dbid = $1 if /^proteinInteractor\.proteinInteractor\.xref\.primaryRef\.id=(.*)/;
		    
		    if ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Swiss-Prot' ) {
			$_ = $children[ ++ $i ];
			( $swp ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
			#print $swp,"\n";
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=PIR' ) {
			$_ = $children[ ++ $i ];
			( $pir ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    	#print $pir,"\n";
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=GI' ) {
			$_ = $children[ ++ $i ];
			( $gi ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    #print $gi,"\n";
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Ref-Seq' ) {
			$_ = $children[ ++ $i ];
			( $ref ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    #print $ref,"\n";
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Unigene' ) {
			$_ = $children[ ++ $i ];
			( $unigene ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    #print $unigene,"\n";
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Locus-Link' ) {
			$_ = $children[ ++ $i ];
			( $locuslink ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    #print $locuslink,"\n";
			}
		}
		
		# the id for a node priority: RefSeq id, GI, SwissProt, PIR, Locus-Link, Unigene, hprd
		
		my $canonical = "HPRD:${id}";
		
		if($ref ne ''){
			$canonical = $ref;
		}elsif($gi ne ''){
			$canonical = "GI:${gi}";
		}elsif($swp ne ''){
			$canonical = "UniProt:${swp}";
		}elsif($pir ne ''){
			$canonical = "PIR:${pir}";
		}elsif($locuslink ne ''){
			$canonical = "LocLink:${locuslink}";
		}elsif($unigene ne ''){
			$canonical = "Unigene:${unigene}";
		}elsif($dbid ne ''){
			$canonical = "HPRD:${dbid}";
		}
		
		( $full ) =~ s/\,//g;
		
		## Note dipid can be used to look up node:
		## http://dip.doe-mbi.ucla.edu/dip/DIPview.cgi?PK=232   for $dbid='232N'
		
		if($taxid eq ''){
			$numInteractorsNoTaxid++;
		}
		
		$ids{$id} = $canonical;
		$specs{$id} = $taxid;
		#$infos{$id} = '' . ( $ref ne '' ? "ref=$ref|" : "" ) .
		#                   ( $gi ne '' ? "gi=$gi|" : "" ) .
	    #			   ( $swp ne '' ? "swp=$swp|" : "" ) .
	    #		   ( $pir ne '' ? "pir=$pir|" : "" ) .
		#		   ( $full ne '' ? "long=$full|" : "" ) .
		#		   ( $dbid ne '' ? "hprdID=$dbid" : "" );
		
		print STDERR "$id\n";
	    }
	}
	##( $line ) =~ s/\n//g;
	##( $line ) =~ s/\.=/=/g;
	##print "$line\n";
	##last;

	last if /\<\/interactorList\>/;
	print if /\<\/interactorList\>/;

	while( <DATA> ) {
	    $line1 = $_;
	    last if /\<proteinInteractor id/ || /\<\/interactorList\>/;
	}
	@lines = ( $line1 );

	last if /\<\/interactorList\>/;
	print if /\<\/interactorList\>/;
	#goto HERE;
    }
}

print "Total num interactors = $numInteractors, without species = $numInteractorsNoTaxid\n";

HERE:

#print Data::Dumper->Dump( [\%ids], ["ids"]), $/;
#print Data::Dumper->Dump( [\%specs], ["specs"]), $/;
#print Data::Dumper->Dump( [\%infos], ["infos"]), $/;

while( <DATA> ) {
    $line1 = $_;
    last if /\<interaction\>/;
}

# this while loop parses the interactions
# todo: I am not sure of the format/size of a dipid
$dbh->do("DROP TABLE IF EXISTS interactions");
$dbh->do("CREATE TABLE interactions (id VARCHAR(25), i1 VARCHAR(25), interactionType VARCHAR(2), i2 VARCHAR(25), taxid1 INT, taxid2 INT, KEY(id), INDEX(taxid1), INDEX(taxid2) )");

open X, ">>hprd/interactions/interactions\.txt";

my @lines = ( $line1 );
while( <DATA> ) {
    push @lines, $_;
    if ( /\<\/interaction\>/ ) {
	my $xp = XML::XPath->new( join( '', @lines ) );
	my @records = $xp->findnodes( '/interaction' );
	
	my $line = '';
	foreach my $record ( @records ) {
	   
	    ##my @fields = $xp->find( './child::*', $record )->get_nodelist();
	    my @fields = $xp->find( '.', $record )->get_nodelist();
	   
	    my ( $pmid, $id1, $id2, $iid );
	   
	    $id1 = '';
	    $id2 = '';
	    
	    foreach my $fld ( @fields ) {
		
		my @children = &getChildren( $fld );
		
		##$line .= join( "\t", @children ) . "\t";
		
		for my $i ( 0..$#children ) {
		    $_ = $children[ $i ];
		    #print "$_\n";
		    $iid = $1 if /^interaction.xref\.primaryRef\.id=(.*)/;
		    if ( $_ eq 'interaction.experimentList.experimentDescription.experimentDescription.bibref.xref.primaryRef.db=pubmed' ) {
			$_ = $children[ ++ $i ];
			( $pmid ) = /^interaction\.experimentList\.experimentDescription\.experimentDescription\.bibref\.xref\.primaryRef\.id=(.*)/;
		    }
		    elsif ( /^interaction\.participantList\.proteinParticipant\.proteinInteractorRef\.ref=(.*)/ ) {
			$id1 = $1 if $id1 eq '';
			$id2 = $1 if $id1 ne '';
		    }
		}
	    }

	    ##print "HERE: $id1 $id2 $pmid $iid\n";
	    ##( $line ) =~ s/\n//g;
	    ##( $line ) =~ s/\.=/=/g;
	    ##print "$line\n";
	    #last;

	    ## NOTE: $iid can be used to get the interaction URL from dip site:
	    ## http://dip.doe-mbi.ucla.edu/dip/DIPview.cgi?IK=1             for $iid = '1E'

	    my $i1 = $ids{$id1};
	    print STDERR "WARNING: no id1\n" if $id1 eq '';
	   
	    my $i2 = $ids{$id2};
	    print STDERR "WARNING: no id2\n" if $id2 eq '';
	   
	    my $taxid1 = $specs{$id1};
	    my $taxid2 = $specs{$id2};
	   
	    
	   # $info1 = $infos{$id1};
	   # ( $info1 ) =~ s/=/1=/g;
	   # $info2 = $infos{$id2};
	   # ( $info2 ) =~ s/=/2=/g;
	   # $infoA = "PubMedID=$pmid|hprdID=$iid|$info1|$info2";
	   # $info1 = $infos{$id1};
	   # ( $info1 ) =~ s/=/2=/g;
	   # $info2 = $infos{$id2};
	   # ( $info2 ) =~ s/=/1=/g;
	   # $infoB = "PubMedID=$pmid|hprdID=$iid|$info1|$info2";
	    
	    # not sure why in some cases the interactors do not have a name...
	    if($i1 ne '' and $i2 ne ''){	
	   		print X "$iid\t$i1\tpp\t$i2\t$taxid1\t$taxid2\n";
	    }
	}

	while( <DATA> ) {
	    $line1 = $_;
	    last if /\<interaction\>/;
	}
	@lines = ( $line1 );

	##exit;
    }
}

close DATA;
$fullFilePath = getcwd."/hprd/interactions/interactions.txt";
print "Loading interactions...\n";
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE interactions") or die "Error: $dbh->errstr";
print "done.\n";

#########################################################################
sub getChildren {
    my ( $fld, $parent ) = @_;
    $parent = "$parent\." if defined $parent;

    my @out = ();

    my @attrs = $fld->getAttributeNodes();
    ##print "XXX: $parent".$fld->getName()." ".$#attrs."\n";
    if ( $#attrs >= 0 ) {
        $parent = $parent . $fld->getName() . "." if ( defined $parent );
        $parent = $fld->getName() . "." if ( ! defined $parent );
        foreach $att ( @attrs ) {
            ##print "HERE: $parent|".$att->getName()."|".$att->string_value()."\n";
            push @out, $parent . $att->getName() . "=" . 
                $att->string_value() if defined $att->string_value();
        }
    }

    my @children = $fld->getChildNodes();
    if ( $#children >= 0 ) {
        foreach $child ( @children ) {
            push @out, &getChildren( $child, $parent . $fld->getName() );
            #print $child->getName() . "=" . $child->string_value();
        }
    } else {
        my $val = $fld->string_value();
        $val =~ s/^\s+//g;
        $val =~ s/\s+$//g;
        push @out, $parent . $fld->getName() . "=$val" if defined $val && $val ne "";
        ##print $parent . $fld->getName() . "='$val'\n";
    }

    @out;
}


