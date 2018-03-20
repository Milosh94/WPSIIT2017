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
		
		return retObj;
	}
	
	territory.$inject = ["Restangular"];
})();