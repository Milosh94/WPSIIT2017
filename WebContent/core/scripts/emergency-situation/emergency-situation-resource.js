(function(){
	app.factory("EmergencySituationResource", emergencySituation);
	
	function emergencySituation(Restangular){
		var retObj = {};
		
		retObj.search = function(searchParams){
			return Restangular.all("search").customGET("", searchParams);
		}
		
		retObj.report = function(fd){
			return Restangular.all("report").withHttpConfig({transformRequest: angular.identity}).customPOST(fd, "", undefined, {"Content-Type": undefined});
		}
		
		return retObj;
	}
	
	emergencySituation.$inject = ["Restangular"];
})();