
	        </div>
	        <!-- end page content -->
        </div> 
	
        <div id="footer" class="slice">
			<p>
				Funding for <a href="http://www.cytoscape.org">Cytoscape</a> is provided by a federal grant from the U.S. 
				<a href="http://www.nigms.nih.gov">National Institute of General Medical Sciences (NIGMS)</a> 
				of the <a href="http://www.nih.gov">National Institutes of Health (NIH)</a> under award 
				number GM070743-01 and the U.S. <a href="http://www.nsf.gov">National Science Foundation (NSF)</a>.
			</p>
			<p>
		  		<a href="http://www.systemsbiology.org"> ISB </a> | 
		  		<a href="http://www.ucsd.edu"> UCSD </a> | 
		  		<a href="http://cbio.mskcc.org"> MSKCC </a> | 
		  		<a href="http://www.pasteur.fr"> Pasteur </a> | 
		  		<a href="http://www.agilent.com/"> Agilent </a> | 
		  		<a href="http://www.ucsf.edu/"> UCSF </a> |
				<a href="http://www.unilever.com"> Unilever </a> |
				<a href="http://www.utoronto.ca"> Toronto </a> |
				<a href="http://portal.ncibi.org/gateway/index.html"> NCIBI </a>
		  	</p>

			<p>
				&copy; 
	            <?php
	                $year = date("Y");
	                
	                if( $year == $first_year_of_project_release ){
	                    echo $year;
	                } else {
	                    echo $first_year_of_project_release . '&ndash;' . $year;
	                }
	            ?>
            	Cytoscape Consortium
        	</p>
        </div>

    </body>

</html>