var Color = net.brehaut.Color;

var margin = 30;
//Width and height
var w = 400 - (2*margin);
var h = 400 - (2*margin);

// transform to using audio video/data as 1st level keys, channel ids as second level keys
var avdView = (chViewData) => {
    var extractParam = (param, data) =>
        _.chain(data)
            .filter((ch) => !((typeof ch.id == 'string') && ch.id.indexOf('NULL') > -1))
            .map((ch)=> {return {id: _.padLeft(ch.id, 3, '0')+'-'+param,
                                 value: (_.find(ch.data, (p)=>p.id == param)).value}})
            .value();
    var avd = _.chain(['audio', 'video', 'data'])
        .map((p)=>{return {id:'total_'+p, data: extractParam(p, chViewData)}})
        .value();
    var oldNull = _.find(chViewData, (d)=>d.id=='NULL');
    avd.push({id: 'NULL ', data:[{id: 'NULL', value: _.find(oldNull.data, (d)=>d.id == 'NULL').value}]});
    console.log(avd);
    return avd;
};

var outerRadius = w / 2;
var innerRadius = 0;
var arc = d3.svg.arc()
    .innerRadius(innerRadius)
    .outerRadius(outerRadius);
var arcHover = d3.svg.arc()
    .innerRadius(innerRadius)
    .outerRadius(outerRadius + 10);

var compare = (a,b) => {
    if(a.id == b.id) return 0;
    if(a.id < b.id) return -1;
    if(a.id > b.id) return 1;
};

var pieVal = d3.layout.pie()
    .value((d)=>d.value)
    .sort(compare);

var pieTotal = d3.layout.pie()
    .value((pr) => _.chain(pr.data).pluck('value').sum().value())
    .sort(compare);

var color10 = d3.scale.category10();
var colorFn = (id) => {
    if(typeof id == 'string') {
        if(id.indexOf('audio') > -1) return  "#2ca02c"; //"#00b519" commented colors are from markup
        if(id.indexOf('video') > -1) return "#d62728";  //"#ff1500"
        if(id.indexOf('data') > -1) return "#1f77b4";   //"#0073ff"
        if(id.indexOf('NULL') > -1) return '#474747';
    }
    return color10(id)
};

var expandedSegment;
var expandedColor;

var hover = (d, id) => {
    d3.selectAll(".hover-arc").remove();
    d3.select("#"+piechartId(id))
        .append("path")
        .attr("stroke", "none")
        .attr("fill", colorFn(d.data.id))
        .attr("d", arcHover({startAngle: d.startAngle, endAngle: d.endAngle}))
        .attr("class", "hover-arc")
        .on("mouseout", () => {
            d3.selectAll(".hover-arc").remove()
        })
};

var updateLegend = (id, data, colors) => {
    var selection = d3.select("#"+id)
        .selectAll(".legend-item")
        .data(data, (d)=>d.id);
    selection.exit().remove();
    var items = selection.enter()
        .append("div")
        .attr("class", "legend-item");
    items.append("div")
        .attr("class", "legend-box")
        .style("background", (d)=>colors(d.id));
    items.append("p")
        .attr("class", "legend-label")
        .text((d)=>d.id)
        .attr("title", (d)=>d.id)
};

var hoverText = (id, total, value) => {
    var percent = (value*100/total).toFixed(1);
    return ""+id+": "+percent+"% ("+value.toFixed(2)+" Mbit/s of total "+total.toFixed(2)+" Mbit/s)";
};

var displayData = (mode, newData, id) => {
    var total = totalCapacity(newData);
    var displayedData = mode == "total" ? avdView(newData) : newData;
    d3.selectAll(".hover-arc").remove();
    var selection = d3.select("#"+piechartId(id))
        .selectAll(".arc")
        .data(pieTotal(displayedData), (d)=>d.data.id);
    selection.exit()
        .transition()
        .duration(500)
        .each("start",
        // can't use es6 lambda here because of different 'this' semantics
        function() {
            d3.select(this).on("click", null);
            d3.select(this).on("mouseover", null);
            d3.select("#widget-1-parameters").on("click", null);
            d3.select("#widget-1-channels").on("click", null)
        })
        .attrTween("d", (d) => {
            var inter = d3.interpolate(d, {startAngle: 0, endAngle: 0});
            return (t) => arc(inter(t))
        })
        .remove();
    selection.enter()
        .append("path")
        .attr("class", "arc")
        .attr("stroke", "white")
        .attr("fill", (d) => colorFn(d.data.id))
        .transition()
        .delay(500)
        .duration(500)
        .attrTween("d", (d) => {
            var inter = d3.interpolate({
                startAngle: 0,
                endAngle: 0
            }, d);
            return (t) => arc(inter(t))
        })
        .each("end",
            // can't use es6 lambda here because of different 'this' semantics
            function () {
                d3.select(this)
                    .on("mouseover", (d)=> hover(d,id))
                    .on("click",
                    (d) => {
                        if(!((typeof d.data.id == 'string') && (d.data.id.indexOf('NULL') > -1))) {
                            d3.selectAll(".hover-arc").remove();
                            expandedSegment = d;
                            expandedColor = colorFn(d.data.id);
                            expandSegment(id, d.data.data, d, displayedData)
                        }
                    });
                d3.select("#"+totalId(id)).on("click", ()=>showTotal(newData, id));
                d3.select("#"+channelsId(id)).on("click", ()=>showChannels(newData, id));
        });

    selection
        .append("title")
        .text((d)=>hoverText(
            d.data.id,
            total,
            _.chain(d.data.data).pluck('value').sum().value()));

    updateLegend(legendId(id), displayedData, colorFn)
};

var showTotal = (data, id) => displayData("total", data , id);
var showChannels = (data, id) => displayData("channels", data, id);

var shadesOfColor = function(baseColor, num) {
    return _.chain(_.range(num))
        .map((n) => (1/4 + n/(2*num)))
        .map((k) => Color(baseColor).setLightness(1-k).toString())
        .value();
};

var expandSegment = function(id, expandedData, segment, collapsedData) {
    var total = _.chain(expandedData).pluck('value').sum().value();
    d3.selectAll(".hover-arc").remove();
    var startAngle = segment.startAngle;
    var endAngle = segment.endAngle;
    var angleStep = (endAngle - startAngle) / expandedData.length;
    var length = expandedData.length;
    // calculate arc angles for animation
    var ids = _.chain(expandedData)
        .pluck('id')
        .sortBy()
        .value();
    var order = d3.scale.ordinal().domain(ids).range(_.range(length));
    var angles = (id) => {
        return {
            startAngle: startAngle + order(id)*angleStep,
            endAngle: startAngle + (order(id)+1)*angleStep
        }
    };
    var expandedColors;
    switch(segment.data.id) {
        //case 'total_video': expandedColors = d3.scale.ordinal().range(shadesOfColor("#d62728", length)); break;
        case 'total_video': expandedColors = d3.scale.ordinal().domain(ids).range(shadesOfColor("#d62728", length)); break;
        case 'total_audio': expandedColors = d3.scale.ordinal().domain(ids).range(shadesOfColor("#2ca02c", length)); break;
        case 'total_data':  expandedColors = d3.scale.ordinal().domain(ids).range(shadesOfColor("#1f77b4", length)); break;
        default: expandedColors = colorFn
    }
    var selection = d3.select("#"+piechartId(id))
        .selectAll(".arc")
        .data(pieVal(expandedData), (d)=>d.data.id);
    selection.exit()
        .remove();
    selection.enter()
        .append("path")
        .attr("stroke", "white")
        .attr("class", "arc")
        .transition()
        .duration(500)
        .attrTween("fill", (d) => d3.interpolate(expandedColor, expandedColors(d.data.id)))
        .attrTween("d", (d) => {
            var inter = d3.interpolate(angles(d.data.id), d);
            return (t) => arc(inter(t))
        })
        .each("end",
        // can't use es6 lambda here because of different 'this' semantics
        function () {
            d3.select(this)
                .on("click", ()=>collapseSegment(id, collapsedData, expandedSegment, angles))
                .on("mouseover", (d)=> hover(d,id))
        });

    selection
        .append("title")
        .text((d)=>hoverText(
            d.data.id,
            total,
            d.data.value));

    updateLegend(legendId(id), expandedData, expandedColors)
};

var collapseSegment = function(id, collapsedData, segment, angles) {
    var total = totalCapacity(collapsedData);
    d3.selectAll(".hover-arc").remove();
    var selection = d3.select("#"+piechartId(id))
        .selectAll(".arc")
        .data(pieTotal(collapsedData), (d)=>d.data.id);
    selection.exit()
        .transition()
        .duration(500)
        .each("start",
        // can't use es6 lambda here because of different 'this' semantics
        function() {
            d3.select(this).on("click", null);
            d3.select(this).on("mouseover", null);
        })
        .attr("fill", () => expandedColor)
        .attrTween("d", (d) => {
            var inter = d3.interpolate(d, angles(d.data.id));
            return (t) => arc(inter(t))
        })
        .remove();

    selection.enter()
        .append("path")
        .attr("class", "arc")
        .attr("fill", (d) => colorFn(d.data.id))
        .attr("stroke", "white")
        .on("click", (d) => {
            if(!((typeof d.data.id == 'string') && (d.data.id.indexOf('NULL') > -1))) {
                expandedSegment = d;
                expandedColor = colorFn(d.data.id);
                expandSegment(id, d.data.data, d, collapsedData)
            }
        })
        .transition()
        .delay(500)
        .attr("d", arc)
        .each("end",
        // can't use es6 lambda here because of different 'this' semantics
        function () {d3.select(this).on("mouseover", (d) => hover(d, id))});
    expandedSegment = null;
    expandedColor = null;

    selection
        .append("title")
        .text((d)=>hoverText(
            d.data.id,
            total,
            _.chain(d.data.data).pluck('value').sum().value()));

    updateLegend(legendId(id), collapsedData, colorFn)
};

var piechartId = (id) => "piechart-"+id;
var legendId = (id) => "legend-"+id;
var channelsId = (id) => "channels-"+id;
var totalId = (id) => "total-"+id;

var totalCapacity = (data) =>
    _.chain(data)
        .pluck("data")
        .flatten()
        .pluck("value")
        .sum()
        .value();

window.initPieChart = (id, data, configuredCapacity) => {
    console.log("initPieChart");
    console.log(data);
    var used = totalCapacity(data);
    var unused = configuredCapacity-used;
    var capacity = configuredCapacity;
    if (unused < 0) {
        unused = 0;
        capacity = Math.ceil(used);
    }
    var dataCopy = _.clone(data, true);
    dataCopy.push({id: 'NULL', data:[
        {id: 'audio', value: 0}, {id: 'video', value: 0},  {id: 'data', value: 0},
        {id: 'NULL', value: unused}
    ]});

    var selection = $('#'+id);
    if($('#'+channelsId(id)).length == 0) {
        selection.append('<button class="widget-button" id="'+channelsId(id)+'">Channels</button>');
    }
    if($('#'+totalId(id)).length == 0) {
        selection.append('<button class="widget-button" id="'+totalId(id)+'">Parameters</button>');
    }
    if($('#'+legendId(id)).length == 0) {
        selection.append('<div class="legend" id="'+legendId(id)+'"></div>')
    }
    if(selection.find('.pie-container').length == 0) {
        selection.append('<div class="pie-container"></div>');
        d3.select('#'+id)
            .select(".pie-container")
            .append("svg")
            .attr("viewBox", "0 0 " + (w+2*margin) + " " + (h+2*margin))
            .attr("preserveAspectRatio", "xMidYMid meet")
            .style("max-height", "calc(100% - 50px)")
            .style("max-width", "100%")
            .style("display", "block")
            .style("margin", "0 auto")
            .append("g")
            .attr("id", piechartId(id))
            .attr("transform", "translate(" + (outerRadius+margin) + "," + (outerRadius+margin) + ")")
            .on("mouseout", ()=>d3.selectAll(".hover-arc").remove());
        d3.select('#'+id)
            .select(".pie-container")
            .append("p")
            .attr("class", "total-bitrate")
            .text("Capacity: " + capacity + "Mbit/s");
        d3.select('#'+id)
            .select(".pie-container")
            .append("p")
            .attr("class", "total-bitrate")
            .text("Total bitrate: " + (used.toFixed(2)) + "Mbit/s");
    }

    showChannels(dataCopy,id)
};
