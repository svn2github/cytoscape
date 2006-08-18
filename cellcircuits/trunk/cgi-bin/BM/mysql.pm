package BM::mysql;

use strict;
use warnings;

###############
# CONSTRUCTOR #
###############
sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self = { };
    bless($self, $class);
    return $self;
}

##################################################
# is_pk - Determines if a column is a primary key.
# Parameters: DBI statement handle and a column
# number from that handle.
# Returns: true or false.

sub is_pk ($$$) {
    my $self = shift;
    my $sth = shift;
    my $i = shift;

    return 1 if $$sth->{mysql_is_pri_key}->[$i];
    return 0;
}

###################################################
# is_auto_increment - Determines if a column is an
# AUTO_INCREMENT column.
# Parameters: DBI statement.
# handle and a column number from that handle.
# Returns: true or false.
sub is_auto_increment($$$) {
    my $self = shift;
    my $sth = shift;
    my $i = shift;

    return 1 if $$sth->{mysql_is_auto_increment}->[$i];
    return 0;
}

################################################
# get_pk_value - Returns the last AUTO_INCREMENT
# value for this connection.
# Paramaters: DBI database handle.
# Returns: PK value.
sub get_pk_value {
    my $self = shift;
    my $dbh = shift or die "mysql::get_pk needs a Database Handle...";
    my $mysqlPk = "Select last_insert_id() as pk";
    my $mysqlSth = $dbh->prepare($mysqlPk);
    $mysqlSth->execute();
    my $mysqlHR = $mysqlSth->fetchrow_hashref;
    my $pk = $mysqlHR->{"pk"};
    $mysqlSth->finish;
    return $pk;
}

1;
