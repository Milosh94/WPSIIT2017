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
		
		retObj.getVolunteerSituations = function(){
			return Restangular.all("volunteer-emergency-situations").customGET("");
		}
		
		retObj.searchVolunteers = function(search){
			return Restangular.all("search-volunteers").customGET("", {search: search}, {});
		}
		
		retObj.blockUnblock = function(username, block){
			return Restangular.all("block-unblock").customPUT({}, "", {username: username, block: block}, {});
		}
		
		return retObj;
	}
	
	volunteerResource.$inject = ["Restangular"];
})();