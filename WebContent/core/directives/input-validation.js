(function(){
	app.directive("inputValidation", inputValidation);
	
	function inputValidation(){
		return{
		    restrict: "A", 
			require: "ngModel",
		    link: function(scope, element, attr, ngModelCtrl){
		    	function fromUser(text){
		    		var transformedInput = text.replace(/;/g, "");
		    		if(transformedInput !== text){
		    			ngModelCtrl.$setViewValue(transformedInput);
		    			ngModelCtrl.$render();
		    		}
		    		return transformedInput;
		    	}
		    	ngModelCtrl.$parsers.push(fromUser);
		    }
		}; 
	}
	
	inputValidation.$inject = [];
})();