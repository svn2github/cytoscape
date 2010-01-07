$(function(){

    var TIME_BEFORE_SHOWING_LOADER = 500;

    function open(fn) {
        fn.addClass("selected").removeClass("unselected");
        fn.nextAll(".details:first").addClass("selected").removeClass("unselected");
        fn.nextAll(".description:first").addClass("selected").removeClass("unselected");
    }
    
    function close(fn) {
        fn.removeClass("selected").addClass("unselected");
        fn.nextAll(".details:first").removeClass("selected").addClass("unselected");
        fn.nextAll(".description:first").removeClass("selected").addClass("unselected");
    }

    $("#example").hide().append("<iframe></iframe>");
    
    $("h1").each(function(){
        close( $(this) );
    }).click(function(){
        var fn = $(this);
        var should_open = fn.hasClass("unselected");
    
        $("h1").each(function(){
            close( $(this) );
        });
        
        if( should_open ) {
            open( fn );
            
            $("#example").hide();
            
            var timeout = null;
            function show_loader(){
                $("#example").fadeIn().addClass("loading");
                clearTimeout(timeout);
                timeout = null;
            }
            timeout = setTimeout(show_loader, TIME_BEFORE_SHOWING_LOADER);
            var offset = fn.offset().top - fn.parent().offset().top;
            $("#example").css("margin-top", offset);
            $("#example").height(  $(".left").height() - offset );
            $("#example").find("iframe").attr("src", "/get/documentation/example?function=" + fn.attr("id"));
            $("#example").find("iframe").load(function(){
                clearTimeout(timeout);
                $("#example").removeClass("loading").fadeIn();
            });
            
        } else {
            $("#example").hide();
        }
    });

});