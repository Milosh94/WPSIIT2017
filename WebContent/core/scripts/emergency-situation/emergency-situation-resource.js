(function(){
	app.factory("EmergencySituationResource", emergencySituation);
	
	function emergencySituation(Restangular){
		var retObj = {};
		
		retObj.search = function(searchParams){
			return Restangular.all("search").customGET("", searchParams);
		}
		
		return retObj;
	}
	
	emergencySituation.$inject = ["Restangular"];
})();