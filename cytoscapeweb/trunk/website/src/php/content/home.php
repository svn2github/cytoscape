<div id="about">
    <div id="use_line">
        <span>Use Cytoscape Web to embed graphs in your site, like this:</span>
    </div>
    
    <div id="example">
        <div id="location"></div>
        <div id="loader"></div>
        <div id="viz"></div>
    </div>
    
</div>

<div id="side_links">

    <div class="link" id="download">
        <a href="/download#now"><div id="download_latest" class="button">Download latest version</div></a>
        <div id="version">
            Version <?php echo $version ?>
        </div>
        <div id="date">
            <?php
                $raw_date = strtotime( $date );
                echo date("j M Y", $raw_date);
            ?>
        </div>
    </div>
    
    <div class="link">
        <a href="/demo"><div id="live_demo" class="button">View live demo</div></a>
    </div>
    
    <div class="link">
        <a href="/documentation/tutorial"><div id="tutorial" class="button">View tutorial</div></a>
    </div>

    <div id="features">
        <label>Cytoscape Web is ...</label>
        <ul>
            <li>... a <em>reusable</em> component that allows you to <em>embed graphs</em> within HTML documents.</li>
            <li>... easily integrated in HTML via its <em>Javascript API</em>.</li>
            <li>... <em>customisable</em> in what <em>data</em> it loads&mdash;<em>XGMML</em> and <em>GraphML</em> are supported&mdash;and how it <em> visually displays</em> that data.</li>
            <li>... an <em>open source</em> project to which anyone can contribute.</li>
        </ul>
    </div>
</div>