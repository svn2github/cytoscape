$(window).load(function(){

    var ul_root = $("<ul></ul>");
    $("#content .right .nav").append("<h1>Contents</h1>");
    $("#content .right .nav").append(ul_root); 
    var a_show_all = $("<a href=\"#\">Show all sections</a>");
    var li_show_all = $("<li></li>");
    ul_root.append(li_show_all);
    li_show_all.append(a_show_all);
     
    function create_level(ul, h) {
        var name = (h.attr("name") != undefined ? h.attr("name") : h.text());
        var id = (h.attr("id") != undefined ? h.attr("id") : "");
        var a = $('<a href="#' + id + '" name="' + name + '">' + name + '</a>');
        var li = $("<li></li>");
        var tag;
        var tags = "";
        
        h.each(function(){
            tag = this.tagName;
        });
        
        switch( tag.toLowerCase() ) {
            case "h1":
                tags = "h1";
                break;
            case "h2":
                tags = "h1, h2";
                break;
            case "h3":
                tags = "h1, h2, h3";
                break;
        }
        
        ul.append(li);
        li.append(a);
        
        a.click(function(){ 
            
            $("#next_section_link").unbind("click").remove();
            $("#prev_section_link").unbind("click").remove();
            
            h.hide().show();
            h.nextAll().show();
            h.nextAll(tags).hide();
            h.nextAll(tags).nextAll().hide();
            h.prevAll().hide();
            
                       
            var prev = h.prevAll(tags + ":first");
            var prev_name = (prev.attr("name") != undefined ? prev.attr("name") : prev.text());
            if( prev.size() > 0 ) {
                h.before("<div id=\"prev_section_link\"><a href=\"#\">Previous section: <em>" + prev_name + "</em></a></div>");
            }
            
            $("#prev_section_link").bind("click", function(){
                $("#content .right .selected").prev().find("a").trigger("click");
            });
            
            var next = h.nextAll(tags + ":first");
            var next_name = (next.attr("name") != undefined ? next.attr("name") : next.text());
            if( next.size() > 0 ) {
                next.before("<div id=\"next_section_link\"><a href=\"#\">Next section: <em>" + next_name + "</em></a></div>");
            }
            
            $("#next_section_link").bind("click", function(){
                $("#content .right .selected").next().find("a").trigger("click");
            });
            
            fix_height();
            
            return false;
        });
    }
    
    function fix_height() {
        if( $("#content .left").height() < $("#content .right").height() ) {
            $("#content .left").css( "min-height", $("#content .right").height() );
        }
    }
    
    var h1_ul = $("<ul></ul>");
    ul_root.append(h1_ul);
    $("#content .left h1").each(function(){
        create_level(h1_ul, $(this));
        
        return; // support only top-level for now
        
        var h2_ul = $("<ul></ul>");
        h1_ul.append(h2_ul);
        $(this).nextAll("h2").each(function(){
            create_level(h2_ul, $(this));   
            
            var h3_ul = $("<ul></ul>");
            h2_ul.append(h3_ul);
            $(this).nextAll("h3").each(function(){
                create_level(h3_ul, $(this)); 
            });
        });
    });
    
    a_show_all.click(function(){
         $("#content .left").children().show();
         $("#next_section_link").remove();
         $("#prev_section_link").remove();
         
         return false;
    });
    
    $("#content .right a").click(function(){
        $("#content .right li").removeClass("selected");
        $(this).parent("li").addClass("selected");
        $("#content .right").removeClass("scroll");
        $(window).scrollTop(0);
    });
    
    a_show_all.click();
    
    /* disable scrolling for now
    var content_position = $("#content .right .nav").offset().top;  
    $(window).scroll(function(){
        var scroll = $(window).scrollTop();

        if( scroll > content_position ) {
            $("#content .right").addClass("scroll");
        } else {
            $("#content .right").removeClass("scroll");
        }
        
    });
    
    */
    
    
    fix_height();
    
    var anchor_matches = window.location.href.match(/\#(.+)/);
    if( anchor_matches ){
        var match = anchor_matches[0];
        
        $("#content .right a[href=" + match + "]").click();
    }
});