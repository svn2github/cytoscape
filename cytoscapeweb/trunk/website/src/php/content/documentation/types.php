<div class="left api">

<?php

    $tag = $page_link;

    foreach($cls_info->funcs as $fn){
    
        if( ! in_array($tag, $fn->tags) ) {
            continue;
        }
            
        // title
        echo "<h1 name=\"$fn->name\">";
        echo $fn->name;
        echo ($fn->type ? " <span class=\"field_type\">(of type $fn->type)</span>" : "");
        echo "</h1>";
        
        // description
        echo "<div class=\"description\">$fn->description</div>";
        
        // members
        
        if( count($fn->params) > 0 ) {
            echo "<label>Members</label>";
            
            foreach($fn->params as $param){
                echo "<div class=\"parameter\">";
                    echo ($param->optional ? "[ " : "");
                    echo "<em>$param->name</em>";
                    echo ($param->optional ? " ]" : "");
                    echo ($param->type ? " <span class=\"type\">($param->type)</span>" : "");
                    echo ($param->description ? " : $param->description" : "");
                echo "</div>";
            }
        }
    
    
    }

?>

</div>