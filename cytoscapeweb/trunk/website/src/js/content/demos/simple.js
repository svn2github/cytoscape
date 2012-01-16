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

    $("#content").html('\
        <div class="tools">\
            <label>Layout:</label>\
            <select id="layouts">\
                <option value="ForceDirected" selected="selected">Force Directed</option>\
                <option value="Circle">Circle</option>\
                <option value="Radial">Radial</option>\
                <option value="Tree">Tree</option>\
            </select>\
            <label for="showNodeLabels">Node Labels</label>\
            <input type="checkbox" id="showNodeLabels" checked="checked"/> \
        </div>\
        <div id="cytoscapeweb" width="*">\
            Cytoscape Web will replace the contents of this div with your graph.\
        </div>\
    ');

    var div_id = "cytoscapeweb";
    var vis;
    
    // options used for Cytoscape Web
    var options = {
        nodeTooltipsEnabled: true,
        edgeTooltipsEnabled: true,
        panZoomControlVisible: true,
        edgesMerged: false,
        visualStyle: {
            global: {
                backgroundColor: "#fefefe"
            },
            nodes: {
            	shape: { passthroughMapper: { attrName: "shape" } },
                color: { defaultValue: "#cccccc", continuousMapper: { attrName: "weight", minValue: "#ffffff", maxValue: "#0b94b1" } },
                size: { defaultValue: 20, continuousMapper: { attrName: "weight",  minValue: 25, maxValue: 50 } },
                opacity: 0.9,
                borderWidth: 2,
                borderColor: "#707070",
                labelFontColor: "#303030",
                hoverBorderWidth: 4
            },
            edges: {
            	color: "#0b94b1",
            	mergeColor: "#0b94b1",
            	width: { defaultValue: 2, continuousMapper: { attrName: "weight",  minValue: 2, maxValue: 6 } },
            	style: { defaultValue: "SOLID", passthroughMapper: { attrName: "lineStyle" } },
            	sourceArrowShape: { passthroughMapper: { attrName: "sourceArrowShape" } },
				targetArrowShape: { passthroughMapper: { attrName: "targetArrowShape" } },
                labelFontSize: 10
             }
        }   
    };
    
    function draw(data) {
    	$("input, select").attr("disabled", true);

        if (data) {
			options.layout = { name: "ForceDirected" };
        	options.network = data;
            options.nodeLabelsVisible = $("#showNodeLabels").is(":checked");

        	d1 = new Date();
        	vis.draw(options);
        } else {
            var url = "/file/example_graphs/sample1.graphml";
            $.get(url, function(dt) {
                if (typeof dt !== "string") {
                    if (window.ActiveXObject) {
                        dt = dt.xml;
                    } else {
                        dt = (new XMLSerializer()).serializeToString(dt);
                    }
                }
                draw(dt);
            });
        }
    }

    $("input").attr("disabled", true);

    // init and draw
    vis = new org.cytoscapeweb.Visualization(div_id, { swfPath: "/swf/CytoscapeWeb" });
    
    vis.ready(function() {
        var layout = vis.layout();
        $("#layouts").val(layout.name);
        $("input, select").attr("disabled", false);
        $("#showNodeLabels").attr("checked", vis.nodeLabelsVisible());
    });
    
    vis.addListener("error", function(err) {
		alert(err.value.msg);
    });

    // Register control liteners:
    $("#layouts").change(function(evt) {
        vis.layout($("#layouts").val());
    });
    $("#showNodeLabels").change(function(evt) {
        vis.nodeLabelsVisible($("#showNodeLabels").is(":checked"));
    });

    draw();
        
});