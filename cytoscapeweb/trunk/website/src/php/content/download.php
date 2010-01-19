<div class="left">

    <?php
    
        $first = true;
        foreach($apis as $api) {
            
            $version = $api->version;
            $date = strtotime( $api->date );
            $info = $api->release_info;
            
            // do not show before the oldest date or oldest version
            // but show at least the first download (can't have empty page)
            if( ($date < $oldest_date_to_show || $version < $oldest_version_to_show) && !$first ) {
                continue;
            }
        
    ?>
        
            <h1<?php echo ($first ? " class=\"latest\"" : ""); ?>>Version <?php echo $version . ($first ? " (latest)" : ""); ?></h1>
            <div class="subtitle <?php echo ($first ? "latest" : ""); ?>">
                <label class="date"><?php echo date("j M Y", $date); ?></label>
                <?php if( ( $date >= $oldest_date_to_dl && $version >= $oldest_version_to_dl ) || $first ) { ?>
                    <a class="dl" href="<?php echo get_download_url($version); ?>">Download</a>
                <?php } ?>
            </div>
            
            <?php
                echo $info;
            ?>
            
            <div class="description <?php echo ($first ? "latest" : ""); ?>"><?php echo $description; ?></div>
           
    <?php
        
            $first = false;
        }
    
    ?>

    <h1>Source Code</h1>
    <div class="description">
        <p>
            You can browse the Cytoscape Web source code <a href="http://chianti.ucsd.edu/svn/cytoscapeweb/" rel="external">here</a>.
        </p>
        <p>
            If you want to download the latest source from our 
            <a href="http://subversion.tigris.org" rel="external">Subversion</a> server, use one of the following commands.
        </p>
        <p>
            Download only the Cytoscape Web project:
            <pre class="ln- collapsed">svn checkout http://chianti.ucsd.edu/svn/cytoscapeweb/trunk/cytoscapeweb cytoscapeweb-read-only</pre>
        </p>
        <p> </p>
        <p>
            Download Cytoscape Web and the website, including the demo application:
            <pre class="ln- collapsed">svn checkout http://chianti.ucsd.edu/svn/cytoscapeweb/trunk cytoscapeweb-all-read-only</pre>
        </p>
    </div>

</div>