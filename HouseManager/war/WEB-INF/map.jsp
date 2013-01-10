<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Case a Pavia...</title>
        
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <style type="text/css">
            html { height: 100% }
            body { height: 100%; margin: 0; padding: 0 }
            #map_canvas { height: 100% }
        </style>
        <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDG2ho3ev9azmNy8uwiC1sz9HmudJ9KFtY&sensor=false">
        </script>
        <script type="text/javascript">
            function initialize() {
                var mapOptions = {
                    center: new google.maps.LatLng(45.185182663283705, 9.158477783203125),
                    zoom: 8,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

                var southWest = new google.maps.LatLng(45.212036101115885, 9.116249084472656);
                var northEast = new google.maps.LatLng(45.168725648565285, 9.203453063964844);
                var bounds = new google.maps.LatLngBounds(southWest,northEast);
                map.fitBounds(bounds);
              
                <c:forEach var="announce" items="${announces}">
                    var location = new google.maps.LatLng(
                        "${announce.lat}", "${announce.lon}");
                    var marker = new google.maps.Marker({
                        position: location,
                        map: map
                    });
                    marker.setTitle("${announce.title}");
                    google.maps.event.addListener(marker, 'click', function() {
                        map.setCenter(marker.getPosition());
                    });                
                </c:forEach>              
            }
        </script>
    </head>

<body onload="initialize()">
    <c:choose>
        <c:when test="${user != null}">
            <p>
                Welcome, ${user}!
            </p>
        </c:when>
        <c:otherwise>
            <p>
                Welcome!
            </p>
        </c:otherwise>
    </c:choose>
    
    <div id="map_canvas" style="width:100%; height:100%"></div>
    
</body>
