# Syncs latest file changes to cytoscape.org
# - excludes all svn directories

#  Configure for production
ant config_prod

#  Issue rsync command
rsync -av --exclude ".svn/" --exclude "deploy.sh" --exclude "deploy_enoteca.sh" --exclude "build.xml" . mes5k,cytoscape@web.sourceforge.net:htdocs/

