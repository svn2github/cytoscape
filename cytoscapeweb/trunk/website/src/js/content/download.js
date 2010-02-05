$(window).load(function(){

    var url = "" + $(window).attr("location");

    if( url.match(/.+#now/) ) {
        window.location.href = $("a.dl:first").attr("href");
    }
    
    
    $(".relase_details .bug_id").each(function() {
    	 var bug_id = $(this).text();
    	 var bug_link = '<a href="http://cbio.mskcc.org/cytoscape/bugs/view.php?id='+bug_id+'" rel="external" target="_blank">'+bug_id+'</a>';
    	 $(this).html(bug_link);
    });
    
    $(".relase_details").each(function() {
    	var html = $(this).html();
    	html = '<span class="like_link pre_plain_link">Release Notes</span>' +
    	       '<div class="notes">' + html + '</div>';
    	$(this).html(html);
    	
    	$(this).find(".bug_fixes").before('<h3>Fixed Issues:</h3>');
    	$(this).find(".other_changes").before('<h3>Other Changes:</h3>');
    });
    
    $(".relase_details .like_link:first-child").click(function() {
    	var notes = $(this).parent().find(".notes");
    	if (notes.is(":visible")) {
    		notes.hide();
    	} else {
    		notes.show();
    	}
    });
});