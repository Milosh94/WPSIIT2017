(function(){
	app.directive("showMap", showMap);
	
	function showMap(){
		return {
			restrict: "A", 
			scope: {
				location: "=showMapLocation",
				coordinates: "=showMapCoordinates"
			},
			link: function(scope, element, attrs){
		        var geocoder = new google.maps.Geocoder;
		        var infowindow = new google.maps.InfoWindow;
		        var updateFunction = function(){
		        	if(scope.location !== undefined && scope.location.trim() !== ""){
        				geocoder.geocode({"address": scope.location}, function(results, status){
        					var blank = true;
	        		    	if(status === "OK"){
	        		    		if(results[0]){
	        		    			if(scope.location === results[0].formatted_address){
	        		    				blank = false;
	        		    				element.addClass("map-showed");
			        		    		var mapOptions = {
		                		    			zoom: 15,
		                		    			center: {lat: results[0].geometry.location.lat(), lng: results[0].geometry.location.lng()}
		                		        };
		                		        var map = new google.maps.Map(element[0], mapOptions);
			                			var marker = new google.maps.Marker({
			                				position: results[0].geometry.location,
			                				map: map
			                			});
			                			infowindow.setContent(results[0].formatted_address);
			                			google.maps.event.addListener(marker, "click", function() {
			    				        	infowindow.open(map, marker);
			    				        });
	        		    			}
	        		    		}
	        		    	}
	        		    	if(blank === true){
	        		    		element.removeClass("map-showed");
	        		    		element.parent().removeClass("height-400px");
		        		    	element.html("<b>Location don't exist on the map.</b>");
	        		    	}
	        		    });
        			}
        			else{
        				element.removeClass("map-showed");
        				element.parent().removeClass("height-400px");
        		    	element.html("<b>Location don't exist on the map.</b>");
        			}
		        }
		        scope.$watch("location + coordinates", function(newValue, oldValue){
		        	if(newValue !== oldValue){
		        		var update = true;
		        		if(scope.coordinates !== undefined && scope.coordinates.trim() !== ""){
		        			update = false;
		        			var latlngStr = scope.coordinates.split(":");
		        			if(latlngStr.length === 2){
		        				var latlng = {lat: parseFloat(latlngStr[0]), lng: parseFloat(latlngStr[1])};
				                geocoder.geocode({"location": latlng}, function(results, status){
				                	var updateAfter = true;
				                	if(status === "OK"){
				                		if(results[0]){
				                			if(scope.location === results[0].formatted_address){
				                				updateAfter = false;
				                				element.addClass("map-showed");
				                		    	var mapOptions = {
				                		    			zoom: 15,
				                		    			center: {lat: latlng.lat, lng: latlng.lng}
				                		        };
				                		        var map = new google.maps.Map(element[0], mapOptions);
					                			var marker = new google.maps.Marker({
					                				position: latlng,
					                				map: map
					                			});
					                			infowindow.setContent(results[0].formatted_address);
					                			google.maps.event.addListener(marker, "click", function() {
					    				        	infowindow.open(map, marker);
					    				        });
				                			}
				                		}
				                	}
				                	if(updateAfter === true){
				                		updateFunction();
				                	}
				                });
		        			}
		        			else{
		        				update = true;
		        			}
		        		}
		        		if(update === true){
		        			updateFunction();
		        		}
		        	}
		        });
			}
		};
	}
	
	showMap.$inject = [];
})();