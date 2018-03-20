(function(){
	app.directive("focusField", focusFunction);
	
	function focusFunction(){
		return{
			restrict: "A",
			scope: {
				trigger: "=focusField"
			},
		    link: function(scope, element){
		    	scope.$watch("trigger", function(value){
		    		if(value === true){ 
		    			element[0].focus();
		    			scope.trigger = false;
		    		}
		    	});
		    }
		};
	}
	
	focusFunction.$inject = [];	
})();