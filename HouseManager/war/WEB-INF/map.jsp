<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Case a Pavia...</title>
        
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <link rel="stylesheet" type="text/css" href="style.css">
        
        <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDG2ho3ev9azmNy8uwiC1sz9HmudJ9KFtY&sensor=false">
        </script>
        <script type="text/javascript">
            function initialize() {
                var mapOptions = {
                    //center: new google.maps.LatLng(45.185182663283705, 9.158477783203125),
                    zoom: 8,
                    mapTypeId: google.maps.MapTypeId.SATELLITE
                };
                var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

                //Pavia borders
                var southWest = new google.maps.LatLng(${mapSWLat}, ${mapSWLng});
                var northEast = new google.maps.LatLng(${mapNELat}, ${mapNELng});
                var bounds = new google.maps.LatLngBounds(southWest,northEast);
                map.fitBounds(bounds);
                
                //infiwindow global object
                infoWindow = new google.maps.InfoWindow();

                <%--
                //<c:forEach var="announce" items="${announces}">
                //    createMarker(map, '${announce.lat}', '${announce.lon}', "${announce.title}", "${announce.detailUrl}");
                //</c:forEach>
                --%>
                var announces = JSON.parse('${announces}');
                
                for(var i=0, len=announces.length; i < len; i++) {
                    createMarker(map, announces[i]);
                }
            }
            
            function createMarker(map, announce) {
                //filter by area
                //if (announce.area < 55) return;
                var location = new google.maps.LatLng(announce.lat, announce.lon);
                var marker = new google.maps.Marker({
                    position: location,
                    map: map
                });
                marker.setTitle(unescape(announce.title));
                google.maps.event.addListener(marker, 'click', function() {
                    infoWindow.setContent(createContent(announce));
                    infoWindow.open(map, marker);
                });
            }
            
            function createContent(announce) {
                var content = "<strong>" + unescape(announce.title) + "</strong> <br>"
                content += "<table><tr>"
                content += "<td>"
                content += "<img src=\"" + announce.imgUrl + "\" alt=\"" + announce.title + "\">";
                content += "</td><td>"
                content += "<strong>Area: </strong>" + announce.area + " mq<br>"
                content += "<strong>Prezzo: </strong>";
                if (announce.price > 0) content += addCommas(announce.price) + " euro"; else content += "Sconosciuto";
                content += "</td>"
                content += "</tr></table> <br>"
                content += unescape(announce.shortDesc) + "<br>";
                content += "<a href=\"" + announce.detailUrl + "\" target=\"_black\">Annuncio originale</a>";
                return content;
            }
            
            function addCommas(nStr)
            {
                nStr += '';
                x = nStr.split('.');
                x1 = x[0];
                x2 = x.length > 1 ? '.' + x[1] : '';
                var rgx = /(\d+)(\d{3})/;
                while (rgx.test(x1)) {
                    x1 = x1.replace(rgx, '$1' + '.' + '$2');
                }
                return x1 + x2;
            }            
        </script>
    </head>

<body onload="initialize()">

    <div id="navigation">
	    <p>
	    <c:choose>
	        <c:when test="${areAgentsRunning}">
	            Latest refresh of data: ${latestDataUpdate}
	        </c:when>
	        <c:otherwise>
	            Data refresh in progress...
	        </c:otherwise>
	    </c:choose>
	    <br>Total announces: ${totalAnnounces}
	    </p>
    </div>

    <!-- Remember, one of the divs, content or map_canvas, has to have the height to 100%, and body too
         Reference: http://stackoverflow.com/questions/10712523/how-to-set-a-map-to-div-within-another-div
    -->    
    <div id="content">
        <div id="map_canvas"></div>
    </div>
    
</body>
