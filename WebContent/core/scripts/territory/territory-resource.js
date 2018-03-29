(function(){
	app.factory("TerritoryResource", territory);
	
	function territory(Restangular){
		var retObj = {};
		
		retObj.getTerritories = function(){
			return Restangular.all("territory").getList();
		};
		
		retObj.getStates = function(){
			return Restangular.all("territory").customGET("states");
		}
		
		retObj.deleteTerritory = function(id){
			return Restangular.one("territory", id).remove();
		}
		
		retObj.territoryExists = function(name){
			return Restangular.all("territory").customGET("exists", {name: name});
		}
		
		retObj.addTerritory = function(territory){
			return Restangular.all("territory").post(territory);
		}
		
		retObj.searchTerritories = function(search){
			return Restangular.all("territory").customGET("search", {search: search}, {});
		}
		
		retObj.updateTerritory = function(territory){
			return Restangular.one("territory", territory.id).customPUT(territory, "", {}, {});
		}
		
		retObj.deleteTerritory = function(territoryId){
			return Restangular.one("territory", territoryId).remove();
		}
		
		return retObj;
	}
	
	territory.$inject = ["Restangular"];
})();