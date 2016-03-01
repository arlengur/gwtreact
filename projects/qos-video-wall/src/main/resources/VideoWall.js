var AllStreams = [
	{cities: OrbitaM, satellites: OrbitaM_Sat, parentId: 'TimeZones0', styling: false,                    height: 110, width: 143},
	{cities: Orbita4, satellites: Orbita4_Sat, parentId: 'TimeZones2', styling: 'border-color: #656566;', height:  90, width: 133},
	{cities: Orbita3, satellites: Orbita3_Sat, parentId: 'TimeZones4', styling: 'border-color: #323233;', height:  90, width: 133},
	{cities: Orbita2, satellites: Orbita2_Sat, parentId: 'TimeZones6', styling: 'border-color: #656566;', height:  90, width: 133},
	{cities: Orbita1, satellites: Orbita1_Sat, parentId: 'TimeZones8', styling: 'border-color: #323233;', height:  90, width: 133}
];

// использовать скриншоты или видео
var useScreenshots = true;
// интервал обновления скришнотов в секундах
var screenshotRefreshInterval = 10;
var qligentRed5PluginPath = "qligentPlayer"
var screenshotServletPath = "stream_image";

var AllCities = {};
AllStreams.map(function(stream){ jQuery.extend(AllCities, stream.cities, stream.satellites) });
var cityNameForSinglePlayerWindow;
	
function createVideoWall() {
	previousStreamCityCount=0;
	initDelay=0;
	for (var stream in AllStreams) {
        if (useScreenshots) {
            // нет необходимости в паузах при инициализации
            processStream(AllStreams[stream]);
        }
        else {
            setTimeout(processStream, initDelay + previousStreamCityCount*200, AllStreams[stream]);
            initDelay += previousStreamCityCount*200;
            previousStreamCityCount = Object.keys(AllStreams[stream].cities).length +  Object.keys(AllStreams[stream].satellites).length;
        }
	}
    
    if (useScreenshots) {
        setInterval(refreshScreenshots, screenshotRefreshInterval * 1000);
    }
}

function refreshScreenshots() {
	var images = $('img.screenshot');
    for (var index = 0; index < images.size(); index++) {
        var image = images[index];
        var newSrc = $(image).attr('originalSrc') + '?' + new Date().getTime();
        $(image).attr('src', newSrc);
		$(image).error(function() {
			$(this).attr('src', "images/missing.png");
		})
    }
}


function getVideoLink(cityInfo) {
	return 'rtmp://' + cityInfo["ip"] + cityInfo["url"];
}

function getImageLink(cityInfo) {
	return 'http://' + cityInfo["ip"] + cityInfo["url"];
}

playerURL = "bst-flash-player-1.3.swf";

function getVideoPlayerHtml(URL, name, height, width) {
	return '<embed type="application/x-shockwave-flash" id="' + name + '" src="' + playerURL + '" name="' + name + '" ' +
		'flashvars="playerId=' + name + '&amp;autoplay=true&amp;mediaURL=' + URL + '" allowscriptaccess="always"' +
		'allowfullscreen="true" bgcolor="#000000" height="' + height + '" width="' + width + '">';
}

function getScreenshotPlayerHtml(URL, name, height, width) {
    var imgUrl = URL.replace(qligentRed5PluginPath + "/", qligentRed5PluginPath + "/" + screenshotServletPath+ "/");
	return '<img class="screenshot" id="' + name + '" originalSrc="' + imgUrl + '" src="' + imgUrl + '" name="' + name + '" height="' + height + '" width="' + width + '">';
}

function createPlayer(cityName, cityInfo, parent, styling, height, width) {
	var playerHTML = useScreenshots ? 
        getScreenshotPlayerHtml(getImageLink(cityInfo), 'player' + cityName, height, width) : 
        getVideoPlayerHtml(getVideoLink(cityInfo), 'player' + cityName, height, width);
	var html = styling ? '<table style = "' + styling + '">' : '<table>';
	html += '<tr><td><a class="cityLink" href="#" onclick="newPlayerWindow(\'' + cityName + '\'); return false;">' + cityName + '</a></td></tr>';
	html += '<tr><td><span id="video' + cityName + '">' + playerHTML + '</span></td></tr>';
	html += '</table>';
	var spn = document.createElement('span');
	spn.innerHTML = html;
	parent.appendChild(spn);
}

function processStream(stream) {
	var parent = document.getElementById(stream.parentId);
	sortedCityNames = Object.keys(stream.cities).sort();
	for (i = 0; i < sortedCityNames.length; i++) {
		createPlayer(sortedCityNames[i], stream.cities[sortedCityNames[i]], parent, stream.styling, stream.height, stream.width);
	}
	for (var cityName in stream.satellites) {
		createPlayer(cityName, stream.satellites[cityName], parent, stream.styling, stream.height, stream.width);
	};
}

function newPlayerWindow(cityName) {
	cityNameForSinglePlayerWindow = cityName;
	var playerWindow = window.open("singlePlayer.html", '_blank', "width=400, height=400");
}

function vrem() {
	ndata = new Date();
	var moscow = 4;
	var timezone = ndata.getTimezoneOffset() / 60 + moscow;
	hours = ndata.getHours() + timezone;
	hours2 = ndata.getHours() + 2 + timezone;
	if (hours2 > 23) {
		hours2 = hours2 - 23;
	}
	hours4 = ndata.getHours() + 4 + timezone;
	if (hours4 > 23) {
		hours4 = hours4 - 23;
	}
	hours6 = ndata.getHours() + 6 + timezone;
	if (hours6 > 23) {
		hours6 = hours6 - 23;
	}
	hours8 = ndata.getHours() + 8 + timezone;
	if (hours8 > 23) {
		hours8 = hours8 - 23;
	}
	mins = ndata.getMinutes();
	secs = ndata.getSeconds();
	tochki = '<span style="opacity: 0.2;">:</span>';
	if (mins < 10) {
		mins = "0" + mins
	}
	if (secs < 10) {
		secs = "0" + secs
	}
	if (secs % 2 == 0) {
		document.getElementById("vremya").innerHTML = hours + ":" + mins;
		document.getElementById("vremya2").innerHTML = hours2 + ":" + mins;
		document.getElementById("vremya4").innerHTML = hours4 + ":" + mins;
		document.getElementById("vremya6").innerHTML = hours6 + ":" + mins;
		document.getElementById("vremya8").innerHTML = hours8 + ":" + mins;
	} else {
		document.getElementById("vremya").innerHTML = hours + tochki + mins;
		document.getElementById("vremya2").innerHTML = hours2 + tochki + mins;
		document.getElementById("vremya4").innerHTML = hours4 + tochki + mins;
		document.getElementById("vremya6").innerHTML = hours6 + tochki + mins;
		document.getElementById("vremya8").innerHTML = hours8 + tochki + mins;
	}
	setTimeout("vrem()", 1000);
}
