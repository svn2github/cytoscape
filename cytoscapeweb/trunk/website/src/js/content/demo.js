/*
  This file is part of Cytoscape Web.
  Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
    - Agilent Technologies
    - Institut Pasteur
    - Institute for Systems Biology
    - Memorial Sloan-Kettering Cancer Center
    - National Center for Integrative Biomedical Informatics
    - Unilever
    - University of California San Diego
    - University of California San Francisco
    - University of Toronto

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
*/
$(function(){

    // delays
    var DELAY_BEFORE_HIDING_LOADERS = 250;
    var DELAY_BEFORE_HIDING_SAVE_MENU = 250;
    var VALIDATION_DELAY = 1000;
    var MESSAGE_HIDE_SPEED = 0;
    var FILTER_DELAY_ON_SLIDER = 25;
    var FILTER_STEPS_ON_SLIDER = 100;
    
    // sizes
    var SIDE_BAR_MIN_SIZE = 350;
    var SIDE_BAR_MAX_SIZE = 600;
    var SIDE_BAR_RESIZER_GRIP_SIZE = 16;
    
    // versions
    var MIN_FLASH_VERSION = 10;
    
    // Path util
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    function path(str){
        function has_slash(str){
            return str.substr(0, 1) == "/";
        }
    
        if( window.location.protocol == "file:" || window.location.protocol != "http:" ){
            if( has_slash(str) ){
                return str.substr(1);
            } else {
                return str;
            }
        } else {
            if( has_slash(str) ){
                return str;
            } else {
                return "/" + str;
            }
        }
    }
    
    // Loading and error
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    function show_msg( options ){
        var options = $.extend({
            type: "info",
            showCorner: false
        }, options);
        
        var obj = options.target;
    
        var err = $(  "<div class=\"" + options.type + "_screen screen\">\
                            " + (options.showCorner ? ((options.cornerLink ? '<a href="' + options.cornerLink + '">' : "") + "<div class=\"corner\"><div class=\"icon\"></div><span>" + options.cornerText + "</span></div>" + (options.cornerLink ? '</a>' : "")) : ("")) + "\
                            <div class=\"notification\">\
                                <div class=\"icon\"></div>\
                                <div class=\"heading\">" + (options.heading || "") + "</div>\
                                <div class=\"message\">" + (options.message || "") + "</div>\
                            </div>\
                        </div>");
        
        $(obj).append(err);
    
        err.find(".corner").click(function(){
            hide_msg( options );
        });
    }
    
    function hide_msg( options ){
        var obj = options.target;
        
        $(obj).find( (options.type ? "." + options.type + "_screen" : ".screen") ).fadeOut(MESSAGE_HIDE_SPEED, function(){
            $(this).remove();
        });
    }
    
    function show_msg_on_tabs( options ){
        show_msg( $.extend( { target: $("#side") }, options ) );
    }
    
    function hide_msg_on_tabs( options ){
        hide_msg( $.extend( { target: $("#side") }, options ) );
    }
    
    function show_msg_on_all( options ){
        show_msg_on_tabs(options);
        show_msg( $.extend( { target: $("#cytoweb") }, options ) );
    }
    
    function hide_msg_on_all( options ){
        hide_msg_on_tabs(options);
        hide_msg( $.extend( { target: $("#cytoweb") }, options ) );
    }
    
    // Detect flash version
    ////////////////////////////////////////////////////////////////////////////////////////////////
   
    if( !FlashDetect.versionAtLeast(MIN_FLASH_VERSION) ){
        if( $("#content").length > 0 ){
            
            $("#content .left").html('<h1>A newer version of Flash is required to view the demo</h1>\
            <p>You must install <a href="http://get.adobe.com/flashplayer">Flash ' + MIN_FLASH_VERSION + '</a> or newer to view the demo.\
            Please install a newer version of Flash and reload this page.</p>');
            
            $("#content .right").html('<h1>What if my broswer does not support Flash?</h1>\
            <p>Please consider <a href="http://mozilla.com">upgrading your browser</a>.</p>');
        } else {
            show_msg({
                type: "error",
                target: $("body"),
                message: '<a href="http://get.adobe.com/flashplayer">Flash ' + MIN_FLASH_VERSION + '</a> or newer must be installed for this demo to work properly.',
                showCorner: true,
                cornerText: "Back to site" 
            });
        }
        return; // no more demo
    }
   
    // [layout] Layout set up and override
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    $("body").html('\
                        <div id="header" class="slice">\
                            <a href="/"><div id="logo"></div></a>\
                            <div class="text">Try out the features of Cytoscape Web to see how you would use it in your site!</div>\
                        </div>\
                        <div id="cytoweb">\
                            <div id="menu"></div>\
                            <div id="cytoweb_container"></div>\
                        </div>\
                        <div id="side">\
                            <ul>\
                                <li><a id="examples_link" href="#examples"><span>Examples</span></a></li>\
                                <li><a id="vizmapper_link" href="#vizmapper"><span>Visual style</span></a></li>\
                                <li><a id="filter_link" href="#filter"><span>Filter</span></a></li>\
                                <li><a id="info_link" href="#info"><span>Properties</span></a></li>\
                            </ul>\
                            <div class="ui-layout-header">\
                                <div id="filter_header" class="header"></div>\
                            </div>\
                            <div class="ui-layout-content">\
                                <div id="examples" class="content"></div>\
                                <div id="vizmapper" class="content"></div>\
                                <div id="filter" class="content"></div>\
                                <div id="info" class="content"></div>\
                            </div>\
                        </div>\
    ');
    
    show_msg({
        type: "loading",
        target: $("body"),
        message: "Please wait a moment while the Cytoscape Web demo loads.  (It's worth the wait.)",
        heading: "Loading",
        showCorner: true,
        cornerText: "Go back",
        cornerLink: "javascript:window.history.back()"
    });
    
    // Flash steals events, and this is a problem with things in the side bar (e.g. resizing).
    // When resizing, for example, Flash will steal the mouse up event needed to finish resizing.
    // So, we put an overlay to get the events, so Flash doesn't steal it.  Show the overlay to
    // prevent event stealing, but hide it when finished or it will block the page.
    $("body").append('<div id="overlay"></div>');
    
       
    $("body").addClass("demo");

    var side_min_size = SIDE_BAR_MIN_SIZE;
    var side_max_size = SIDE_BAR_MAX_SIZE;
    var grip_size = SIDE_BAR_RESIZER_GRIP_SIZE;
    var layout = $("body").layout({
       
        defaults: {
            size: "auto",
            resizable: true,
            fxName: "fade",
            fxSpeed: "normal",
            spacing_open: 0,
		    togglerLength_open: 0,
		    contentIgnoreSelector: "span"

        },
        
        north: {
            paneSelector: "#header"
        },
        
        center: {
            paneSelector: "#cytoweb"
        },
        
        east: {
            paneSelector: "#side",
            minSize: parseInt(side_min_size),
            maxSize: parseInt(side_max_size),
            spacing_open: parseInt(grip_size),
            resizable: true,
            closable: true,
            resizerTip: "",
            onresize_start: function(){
                $("#side").add("#cytoweb").addClass("resizing");
                $("#overlay").show();
                cytoweb_layout.resizeAll();
            },
            onresize_end: function(){
                $("#side").add("#cytoweb").removeClass("resizing");
                $("#overlay").hide();
                cytoweb_layout.resizeAll();
            },
            onresize: function(){
                cytoweb_layout.resizeAll();
            }

        }

    });
    
    
    // create tabs
    $("#side").tabs({
        show: function(event, ui){
            // show header for selected tab
            
            var panel_id = $(ui.panel).attr("id");
            $("#side .header").not("#" + panel_id + "_header").hide();
            $("#" + panel_id + "_header").show();
            
            layout.resizeContent("east"); 
        }
    });



    // [open] Create examples and utility function to open new graphs
    ////////////////////////////////////////////////////////////////////////////////////////////////
       
    // utility for opening a graph
    function open_graph(opt){
        var description;
        
        options = opt;
        
        if(opt.name) {
            description = opt.name;
        } else if(opt.url){
            var partsOfFile = opt.url.split("/");
            description = partsOfFile[partsOfFile.length-1];
        }
        
        // we only need show this msg if the initial one that covers the whole page doesn't exist
        
        hide_msg({
            target: $("body")
        });
        
        if( $("body .screen").length <= 0 ){
            show_msg_on_all({
                type: "loading",
                message: "Please wait while the network data loads.",
                heading: description
            });
            
        }
        
        if( options.url != undefined ) {
            $.ajax({
                url: options.url,
                
                success: function(data){
                    opt.network = data;
                    $("#cytoweb_container").cw().draw( opt );
                },
                
                error: function(){
                    hide_msg({ target: $("body") });
                    
                    show_msg({
                        type: "error",
                        target: $("body"),
                        message: "The file you specified could not be loaded.  Please go back to your previous file.",
                        heading: "File not found",
                        showCorner: true,
                        cornerText: "Back to previous file"
                    });
                }
            });
        } else {
            $("#cytoweb_container").cw().draw( opt );
        }
        
    } 
    
    // example graphs
    var default_options = {
        panZoomControlVisible: true,
		edgesMerged: false,
		nodeLabelsVisible: true,
		edgeLabelsVisible: false,
		nodeTooltipsEnabled: true,
		edgeTooltipsEnabled: true,
		swfPath: path("swf/CytoscapeWeb"),
		flashInstallerPath: path("swf/playerProductInstall"),
		useProxy: false
    };
    
    var example = {
        
        shapes: {
            name: "Shapes example",
            description: "A graph that contains all possible shapes for nodes and arrows",
            url: path("file/example_graphs/sample1.graphml"),
            visualStyleName: "Shapes",
            visualStyle: GRAPH_STYLES["Shapes"],
            layout: "ForceDirected",
            nodeLabelsVisible: false
        },
        
        peterson: {
            name: "Petersen example",
            description: "The Petersen graph",
            url: path("file/example_graphs/petersen.xgmml"),
            visualStyleName: "Circles",
            visualStyle: GRAPH_STYLES["Circles"],
            nodeLabelsVisible: false
        },
        
        disconnected: {
            name: "Disconnected example",
            description: "A graph that contains several, disconnected components",
            url: path("file/example_graphs/sample2.graphml"),
            visualStyleName: "Dark",
            visualStyle: GRAPH_STYLES["Dark"],
            layout: "ForceDirected",
            nodeLabelsVisible: false
        },

        genetics: {
            name: "Genetics example",
            description: "A modified graph from GeneMANIA with different visual styles",
            url: path("file/example_graphs/sample3.graphml"),
            visualStyleName: "Diamonds",
            visualStyle: GRAPH_STYLES["Diamonds"],
            layout: "ForceDirected",
            nodeLabelsVisible: false
        },
        
        pathguide: {
            name: "Pathguide example",
            description: "An interaction of databases exported from Cytoscape",
            url: path("file/example_graphs/sample4.xgmml"),
            visualStyleName: "Cytoscape",
            visualStyle: GRAPH_STYLES["Cytoscape"],
            layout: "Preset",
            nodeLabelsVisible: true
        }

    };
    
    // first example is default
    for(var opt in example) {
        var options = $.extend(default_options, example[opt]);
        break;
    }
    
    $("*").live("available", function(){
        $(this).data("available", true);
    });
    
    $("*").live("unavailable", function(){
        $(this).removeData("available");
    });
    
    // create cytoweb
    $("#cytoweb_container").cytoscapeweb(options);
    
    // call back for when the graph is opened and fully loaded
    $("#cytoweb_container").cw().ready(function(){
        $("#cytoweb_container").cw().addListener("error", function(err){
            
            hide_msg({ target: $("body") });
            
            show_msg({
                target: $("#cytoweb_container").add("#side"),
                type: "error",
                heading: err.value.name,
                message: err.value.msg + ( err.value.id != undefined ? " (id = " + err.value.id + ")" : "" )
            });
            
            show_msg({
                target: $("#side"),
                type: "info",
                heading: "Area unavailable",
                message: "This area is unavailable when the graph file can not be loaded."
            });
        });
    
        $("#cytoweb_container").trigger("available");
    }); // end cl load
    
    create_menu();
    $(window).trigger("resize");
    create_open();
    create_save();
    
    $("#cytoweb_container").bind("available", function(){
        update_background();
        update_menu();
        update_info(); 
        update_vizmapper();
        update_filter();
       
        hide_msg({
            type: "loading",
            target: $("body")
        });
        
        $(window).trigger("resize");
    });

    open_graph(options);
    
    // [menu] Create the menu above Cytoscape Web
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Update the menu after the graph has loaded
    function create_menu(){
        $("#menu").children().remove(); // remove old menu if needed (we don't want two after a redraw)
        $("#menu").append(      '<ul>\
                                    <li id="save_file"><label>Save file</label></li>\
                                    <li id="open_file"><label>Open file</label><span id="file_importer"></span></li>\
                                    \
                                    <li><label>Style</label>\
                                        <ul>\
                                            <li id="merge_edges" class="ui-menu-checkable"><label>Merge edges</label></li>\
                                            <li id="show_node_labels" class="ui-menu-checkable"><label>Show node labels</label></li>\
                                            <li id="show_edge_labels" class="ui-menu-checkable"><label>Show edge labels</label></li>\
                                            <li>\
                                                <label>Visualisation</label>\
                                                <ul id="visual_style" class="ui-menu-one-checkable">\
                                                </ul>\
                                            </li>\
                                        </ul>\
                                    </li>\
                                    \
                                    <li><label>Layout</label>\
                                        <ul>\
                                            <li id="recalculate_layout"><label>Recalculate layout</label></li>\
                                            <li>\
                                                <label>Mechanism</label>\
                                                <ul id="layout_style" class="ui-menu-one-checkable">\
                                                </ul>\
                                            </li>\
                                        </ul>\
                                    </li>\
                                </ul>\
                                ');
    
        // add layout styles
        var layout_names = new Array();
        layout_names["ForceDirected"] = "Force directed";
        layout_names["Circle"] = "Circle";
        layout_names["CircleTree"] = "Circle tree";
        layout_names["Radial"] = "Radial";
        layout_names["Tree"] = "Tree";

        // add layouts to menu
        for(var i in layout_names) {
            var layout_id = i;
            var layout_name = layout_names[layout_id];
            $("#layout_style").append("<li class=\"ui-menu-checkable\" layout_id=\"" + layout_id + "\"><label>" + layout_name + "</label></li>");
        }
        
        // add visual styles to menu (styles predefined in demo_styles.js)
        var viss = GRAPH_STYLES;
        $("#visual_style").append("<li class=\"ui-menu-checkable\" id=\"custom_visual_style\"><label>Custom</label></li>");
        for(var i in viss){
            var vis_name = i;
            var vis = viss[i];
            $("#visual_style").append("<li class=\"ui-menu-checkable\"><label>" + vis_name + "</label></li>");
        }
        
        
        // add examples to menu
        /*
        for(var i in example){
            var id = i;
            var name = example[i].name;
            $("#open_example").append("<li example_id=\"" + id + "\"><label>" + name + "</label></li>");
        }
        */  
             
        // create the menu and add handlers for when items are selected
        $("#menu").menu({
        	menuItemMaxWidth: 180,
            onMenuItemClick: function(li){
                switch( li.attr("id") || li.parent().attr("id") ) {
                case "layout_style":
                    $("#cytoweb_container").cw().layout( li.attr("layout_id") );
                    break;
                    
                case "visual_style":
                    $("#cytoweb_container").cw().visualStyle( viss[ li.text() ] );
                    update_background();
                    
                    show_msg_on_tabs({
                        type: "loading",
                        message: "Please wait while the style is updated."
                    });
                    
                    $.thread({
                        worker: function(){
                            update_vizmapper();
                            
                            hide_msg_on_tabs({
                                type: "loading"
                            });
                        }
                    });
                    
                    break;
                
                case "recalculate_layout":
                	var layout = $("#layout_style .ui-menu-checked").parent().attr("layout_id");
                    $("#cytoweb_container").cw().layout(layout);
                    break;
                
                case "open_example":
                    var ex = example[li.attr("example_id")];
                    open_graph(ex);
                    break;
                }
            },
           
                       
            onMenuItemCheck: function(li){
                switch( li.attr("id") ) {
                case "show_node_labels":
                    $("#cytoweb_container").cw().nodeLabelsVisible(true);
                    break;
                case "show_edge_labels":
                	$("#cytoweb_container").cw().edgeLabelsVisible(true);
                	break;
                case "merge_edges":
                    $("#cytoweb_container").cw().edgesMerged(true);
                    break;
                }
            },
            
            onMenuItemUncheck: function(li){
                switch( li.attr("id") ) {
                case "show_node_labels":
                    $("#cytoweb_container").cw().nodeLabelsVisible(false);
                    break;
                case "show_edge_labels":
                	$("#cytoweb_container").cw().edgeLabelsVisible(false);
                	break;
                case "merge_edges":
                    $("#cytoweb_container").cw().edgesMerged(false);
                    break;
                }
            }
        });
        
        $("#save_file").click(function(){
            show_save_menu();
        });
        
        // menu should not span
        var last_top_lvl_item = $("#menu > ul > li:last");
        $("#menu").css( "min-width", last_top_lvl_item.offset().left + last_top_lvl_item.outerWidth(true) );
    }
    
    
    function create_open(){
        var options = {
                swfPath: path("swf/Importer"),
                flashInstallerPath: path("swf/playerProductInstall"),
                data: function(data){
					var new_graph_options = {
						network: data.string,
					    name: data.metadata.name,
					    description: "",
					    visualStyle: GRAPH_STYLES["Default"],
					    layout: "ForceDirected",
					    nodeLabelsVisible: true
					};
					open_graph(new_graph_options);
				},
	            ready: function(){
	                $("#open_file").trigger("available");
	            },
	            typeFilter: function(){
	                return "*.xgmml;*.graphml;*.xml";
	            },
	            typeDescription: function(){
	            	return "Network file";
	            }
            };
            
        new org.cytoscapeweb.demo.Importer("file_importer", options);
    }
    
    function show_save_menu(){
        hide_msg_on_tabs({
            type: "info"
        });
        
        show_msg_on_tabs({
            type: "info",
            message: "This area will be available again when you finish up saving and go back to the network."            
        });
        
        $("#cytoweb_container").children().not(".save_screen").addClass("hidden");
        $("#cytoweb").find(".save_screen").removeClass("hidden");
    }
    
    function hide_save_menu(){
        $("#cytoweb_container").children().not(".save_screen").removeClass("hidden");
        $("#cytoweb").find(".save_screen").addClass("hidden");

        hide_msg_on_tabs({
            type: "info"
        });
    }
    
    function create_save(){
        var parent = $("#cytoweb");
    
        function default_file_name(extension){
            var d = new Date();
            
            function pad(num){
                if( num < 10 ) {
                    return "0" + num;
                }
                return num;
            }
            
            return "network_" + d.getFullYear() + "." + pad(d.getMonth()+1) + "." + pad(d.getDay()) + "_" + pad(d.getHours()) + "." + pad(d.getMinutes()) + "." + extension;
        }
        
        
        parent.find(".save_screen").remove();
        parent.append("\
            <div class=\"save_screen\">\
                <div class=\"corner\"><span>Back to network</span><div class=\"icon\"></div></div>\
                <div class=\"selections\">\
                    <div class=\"description\">Select a file type to save your file.</div>\
                </div>\
            </div>");
        
        parent.find(".save_screen").find(".corner").click(function(){
            hide_save_menu();
        });
        
        function hide(){
            parent.find(".save_screen").addClass("hidden");
            
            show_msg({
                type: "loading",
                target: parent,
                message: "Please wait while your file is prepared.",
                heading: "Preparing"
            });
        }
        
        function show(){
            parent.find(".save_screen").removeClass("hidden").fadeIn();
            
            hide_msg({
                target: parent
            });
        }

        function make_selection(fn, title, description, base64){
        	var id = "exporter_" + fn;
        	
        	parent.find(".save_screen").find(".selections").append("\
                <div class=\"selection\" id=\"save_" + fn + "\">\
                    <div class=\"icon\"></div>\
                    <div class=\"description\"><label>" + title + "</label>\
                        <span>" + description + "</span></div>\
                    <div id=\""+id+"\"></div>\
                </div>\
            ");
            
            var options = {
                    swfPath: path("swf/Exporter"),
                    flashInstallerPath: path("swf/playerProductInstall"),
                    base64: base64,
                	data: function(){
                		return $("#cytoweb_container").cw()[fn]();
                    },
                    fileName: function() {
                    	return default_file_name(fn);
                    },
		            ready: function() {
		            	$("#"+id).trigger("available");
                    }
                };
                
            new org.cytoscapeweb.demo.Exporter(id, options);
        }
        
        make_selection(
            "xgmml",
            "XGMML",
            "eXtensible Graph Markup and Modeling Language",
            false
        );
        make_selection(
            "graphml",
            "GraphML",
            "Graph Markup Language",
            false
        );
        make_selection(
            "pdf",
            "PDF",
            "Vector Image",
            true
        );
        make_selection(
    		"png",
    		"PNG",
    		"Bitmap Image",
    		true
        );
        
        hide_save_menu();
    }
    
    
    
    function update_menu(){
        // add initial state of check marks
        var check = {};
        check["merge_edges"] = $("#cytoweb_container").cw().edgesMerged();
        check["show_node_labels"] = $("#cytoweb_container").cw().nodeLabelsVisible();
        check["show_edge_labels"] = $("#cytoweb_container").cw().edgeLabelsVisible();
        
        for( var i in check ){
            var id = i;
            var checked = check[i];
            
            if(checked) {
                $("#" + id).find(".ui-menu-check-icon").addClass("ui-menu-checked");
            } else {
                $("#" + id).find(".ui-menu-check-icon").removeClass("ui-menu-checked");
            }
        }
               
        // add initial state of one check marks
        
        $("#layout_style").find(".ui-menu-check-icon").removeClass("ui-menu-checked");
        $("#layout_style").find("[layout_id=" + $("#cytoweb_container").cw().layout() + "]").find(".ui-menu-check-icon").addClass("ui-menu-checked");
        
        $("#visual_style").find(".ui-menu-check-icon").removeClass("ui-menu-checked");
        $("#visual_style").find("li:contains(" + options.visualStyleName + ")").find(".ui-menu-check-icon").addClass("ui-menu-checked");
        
        $("#menu").trigger("available");
    }
    
    // [cytoweb] Cytoscape Web area
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    // recalculate cytoweb layout
    
    var cytoweb_layout = $("#cytoweb").layout({
        defaults: {
            size: "auto",
            resizable: true,
            fxName: "slide",
            fxSpeed: "normal",
            spacing_open: 0,
            togglerLength_open: 0
        },
        
        north: {
            paneSelector: "#menu",
            showOverflowOnHover: true
        },
        
        center: {
            paneSelector: "#cytoweb_container"
        }
        
    });
    
   
    // update background to match cytoweb component
    function update_background(){
        $("#cytoweb").css("background-color", $("#cytoweb_container").cw().visualStyle().global.backgroundColor );
    }
    
    
    
    
    // [info] Info for selected objects
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    // Update the info tab with selection information after the graph has loaded
    function update_info(){
        
        update();
    
        $("#cytoweb_container").cw().addListener("select", "nodes", function(){
            update();
        });
        
        $("#cytoweb_container").cw().addListener("deselect", "nodes", function(){
            update();
        });
        
        $("#cytoweb_container").cw().addListener("select", "edges", function(){
            update();
        });
        
        $("#cytoweb_container").cw().addListener("deselect", "edges", function(){
            update();
        });
        
        $("#cytoweb_container").cw().addListener("dblClick", "nodes", function(){
            $("#info_link").click();
        });
        
        $("#cytoweb_container").cw().addListener("dblClick", "edges", function(){
            $("#info_link").click();
        });
        
        $("#info_link").bind("click", function(){
            update();
        });
        
        function update(){
            var edges = $("#cytoweb_container").cw().selected("edges");
            var nodes = $("#cytoweb_container").cw().selected("nodes");
            var container = $("#info");
            
            container.html(""); // clear info area
            
            function print_selection(group, name){
                var headings = [];
                
                var half = $("<div class=\"half\"></div>");
                container.append(half);
            
                var section = $("<div class=\"section\"></div>");
                half.append(section);
            
                section.append("<h1>" + name + "</h1>");
                
                if( group.length > 0 ) {
                    var table = $("<table class=\"tablesorter\"></table>");
                    section.append(table);
                    
                    var thead = $("<thead></thead>");
                    table.append(thead); 
                    
                    var thead_row = $("<tr></tr>");
                    thead.append(thead_row);
                    for(var j in group[0].data){
                        headings.push(j);
                    }
                    headings.sort();
                    for(var j in headings){
                        var heading = headings[j];
                        thead_row.append("<th><label>" + ("" + heading).replace(/(\s)/g, "&nbsp;") + "</label></th>");
                    }
                    
                    var tbody = $("<tbody></tbody>");
                    table.append(tbody);
                    
                    for(var i in group){
                        var data = group[i].data;
                        var row = $("<tr name=\"" + data.id + "\"></tr>");
                        tbody.append(row);
                        
                        for(var j in headings){
                            var param_name = headings[j];
                            var param_val = data[param_name];
                            
                            var val = ("" + param_val).replace(/(\s)/g, "&nbsp;");
                            var entry = $("<td><code>" + val + "</code></td>");
                            row.append(entry);
                        }
                    }
                    
                    
                    
                } else {
                    section.append("<p>No " + name.toLowerCase() + " are selected.</p>");
                }
                
            }
            
            print_selection(nodes, "Nodes");
            print_selection(edges, "Edges");
            

            $("#info").find(".tablesorter").tablesorter();

        }
        
        $("#info").trigger("available");
    }
    
    
    // [attr] Attributes generation
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    function get_attributes(){
        var attr = {
        };
        
        function attribute_class(value){
            if( value.match(/^(-){0,1}([0-9])+((\.)([0-9])+){0,1}$/) ){
                return "continuous";
            } else {
                return "discrete";
            }
        }
        
        function attribute_js_type(value){
            if( value.match(/^(-){0,1}([0-9])+((\.)([0-9])+){0,1}$/) ){
                return "number";
            } else if( value == true || ("" + value).toLowerCase() == "true"
            || value == false || ("" + value).toLowerCase() == "false") {
                return "boolean";
            } else {
                return "string";
            }
        }
        
        // attributes are data within cytoweb (e.g. nodes.data, edges.data)
        function build_attr(group_name){
            var group = $("#cytoweb_container").cw()[group_name]();
            attr[group_name] = {};
            
            // add values, types, etc to attr
            for(var i in group){
                var group_item = group[i];
                var data_struct = group_item.data;
                
                for(var j in data_struct){
                	// ignore some attributes
                	if (group_name === "edges" && (j === "source" || j === "target")) {
                        continue;
                    }
                	
                    var data = data_struct[j];
                    var name = j;
                    var value = "" + data;
                    var type = attribute_class(value);
                    var js_type = attribute_js_type(value);
                    var attribute = attr[group_name][name];
                    
                    if( attribute == undefined ){
                        attribute = {};
                        attr[group_name][name] = attribute;
                        
                        attribute.name = name;
                        attribute.type = type;
                        attribute.js_type = js_type;
                        attribute.values = [];
                        attribute.multiplicities = {};
                        attribute.shown = undefined;
                    } 
                    
                    if( $.inArray(data, attribute.values) < 0 ){
                        attribute.values.push(data);
                        attribute.multiplicities[data] = 1;
                    } else {
                        attribute.multiplicities[data]++;
                    }
                    
                    // if one piece of data is discrete, so is the set overall
                    if( type == "discrete" ){
                        attribute.type = "discrete";
                    }
                    
                    // not matching => have to use string
                    if( js_type != attribute.js_type ){
                        attribute.type = "string";
                    }
                }
            }
            
            // make values sorted in the list
            for(var j in attr[group_name]){
                var attribute = attr[group_name][j];
                
                if( attribute.type == "continuous" ){
                    for(var k in attribute.values){
                        attribute.values[k] = parseFloat( attribute.values[k] );
                    }
                }
                
                attribute.values = attribute.values.sort(function(a, b){
                    if( a > b ){
                        return 1;
                    } else if( a < b ){
                        return -1;
                    } else {
                        return 0;
                    }
                });
            }
        }
        build_attr("nodes");
        build_attr("edges");
        
        return attr;
    }
    
    
    // [vizmapper] Style tab generation
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    function update_vizmapper(){
        var parent = $("#vizmapper");
        parent.empty();
        
        $("#vizmapper_link").bind("click", function(){
            $("#custom_visual_style").find(".ui-menu-check-icon").addClass("ui-menu-checked");
            $("#custom_visual_style").siblings().find(".ui-menu-check-icon").removeClass("ui-menu-checked");
        });
        
        $("#cytoweb_container").cw().removeListener("visualStyleChange");
        $("#cytoweb_container").cw().addListener("visualStyleChange", function(){
            update_background();
        });
        
        var attr = get_attributes();

        // properties to show in the tab that the user can change
        // only this should change when adding/removing items in the style tab
        var properties = {
            groups: [
                {
                    name: "Background",
                    properties: [
                        {
                            name: "Colour",
                            variable: "global.backgroundColor",
                            type: "colour",
                            mappable: false
                        }
                    ]
                },
                
                {
                    name: "Nodes",
                    groups: [
                        {
                            name: "Outer line",
                            properties: [
                                {
                                    name: "Size",
                                    variable: "nodes.borderWidth",
                                    type: "number",
                                    mappable: true,
                                    mapgroup: "nodes"
                                },
                                
                                {
                                    name: "Colour",
                                    variable: "nodes.borderColor",
                                    type: "colour",
                                    mappable: true,
                                    mapgroup: "nodes"
                                }
                            ]
                        },
                        
                        {
                            name: "Fill",
                            properties: [
                                {
                                    name: "Size",
                                    variable: "nodes.size",
                                    type: "number",
                                    mappable: true,
                                    mapgroup: "nodes"
                                },
                                
                                {
                                    name: "Colour",
                                    variable: "nodes.color",
                                    type: "colour",
                                    mappable: true,
                                    mapgroup: "nodes"
                                },
                                
                                {
                                    name: "Opacity",
                                    variable: "nodes.opacity",
                                    type: "per cent number",
                                    mappable: true,
                                    mapgroup: "nodes"
                                },
                                
                                {
                                    name: "Shape",
                                    variable: "nodes.shape",
                                    type: "node shape",
                                    mappable: true,
                                    mapgroup: "nodes"
                                }
                                
                                
                            ]
                        }
                        
                    
                    ]
                },
                
                {
                    name: "Edges",
                    groups: [
                        {
                            name: "Line",
                            properties: [
                                {
                                    name: "Size",
                                    variable: "edges.width",
                                    type: "number",
                                    mappable: true,
                                    mapgroup: "edges"
                                },
                                
                                {
                                    name: "Colour",
                                    variable: "edges.color",
                                    type: "colour",
                                    mappable: true,
                                    mapgroup: "edges"
                                },
                                
                                {
                                    name: "Opacity",
                                    variable: "edges.opacity",
                                    type: "per cent number",
                                    mappable: true,
                                    mapgroup: "edges"
                                }
                                
                            ]
                        },
                        
                        {
                            name: "Arrow",
                            properties: [
                                {
                                    name: "Target shape",
                                    variable: "edges.targetArrowShape",
                                    type: "edge shape",
                                    mappable: true,
                                    mapgroup: "edges"
                                },
                                
                                {
                                    name: "Source shape",
                                    variable: "edges.sourceArrowShape",
                                    type: "edge shape",
                                    mappable: true,
                                    mapgroup: "edges"
                                }
                            ]
                        }
                    ]
                }
                
            ]
        };
        
        var cached_style = $("#cytoweb_container").cw().visualStyle();
        
        function get_property(variable){
            var style = cached_style;
    
            var objs = variable.split(".");
            var property = style;
            for(var i in objs){
                var obj = objs[i];
                property = property[obj];
            }
            
            return property;
        }
        
        function set_property(variable, value){
            var old_style = cached_style;
            var style = {};
                      
            var current_lvl = style;
            var old_current_lvl = old_style;
            var objs = variable.split(".");
            for(var i = 0; i < objs.length; i++){
                var obj = objs[i];
                
                if( i == objs.length - 1 ){
                    current_lvl[obj] = value;
                    old_current_lvl[obj] = undefined;
                } else {
                    current_lvl[obj] = {};
                }
                current_lvl = current_lvl[obj];
                old_current_lvl = old_current_lvl[obj];
            }
            
            cached_style = $.extend( true, cached_style, style );
            $("#cytoweb_container").cw().visualStyle(cached_style);
        }
        
        function cast_value(value, type){
            switch( type ) {
                case "colour":
                    return "" + value;
                case "number":
                case "per cent number":
                    return parseFloat(value);
                case "integer":
                    return parseInt(value);
                case "string":
                case "non-empty string":
                case "node shape":
                case "edge shape":
                    return "" + value;
            }
            
            return value;
        }
        
        function property_class(property){
            switch(property.type){
                case "colour":
                case "number":
                case "per cent number":
                case "integer":
                    return "continuous";
                case "string":
                case "non-empty string":
                case "node shape":
                case "edge shape":
                    return "discrete";
            }
        }
        
        function valid_value(value, type){
            switch( type ) {
                case "colour":
                    return value.match(/^(\#)([0-9]|[a-f]|[A-F]){6}$/);
                case "number":
                    return value.match(/^(-){0,1}([0-9])+((\.)([0-9])+){0,1}$/);
                case "per cent number":
                    return value.match(/^((1)|(0)((\.)([0-9])+){0,1})$/);
                case "integer":
                    return value.match(/^([0-9])+$/);
                case "string":
                    return true;
                case "non-empty string":
                    return value != null && value != "";
                case "node shape":
                    return value.match(/^(ellipse)|(diamond)|(rectangle)|(triangle)|(hexagon)|(roundrect)|(parallelogram)|(octagon)$/i);
                case "edge shape":
                    return value.match(/^(circle)|(diamond)|(delta)|(T)|(none)$/i);
            }
            
            return false;
        }
        
        function print_property(property){
            var div = $('<div class="property" property="' + property.variable + '" type="' + property.type + '"></div>');
            $(parent).append(div);
            
            var input_label = $('<label class="style">' + property.name + '</label>');
            div.append(input_label);
            
            initial_property = get_property(property.variable);
            var input = $('<input class="default" type="text" />');
            div.append(input);
            
            var initial_property_is_mapped = typeof initial_property == "object";
            if(initial_property_is_mapped){
                input.val(initial_property.defaultValue);
            } else {
                input.val(initial_property);
            }
                        
            function apply_continuous_mapping_from_inputs(){
                if( div.find(".continuous input.error:visible").length <= 0 ){
                    var continuous_min = div.find(".continuous_min:first");
                    var continuous_max = div.find(".continuous_max:first");              
                    
                    var mapping = {
                        defaultValue: cast_value(input.val(), property.type),
                        continuousMapper: {
                            attrName: div.find(".selector").attr("attribute"),
                            minValue: cast_value(continuous_min.val(), property.type),
                            maxValue: cast_value(continuous_max.val(), property.type)
                        }
                    };
                    
                    set_property( property.variable, mapping );
                
                }
            }
            
            function apply_discrete_mapping_from_inputs(){
                if( div.find(".discrete input.error:visible").length <= 0 ){
                    var discrete = div.find(".discrete:first");
                    var category = discrete.children(":visible");
                    var attribute = category.attr("attribute");
                    
                    var mapping = {
                        defaultValue: cast_value(input.val(), property.type),
                        discreteMapper: {
                            attrName: attribute,
                            entries: []
                        }
                    };
                    
                    // grab mapping from each input
                    category.find("input").each(function(){
                        
                        mapping.discreteMapper.entries.push({
                            attrValue: $(this).attr("attribute_value"),
                            value: cast_value($(this).val(), property.type)
                        });
                    });
                    
                    set_property( property.variable, mapping );
                }
            }
            
            input.validate({
                label: input_label,
                errorMessage: function(str){
                    return "must be a valid " + property.type;
                },
                valid: function(str){
                    return valid_value( str, property.type );
                }
            });
            
            input.bind("valid", function(){
                var continuous = div.find(".continuous:first");
                var discrete = div.find(".discrete:first");
                
                if( continuous.is(":visible") ){
                    apply_continuous_mapping_from_inputs();
                } else if( discrete.is(":visible") ) {
                    apply_discrete_mapping_from_inputs();
                } else {
                    set_property( property.variable, cast_value($(this).val(), property.type) );
                }
            });
            
            
            if( property.mappable ) {
                var map_button_open_map_class = "ui-icon-transferthick-e-w";
                var map_button_close_map_class = "ui-icon-close";
                
                var map_button = $('<div class="ui-state-default ui-corner-all map_button"><div class="ui-icon ' + map_button_open_map_class + '"></div></div>');
                div.append(map_button);
                
                map_button.bind("mouseover", function(){
                    $(this).addClass("ui-state-hover");
                }).bind("mouseout", function(){
                    $(this).removeClass("ui-state-hover");
                });
                
                var map_section = $('<div class="map_section ui-corner-bottom"></div>');
                div.append(map_section);
                map_section.hide();
                
                
                var selector = $('\
                <div class="selector"><ul>\
                    <li class="title"><label class="title">Select attribute to map</label>\
                        <ul>\
                        </ul>\
                     </li>\
                </ul></div>');
                map_section.append(selector);
                    
                var attr_group = attr[property.mapgroup];
                var attr_group_names = [];
                for(var i in attr_group){
                    var name = i;
                    attr_group_names.push(name);
                }
                attr_group_names.sort();
                
                // add menu for what attribute to map to
                for(var i in attr_group_names){
                    var name = attr_group_names[i];
                    var attribute = attr_group[name];
                    var type = ( property_class(property) == "discrete" ? "discrete" : attribute.type );
                    var name = attribute.name;
                    
                    var li = $('<li type="' + type + '"><label>' + name + '</label></li>');
                    selector.find("ul:first").find("ul:first").append(li);
                    
                    if( type == "continuous" ){
                        li.append('\
                        <ul>\
                            <li class="type_selector" type="continuous"><label>Continuous</label></li>\
                            <li class="type_selector" type="discrete"><label>Discrete</label></li>\
                        </ul>\
                        ');
                    }
                }
                
                
                function open_map_section(){
                    map_section.show();
                    map_button.find(".ui-icon").removeClass(map_button_open_map_class).addClass(map_button_close_map_class);
                }
                
                function close_map_section(){
                    map_section.hide();
                    map_button.find(".ui-icon").addClass(map_button_open_map_class).removeClass(map_button_close_map_class);
                }
                
                map_button.click(function(){
                    if( !map_section.is(":visible") ){
                        open_map_section();
                    } else {
                        close_map_section();
                    }
                    input.trigger("validate");
                });
                
                var continuous = $('<div class="continuous ui-corner-bottom"></div>');
                map_section.append(continuous);
                continuous.hide();
                   
                var continuous_max = $('<input type="text" class="continuous_max"/>');
                continuous.append('<label class="continuous_max_name">High</span></label>');
                continuous.append(continuous_max);
                
                continuous_max.validate({
                    label: continuous.find(".continuous_max_name"),
                    valid: function(str){
                        return valid_value( str, property.type );
                    },
                    errorMessage: function(str){
                        return "must be a valid " + property.type;
                    }
                });
                continuous_max.bind("valid", function(){                
                    apply_continuous_mapping_from_inputs();
                });
                
                var continuous_min = $('<input type="text" class="continuous_min"/>');
                continuous.append('<label class="continuous_min_name">Low</span></label>');
                continuous.append(continuous_min);
                continuous_min.validate({
                    label: continuous.find(".continuous_min_name"),
                    valid: function(str){
                        return valid_value( str, property.type );
                    },
                    errorMessage: function(str){
                        return "must be a valid " + property.type;
                    }
                });
                continuous_min.bind("valid", function(){                
                    apply_continuous_mapping_from_inputs();
                });
                
                
                
                var line = $('<div class="line"></div>');
                continuous.append(line);
                line.append('<div class="ui-state-disabled arrow_top"><div class="ui-icon ui-icon-arrowthickstop-1-n"></div></div>');
                line.append('<div class="ui-state-disabled arrow_bottom"><div class="ui-icon ui-icon-arrowthickstop-1-s"></div></div>');
                line.append('<div class="middle ui-state-disabled">Range</div>');
                
                var discrete = $('<div class="discrete ui-corner-bottom"></div>');
                map_section.append(discrete);
                discrete.hide();
                
                function create_discrete(attribute){                    
                    var attr_vals = attr[property.mapgroup][attribute].values;
                    var category = $('<div attribute="' + attribute + '"></div>');
                    discrete.append(category);
                    
                    for(var i in attr_vals){
                        var val = attr_vals[i];
                        
                        var discrete_input = $('<input attribute_value="' + val + '" type="text" />');
                        var discrete_input_label = $('<label class="discrete_name">' + val + '</label>');
                        category.append(discrete_input_label);
                        category.append(discrete_input);
                        
                        discrete_input.validate({
                            label: discrete_input_label,
                            valid: function(str){
                                return valid_value( str, property.type );
                            },
                            errorMessage: function(str){
                                return "must be a valid " + property.type;
                            }
                        });
                        
                        discrete_input.bind("valid", function(){                
                            apply_discrete_mapping_from_inputs();
                        });
                    }
                }
                
                for(var i in attr_group_names){
                    var name = attr_group_names[i];
                    var attribute = attr_group[name];
                    
                    create_discrete(name);
                }
                
                function set_selector_title(name, type){
                    selector.attr("attribute", name);
                    selector.find("label.title").text( name + " (" + type + ")" );
                }
                
                function display_continuous(use_initial_property){
                    continuous.show();
                    discrete.hide();
                    
                    if(use_initial_property){
                        continuous_min.val( initial_property.continuousMapper.minValue );
                        continuous_max.val( initial_property.continuousMapper.maxValue );
                    } else {
                        if( valid_value( input.val(), property.type ) ){
                            continuous_min.val( input.val() );
                            continuous_max.val( input.val() );
                        }
                    }
                }
                
                function display_discrete(use_initial_property){
                    continuous.hide();
                    discrete.show();
                    
                    discrete.children().each(function(){
                        if( $(this).hasClass("[attribute=" + selector.attr("attribute") + "]") ){
                            $(this).show();
                            var discrete_category = $(this);
                            
                            if(use_initial_property){
                                var entries = initial_property.discreteMapper.entries;
                                for(var i in entries){
                                    var entry = entries[i];
                                    var attr_val = entry.attrValue;
                                    var value = entry.value;
                                    
                                    discrete_category.find("input[attribute_value=" + attr_val + "]").val( value );
                                }
                            } else if( valid_value( input.val(), property.type ) ){
                                $(this).find("input").val( input.val() );
                            }
                        
                        } else {
                            $(this).hide();
                        }
                    });
                }
                
                // create menu to select what attribute to map to the property
                selector.menu({
                    addArrow: false,
                    onMenuItemClick: function(li){
                        if( li.parents("ul").length > 1 ) {
                            var name = li.find("label:first").text();
                            var type = li.attr("type");
                            
                            if( li.hasClass("type_selector") ){
                                name = li.parents("li:first").find("label:first").text();
                            }
                            
                            set_selector_title(name, type);
                            if( type == "continuous" ) {
                                display_continuous();
                            } else {
                                display_discrete();
                            }
                            
                            input.trigger("validate");
                        }
                    }
                });
                
                if(initial_property_is_mapped){
                    open_map_section();
                    
                    if(initial_property.continuousMapper != undefined){
                        set_selector_title(initial_property.continuousMapper.attrName, "continuous");
                        display_continuous(true);
                    } else if(initial_property.discreteMapper != undefined) {
                        set_selector_title(initial_property.discreteMapper.attrName, "discrete");
                        display_discrete(true);
                    }
                }
                
            }
        }
        
        function print_group_set(groups, level){
            for(var i in groups){
                var group = groups[i];
                var name = group.name;
                var properties = group.properties;
                var sub_groups = group.groups;
                
                parent.append("<h" + level + ">" + name + "</h" + level + ">");
                
                for(var j in properties){
                    print_property( properties[j] );
                }
                
                if( sub_groups != undefined ){
                    print_group_set(sub_groups, level + 1);
                }
            }
        }
        print_group_set(properties.groups, 1);
        
        
        
        
        // utility for pickers
        function position_at_input(picker, input){
            $(picker).css({
                position: "absolute",
                left: $(input).offset().left,
                top: $(input).offset().top + $(input).outerHeight()
            });
            
            if( $(picker).offset().top + $(picker).outerHeight() > $(window).height() ){
                $(picker).css({
                    top: $(input).offset().top - $(picker).outerHeight()
                });
            }       
        }
        
        function hide_with_input(picker, input, fn){
        	if ($(picker).find(".header").is(":visible")) {
        		// IE set focus to picker, forcing a blur on input, so blur cannot be used here.
        		// Let's just create a close button instead:
        		$("#colour_picker .ui-icon-close").bind("click", function(evt){
        			fn();
        			evt.preventDefault();
        		});
        	} else {
        		// Regular GOOD browsers!
	        	$(input).bind("blur", function(){
	            	fn();
	            });
        	}
            
            $(parent).parent().bind("scroll", function(){
                fn();
            });
            
            $(window).bind("resize", function(){
                fn();
            });
        }
        
        // add colour pickers
        var picker; // parent div to farbtastic
        var picker_internals; // farbtastic instance
        
        function remove_picker(){
            if( picker != undefined ){
                $(picker).remove();
                picker = undefined;
                picker_internals = undefined;
            }
        }
        
        $(parent).find(".property[type=colour] input").each(function(){
            var input = $(this);
            
            $(input).addClass("colour_sample_bg");
            
            function set_colour(){
                $(input).css({
                    backgroundColor: $(input).val()
                });
            }
            set_colour();
            
            // update colour on picker after typing
            $(input).bind("valid", function(){
                if( picker_internals != undefined ){
                    picker_internals.setColor( $(input).val() );
                }
                
                set_colour();
            }).bind("invalid", function(){
                $(input).css("background-color", "transparent");
            });
            
            // on empty put # so user doesn't have to
            $(input).bind("keyup", function(){
                if( $(this).val() == "" ){
                    $(this).val("#");
                }
            });
            
            // add clicker near input when clicked
            $(input).bind("click", function(){
                remove_picker();
            
                picker = $('<div id="colour_picker" class="floating_widget">' +
                		   '<div class="header ui-state-default"><div class="ui-icon ui-icon-close"/></div><div class="content"/></div>');
                $("body").append(picker);
                
                if (!$.browser.msie) { $("#colour_picker .header").hide(); }
                
                picker_internals = $.farbtastic($("#colour_picker .content"), function(colour){
                    $(input).val(colour).trigger("validate");
                });

                position_at_input($(picker), $(input));
                
                $(input).trigger("validate");
                
                hide_with_input($(picker), $(input), function(){
                    remove_picker();
                });
            });
        });
        
        // add node shape pickers
        var node_shape_picker; // parent div to farbtastic
        
        function remove_node_shape_picker(){
            if( node_shape_picker != undefined ){
                $(node_shape_picker).remove();
                node_shape_picker = undefined;
            }
        }
        
        $(parent).find(".property[type=node shape] input").each(function(){
            var input = $(this);
                       
            // add clicker near input when clicked
            $(input).bind("click", function(){
                remove_node_shape_picker();
            
                node_shape_picker = $('<div id="node_shape_picker" class="shape_picker floating_widget"></div>');
                $("body").append(node_shape_picker);
                
                var types = [ "ellipse", "triangle", "diamond", "rectangle", "roundrect", "parallelogram",  "hexagon", "octagon" ];
                for(var i in types){
                    var type = types[i];
                
                    $(node_shape_picker).append('<div class="shape ' + type + '" shape="' + type + '"></div>');
                }
                
                $(node_shape_picker).bind("mousedown", function(){
                    return false;
                });
                $(node_shape_picker).children().bind("mousedown", function(){
                    var type = $(this).attr("shape");
                    
                    $(input).val(type);
                    $(input).trigger("validate");
                
                    return false;
                });
                
                position_at_input($(node_shape_picker), $(input));
                
                $(input).trigger("validate");
                
                hide_with_input($(node_shape_picker), $(input), function(){
                    remove_node_shape_picker();
                });
            });
        });
        
        // add node shape pickers
        var edge_shape_picker; // parent div to farbtastic
        
        function remove_edge_shape_picker(){
            if( edge_shape_picker != undefined ){
                $(edge_shape_picker).remove();
                edge_shape_picker = undefined;
            }
        }
        
        $(parent).find(".property[type=edge shape] input").each(function(){
            var input = $(this);
                       
            // add clicker near input when clicked
            $(input).bind("click", function(){
                remove_edge_shape_picker();
            
                edge_shape_picker = $('<div id="edge_shape_picker" class="shape_picker floating_widget"></div>');
                $("body").append(edge_shape_picker);
                
                var types = [ "circle", "diamond", "delta", "t", "none" ];
                for(var i in types){
                    var type = types[i];
                
                    $(edge_shape_picker).append('<div class="shape ' + type + '" shape="' + type + '"></div>');
                }
                $(edge_shape_picker).bind("mousedown", function(){
                    return false;
                });
                $(edge_shape_picker).children().bind("mousedown", function(){
                    var type = $(this).attr("shape");
                    
                    $(input).val(type);
                    $(input).trigger("validate");
                
                    return false;
                });
                
                position_at_input($(edge_shape_picker), $(input));
                
                $(input).trigger("validate");
                
                hide_with_input($(edge_shape_picker), $(input), function(){
                    remove_edge_shape_picker();
                });
            });
        });
    
    
        
        $("#vizmapper").trigger("available");
    }
    
    // [filters] Generation of the filters tab
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    function update_filter(){
        var attr = get_attributes();
        var parent = $("#filter");
        var header = $("#filter_header");
        var operation;
        
        var cached_elements = {};
        cached_elements.nodes = function(){
            if(cached_elements.cached_nodes == undefined){
                cached_elements.cached_nodes = $("#cytoweb_container").cw().nodes();
            }
            return cached_elements.cached_nodes;
        }
        cached_elements.edges = function(){
            if(cached_elements.cached_edges == undefined){
                cached_elements.cached_edges = $("#cytoweb_container").cw().edges();
            }
            return cached_elements.cached_edges;
        }
        
        
        parent.empty();
        header.empty();
        
        header.append('\
        Filter such that\
        <ul>\
            <li><a href="#" operation="and">every</a></li>\
            <li><a href="#" operation="or">any</a></li>\
        </ul>\
        filter is satisfied.');
        
        header.find("a").click(function(){
            header.find("a").not(this).removeClass("selected");
            $(this).addClass("selected");
            operation = $(this).attr("operation");
            
            parent.trigger("filternodes").trigger("filteredges");
            
            return false; // no changing the URL
        });
        header.find("[operation=and]").click();
        
        function capitalise(str){
            return str.substr(0, 1).toUpperCase() + str.substr(1);
        }
        
        function append_group(group, group_name){
            parent.append('<h1>' + capitalise(group_name) + '</h1>');
            
            var attribute_names = [];
            for(var j in group){
                attribute_names.push(j);
            }
            
            attribute_names.sort();
            for(var j in attribute_names){
                var name = attribute_names[j];
                var attribute = group[name];
                append_attribute(attribute);
            }
            
            parent.bind("filter" + group_name, function(){         
                
                $("#cytoweb_container").cw().filter(group_name, function(ele){
                    
                    for(var j in ele.data){
                        var ele_attr_name = j;
                        var ele_attr_val = ele.data[j];
                        
                        if( attr[group_name][ele_attr_name] == undefined || attr[group_name][ele_attr_name].shown == undefined ){
                            continue; // ignore if shown not set (i.e. filter not set)
                        }
                        
                        var shown = attr[group_name][ele_attr_name].shown[ele_attr_val];
                        
                        switch(operation){
                            case "and":
                                if( !shown ){
                                    return false; // at least 1 not shown
                                }
                                break;
                                
                            case "or":
                                if( shown ){
                                    return true; // at least 1 shown
                                }
                                break;
                        }
                    }
                    
                    switch(operation){
                        case "and":
                            return true; // all shown in loop
                            
                        case "or":
                            return false; // no shown in loop
                    }
                    
                });
                
                
            });
            
            function append_attribute(attribute){
                var attribute_label = $('<label>' + attribute.name + '</label>');
                parent.append(attribute_label);
                var div = $('<div class="attribute" attribute_name="' + attribute.name + '"></div>');
                parent.append(div);
                
                var string_search = $('<input type="text" class="inactive string_search" value="Find a value to filter" />');
                div.append(string_search);
                
                string_search.bind("focus", function(){
                    if( $(this).hasClass("inactive") ){
                        $(this).removeClass("inactive");
                        $(this).val("");
                    }
                });
                
                var results_area = $('<div class="results_area"></div>');
                div.append(results_area);
                
                var stats_area = $('<div class="stats_area"></div>');
                results_area.append(stats_area);
                
                var slider_area = $('<div class="slider_area"></div>');
                results_area.append(slider_area);
                
                var label_min = $('<span class="slider_min"></span>');
                slider_area.append(label_min);
                
                var label_max = $('<span class="slider_max"></span>');
                slider_area.append(label_max);
                
                var slider = $('<div class="slider"></div>');
                slider_area.append(slider);
                
                var range_area = $('<div class="range_area"></div>');
                slider_area.append(range_area);
                
                var range_min = $('<input type="text" class="range_min" />');
                range_area.append(range_min);
                
                var range_max = $('<input type="text" class="range_max" />');
                range_area.append(range_max);
                
                if( attribute.type == "continuous" ){
                    use_continuous_logic();
                } else {
                    use_discrete_logic();
                }
                
                function add_slider_logic(attribute){
                    var steps = FILTER_STEPS_ON_SLIDER;
                    
                    var min, max;
                    if(attribute.type == "continuous"){
                        min = attribute.values[ 0 ];
                        max = attribute.values[ attribute.values.length - 1 ];
                    } else if( attribute.type == "discrete" ){
                        min = attribute.diff_values[ 0 ];
                        max = attribute.diff_values[ attribute.diff_values.length - 1 ];
                        
                    }
                    
                    // add shown to all, since we're now adding a slider that has all values shown
                    attr[group_name][attribute.name].shown = {};
                    for(var i in attribute.values){
                        var val = attribute.values[i];
                        
                        attr[group_name][attribute.name].shown[val] = true;
                    }
                    
                    var timeout;
                    slider.slider("destroy").empty().slider({
                        animate: "fast",
                        min: min,
                        max: max,
                        step: (max - min)/steps,
                        values: [min, max],
                        range: true,
                        start: function(event, ui){
                            // clear errors on start
                            range_min.val( ui.values[0] );
                            range_max.val( ui.values[1] );
                            
                            range_min.trigger("validate");
                            range_max.trigger("validate");
                        },
                        slide: function(event, ui){                            
                            range_min.val( ui.values[0] );
                            range_max.val( ui.values[1] );
                            
                            function set_timeout(){
                                timeout = setTimeout(function(){
                                    filter();
                                    timeout = undefined;
                                }, FILTER_DELAY_ON_SLIDER);
                            }
                            
                            if( timeout == undefined ){
                                set_timeout();
                            } else {
                                clearTimeout(timeout);
                                set_timeout();
                            }
                            
                        },
                        change: function(event, ui){                            
                        },
                        stop: function(event, ui){
                            filter();
                        }
                    });
                    
                    label_min.text( min );
                    label_max.text( max );
                    
                    range_min.val( min );
                    range_max.val( max );
                    
                    for(var i in attribute.values){
                        var val = attribute.values[i];
                        
                        for(var j = 0; j < attribute.multiplicities[val]; j++){
                            var stat = $('<div class="stat"></div>');
                            stats_area.append(stat);
                            
                            if( attribute.type == "continuous" ){
                                // val as is
                            } else if(attribute.type == "discrete") {
                                val = attribute.diff[val];  
                            }
                            
                            var percent = ((val - min) / (max - min));
                            stat.css({
                                left: ( (percent*100) + "%" )
                            });
                        }
                    }
                    
                    function update_slider_from_inputs(){
                        var values = [ parseFloat($(range_min).val()), parseFloat($(range_max).val()) ];
                        
                        for(var i in values){
                            slider.slider("values", i, values[i]);
                        }
                    }
                    
                    function valid_val(str, type){
                        if(str.match(/^(-){0,1}([0-9])+((\.)([0-9])+){0,1}$/)){
                            var val = parseFloat(str);
                            
                            var smin =  parseFloat( range_min.val() );
                            var smax =  parseFloat( range_max.val() );
                            
                            if( val < min || val > max ){
                                return false;
                            }
                            
                            if( type == "min" && val >= smax ){
                                return false;
                            }
                            
                            if( type == "max" && val <= smin ){
                                return false;
                            }
                            
                            return true;
                        }
                        
                        return false;
                    }
                    
                    function filter(){
                        var elements = cached_elements[group_name](); 
                        
                        // don't actually call cytoweb filter; just update the filter maps since
                        // we can not filter by just looking at ONE filter; we need to consider
                        // ALL filters
                        attr[group_name][attribute.name].shown = {};
                        for(var i in elements){
                            var ele = elements[i];
                            
                            var data = ele.data[attribute.name]
                            
                            if(attr[group_name][attribute.name].shown[data] != undefined){
                                continue;
                            }
                            
                            var val = data;
                            
                            switch(attribute.type){
                                case "continuous":
                                    val = parseFloat(val);
                                    break;
                                case "discrete":
                                    val = attribute.diff[val];
                                    break;
                            }

                            
                            var smin =  parseFloat( $(range_min).val() );
                            var smax =  parseFloat( $(range_max).val() );
                            var shown = (smin <= val && val <= smax);
                            
                            attr[group_name][attribute.name].shown[data] = shown;
                        }
                        
                        // now, let the parent filter everything based on the maps
                        parent.trigger("filter" + group_name);
                    }
                    
                    range_min.validate({
                        valid: function(str){
                            return valid_val(str, "min");
                        }
                    });
                    
                    range_max.validate({
                        valid: function(str){
                            return valid_val(str, "max");
                        }
                    });
                    
                    range_min.add(range_max).bind("valid", function(){
                        update_slider_from_inputs();
                        filter();
                    });
                }
                
                function use_continuous_logic(){
                    string_search.hide();
                    add_slider_logic(attribute);
                }
                
                function use_discrete_logic(){
                    function hide_slider(){
                        results_area.hide();
                        
                    }
                    hide_slider();
                    
                    function show_slider(){
                        if( results_area.is(":visible") ){
                            results_area.hide().fadeIn();
                        } else {
                            results_area.show();
                        }
                    }
                    
                    $(range_min).add(range_max).hide();
                    
                    function update_discrete_attribute(){   
                        attribute.diff = {};
                        attribute.diff_values = [];
                    
                        for(var i in attribute.values){
                            var val = "" + attribute.values[i];
                            var desired = "" + $(string_search).val();
                            
                            var diff = levenshtein(desired.toLowerCase(), val.toLowerCase());
                            
                            attribute.diff[val] = diff;
                            
                            if( $.inArray(diff, attribute.diff_values) < 0 ){
                                attribute.diff_values.push(diff);
                            }
                        }
                        attribute.diff_values = attribute.diff_values.sort(function(a, b){
                            if( a > b ){
                                return 1;
                            } else if( a < b ){
                                return -1;
                            } else {
                                return 0;
                            }
                        });
                        
                        attribute.desired = string_search.val();
                        
                    }
                    
                    var prev_string_search_val = string_search.val();
                    string_search.validate({
                        label: attribute_label,
                        valid: function(str){
                            if( str == "" ){
                                return false;
                            }
                        
                            update_discrete_attribute();
                            
                            if(attribute.diff_values.length > 1){
                                return true;
                            } else {
                                return false;
                            }
                        },
                        errorMessage: function(str){
                            if( str == "" ){
                                return "can not be blank to filter";
                            }
                            
                            return "needs a better matching string";
                        }
                    }).bind("valid", function(){
                        if( $(this).val() != prev_string_search_val ){
                            slider.slider("disable");
                            
                            $.thread({
                                worker: function(){
                                    add_slider_logic(attribute);
                                    
                                    if( attr[group_name][attribute.name].js_type == "boolean" ){                                        
                                        label_min.text("true");
                                        label_max.text("false");
                                    } else {
                                        label_min.text("most similar");
                                        label_max.text("most different");
                                    }
                                    
                                    slider.slider("enable");
                                    show_slider();
                                }
                            });
                            
                        }
                        
                        prev_string_search_val = $(this).val();
                    }).bind("invalid", function(){
                        hide_slider();
                        attr[group_name][attribute.name].shown = undefined; // hidden slider => filter has no effect
                        parent.trigger("filter" + group_name);
                    
                        prev_string_search_val = $(this).val();
                        
                        // this means all values are true or all values are false and filtering is
                        // completely useless, so just remove the filter
                        if( attr[group_name][attribute.name].js_type == "boolean" ){
                            attribute_label.remove();
                            div.remove();
                        }
                    });
                    
                    
                    // boolean is just a special case of discrete so just configure the ui so
                    // it's nice for users
                    if( attr[group_name][attribute.name].js_type == "boolean" ){
                        string_search.val("true").trigger("change").hide();
                    }
                    
                } // end use_discrete_logic
                
            } // end append_attribute
            
        } // end append_group
        
        
        
        for(var i in attr){
            var group = attr[i];
            var group_name = i;
            append_group(group, group_name);
        }
        
        
        $("#cytoweb_container").cw().removeFilter();
        
        $("#filter").trigger("available");
    }
    
    // [examples] Generations of the examples tab
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    // create examples in side tab (static -- does not change on new graph load)
    for(var id in example){
        var ex = example[id];
        var entry = $(          "<div class=\"entry " + id + "\" example_id=\"" + id + "\">\
                                    <label class=\"name\">" + ex.name + "</label>\
                                    <div class=\"icon\"></div>\
                                    <label class=\"description\">" + ex.description + "</label>\
                                </div>");
                    
        entry.click(function(){
            open_graph( example[ $(this).attr("example_id") ] );
        });
                    
        $("#examples").append(entry);
    }

    
    
    // [end] Some clean up
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    // trigger resize to recalculate the layout
    $(window).trigger("resize");
    
    
});