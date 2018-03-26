(function(){
	app.factory("VolunteerResource", volunteerResource);
	
	function volunteerResource(Restangular){
		retObj = {};
		
		retObj.getVolunteers = function(){
			return Restangular.all("volunteers").customGET("");
		}
		
		retObj.getTerritoryVolunteers = function(territoryId){
			return Restangular.one("territory", territoryId).getList("volunteers");
		}
		
		return retObj;
	}
	
	volunteerResource.$inject = ["Restangular"];
})();