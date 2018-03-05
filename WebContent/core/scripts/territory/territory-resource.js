(function(){
	app.factory("TerritoryResource", territory);
	
	function territory(Restangular){
		var retObj = {};
		
		retObj.getTerritories = function(){
			return Restangular.all("territory").getList();
		};
		
		return retObj;
	}
	
	territory.$inject = ["Restangular"];
})();