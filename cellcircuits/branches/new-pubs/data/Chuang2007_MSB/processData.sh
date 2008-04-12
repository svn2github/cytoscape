#
# Commands used to import HanYu's models
# cmak
# Sept 7, 2007
#
# Setup directories and move files to proper locations
mkdir sif
mkdir sif/Wang
mkdir sif/vandeVijver

mkdir lrg_img
mkdir lrg_img/Wang
mkdir lrg_img/vandeVijver

mkdir sml_img
mkdir sml_img/Wang
mkdir sml_img/vandeVijver

mkdir eps
mkdir eps/Wang
mkdir eps/vandeVijver

mv Wang/*.sif sif/Wang
mv Wang/*.eps eps/Wang

mv vandeVijver/*.sif sif/vandeVijver
mv vandeVijver/*.eps eps/vandeVijver

# Make the sifList file
find ./sif/ -name "*.sif" -print | awk '{ print $1 "\t" "Homo Sapiens"}' > sifList

# Create images (may need to uncomment somes lines)
processImages.sh

# Score models

# Generate SQL commands

# Test database

# Update hardcoded Perl locations
