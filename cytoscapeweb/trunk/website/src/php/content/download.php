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
                echo $release_info;
            ?>
            
            <div class="description <?php echo ($first ? "latest" : ""); ?>"><?php echo $description; ?></div>
           
    <?php
        
            $first = false;
        }
    
    ?>

    <h1>Source Code</h1>
    <div class="description">
        <p>In order to download the latest Cytoscape Web source code from our 
           <a href="http://subversion.tigris.org">Subversion</a> server, use the following command:</p>
        <pre class="ln- collapsed">svn checkout http://chianti.ucsd.edu/svn/cytoscapeweb/trunk cytoscapeweb-read-only</pre> 
    </div>

</div>