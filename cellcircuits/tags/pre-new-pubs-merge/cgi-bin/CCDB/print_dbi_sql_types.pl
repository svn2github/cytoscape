#!/usr/bin/perl

#use strict;
use warnings;
use DBI qw(:sql_types);

foreach (@{ $DBI::EXPORT_TAGS{sql_types} }) {
    printf "%s=%d\n", $_, &{"DBI::$_"};
}
