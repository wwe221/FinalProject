<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script
	src="https://api2.sktelecom.com/tmap/js?version=1&format=javascript&appKey=a9ee13e1-cb7e-46a8-b144-14bfd0103a90"></script>
<script type="text/javascript"></script>
<script>							
// 1. ���� ����
function initTmap(){
	// map ����
	// Tmap.map�� �̿��Ͽ�, ������ �� div, ����, ���̸� �����մϴ�.
	map = new Tmap.Map({
	div : 'map_div',
	width : "100%",
	height : "400px",
});
map.setCenter(new Tmap.LonLat("127.058908811749", "37.52084364186228").transform("EPSG:4326", "EPSG:3857"), 12);
routeLayer = new Tmap.Layer.Vector("route");
map.addLayer(routeLayer);

markerStartLayer = new Tmap.Layer.Markers("start");
markerEndLayer = new Tmap.Layer.Markers("end");
markerWaypointLayer = new Tmap.Layer.Markers("waypoint");
markerWaypointLayer2 = new Tmap.Layer.Markers("waypoint2");
pointLayer = new Tmap.Layer.Vector("point");

// 2. ����, ���� �ɺ����
// ����
map.addLayer(markerStartLayer);

var size = new Tmap.Size(24, 38);
var offset = new Tmap.Pixel(-(size.w / 2), -size.h);
var icon = new Tmap.IconHtml("<img src='http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png' />", size, offset);
var marker_s = new Tmap.Marker(new Tmap.LonLat("127.02810900563199", "37.519892712436906").transform("EPSG:4326", "EPSG:3857"), icon);
markerStartLayer.addMarker(marker_s);

// ����
map.addLayer(markerEndLayer);

var size = new Tmap.Size(24, 38);
var offset = new Tmap.Pixel(-(size.w / 2), -size.h);
var icon = new Tmap.IconHtml("<img src='http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png' />", size, offset);
var marker_e = new Tmap.Marker(new Tmap.LonLat("127.13281976335786", "37.52130314703887").transform("EPSG:4326", "EPSG:3857"), icon);
markerEndLayer.addMarker(marker_e);

//������ ��Ŀ ����
markerWaypointLayer.clearMarkers();
markerWaypointLayer2.clearMarkers();


// 3. ������ �ɺ� ���
map.addLayer(markerWaypointLayer);

var size = new Tmap.Size(24, 38);
var offset = new Tmap.Pixel(-(size.w / 2), -size.h); 
var icon = new Tmap.IconHtml("<img src='http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png' />", size, offset);
var marker = new Tmap.Marker(new Tmap.LonLat("127.04724656694417","37.524162226778515").transform("EPSG:4326", "EPSG:3857"), icon);
markerWaypointLayer.addMarker(marker);

var size = new Tmap.Size(24, 38);
var offset = new Tmap.Pixel(-(size.w / 2), -size.h);
var icon = new Tmap.IconHtml("<img src='http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png' />", size, offset);
var marker = new Tmap.Marker(new Tmap.LonLat("127.10887300128256","37.5289781669373").transform("EPSG:4326", "EPSG:3857"), icon);
markerWaypointLayer.addMarker(marker);

markerWaypointLayer2.clearMarkers();


// 4. ��� Ž�� API ����û
var startX = 127.02810900563199;
var startY = 37.519892712436906;
var endX = 127.13281976335786;
var endY = 37.52130314703887;
var passList = "127.04724656694417,37.524162226778515_127.10887300128256,37.5289781669373";
var prtcl;
var headers = {}; 
headers["appKey"]="a9ee13e1-cb7e-46a8-b144-14bfd0103a90";
$.ajax({
		method:"POST",
		headers : headers,
		url:"https://apis.openapi.sk.com/tmap/routes?version=1&format=xml",//
		async:false,
		data:{
			startX : startX,
			startY : startY,
			endX : endX,
			endY : endY,
			passList : passList,
			reqCoordType : "WGS84GEO",
			resCoordType : "EPSG3857",
			angle : "172",
			searchOption : "0",
			trafficInfo : "Y" //�������� ǥ�� �ɼ��Դϴ�.
		},
		success:function(response){
			prtcl = response;
		
		//5. ���Ž�� ��� Line �׸���
		//�����,������ ��Ŀ ����
		markerStartLayer.clearMarkers();
		markerEndLayer.clearMarkers();
		//������ ��Ŀ ����
		markerWaypointLayer.clearMarkers();
						
		var trafficColors = {
				extractStyles:true,
				
				/* ���� ���������� ǥ��Ǹ� �Ʒ��� ���� Color�� Line�� �����˴ϴ�. */
				trafficDefaultColor:"#000000", //Default
				trafficType1Color:"#009900", //����
				trafficType2Color:"#8E8111", //��ü
				trafficType3Color:"#FF0000"  //��ü
				
			};    
		var kmlForm = new Tmap.Format.KML(trafficColors).readTraffic(prtcl);
		routeLayer = new Tmap.Layer.Vector("vectorLayerID"); //���� ���̾� ����
		routeLayer.addFeatures(kmlForm); //���������� ���� ���̾ �߰�   
		
		map.addLayer(routeLayer); //������ ���� ���̾� �߰�
		
		markerWaypointLayer2 = new Tmap.Layer.Markers("waypoint2");
		map.addLayer(markerWaypointLayer2);
		
		var size = new Tmap.Size(24, 38);
		var offset = new Tmap.Pixel(-(size.w / 2), -size.h); 
		var icon = new Tmap.IconHtml("<img src='http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png' />", size, offset);
		var marker = new Tmap.Marker(new Tmap.LonLat("127.07389565460413","37.5591696189164").transform("EPSG:4326", "EPSG:3857"), icon);
		markerWaypointLayer2.addMarker(marker);
		
		var size = new Tmap.Size(24, 38);
		var offset = new Tmap.Pixel(-(size.w / 2), -size.h);
		var icon = new Tmap.IconHtml("<img src='http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png' />", size, offset);
		var marker = new Tmap.Marker(new Tmap.LonLat("127.13346617572014", "37.52127761904626").transform("EPSG:4326", "EPSG:3857"), icon);
		markerWaypointLayer2.addMarker(marker);
		
		
		
		// 6. ���Ž�� ��� �ݰ游ŭ ���� ���� ����
		map.zoomToExtent(routeLayer.getDataExtent());
		
	},
	error:function(request,status,error){
		console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
	}
});



// �� ���� ����
initTmap();
</script>

<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body onload="initTmap()">
	<div id="map_div"></div>
	<p id="result">��� ǥ��</p>
</body>
</html>