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

echo "Getting model table"
mysql -u mdaly --password=mdalysql $DB \
    -e "SELECT * FROM model;" > $OUT/cache/model.txt

echo "Getting species table"
mysql -u mdaly --password=mdalysql $DB \
    -e "SELECT id, CONCAT_WS(' ', genus, species) as name FROM species
        WHERE (genus = 'Homo' and species = 'sapiens') OR
        (genus = 'Saccharomyces' and species = 'cerevisiae') OR
        (genus = 'Caenorhabditis' and species = 'elegans') OR
        (genus = 'Drosophila' and species = 'melanogaster') OR
        (genus = 'Plasmodium' and species = 'falciparum');" \
    > $OUT/cache/species.txt

echo "Getting unknown term ids"
mysql -u mdaly --password=mdalysql $DB \
    -e "SELECT id, name FROM term WHERE name = 'molecular function unknown' or name = 'biological process unknown' or name = 'cellular component unknown';" > $OUT/cache/unknown-terms.txt
