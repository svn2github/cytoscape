
# need to install RCurl
# install.package("RCurl")
# library("RCurl")

res <- postForm("http://127.0.0.1:2609/cytoscape/network/load-file/",data="file=/Users/mes/galFiltered.sif", style="post")



