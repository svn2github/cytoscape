#!/usr/bin/perl -w

my $ep1 = .7;
my $ep2 = .299;

sub p
{
    my $type = shift @_;
    my $i = shift @_;
    my $p1 = shift @_;
    my $p2 = shift @_;
    my $p3 = shift @_;

    print $type . " " . $i . " " . $p1 . " " . $p2;

    if(defined($p3))
    {
	print " " . $p3;
    }

    print "\n";
}

print "
#
# What does it take to get a sign variable to change?
#

0 x .75 .25
1 d .6 .4 +1
2 s .5 .5
3 k .85 .1 .05
4 p .5 .5\n\n";


p("ts", 2,
  $ep1*.75*.6*.85*.5,
  1*.75*.6*.85*.5);

