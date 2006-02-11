#!/usr/bin/perl

#use warnings;
#use strict;
# NP_620116,pp,NP_612815,0.0,PubMedID=9194558|dipID=1E|ref1=NP_620116|gi1=539664|swp1=Q07812|pir1=A47538|long1=bcl-2-associated protein x alpha splice form|dipID1=232N|ref2=NP_612815|gi2=2118487|swp2=Q07817|pir2=B47537|long2=apoptosis regulator bcl-xL|dipID2=328N

use XML::XPath;
use Data::Dumper;
use DBI();
use Cwd;

print "--------------------- update_dip.pl ------------------\n";

if(scalar(@ARGV) < 3){
	print "USAGE: perl update_dip.pl <db user> <db password> <db name>\n";
 	die;
}

$dbuser = $ARGV[0];
$dbpwd = $ARGV[1];
$dipname = $ARGV[2];

$starttime = time;

# connect to the DIP database
my $diph = DBI->connect("dbi:mysql:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$diph->do("CREATE DATABASE IF NOT EXISTS $dipname");
$diph->do("USE $dipname") or die "Error: $dbh->errstr";

# connect to the synonyms database
$metah = DBI->connect("dbi:mysql:database=metainfo:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";
$sth = $metah->prepare_cached("SELECT dbname FROM db_name WHERE db=?") or die "Error: $dbh->errstr";
$sth->execute("synonyms") or die "Error: $dbh->errstr";
@row = $sth->fetchrow_array;
$synname = $row[0];
my $synh = DBI->connect("dbi:mysql:database=$synname:host=localhost", $dbuser, $dbpwd) or die "Can't make database connect: $DBI::errstr\n";


# todo: how do I get this from an ftp site? I need to login...


system("rm dip/interactions/*");
system("mkdir dip/interactions");

open DATA, "dip/dip20060116.mif";
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
	
	foreach my $record ( @records ) {
	    
	    ##my @fields = $xp->find( './child::*', $record )->get_nodelist();
	    
	    my @fields = $xp->find( '.', $record )->get_nodelist();
	    
	    foreach my $fld ( @fields ) {
		
		my @children = &getChildren( $fld );
		
		my ( $id, $ref, $spec, $swp, $pir, $gi, $full, $dipid );
		
		for my $i ( 0..$#children ) {
		
		    $_ = $children[ $i ];
		    $id = $1 if /^proteinInteractor\.id=(.*)/;
		    $spec = $1 if /^proteinInteractor\.proteinInteractor\.organism\.organism\.names\.fullName\.=(.*)/;
		    $full = $1 if /^proteinInteractor\.proteinInteractor\.names\.fullName\.=(.*)/;
		    $dipid = $1 if /^proteinInteractor\.proteinInteractor\.xref\.primaryRef\.id=(.*)/;
		    
		    if ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=SWP' ) {
			$_ = $children[ ++ $i ];
			( $swp ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=PIR' ) {
			$_ = $children[ ++ $i ];
			( $pir ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=GI' ) {
			$_ = $children[ ++ $i ];
			( $gi ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		    elsif ( $_ eq 'proteinInteractor.proteinInteractor.xref.secondaryRef.db=RefSeq' ) {
			$_ = $children[ ++ $i ];
			( $ref ) = /^proteinInteractor\.proteinInteractor\.xref\.secondaryRef\.id=(.*)/;
		    }
		}
		
		# the id for a node priority: RefSeq id, GI, SwissProt, PIR
		
		my $canonical = $ref ne '' ? $ref : ( 
				$gi ne '' ? "GI:$gi" : ( 
				$swp ne '' ? "SPROT:$swp" : "PIR:$pir" ) );
		
		( $full ) =~ s/\,//g;
		
		## Note dipid can be used to look up node:
		## http://dip.doe-mbi.ucla.edu/dip/DIPview.cgi?PK=232   for $dipid='232N'
		
		$ids{$id} = $canonical;
		$specs{$id} = $spec;
		$infos{$id} = '' . ( $ref ne '' ? "ref=$ref|" : "" ) .
		                   ( $gi ne '' ? "gi=$gi|" : "" ) .
				   ( $swp ne '' ? "swp=$swp|" : "" ) .
				   ( $pir ne '' ? "pir=$pir|" : "" ) .
				   ( $full ne '' ? "long=$full|" : "" ) .
				   ( $dipid ne '' ? "dipID=$dipid" : "" );
		print STDERR "$id\n"; ##\t$ids{$id}\t$infos{$id}\t$specs{$id}\n";
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

while( <DATA> ) {
    $line1 = $_;
    last if /\<interaction\>/;
}

# this while loop parses the interactions
# todo: I am not sure of the format/size of a dipid
$diph->do("DROP TABLE IF EXISTS interactions");
$diph->do("CREATE TABLE interactions (dipid VARCHAR(25), i1 VARCHAR(25), interactionType VARCHAR(2), i2 VARCHAR(25), taxid INT, KEY(dipid), INDEX(taxid) )");
$sth = $synh->prepare_cached("SELECT taxid FROM ncbi_taxid_species WHERE name like ?");

open X, ">>dip/interactions/interactions\.txt";

my %speciesToTaxid;

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
	   
	    $spec1 = $specs{$id1};
	    $spec2 = $specs{$id2};
	   
	    my $spec = $spec1;
	    
	    # find the taxid of the species spec
	    $taxid = $speciesToTaxid{$spec};
	    if($taxid eq ""){
	    		$sth->execute($spec);
	    		@row = $sth->fetchrow_array;
	    		$taxid = $row[0];
	    		if($taxid eq ""){
	    			$taxid = 0; # for now
	    		}
	    		$speciesToTaxid{$spec} = $taxid;
	    }
	    
	    ( $spec ) =~ s/\s/\_/g;

	    $info1 = $infos{$id1};
	    ( $info1 ) =~ s/=/1=/g;
	    $info2 = $infos{$id2};
	    ( $info2 ) =~ s/=/2=/g;
	    $infoA = "PubMedID=$pmid|dipID=$iid|$info1|$info2";
	    $info1 = $infos{$id1};
	    ( $info1 ) =~ s/=/2=/g;
	    $info2 = $infos{$id2};
	    ( $info2 ) =~ s/=/1=/g;
	    $infoB = "PubMedID=$pmid|dipID=$iid|$info1|$info2";
	    
	    if($spec1 eq $spec2){
	   		print X "$iid\t$i1\tpp\t$i2\t$taxid\n";
	    		#print X "$i2\,pp,$i1\,0.0,$infoB\n";
	    }else{
	    		# print to a file of interspecies interactions
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

$fullFilePath = getcwd."/dip/interactions/interactions.txt";
print "Loading interactions...\n";
$diph->do("LOAD DATA LOCAL INFILE \'${fullFilePath}\' IGNORE INTO TABLE interactions") or die "Error: $synh->errstr";
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


