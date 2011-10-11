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
        	<input type="button" id="reapplyLayout" value="Reapply layout"/>\
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
        nodeTooltipsEnabled: true,
        edgesMerged: false,
        //mouseDownToDragDelay: -1,
        visualStyle: {
            global: {
                backgroundColor: "#fefefe",
                tooltipDelay: 1000
            },
            nodes: {
                shape: "ELLIPSE",
                compoundShape: "RECTANGLE",
                color: { defaultValue: "#cccccc", continuousMapper: { attrName: "weight", minValue: "#ffffff", maxValue: "#0b94b1" } },
                opacity: 0.9,
                size: 30,
                borderWidth: 2,
                borderColor: "#707070",
                compoundBorderColor: "#abcfd6",
                compoundBorderWidth: 2,
                //image: "http://chart.apis.google.com/chart?chs=300x300&cht=p&chd=e0:U-gh..bR",
                //compoundImage: "http://chart.apis.google.com/chart?chxr=0,0,160&chxt=x&chbh=a&chs=440x220&cht=bhs&chco=4D89F9,C6D9FD&chd=s:GflxYlS,fl9YSYS",
                labelFontSize: { defaultValue: 12, continuousMapper: { attrName: "weight",  minValue: 10, maxValue: 24 } },
                tooltipText: { customMapper: { functionName: "onNodeTooltip" } },
                selectionGlowOpacity: 0,
                selectionBorderColor: "ff0000",
                hoverBorderWidth: 4
            },
            edges: {
            	color: "#0b94b1",
                width: { defaultValue: 2, continuousMapper: { attrName: "weight",  minValue: 2, maxValue: 8 } },
                mergeWidth: { defaultValue: 2, continuousMapper: { attrName: "weight",  minValue: 2, maxValue: 8 } },
                mergeColor: "#0b94b1",
                opacity: 0.7,
                labelFontSize: 10,
                labelFontWeight: "bold",
                selectionGlowOpacity: 0,
                selectionColor: "ff0000",
                tooltipText: "${weight}"
             }
        }   
    };
    
    function draw(url) {
    	$("input, select").attr("disabled", true);

        $.get(url, function(dt) {
            if (typeof dt !== "string") {
                if (window.ActiveXObject) {
                    dt = dt.xml;
                } else {
                    dt = (new XMLSerializer()).serializeToString(dt);
                }
            }
            
			options.layout = { name: "CompoundSpringEmbedder" };
        	options.network = dt;
            options.nodeLabelsVisible = $("#showNodeLabels").is(":checked");

        	d1 = new Date();
        	vis.draw(options);
        });
    }

    var _srcId;
    function clickNodeToAddEdge(evt) {
        if (_srcId != null) {
        	vis.removeListener("click", "nodes", clickNodeToAddEdge);
        	var e = vis.addEdge({ source: _srcId, target: evt.target.data.id, }, true);
        	_srcId = null;
        }
    }
    
    $("input").attr("disabled", true);

    // init and draw
    vis = new org.cytoscapeweb.Visualization(div_id, { swfPath: "/swf/CytoscapeWeb" });
    
    vis.ready(function() {
        var layout = vis.layout();
        $("input, select").attr("disabled", false);
        $("#showNodeLabels").attr("checked", vis.nodeLabelsVisible());
        
        vis.addContextMenuItem("Delete node", "nodes", function(evt) {
            vis.removeNode(evt.target.data.id, true);
        })
        .addContextMenuItem("Delete edge", "edges", function(evt) {
            vis.removeEdge(evt.target.data.id, true);
        })
        .addContextMenuItem("Add new node", function(evt) {
            var x = evt.mouseX;
            var y = evt.mouseY;
            var parentId;console.log(evt)
            if (evt.target != null && evt.target.group == "nodes") {
                parentId = evt.target.data.id;
                x = evt.target.x;
                y = evt.target.y;
                x += Math.random() * (evt.target.width/2) * (Math.round(x)%2==0 ? 1 : -1);
                y += Math.random() * (evt.target.height/2) * (Math.round(y)%2==0 ? 1 : -1);
            }
            var n = vis.addNode(x, y, { weight: Math.random(), label: "New" }, parentId, true);
        })
        .addContextMenuItem("Add new edge", "nodes", function(evt) {
        	_srcId = evt.target.data.id;
            vis.removeListener("click", "nodes", clickNodeToAddEdge);
            vis.addListener("click", "nodes", clickNodeToAddEdge);
        })
        .addContextMenuItem("Delete selected", function(evt) {
            var items = vis.selected();
            if (items.length > 0) { vis.removeElements(items, true); }
        });
    });
    
    vis.addListener("error", function(err) {
		alert(err.value.msg);
    });

    // Register control liteners:
    $("#showNodeLabels").change(function(evt) {
        vis.nodeLabelsVisible($("#showNodeLabels").is(":checked"));
    });
    $("#reapplyLayout").click(function(evt) {
        var layout = vis.layout();
        vis.layout(layout);
    });

    draw("/file/example_graphs/compound.graphml");
        
});