echo "IsSubtelomeric (class=java.lang.String)" > SC-subtelomeric.byName.na

more SC-subt-genes.tab | cut -f 1 | replace-orf-with-name.pl | awk '{ print $1 " = subtelomeric"}' >> SC-subtelomeric.byName.na

echo "IsSubtelomeric (class=java.lang.String)" > SC-subtelomeric.na

more SC-subt-genes.tab | cut -f 1 | awk '{ print $1 " = subtelomeric"}' >> SC-subtelomeric.na
