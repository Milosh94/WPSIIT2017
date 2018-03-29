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
		
		retObj.getUnpublishedSituations = function(){
			return Restangular.all("publish").customGET("");
		}
		
		retObj.getEmergencySituation = function(id){
			return Restangular.all("emergency-situation").get(id);
		}
		
		retObj.publishSituation = function(id){
			return Restangular.all("publish").customPUT({}, id, {}, {});
		}
		
		retObj.archiveSituation = function(id){
			return Restangular.all("archive").customPUT({}, id, {}, {});
		}
		
		retObj.changeVolunteer = function(situationId, username){
			return Restangular.one("emergency-situation", situationId).customPUT({}, "change-volunteer", {username: username}, {});
		}
		
		retObj.postComment = function(situationId, comment){
			return Restangular.one("emergency-situation", situationId).customPOST({comment: comment}, "comment", {}, {});
		}
		
		retObj.changeTerritory = function(situationId, territoryId){
			return Restangular.one("emergency-situation", situationId).customPUT({}, "change-territory", {territory: territoryId}, {});
		}
		
		return retObj;
	}
	
	emergencySituation.$inject = ["Restangular"];
})();