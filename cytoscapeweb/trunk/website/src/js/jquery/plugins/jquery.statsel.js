;(function($){
       
    function posToVal(pos, max_pos, min_val, max_val){
        var percent_pos = parseFloat(pos) / parseFloat(max_pos);
        var range_val = parseFloat(max_val) - parseFloat(min_val);
        
        return percent_pos * range_val + parseFloat(min_val);
    }
    
    function valToPos(val, max_pos, min_val, max_val){
        var range_val = parseFloat(max_val) - parseFloat(min_val);
        var percent_val = ( parseFloat(val) - parseFloat(min_val) ) / range_val;
        
        return percent_val * max_pos;
    }
    
    function cssToInt(css){
        var ret = parseInt(css);
        
        if(ret == undefined || isNaN(ret)){
            return 0;
        }
        
        return ret;
    }
    
    function debug(msg){
        //console.log(msg);
    }
    
    $.fn.statisticalSelectable = function(option_set) {
        return this.each(function() {
        
            // Options
            ////////////////////////////////////////
        
            var example_data = [];
            for (var i = 0; i <= Math.PI; i += Math.PI/100 ) {
                example_data.push([i, Math.sin(i)]);
            }
            
            var defaults = {
                containerClass: "ui-statsel-container",
                resizerClass: "ui-statsel-resizer",
                activeResizerClass: "ui-statsel-resizer-active",
                addResizerAnimationSpeed: 100,
                addResizerAnimationType: "puff",
                removeResizerAnimationSpeed: 250,
                removeResizerAnimationType: "puff",
                graphClass: "ui-statsel-graph",
                lineClass: "ui-statsel-legend-line",
                lineTopClass: "ui-statsel-legend-line-top",
                lineBottomClass: "ui-statsel-legend-line-bottom",
                lineRightClass: "ui-statsel-legend-line-right",
                lineLeftClass: "ui-statsel-legend-line-left",
                labelClass: "ui-statsel-label",
                labelTopClass: "ui-statsel-label-top",
                labelBottomClass: "ui-statsel-label-bottom",
                labelRightClass: "ui-statsel-label-right",
                labelLeftClass: "ui-statsel-label-left",
                labelUpdateDelay: 1000,
                plusClass: "ui-statsel-plus",
                minusClass: "ui-statsel-minus",
                resetClass: "ui-statsel-reset",
                buttonClass: "ui-statsel-button",
                xAxisTitle: "",
                yAxisTitle: "",
                axisTitleClass: "ui-statsel-axis-title",
                xAxisTitleClass: "ui-statsel-x-axis-title",
                yAxisTitleClass: "ui-statsel-y-axis-title",
                
                data: undefined,
                
                graphOptions: {
                    grid: {
                        show: false
                    },
                    
                    series: {
                        lines: {
                            show: true,
                            lineWidth: 1,
                            fill: true,
                        },
                        shadowSize: 0
                    },
                    
                    xaxis: {
                    },
                    
                    yaxis: {
                    },
                    
                    colors: [ "#c6949b", "#a7d299", "#dfe494", "#95c9cc", "#a4a6d9", "#a993af", "#ccaf61", "#c9e3bd", "#bbb08d", "#69aca4" ]
                },
                
                selectFrequency: 100, // prevent flooding of select events (select events occur at most every selectFrequency milliseconds)
                select: function(range){},
                start: function(range){},
                stop: function(range){}
                
                
                
            };
            
            var options = $.extend(true, defaults, option_set);
            $(this).data("statselopts", options);
            
            
            // UI behaviour
            ////////////////////////////////////////
            
            var parent = $(this);
            var container = $("<div class=\"" + options.containerClass + "\"></div>");
            var resizers = [];
            var graph = $("<div class=\"" + options.graphClass + "\"></div>");
                            
            
            parent.append(container);
            
            parent.css({
                position: "relative"
            });
            
            container.css({
                position: "relative",
                overflow: "hidden",
                height: "100%",
                width: "100%"
            });
            
            // Add axes titles
            ////////////////////////////////////////
            
            var x_title = $("<div class=\"" + options.axisTitleClass + " " + options.xAxisTitleClass + "\">" + options.xAxisTitle + "</div>");
            parent.append(x_title);
            x_title.css({
                position: "absolute",
                zIndex: -1
            });
            
            x_title.css({
                top: container.height() + cssToInt(x_title.css("margin-top")),
                left: container.position().left + container.width()/2 - x_title.outerWidth()/2
            });
            
            var y_title = $("<div class=\"" + options.axisTitleClass + " " + options.yAxisTitleClass + "\">" + options.yAxisTitle + "</div>");
            parent.append(y_title);
            y_title.css({
                position: "absolute",
                zIndex: -1
            });
            
            y_title.css({
                top: container.height()/2 - y_title.outerHeight()/2,
                left: container.position().left - y_title.outerWidth() - cssToInt(y_title.css("margin-right")),
            });
            
            // Add labels
            ////////////////////////////////////////
            
            var leftLabel = $("<input type=\"text\" class=\"" + options.labelClass + " " + options.labelLeftClass + "\" />");
            var rightLabel = $("<input type=\"text\" class=\"" + options.labelClass + " " + options.labelRightClass + "\" />");
            var topLabel = $("<input type=\"text\" class=\"" + options.labelClass + " " + options.labelTopClass + "\" />");
            var bottomLabel = $("<input type=\"text\" class=\"" + options.labelClass + " " + options.labelBottomClass + "\" />");
            
            parent.append(leftLabel);
            parent.append(rightLabel);
            parent.append(topLabel);
            parent.append(bottomLabel);
            
            topLabel.css({
                position: "absolute",
                left: 0,
                top: 0,
                zIndex: 1
            });
            
            bottomLabel.css({
                position: "absolute",
                left: 0,
                top: container.height() - bottomLabel.outerHeight(),
                zIndex: 1
            });
            
            leftLabel.css({
                position: "absolute",
                left: container.position().left,
                top: container.height() + cssToInt(leftLabel.css("margin-top")),
                zIndex: 1
            });

            rightLabel.css({
                position: "absolute",
                left: container.position().left + container.width() - rightLabel.outerWidth(),
                top: container.height() + cssToInt(leftLabel.css("margin-top")),
                zIndex: 1
            });
            
            // Add plus and minus
            ////////////////////////////////////////
            
            function append_button(button_class, icon_class){
                var button = $("<div class=\" " + button_class + " " + options.buttonClass + " ui-state-default ui-corner-all\"><div class=\"ui-icon " + icon_class + "\"></div></div>");
                var lastButton = parent.find("." + options.buttonClass + ":last");
                parent.append(button);
                
                var top = (lastButton.size() > 0 ? (lastButton.position().top + lastButton.outerHeight() + cssToInt(lastButton.css("margin-bottom")) + cssToInt(button.css("margin-top")) ) : (0));
                
                button.css({
                    position: "absolute",
                    left: container.position().left + container.width() + cssToInt(button.css("margin-left")),
                    top: top,
                    cursor: "pointer"
                });
                
                button.bind("mouseover", function(){
                    $(this).addClass("ui-state-hover");
                }).bind("mouseout", function(){
                    $(this).removeClass("ui-state-hover");
                });
                
                return button;
            }
            
            var plus = append_button(options.plusClass, "ui-icon-plus");
            var minus = append_button(options.minusClass, "ui-icon-minus");           
            
            plus.bind("click", function(){
                add_resizer(false, true, true);
            });
            
            minus.bind("click", function(){
                remove_selected_resizer(true);
            });
            
            // Add reset
            ////////////////////////////////////////
            
            var reset = append_button(options.resetClass, "ui-icon-arrowreturnthick-1-n");
            
            reset.bind("click", function(){
                remove_all_resizers(false, false);
                add_default_resizers(true);
            });
            
            // Handle text input in labels
            ////////////////////////////////////////
            
            function handle_text_input( ele, type ){
                var timeout;
                var resizer;
                var selection;
                
                function valid_val(val){ 
                    switch(type){
                        case "x.min":
                            return stats.bounds.x.min <= val && val <= stats.bounds.x.max && val < selection.x.max;
                        
                        case "x.max":
                            return stats.bounds.x.min <= val && val <= stats.bounds.x.max && val > selection.x.min;
                        
                        case "y.min":
                            return stats.bounds.y.min <= val && val <= stats.bounds.y.max && val < selection.y.max;
                        
                        case "y.max":
                            return stats.bounds.y.min <= val && val <= stats.bounds.y.max && val > selection.y.min;
                    }
                    return false;
                }
                
                function update_val(){

                    for(var i in resizers) {
                        resizer = resizers[i];
                        selection = stats.selections[i];
                        
                        if( resizer.hasClass(options.activeResizerClass) ) {
                            break;
                        }
                    }
                    
                    var val = parseFloat( $(ele).val() );
                    
                    if( typeof val == "number" && !isNaN(val) && valid_val(val) ) {

                        var position;
                        
                        // TODO: improve calculation so there are no height/width errors
                        switch(type){
                            case "x.min": 
                                position =  valToPos(val, container.width(), stats.bounds.x.min, stats.bounds.x.max);
                                resizer.css({
                                    left: position,
                                    width: valToPos(selection.x.max, container.width(), stats.bounds.x.min, stats.bounds.x.max) - position
                                });
                                selection.x.min = val;
                                break;
                                
                            case "x.max":
                                position =  valToPos(val, container.width(), stats.bounds.x.min, stats.bounds.x.max);
                                resizer.css({
                                    width: position - resizer.position().left
                                });
                                selection.x.max = val;
                                break;
                                
                            case "y.min": 
                                position =  valToPos(val, container.height(), stats.bounds.y.min, stats.bounds.y.max);
                                resizer.css({
                                    height: container.height() - resizer.position().top - position
                                });
                                selection.y.min = val;
                                break;
                                
                            case "y.max": 
                                position =  valToPos(val, container.height(), stats.bounds.y.min, stats.bounds.y.max);
                                resizer.css({
                                    top: container.height() - position,
                                    height: position - valToPos(selection.y.min, container.height(), stats.bounds.y.min, stats.bounds.y.max)
                                });
                                selection.y.max = val;
                                break;
                        }
                        
                        trigger_select([ type ]);
                    }
                    
                    timeout = undefined;
                    last_val = $(ele).val();
                }
                
                var last_val = $(ele).val();
                $(ele).bind("keydown", function(){
                    if($(ele).val() != last_val){
                        clearInterval(timeout);
                        timeout = setTimeout(update_val, options.labelUpdateDelay);
                    }
                }).bind("blur", function(){
                    if($(ele).val() != last_val) {
                        update_val();
                        if(!valid_val( parseFloat($(ele).val()) )){
                            update_labels([ type ]);
                        }
                    }
                });
            
            }
            
            handle_text_input(leftLabel, "x.min");
            handle_text_input(rightLabel, "x.max");
            handle_text_input(topLabel, "y.max");
            handle_text_input(bottomLabel, "y.min");
            
            // Add resizers
            ////////////////////////////////////////
            
            // silent
            function add_resizer(silent, select, trigger){
                var resizer = $("<div class=\"" + options.resizerClass + "\"></div>");
                container.append(resizer);
                
                if( $.inArray(resizer, resizers) < 0 ){
                    resizers.push(resizer);
                }
                
                var leftLine = $("<div class=\"" + options.lineClass + " " + options.lineLeftClass + "\"></div>");
                var rightLine = $("<div class=\"" + options.lineClass + " " + options.lineRightClass + "\"></div>");
                var topLine = $("<div class=\"" + options.lineClass + " " + options.lineTopClass + "\"></div>");
                var bottomLine = $("<div class=\"" + options.lineClass + " " + options.lineBottomClass + "\"></div>");
                
                resizer.append(leftLine);
                resizer.append(rightLine);
                resizer.append(topLine);
                resizer.append(bottomLine);
                
                var force_position = "absolute";
                var handle;
                resizer.draggable({
                    containment: "parent",
                    drag: function(){ select_change(); },
                    start: function(event, ui){
                        $(this).css("position", force_position);
                        select_start();
                    },
                    stop: function(event, ui){ 
                        $(this).css("position", force_position);
                        select_stop();
                    }
                }).resizable({
                    containment: "parent",
                    start: function(event, ui){ 
                        $(this).css("position", force_position);
                        
                        select_start( handle_types(handle) );
                        
                    },
                    stop: function(event, ui){ 
                        $(this).css("position", force_position);
                        
                        select_stop( handle_types(handle) );
                        
                    },
                    resize: function(event, ui){ 
                        select_change( handle_types(handle) );
                    },
                    handles: "all",
                    distance: 5
                }).css({
                    position: force_position,
                    zIndex: 1
                });
                
                $(".ui-resizable-handle").live("mousedown", function(){
                    handle = $(this);
                });
                
                function handle_types(handle){
                
                    var types = [];
                    
                    if( handle.hasClass("ui-resizable-w") || handle.hasClass("ui-resizable-nw") || handle.hasClass("ui-resizable-sw") ) {
                        types.push("x.min");
                    }
                    if( handle.hasClass("ui-resizable-e") || handle.hasClass("ui-resizable-ne") || handle.hasClass("ui-resizable-se") ) {
                        types.push("x.max");
                    }
                    if( handle.hasClass("ui-resizable-s") || handle.hasClass("ui-resizable-sw") || handle.hasClass("ui-resizable-se") ) {
                        types.push("y.min");
                    }
                    if( handle.hasClass("ui-resizable-n") || handle.hasClass("ui-resizable-nw") || handle.hasClass("ui-resizable-ne") ) {
                        types.push("y.max");
                    }
                    
                    return types;
                
                }
                
                //resizer.addClass("ui-state-default");
                
                resizer.find(".ui-icon-gripsmall-diagonal-se").removeClass("ui-icon-gripsmall-diagonal-se").addClass("ui-icon-grip-diagonal-se").css({
                    height: 16,
                    width: 16
                });
                
                if(!silent){
                    resizer.css({
                        width: 0.33 * container.width(),
                        height: 0.33 * container.height(),
                        left: 0.33 * container.width(),
                        top: 0.33 * container.height()
                    });
                }
                
                store_selections();
                
                resizer.bind("mousedown", function(){
                    $(this).siblings("." + options.resizerClass).removeClass(options.activeResizerClass).css({
                        zIndex: 1
                    });
                    $(this).addClass(options.activeResizerClass).css({
                        zIndex: 2
                    });
                    
                    update_labels();
                });
                
                
                var lrHeight = container.height() * 2;
                var tbWidth = container.width() * 2;
                var lineWidth = 1;
                
                leftLine.css({
                    height: lrHeight,
                    width: 1,
                    position: "absolute",
                    left: -1 * cssToInt(resizer.css("border-left-width")),
                    top: 0
                });
                
                rightLine.css({
                    height: lrHeight,
                    width: 1,
                    position: "absolute",
                    right: -1 * cssToInt(resizer.css("border-right-width")),
                    top: 0
                });
                
                topLine.css({
                    height: 1,
                    width: tbWidth,
                    position: "absolute",
                    right: 0,
                    top: -1 * cssToInt(resizer.css("border-top-width"))
                });
                
                bottomLine.css({
                    height: 1,
                    width: tbWidth,
                    position: "absolute",
                    right: 0,
                    bottom: -1 * cssToInt(resizer.css("border-bottom-width"))
                });
                
                if( !silent ) {
                    
                    resizer.show(options.addResizerAnimationType, {}, options.addResizerAnimationSpeed, function(){
                        if(select) {
                            resizer.trigger("mousedown");
                        }
                        
                        if(trigger){
                            trigger_select();
                        }
                    });
                } else if( select ) {
                    resizer.trigger("mousedown");
                    
                    if(trigger){
                        trigger_select();
                    }
                }

            }
            
            function remove_all_resizers(silent, trigger){
                
                for(var i in resizers){
                    var resizer = resizers[i];
                    
                    if( silent ) {
                        resizer.remove();
                    } else {
                        resizer.hide(options.removeResizerAnimationType, {}, options.removeResizerAnimationSpeed, function(){
                            $(this).remove();
                        });
                    }
                }
                resizers = [];
                
                if(trigger) {
                    trigger_select();
                }
            }
            
            // remove active resizer
            function remove_selected_resizer(trigger){
                var selected_resizers = $.grep( resizers, function(resizer){
                    return resizer.hasClass(options.activeResizerClass);
                });
                
                if( selected_resizers.length > 0 ) {
                
                    var selected_resizer = selected_resizers[0];
                    
                    var not_selected_resizers = $.grep( resizers, function(resizer){
                        return ! resizer.hasClass(options.activeResizerClass);
                    });
                    resizers = not_selected_resizers;
                    
                    selected_resizer.hide(options.removeResizerAnimationType, {}, options.removeResizerAnimationSpeed, function(){
                        $(this).remove();
                    });
                    
                    if(trigger) {
                        trigger_select();
                    }
                }
            }
            
            function add_default_resizers(trigger) {
                add_resizer(true, true, trigger);
            }
            
            container.append(graph);

            graph.css({
                position: "absolute",
                top: 0,
                left: 0,
                zIndex: 0,
                height: "100%",
                width: "100%"
            });
            
            
            // Determine bounds of data
            ////////////////////////////////////////
            
            var min_x = options.graphOptions.xaxis.min;
            var max_x = options.graphOptions.xaxis.max;
            var min_y = options.graphOptions.yaxis.min;
            var max_y = options.graphOptions.yaxis.max;
            for(var data_i in options.data){
                var data_set = options.data[data_i];
                
                for(var point_i in data_set){
                    var point = data_set[point_i];
                    var x = point[0];
                    var y = point[1];
                    
                    if(min_x == undefined || x < min_x) {
                        min_x = x;
                    }
                    
                    if(max_x == undefined || x > max_x) {
                        max_x = x;
                    }
                    
                    if(min_y == undefined || y < min_y) {
                        min_y = y;
                    }
                    
                    if(max_y == undefined || y > max_y) {
                        max_y = y;
                    }
                }
            }
            
            if(undefined == options.graphOptions.xaxis.min) {
                options.graphOptions.xaxis.min = min_x;
            }
            if(undefined == options.graphOptions.xaxis.max) {
                options.graphOptions.xaxis.max = max_x;
            }
            if(undefined == options.graphOptions.yaxis.min) {
                options.graphOptions.yaxis.min = min_y;
            }
            if(undefined == options.graphOptions.yaxis.max) {
                options.graphOptions.yaxis.max = max_y;
            }
            
            $.plot(graph, options.data, options.graphOptions);
            
            // Build selection and bounds stats
            ////////////////////////////////////////
            
            var stats = {
                bounds: {
                    x: {
                        min: min_x,
                        max: max_x
                    },
                    
                    y: {
                        min: min_y,
                        max: max_y
                    }
                },
                selections: []
            };
            $(this).data("statselstats", stats);
            
            // types == undefined => all
            function store_selections(types){
                debug("store_selections");
                debug(types);
            
                var old_selections = [];
                for(var i in stats.selections){
                    old_selections[i] = stats.selections[i];
                }
                
                stats.selections = [];
                debug(old_selections);
            
                for(var i in resizers){
                    var resizer = resizers[i];
                    
                    var new_min_x = posToVal( resizer.position().left, container.width(), min_x, max_x );
                    var new_max_x = posToVal( resizer.position().left + resizer.outerWidth(), container.width(), min_x, max_x );
                    var new_min_y = posToVal( container.height() - resizer.position().top - resizer.outerHeight(), container.height(), min_y, max_y );
                    var new_max_y = posToVal( container.height() - resizer.position().top, container.height(), min_y, max_y );
                         
                    var selection = {
                        x: {
                            min: ( (types == undefined || $.inArray("x.min", types) >= 0) ? (new_min_x) : (old_selections[i].x.min) ),
                            max: ( (types == undefined || $.inArray("x.max", types) >= 0) ? (new_max_x) : (old_selections[i].x.max) )
                        },
                        
                        y: {
                            min: ( (types == undefined || $.inArray("y.min", types) >= 0) ? (new_min_y) : (old_selections[i].y.min) ),
                            max: ( (types == undefined || $.inArray("y.max", types) >= 0) ? (new_max_y) : (old_selections[i].y.max) )
                        }
                    };
                    
                    debug(selection);
                    
                    stats.selections.push(selection);
                }
            }
            store_selections(); // initial store
            
            add_default_resizers(true); // initial resizers
            
            function trigger_select(types){
                debug("trigger_select");
                debug(types);
            
                update_labels(types);
                store_selections(types);
                if(options.select != undefined) {
                    options.select(stats);
                }
                $(this).trigger("select");
            }
            
            function trigger_start(types){
                debug("trigger_start");
                debug(types);
            
                update_labels(types);
                store_selections(types);
                if(options.start != undefined) {
                    options.start(stats);
                }
                $(this).trigger("selectstart");
            }
            
            function trigger_stop(types){
                debug("trigger_stop");
                debug(types);
            
                update_labels(types);
                store_selections(types);
                if(options.stop != undefined) {
                    options.stop(stats);
                }
                $(this).trigger("selectstop");
            }
            
            // ui triggered stores
            var timeout;
            function select_change(types){
                
                update_labels(types);
                
                if(options.selectFrequency <= 0){
                    trigger_select(types);
                    return;
                }
                
                if(!timeout) {
                    clearTimeout(timeout);
                    timeout = setTimeout(function(){
                            timeout = undefined;
                            trigger_select(types)
                        }, options.selectFrequency);
                }
            }
            
            function select_start(types){
                trigger_start(types); // no time outs for now
            }
            
            function select_stop(types){
                trigger_stop(types); // no time outs for now
            }
                     
            function update_labels(types){

                for(var i in resizers){
                    var resizer = resizers[i];
                    var selection = stats.selections[i];
                    
                    function in_bounds(reg, min, max){
                        return Math.min( Math.max(reg, min), max );
                    }
                    
                    var top, bottom, left, right;
                    
                    // 2x calculation
                    // 1x with current position
                    // 1x with updated calculated positions as bounds
                    // why? it prevents one resizer from affecting the bounds of the labels for another resizer
                    for(var j = 0; j < 2; j++){
                    
                        var reg_top = resizer.position().top - topLabel.outerHeight();
                        var min_top = 0;
                        var max_top = (bottom == undefined ? bottomLabel.position().top : bottom) - topLabel.outerHeight();
                        var top = in_bounds(reg_top, min_top, max_top);
                        
                        var reg_bottom = resizer.position().top + resizer.height();
                        var min_bottom = (top == undefined ? topLabel.position().top : top) + topLabel.outerHeight();
                        var max_bottom = container.height() - bottomLabel.outerHeight();
                        var bottom = in_bounds(reg_bottom, min_bottom, max_bottom);
                        
                        var reg_left = container.position().left + resizer.position().left - leftLabel.outerWidth();
                        var min_left = container.position().left;
                        var max_left = (right == undefined ? rightLabel.position().left : right) - leftLabel.outerWidth();
                        var left = in_bounds(reg_left, min_left, max_left);
                        
                        var reg_right = container.position().left + resizer.position().left + resizer.width();
                        var min_right = (left == undefined ? leftLabel.position().left : left) + rightLabel.outerWidth();
                        var max_right = container.position().left + container.width() - rightLabel.outerWidth();
                        var right = in_bounds(reg_right, min_right, max_right);
                    }
                    
                    if( resizer.hasClass(options.activeResizerClass) && selection != undefined ){
                        if(types == undefined || $.inArray("x.min", types) >= 0 ) {
                            leftLabel.val(selection.x.min).show().css({ left: left });
                        }
                        if(types == undefined || $.inArray("x.max", types) >= 0 ) {
                            rightLabel.val(selection.x.max).show().css({ left: right });
                        }
                        if(types == undefined || $.inArray("y.max", types) >= 0 ) {
                            topLabel.val(selection.y.max).show().css({ top: top });
                        }
                        if(types == undefined || $.inArray("y.min", types) >= 0 ) {
                            bottomLabel.val(selection.y.min).show().css({ top: bottom });
                        }
                        return;
                    }
                    
                    
                    
                }
                
                leftLabel.val("").hide();
                rightLabel.val("").hide();
                topLabel.val("").hide();
                bottomLabel.val("").hide();
            }
            update_labels(); // initial update
        });
    };
    
    $.fn.statisticalRange = function(points){ 
        var ret;
        
        $(this).each(function(){
        
            // return the stats table itself
            if( points == undefined ) {
                ret = $(this).data("statselstats");
            
            // get list of points inside bounds
            } else {
                var stats = $(this).data("statselstats");
                var selections = stats.selections;
                var in_list = [];
                
                for(var i in points){
                    var point = points[i];
                    var x = point[0];
                    var y = point[1];
                    
                    for(var j in selections){
                        var selection = selections[j];
                        
                        if( selection.x.min <= x && x <= selection.x.max && 
                            selection.y.min <= y && y <= selection.y.max ) {
                            
                            in_list.push(point);
                            break;
                        }
                    }
                    
                }
                
                ret = in_list;
            }
        });
        return ret;
    };
    
    // short names
    $.fn.statsel = $.fn.statisticalSelectable;
    $.fn.statrange = $.fn.statisticalRange;
    
})(jQuery);  