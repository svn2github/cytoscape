<?php

/*
NOTE: Since this is an error page--not a normal page--a declaration for this PHP file cannot be 
used.

It's just an error page, so it's a good idea to keep it simple without additional CSS and JS.
*/

?>

<div class="text">

    <?php
        
        $this_page = ($_SERVER['HTTPS'] ? "https://" : "http://") . $_SERVER['SERVER_NAME'] . $_SERVER['REQUEST_URI'];
        
        $file_split = explode("/", $this_page);
        $file = $file_split[ count($file_split) - 1 ];
        
        $ref_page = $_SERVER['HTTP_REFERER'];
        $ref_split = explode("/", $ref_page);
        $ref_domain = $ref_split[ 2 ];
        
        $this_domain = $_SERVER['SERVER_NAME'];
    
    ?>

    <h1>Content not found</h1>
    
    <p>The address, <label><?php echo $this_page; ?></label>, does not exist.  So, the content you requested, <label><?php echo $file; ?></label>, could not be found.</p>
    
    <h1>What can I do?</h1>
    
    <?php if( $ref_page == "" ) { ?>
        <p>It looks like you may have entered the address of this page yourself.  It may be that the page you are looking for has been moved, or you accidentally specified an incorrect address.</p>
    <?php } else if( $this_domain != $ref_domain ) { ?>
        <form method="post" action="/contact">
            <input type="hidden" name="nature" value="bad link" />
            <input type="hidden" name="message" value="The link from <?php echo $ref_page; ?> to <?php echo $this_page; ?> is not working.  Please contact <?php echo $ref_domain ?> to get them to update their link." />
            <p>It looks like you came from <label><?php echo $ref_domain ?></label>, so it may be the case that the link from <label><?php echo $ref_domain ?></label> is not valid.  Their link may be wrong or outdated.  We would really appreciate it if you would be so kind as to <button class="link_button" type="submit">let us know</button> about the bad link so we can get <label><?php echo $ref_domain ?></label> to update their link.</p>
        </form>
    <?php } else { ?>
        <form method="post" action="/contact">
            <input type="hidden" name="nature" value="bad link" />
            <input type="hidden" name="message" value="The link from <?php echo $ref_page; ?> to <?php echo $this_page; ?> is not working.  Please fix the link." />
            <p>It looks like you got here by clicking an invalid link from this site.  We would really appreciate it if you would be so kind as to <button class="link_button" type="submit">let us know</button> about the bad link.</p>
        </form>
    <?php } ?>
    
    <p>Try to find the content that you are looking for by using the navigation links across the top of this page.</p>
    
    
    
</div>