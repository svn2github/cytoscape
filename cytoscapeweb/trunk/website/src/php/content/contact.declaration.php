<?php
    
    include_js("/js/jquery/plugins/jquery.validate.js");
    
    $content_style="half_and_half";
    
    $email_file = "php/content/contact.email.php";
    $email_var = "\$address_to_send_email";
    
    if ( !(@include("php/content/contact.email.php")) ) {
        echo "<p class='ui-validation-error-message'>$email_file: file not found; please create the file and define $email_var</p>";
    } else if( !preg_match( '/^([a-z|A-Z])+\@([a-z|A-Z])+\.([a-z|A-Z])+$/', $address_to_send_email) ){
        echo "<p class='ui-validation-error-message'>$email_file: $email_var was not properly defined (it should be a valid email address)</p>";
    }
    
    // error messages
    $error_msg = array(
        "empty" => "must not be empty",
        "invalid" => "must be complete and valid"
    );

?>