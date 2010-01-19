<?php

    // api.php gets a list of available api categories and loads them into the
    // $apis array
    //
    // use require_once($api->categories["name"]->files[index]) to populate $cls_info with the
    // api for that file
    
    require_once("php/content/documentation/function.php");
    
    $dir = "php/content/documentation/api";
    
    $apis = array();

    if (is_dir($dir)) {
        if ($dh = opendir($dir)) {

            while ( ($version_dir = readdir($dh)) !== false ) {
                if( !is_dir("$dir/$version_dir") || preg_match('/^\..*/', $version_dir)  ){
                    continue;
                }
                
                $api = new api();
                $api->version = $version_dir;
                $apis[$api->version] = $api;
                
                $date_file = fopen("$dir/$version_dir/.date", "r");
                if( $date_file ){
                    $date = fgets($date_file);
                    fclose($date_file);
                    $api->date = $date;
                }
                
                $release_info_file = fopen("$dir/$version_dir/.release_info", "r");
                if( $release_info_file ){
                	$release_info = "";
                	while ( ! feof($release_info_file) ) {
                    	$release_info = $release_info . "\n" . fgets($release_info_file);
                	}
                    fclose($release_info_file);
                    $api->release_info = $release_info;
                }
                
                if( $version_dirh = opendir("$dir/$version_dir") ){
                    while ( ($cat_dir = readdir($version_dirh)) !== false ) {
                        if( !is_dir("$dir/$version_dir/$cat_dir") || preg_match('/^\..*/', $cat_dir)  ){
                            continue;
                        }
                        
                        $cat = new category();
                        $cat->name = $cat_dir;
                        $api->categories[$cat->name] = $cat;
                        
                        if( @fopen("$dir/$version_dir/$cat_dir/.real_class", "r") ){
                            $cat->real_class = true;
                        }
                        
                        
                        if( $cat_dirh = opendir("$dir/$version_dir/$cat_dir") ){
                            while ( ($file = readdir($cat_dirh)) !== false ) {
                                if( !preg_match('/^.+\.php$/', $file) ){
                                    continue;
                                }
                            
                                $cat->files[] = "$dir/$version_dir/$cat_dir/$file";
                                
                                $cls_name = preg_replace('/\.php/', '', $file);
                                
                                $api->cls_name_to_cat_name[$cls_name] = $cat->name;
                            }
                            closedir($cat_dirh);
                        }
                        
                    }
                    closedir($version_dirh);
                    ksort($api->categories);
                }
                
            }
            closedir($dh);
            krsort($apis);
        }
    
    }

?>

