# Syncs latest file changes to cytoscape.org
# - excludes all CVS directories

#  Configure for production
ant config_prod

#  Issue rsync command
rsync --verbose --progress --stats --compress --rsh=/usr/bin/ssh --recursive --size-only --perms --links --exclude ".svn/" --exclude "deploy.sh" --exclude "deploy_enoteca.sh" --exclude "build.xml" . root@enoteca-5.ucsd.edu:/var/www/html/cytoscape/  
