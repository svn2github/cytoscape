$(function(){

    var all = $("#email").add("#name").add("#nature").add("#message");
    var check_as_email = $("#email");
    var check_as_non_blank = $("#name").add("#nature").add("#message");
    
    $(".example").each(function(){
        var input = $(this).prevAll(":input:first");
        
        if( !input.attr("disabled") ) {
            $(this).show();
        }
        
        $(this).find("a").click(function(){
            
            input.val( $(this).text() );
            input.trigger("validate");
            
            return false;
        });
    });
               
    all.each(function(){
        $(this).before("<div class=\"ui-validation-completion\"></div>");
    });
    
    
    $("#submit").attr("disabled", "true");
    
    check_as_email.each(function(){
        var label = $(this).prevAll("label:first");
        var completion = $(this).prevAll(".ui-validation-completion:first");
        
        $(this).validate({
            completionIcon: completion,
            label: label,
            valid: function(str){
                return str.match( /^(([a-z]|[A-Z])+)(@)(([a-z]|[A-Z])+)(\.)(([a-z]|[A-Z])+)$/ );
            },
            errorMessage: function(str){
                if( str == "" ){
                    return $("#error_msg .empty").text();
                } else {
                    return $("#error_msg .invalid").text();
                }
            },
            validateOnLoad: true
        });
    });
    
    check_as_non_blank.each(function(){
        var label = $(this).prevAll("label:first");
        var completion = $(this).prevAll(".ui-validation-completion:first");
        
        $(this).validate({
            completionIcon: completion,
            label: label,
            valid: function(str){
                return str != "";
            },
            errorMessage: function(str){
                return $("#error_msg .empty").text();
            },
            validateOnLoad: true
        });
    });
    
    
    
    all.bind("validate", function(){
    
        var done = true;
        $(".ui-validation-completion").each(function(){
            if( ! $(this).hasClass(".ui-validation-complete") ) {
                done = false;
            }
        });
        
        if( done ) {
            $("#submit").removeAttr("disabled");
        } else {
            $("#submit").attr("disabled", "true");
        }
    
    });
    
});