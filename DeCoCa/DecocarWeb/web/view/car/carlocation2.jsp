<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://api2.sktelecom.com/tmap/js?version=1&format=javascript&appKey=a9ee13e1-cb7e-46a8-b144-14bfd0103a90"></script>


<p id="result"></p>
<script type="text/javascript">
//�������� �ε��� �� �� ȣ���ϴ� �Լ��Դϴ�.
	var map, marker;
	var gAppKey = 'a9ee13e1-cb7e-46a8-b144-14bfd0103a90';

function initTmap(){
	// map ����
	// Tmap.map�� �̿��Ͽ�, ������ �� div, ����, ���̸� �����մϴ�.
	map = new Tmap.Map({div:'map_div', width:'80%', height:'800px'});//map ����
	markerLayer = new Tmap.Layer.Markers();//��Ŀ���̾ �����մϴ�.
	map.addLayer(markerLayer);//map�� ��Ŀ���̾ �߰��մϴ�.
	map.events.register("moveend", map, onMoveEnd);//map �̵� �̺�Ʈ�� ����մϴ�.
	map.events.register("click", map, onClick);//map Ŭ�� �̺�Ʈ�� ����մϴ�.
	}
function onMoveEnd(){
	var c_ll = map.getCenter(); //���� ������ center ��ǥ�� �����ɴϴ�.
	c_ll = new Tmap.LonLat(c_ll.lon, c_ll.lat).transform("EPSG:3857","EPSG:4326");//WGS84 ��ǥ��� ��ȯ
	loadGetAddressFromLonLat(c_ll);//�߽���ǥ�� �ּҷ� ��ȯ�ϴ� �Լ��Դϴ�.
	 
	//�߽���ǥ�� �ּҷ� ��ȯ�ϴ� �Լ��Դϴ�. 
	function loadGetAddressFromLonLat(ll) {
		var tdata = new Tmap.TData(); //REST API ���� �����Ǵ� ���, ��������, POI �����͸� ���� ó���� �� �ִ� Ŭ�����Դϴ�.
		tdata.events.register("onComplete", tdata,onCompleteLoadGetAddressFromLonLat);//������ �ε尡 ���������� �Ϸ�Ǿ��� �� �߻��ϴ� �̺�Ʈ�Դϴ�.
		var optionObj = {
			version : 1
		};
		tdata.getAddressFromLonLat(ll, optionObj);//��ǥ�� ���� �ּ� ���� �����͸� �ݹ� �Լ��� ���� XML�� �����մϴ�.
	}
	//������ �ε尡 ���������� �Ϸ�Ǹ�, �ܽ� ��ǥ�� �ּҷ� ��ȯ�� ����� ����ϴ� �Լ��Դϴ�.
	function onCompleteLoadGetAddressFromLonLat() {
		var result ='���� ������ �߽� ��ǥ�ּ� : '+jQuery(this.responseXML).find("fullAddress").text()+'';//��µ� ��� �Դϴ�.
		var resultDiv = document.getElementById("result");//id�� ����� ����� result�� element�� ã���ϴ�.
		resultDiv.innerHTML = result;//����� htm�ӿ� ���
	}
}
function onClick(e){
	lonlat = map.getLonLatFromViewPortPx(e.xy); 
	var url = "https://apis.openapi.sk.com/tmap/geo/reversegeocoding"; //Reverse Geocoding api ��û url�Դϴ�.
	var params = {
		"version" : "1"//���� �����Դϴ�.
		,"coordType" : "EPSG3857"
		,"lat" : lonlat.lat //���� ��ǥ�Դϴ�.
		,"lon" : lonlat.lon //�浵 ��ǥ�Դϴ�.
		,"appKey" : gAppKey//�� Ű(appKey) �Դϴ�.
	}
	$.get(url, params, function(data){
		if(data){ 
			markerLayer.removeMarker(marker); //���� ��Ŀ�� �����մϴ�.
			var address = data.addressInfo;//Reverse Geocoding api ��û�Ͽ� ���� ������� �ּ������� �����մϴ�.
			var size = new Tmap.Size(24,38); //Icon ũ�� �Դϴ�.
			var offset = new Tmap.Pixel(-(size.w / 2), -(size.h)); //Icon �߽��� �Դϴ�.
			var icon = new Tmap.Icon('http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_h.png',size, offset); //marker�� ���� ������ �����Դϴ�.  
			var label = new Tmap.Label("�ּ����� : "+address.fullAddress); //label�� ǥ�õ� text�Դϴ�.
			marker = new Tmap.Markers(lonlat, icon, label); //������ ������ �̿��Ͽ� marker�� �����մϴ�.
			markerLayer.addMarker(marker);//layer�� marker �߰��մϴ�.
			marker.popup.show();//marker �˾� ǥ��
		}else{
			alert("�˻������ �����ϴ�.");
		}
	});
}
	
// �� ���� ����
initTmap();
</script>

<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body onload="initTmap()">
      <div id="map_div">
      </div>
      <p id="result">��� ǥ��</p>
    </body>
</html>