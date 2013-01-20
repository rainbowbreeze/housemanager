<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Andamento prezzi vendita case a Pavia</title>
        
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <link rel="stylesheet" type="text/css" href="style.css">
        
        <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDG2ho3ev9azmNy8uwiC1sz9HmudJ9KFtY&sensor=false&libraries=visualization">
        </script>
        <script type="text/javascript">
            function initialize() {
                var map;
                var announces;
                var heatmap;
                var pointarray = [];
                
                var mapOptions = {
                    //center: new google.maps.LatLng(45.185182663283705, 9.158477783203125),
                    //zoom: 8,
                    mapTypeId: google.maps.MapTypeId.HYBRID
                };
                map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

                var southWest = new google.maps.LatLng(${mapSWLat}, ${mapSWLng});
                var northEast = new google.maps.LatLng(${mapNELat}, ${mapNELng});
                var bounds = new google.maps.LatLngBounds(southWest,northEast);
                map.fitBounds(bounds);

                //infiwindow global object
                infoWindow = new google.maps.InfoWindow();

                announces = JSON.parse('${announces}');

                for(var i=0, len=announces.length; i < len; i++) {
                    createMarker(announces[i], pointarray);
                }

                var gradient = [
					'rgba(0, 255, 255, 0)',
					'rgba(0, 255, 255, 1)',
					'rgba(0, 191, 255, 1)',
					'rgba(0, 127, 255, 1)',
					'rgba(0, 63, 255, 1)',
					'rgba(0, 0, 255, 1)',
					'rgba(0, 0, 223, 1)',
					'rgba(0, 0, 191, 1)',
					'rgba(0, 0, 159, 1)',
					'rgba(0, 0, 127, 1)',
					'rgba(63, 0, 91, 1)',
					'rgba(127, 0, 63, 1)',
					'rgba(191, 0, 31, 1)',
					'rgba(255, 0, 0, 1)'
				]                
                var heatmap = new google.maps.visualization.HeatmapLayer({
                    data: pointarray,
                    radius: 50,
                    gradient: gradient
                });
                heatmap.setMap(map);
            }

            function createMarker(announce, pointArray) {
                if (announce.price <= 0) return;
                var position = new google.maps.LatLng(announce.lat, announce.lon);
                var marker = new google.maps.Marker({
                    location: position,
                    weight: announce.price / announce.area
                });
                console.log(marker.weight);
                //adds to markers array
                pointArray.push(marker);
            }
        </script>
    </head>

<body onload="initialize()">

    <div id="navigation">
        <p>
            <span class="paramTitle">Annunci totali: </span>${totalAnnounces}
        </p>

        <c:choose>
            <c:when test="${areAgentsRunning}">
                <p>
                    <div class="paramTitle">Aggiornamento: </div>
                    ${latestDataUpdate}
                </p>
            </c:when>
            <c:otherwise>
                Refresh dei dati in corso...
            </c:otherwise>
        </c:choose>
    </div>
        
    <!-- Remember, one of the divs, content or map_canvas, has to have the height to 100%, and body too
         Reference: http://stackoverflow.com/questions/10712523/how-to-set-a-map-to-div-within-another-div
    -->    
    <div id="content">
        <div id="map_canvas"></div>
    </div>
    
</body>
