<?php
    $content_style="side";
    
    // do not show versions older than one of these measures
    // version must satisfy both measures to be shown
    $oldest_date_to_show = strtotime("2001-01-01"); // oldest version to show by date (no version shown if older than this)  
    $oldest_version_to_show = "0"; // oldest version to show by version (no version shown if previous version to this)
    
    // do not show download links older than one of these measures (can just use preferred one)
     // download link must satisfy both measures to be shown
    $oldest_date_to_dl = strtotime("2001-01-01"); // oldest version to show by date (no version shown if older than this)
    $oldest_version_to_dl = "0"; // oldest version to show by version (no version shown if previous version to this)
    
    
    
    require_once("php/content/documentation/function.php");
    require_once("php/content/documentation/api.php");
?>