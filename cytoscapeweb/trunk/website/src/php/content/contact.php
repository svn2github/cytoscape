<?php 

    $submitted = $_POST["submitted"]; 
    $name = $_POST["name"];
    $email = $_POST["email"]; 
    $nature = $_POST["nature"];
    $message = $_POST["message"]; 

    $name_empty = ($name == "");
    $email_empty = ($email == "");
    $email_invalid = ( !preg_match('/^(([a-z]|[A-Z])+)(@)(([a-z]|[A-Z])+)(\.)(([a-z]|[A-Z])+)$/', $email) );
    $nature_empty = ($nature == "");
    $message_empty = ($message == "");
    
    $parameters_valid = false;

    if( $submitted ) {
        if(!$name_empty && !$email_empty && !$email_invalid && !$nature_empty && !$message_empty) {
            $headers = 'From: ' . "$name <$email>" . "\r\n" .
                'Reply-To: ' . $email . "\r\n" .
                'X-Mailer: PHP/' . phpversion();
            mail($address_to_send_email, $nature, $message, $headers);
            $parameters_valid = true;
        }
    }
?>

<div id="error_msg">
    <?php 
        foreach($error_msg as $e => $m) {
            echo "<div class=\"$e\">$m</div>";
        }
    ?>
</div>

<div class="left">

        <?php 
        $complete = $submitted && $parameters_valid;
        if( $complete ) { ?>
        
             <h1>Thank you!</h1>
        
            <p>Thank you for your feedback!  The message as it was sent is as follows.</p>
               
        <?php } else { ?>
    
            <h1>Contact form</h1>
        
            <p>If you have any questions or comments, please contact us via the form below.</p>
            
        <?php } ?>
    
        <form method="post" action="<?php echo $PHP_SELF; ?>">
    
            <label>Name <span class="ui-validation-error-message"></span></label>
            <input id="name" name="name" type="text" <?php echo ($complete ? "disabled=\"true\"" : ""); ?> <?php echo "value=\"$name\""; ?> <?php echo ($submitted && $name_empty ? "class=\"error\"" : ""); ?> />
            
            <label>Email address <span class="ui-validation-error-message"><?php
                if($submitted && $email_empty) {
                    echo $error_msg["empty"]; 
                } else if($submitted && $email_invalid) {
                    echo $error_msg["invalid"];
                }
            ?></span></label>
            <input id="email" name="email" type="text" <?php echo ($complete ? "disabled=\"true\"" : ""); ?> <?php echo "value=\"$email\""; ?> <?php echo ($submitted && ($email_empty || $email_invalid) ? "class=\"error\"" : ""); ?> />
            
            <label>Nature of message <span class="ui-validation-error-message"></span></label>
            <input id="nature" name="nature" type="text" <?php echo ($complete ? "disabled=\"true\"" : ""); ?> <?php echo "value=\"$nature\""; ?> <?php echo ($submitted && $nature_empty ? "class=\"error\"" : ""); ?> />
            <div class="example">Examples: <a href="#">software bug</a>, <a href="#">feedback</a>, <a href="#">feature request</a></div>
            
            <label>Message <span class="ui-validation-error-message"></span></label>
            <textarea id="message" name="message" <?php echo ($complete ? "disabled=\"true\"" : ""); ?> <?php echo ($submitted && $message_empty ? "class=\"error\"" : ""); ?>><?php echo $message; ?></textarea>
            
            <input type="hidden" id="submitted" name="submitted" value="true" />
            
            <button type="submit" <?php echo ($complete ? "class=\"hidden\"" : ""); ?> id="submit" class="emphasis">Send message</button>
        
        </form>
  
</div>

<div class="right">

    <h1>Guidelines</h1>

    <h2>Reporting software bugs</h2>
    
    <p>The best way to report a bug to us is to <a href="http://cbio.mskcc.org/cytoscape/bugs">use our bug tracker</a>.  However, if you find it difficult to use the bug tracker, feel free to use the contact form on this page to contact us about the bug.</p>
    
    <p>Though we greatly appreciate it when you point out a bug, several pieces of information are required to fix it, including</p>
    
    <ul>
        <li>how to reproduce the bug;</li>
        <li>what part of the API the bug affects;</li>
        <li>a description of the bug as compared to the behaviour you expected.</li>
    </ul>
    
    <h2>Other feedback</h2>
    
    <p>Any other feedback that you provide is greatly appreciated.  To make sure that we get the most out of your feedback, please make sure to provide your name and email address so we can get back to you if need be.</p>
    

</div>