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
    
    var DELAY_BEFORE_HIDING_LOADER = 200; // otherwise, you see the cytoweb fade in from grey
    
    $("#loader").show();
    
    var options = {
        panZoomControlVisible: false,
		edgesMerged: false,
		nodeLabelsVisible: false,
		edgeLabelsVisible: false,
		nodeTooltipsEnabled: false,
		edgeTooltipsEnabled: false,
		swfPath: "/swf/CytoscapeWeb",
		flashInstallerPath: "/swf/playerProductInstall",
        layout: "Preset"
    };
    
    var vis = new org.cytoscapeweb.Visualization("viz", options);
    
    vis.ready(function(){
        setTimeout(function(){
            $("#loader").hide();
        }, DELAY_BEFORE_HIDING_LOADER);
    });
    
    $.get("/file/example_graphs/petersen.xgmml", function(data){
	    options.network = data;
	    vis.draw(options);
	});
    
    $("#location").text( window.location.href );

});