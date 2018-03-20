(function(){
	app.directive("addressAutocomplete", addressAutocomplete);
	
	function addressAutocomplete(){
		return {
			restrict: "A", 
			require: "ngModel", 
			link: function(scope, element, attrs, ngModelCtrl){
					var options = {
							types: ["address"],
							componentRestrictions: {country: "rs"}
					};
					var input = element[0];
					geocoder = new google.maps.Geocoder();
					autocomplete = new google.maps.places.Autocomplete(input, options);
					autocomplete.addListener("place_changed", function(){
						ngModelCtrl.$setViewValue(input.value);
				    });
			}
		};
	}
	
	addressAutocomplete.$inject = [];
})();