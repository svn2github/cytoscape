#!/bin/bash

USAGE="$0: <database instance> <output directory>"

DB=$1
OUT=$2

test $DB || { echo $USAGE; exit; }
test $OUT || { echo $USAGE; exit; }


mkdir -p $OUT/cache

echo "Getting human genes"
mysql -u mdaly --password=mdalysql $DB \
    < cache-sql/dump-human-genes.sql \
    > $OUT/cache/human-genes-in-GO-DB.txt

echo "Getting yeast genes"
mysql -u mdaly --password=mdalysql $DB \
    < cache-sql/dump-yeast-genes.sql \
    > $OUT/cache/yeast-genes-in-GO-DB.txt
