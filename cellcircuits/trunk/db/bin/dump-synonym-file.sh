#!/bin/bash

USAGE="$0: <database instance> <output directory>"

DB=$1
OUT=$2

test $DB || { echo $USAGE; exit; }
test $OUT || { echo $USAGE; exit; }


echo "Getting synonym table"
mysql -u mdaly --password=mdalysql $DB \
    -e "SELECT species_id, gene_product_id as gene_id, symbol, product_synonym 
       FROM species, gene_product_synonym 
       WHERE
        gene_product_synonym.species_id = species.id AND
	((genus = 'Homo' and species = 'sapiens') OR
        (genus = 'Saccharomyces' and species = 'cerevisiae') OR
        (genus = 'Caenorhabditis' and species = 'elegans') OR
        (genus = 'Drosophila' and species = 'melanogaster') OR
        (genus = 'Plasmodium' and species = 'falciparum'))
       ORDER BY species_id;" \
    | make-synonym-tab-from-db-dump.pl > $OUT/synonyms.$DB.`date +%Y%b%d`.tab
