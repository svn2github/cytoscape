$(function(){
       
    $(".function_link.direct").add(".class_link.direct").click(function(){
        var matches = $(this).attr("href").match(/\#(.+)/);
        
        $("#content .right a[href=" + matches[0] + "]").click();
        
        return false;
    });
  
});