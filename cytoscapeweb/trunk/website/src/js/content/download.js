$(window).load(function(){

    var url = "" + $(window).attr("location");

    if( url.match(/.+#now/) ) {
    
        window.location.href = $("a.dl:first").attr("href");
    }
    
});