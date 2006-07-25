#use warnings;
#use strict;
# NP_620116,pp,NP_612815,0.0,PubMedID=9194558|dipID=1E|ref1=NP_620116|gi1=539664|swp1=Q07812|pir1=A47538|long1=bcl-2-associated protein x alpha splice form|dipID1=232N|ref2=NP_612815|gi2=2118487|swp2=Q07817|pir2=B47537|long2=apoptosis regulator bcl-xL|dipID2=328N

# Iliana Avila-Campillo
# BioGRID Download must be obtained before hand and copied to "biogrid" directory at the same level as this script.
# The download can be obtained from BioGRIDs website (http://www.thebiogrid.org/, free registration for academic users), and must be in PSI format.
# Get the "ALL" dataset
# TODO:
# Add an evidence column (example, Two-hybrid, In Vivo).
# Not all BioGRID interactions are pp. Some are genetic interactions. This can be infered from the experimental system in the PSI-MI file.

use XML::XPath;
use Data::Dumper;
#use DBI();
use Cwd;

print "--------------------- update_biogrid.pl ------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_biogrid.pl <db user> <db password> <db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

$starttime = time;

# connect to the BioGRID database
#my $bioh = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
#$bioh->do("CREATE DATABASE IF NOT EXISTS $dbname");
#$bioh->do("USE $bioname") or die "Error: $bioh->errstr";

# connect to the synonyms database
#$metah = DBI->connect("dbi:mysql:database=bionetbuilder_info:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
#$sth = $metah->prepare_cached("SELECT dbname FROM db_name WHERE db=?") or die "Error: $dbh->errstr";
#$sth->execute("synonyms") or die "Error: $dbh->errstr";
#@row = $sth->fetchrow_array;
#$synname = $row[0];
#my $synh = DBI->connect("dbi:mysql:database=$synname:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";

system("rm biogrid/interactions/*");
system("mkdir biogrid/interactions");

# this file must be first downloaded, edit the name of the file!

open DATA, "biogrid/GRID-ALL-SINGLEFILE-2.0.20.psi.xml";
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
$numRefs = 0;
$total = 0;
while( <DATA> ) {
    
    push @lines, $_;
    
    if ( /\<\/proteinInteractor\>/ ) {
	
	my $xp = XML::XPath->new( join( '', @lines ) );
	my @records = $xp->findnodes( '/proteinInteractor' );
	
	my $line = '';
	
	foreach my $record ( @records ) {
	    
	    ##my @fields = $xp->find( './child::*', $record )->get_nodelist();
	    
	    my @fields = $xp->find( '.', $record )->get_nodelist();
	    
	    foreach my $fld ( @fields ) {
		
		my @children = &getChildren( $fld );
		
		my ( $id, $ref, $spec, $taxid, $swp, $pir, $trembl, $full, $hgnc, $biogridid);
		
		for my $i ( 0..$#children ) {
		
		    $_ = $children[ $i ];
		    $id = $1 if /^proteinInteractor\.id=(.*)/;
		    $spec = $1 if /^proteinInteractor\.proteinInteractor\.organism\.organism\.names\.fullName\.=(.*)/;
		    $taxid = $1 if /^proteinInteractor\.proteinInteractor\.organism\.ncbiTaxId=(.*)/;
		    $full = $1 if /^proteinInteractor\.proteinInteractor\.names\.fullName\.=(.*)/;
		    $biogridid = $1 if /^proteinInteractor\.proteinInteractor\.xref\.primaryRef\.id=(.*)/;
		    
		    if ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=SWISSPROT' ) {
			$_ = $children[ ++ $i ];
			( $swp ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }elsif($_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=TREMBL'){
			$_ = $children[++ $i];
       			($trembl) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=PIR' ) {
			$_ = $children[ ++ $i ];
			( $pir ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=PROTEIN Accession') {
			$_ = $children[ ++ $i ];
			( $acc ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
			
                        if($acc =~ /^[A-Z][A-Z]\s[0-9]+$/){
			    $acc =~ s/\s/_/g;	
                            $ref = $acc;
			    $numRefs++;
			}
		    }elsif($_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=HGNC'){
			$_ = $children[++$i];
                       ($hgnc) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		}
		
		# the id for a node priority: RefSeq id,  SwissProt (UniProt), PIR, HGNC, BioGRID
		
		my $canonical = $ref ne '' ? $ref : ( 
				$trembl ne '' ? "UniProt:$trembl" : ( 
				$swp ne '' ? "UniProt:$swp" : ($pir ne '' ? "PIR:$pir" : ( $hgnc ne '' ? "HGNC:$hgnc" : "BioGRID:$biogridid" ) )));
		
		( $full ) =~ s/\,//g;
		
		$ids{$id} = $canonical;
		$specs{$id} = $taxid;
		#$infos{$id} = '' . ( $ref ne '' ? "ref=$ref|" : "" ) .
		#                  ( $gi ne '' ? "gi=$gi|" : "" ) .
		#		   ( $swp ne '' ? "swp=$swp|" : "" ) .
		#		   ( $pir ne '' ? "pir=$pir|" : "" ) .
		#		   ( $full ne '' ? "long=$full|" : "" ) .
		#		   ( $dipid ne '' ? "dipID=$dipid" : "" );
		$total++;
		print STDERR "$id = $canonical\n"; ##\t$ids{$id}\t$infos{$id}\t$specs{$id}\n";
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

HERE:

#print Data::Dumper->Dump( [\%ids], ["ids"]), $/;
#print Data::Dumper->Dump( [\%specs], ["specs"]), $/;
#print Data::Dumper->Dump( [\%infos], ["infos"]), $/;

print " ----------- Total interactors = $total, with RefSeq IDs = $numRefs\n";

while( <DATA> ) {
    $line1 = $_;
    last if /\<interaction\>/;
}

# this while loop parses the interactions
# todo: I am not sure of the format/size of a dipid
#$diph->do("DROP TABLE IF EXISTS interactions");
#$diph->do("CREATE TABLE interactions (id VARCHAR(25), i1 VARCHAR(25), interactionType VARCHAR(2), i2 VARCHAR(25), taxid1 INT, taxid2 INT, KEY(id), INDEX(taxid1), INDEX(taxid2) )");

open X, ">>biogrid/interactions/interactions\.txt";

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
	   
	    $taxid1 = $specs{$id1};
	    $taxid2 = $specs{$id2};
	   
	   # $info1 = $infos{$id1};
	   # ( $info1 ) =~ s/=/1=/g;
	   # $info2 = $infos{$id2};
	   # ( $info2 ) =~ s/=/2=/g;
	   # $infoA = "PubMedID=$pmid|dipID=$iid|$info1|$info2";
	   # $info1 = $infos{$id1};
	   # ( $info1 ) =~ s/=/2=/g;
	   # $info2 = $infos{$id2};
	   # ( $info2 ) =~ s/=/1=/g;
	   # $infoB = "PubMedID=$pmid|dipID=$iid|$info1|$info2";
	    if($i1 ne '' and $i2 ne ''){
	    		print X "$iid\t$i1\tpp\t$i2\t$taxid1\t$taxid2\n";
			print "$iid\t$i1\tpp\t$i2\t$taxid1\t$taxid2\n";
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

#$fullFilePath = getcwd."/dip/interactions/interactions.txt";
#print "Loading interactions...\n";
#$diph->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE interactions") or die "Error: $synh->errstr";
#print "done.\n";

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

