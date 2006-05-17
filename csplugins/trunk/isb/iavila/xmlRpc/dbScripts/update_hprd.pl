#!/usr/bin/perl

use XML::XPath;
use Data::Dumper;
use DBI();
use Cwd;

############### Read arguments ############################################################

print "--------------------- update_hprd.pl ------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_hprd.pl <db user> <db password> <db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dbname = $ARGV[2];

############# Connect to mySQL database where we will store information #####################
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

################ Read PSI-MI file ############################################################

system("rm hprd/interactions/*");
system("mkdir hprd/interactions");

open DATA, "hprd/psimi_single_final.xml";

###### Read interactors list ########
my $line1;
# skip the XML header, get to the first proteinInteractor entry

while( <DATA> ) {
    $line1 = $_;
    last if /\<proteinInteractor id/;
}

my %ids = ();
my %specs = ();
my %infos = ();

my @lines = ( $line1 );

# this while loop accumulates INTERACTORS and their information (id, alternate ids, etc).

$numInteractors = 0;
$numInteractorsNoTaxid = 0;	

while( <DATA> ) {

    push @lines, $_;
    
    if ( /\<\/proteinInteractor\>/ ) {
	
	my $xp = XML::XPath->new( join( '', @lines ) );
	my @records = $xp->findnodes( '/proteinInteractor' );
	
	my $line = '';
		
	foreach my $record ( @records ) {
	    
	    my @fields = $xp->find( '.', $record )->get_nodelist();
	    
		foreach my $fld ( @fields ) {
			
			# $fld is a proteinInteractor element.
			
			my @children = &getChildren( $fld );
			my ( $id, $ref, $spec, $taxid, $swp, $pir, $gi, $locuslink, $unigene, $full, $dbid );
			
			# get each of the above fields:
			for my $i ( 0..$#children ) {
				$numInteractors++;
		    		$_ = $children[ $i ];
		    		$id = $1 if /^proteinInteractor\.id=(.*)/;
		    		$spec = $1 if /^proteinInteractor\.proteinInteractor\.organism\.organism\.names\.fullName\.=(.*)/;
		    		$taxid = $1 if /^proteinInteractor\.proteinInteractor\.organism\.ncbiTaxId=(.*)/;
		    		$full = $1 if /^proteinInteractor\.proteinInteractor\.names\.fullName\.=(.*)/;
		    		$dbid = $1 if /^proteinInteractor\.proteinInteractor\.xref\.primaryRef\.id=(.*)/;
		    
		    		if ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Swiss-Prot' ) {
					$_ = $children[ ++ $i ];
					( $swp ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
					#print $swp,"\n";
		    		}elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=PIR' ) {
					$_ = $children[ ++ $i ];
					( $pir ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    			#print $pir,"\n";
		    		}elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=GI' ) {
					$_ = $children[ ++ $i ];
					( $gi ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    			#print $gi,"\n";
		    		}elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Ref-Seq' ) {
					$_ = $children[ ++ $i ];
					( $ref ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    			#print $ref,"\n";
		    		}elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Unigene' ) {
					$_ = $children[ ++ $i ];
					( $unigene ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    			#print $unigene,"\n";
		    		}elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=Locus-Link' ) {
					$_ = $children[ ++ $i ];
					( $locuslink ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    			#print $locuslink,"\n";
				}
			}# end of for my $i (0..$#children)
		
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
		
		
			if($taxid eq ''){
				$numInteractorsNoTaxid++;
			}
			
			# store the id, the species for the ID
			$ids{$id} = $canonical;
			$specs{$id} = $taxid;
			
			#$infos{$id} = '' . ( $ref ne '' ? "ref=$ref|" : "" ) .
			#                   ( $gi ne '' ? "gi=$gi|" : "" ) .
	    		#			   ( $swp ne '' ? "swp=$swp|" : "" ) .
	    		#		   ( $pir ne '' ? "pir=$pir|" : "" ) .
			#		   ( $full ne '' ? "long=$full|" : "" ) .
			#		   ( $dbid ne '' ? "hprdID=$dbid" : "" );
		
			print STDERR "$id\n";
	    } # end for a proteinInteractor element
	}# end for $record
	
	##( $line ) =~ s/\n//g;
	##( $line ) =~ s/\.=/=/g;
	##print "$line\n";
	##last;

	# if we reached the end of the interactor list, get out of this loop
	last if /\<\/interactorList\>/;
	print if /\<\/interactorList\>/;

	# advance to the next proteinInteractor
	while( <DATA> ) {
	    $line1 = $_;
	    last if /\<proteinInteractor id/ || /\<\/interactorList\>/;
	}
	
	@lines = ( $line1 );
	last if /\<\/interactorList\>/;
	print if /\<\/interactorList\>/;
    }
}

print "Total num interactors = $numInteractors, without species = $numInteractorsNoTaxid\n";

###### Read interactions ##########

# find the first interaction tag
while( <DATA> ) {
    $line1 = $_;
    last if /\<interaction\>/;
}

$dbh->do("DROP TABLE IF EXISTS interactions");
$dbh->do("CREATE TABLE interactions (id VARCHAR(25), i1 VARCHAR(25), interactionType VARCHAR(2), 
	i2 VARCHAR(25), taxid1 INT, taxid2 INT, primaryPubMed INT, secondaryPubMeds VARCHAR(100), detectionMethod VARCHAR(10), KEY(id), INDEX(taxid1), INDEX(taxid2) )");

open X, ">>hprd/interactions/interactions\.txt";

my @lines = ( $line1 );
while( <DATA> ) {
    push @lines, $_;
    if ( /\<\/interaction\>/ ) {
	my $xp = XML::XPath->new( join( '', @lines ) );
	my @records = $xp->findnodes( '/interaction' );
	
	my $line = '';
	foreach my $record ( @records ) {
	   
	    my @fields = $xp->find( '.', $record )->get_nodelist();   
	    my ( $pmid, $pmidSecondary, $detection,$id1, $id2, $iid );
	   
	    $id1 = '';
	    $id2 = '';
	    
	    foreach my $fld ( @fields ) {
			# this is an interaction element
			my @children = &getChildren( $fld );
			
			for my $i ( 0..$#children ) {
		    		$_ = $children[ $i ];
		    		# if you are trying to read an element, but cannot figure out how to match $_ uncomment this line:
		    		# print "$_\n";
		    		$iid = $1 if /^interaction.xref\.primaryRef\.id=(.*)/;
		    		if ( $_ eq 'interaction.experimentList.experimentDescription.experimentDescription.bibref.xref.primaryRef.db=PubMed' ) {
				
					$_ = $children[ ++ $i ];
					( $pmid ) = /^interaction\.experimentList\.experimentDescription\.experimentDescription\.bibref\.xref\.primaryRef\.id=(.*)/;
					
		    			
		    		}elsif($_ eq 'interaction.experimentList.experimentDescription.experimentDescription.bibref.xref.secondaryRef.db=PubMed'){
		    		
		    			$_ = $children[ ++ $i ];
					( $pmid2 ) = /^interaction\.experimentList\.experimentDescription\.experimentDescription\.bibref\.xref\.secondaryRef\.id=(.*)/;
		    			if($pmidSecondary eq ''){
		    				$pmidSecondary = $pmid2;
		    			}else{
		    				$pmidSecondary = $pmidSecondary.','.$pmid2;
		    			}
		    		}elsif( /^interaction\.experimentList\.experimentDescription\.experimentDescription\.interactionDetection\.names\.fullName\.=(.*)/ ){
		    			
		    			( $detection ) = /^interaction\.experimentList\.experimentDescription\.experimentDescription\.interactionDetection\.names\.fullName\.=(.*)/;
		    			print "detection = $detection\n";
		    			
		    		}elsif ( /^interaction\.participantList\.proteinParticipant\.proteinInteractorRef\.ref=(.*)/ ) {
				
					$id1 = $1 if $id1 eq '';
					$id2 = $1 if $id1 ne '';
		    		
		    		}
		    		
			}#for $i (0..$#children)
	    
	    }#for $fld (@fields)

	   
	    ##( $line ) =~ s/\n//g;
	    ##( $line ) =~ s/\.=/=/g;
	    ##print "$line\n";
	    #last;
	    
	    # get the 'canonical' name of the interactors
	    
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
	   		print X "$iid\t$i1\tpp\t$i2\t$taxid1\t$taxid2\t$pmid\t$pmidSecondary\t$detection\n";
	   		print "$iid\t$i1\tpp\t$i2\t$taxid1\t$taxid2\t$pmid\t$pmidSecondary\t$detection\n";
	    }
	}

	# advance to the next interaction
	while( <DATA> ) {
	    $line1 = $_;
	    last if /\<interaction\>/;
	}
	@lines = ( $line1 );

	#exit;
    }
}

close DATA;

$fullFilePath = getcwd."/hprd/interactions/interactions.txt";
print "Loading interactions...\n";
# the extra fields in the txt file are ignored
$dbh->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE interactions") or die "Error: $dbh->errstr";
print "done.\n";

############# Subroutines ############################################################

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