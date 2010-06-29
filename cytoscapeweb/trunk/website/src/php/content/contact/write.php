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
        
            <p>Please note that the <a href="http://groups.google.com/group/cytoscapeweb-discuss" rel="external">discussion group</a> is the preferred method of communication, as you will get a faster response there compared to here.</p>
            
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
	
	<h1>What can I write here?</h1>
	
	<p class="warning"><strong>Do not use this form to ask questions on how to use Cytoscape Web.</strong>  That information can be found in the <a href="/documentation">documentation</a> or asked in the <a href="http://groups.google.com/group/cytoscapeweb-discuss" rel="external">discussion group</a>.</p>
	
	<p>This form is a way for you to contact the developers of Cytoscape Web.  However, you will probably get a slower response to your query here as compared to the <a href="http://groups.google.com/group/cytoscapeweb-discuss" rel="external">discussion group</a>.</p>
	
	<p>If you have ideas on how to improve Cytoscape Web or you would like to report a bug, we would greatly appreciate your feedback.</p>
	
</div>