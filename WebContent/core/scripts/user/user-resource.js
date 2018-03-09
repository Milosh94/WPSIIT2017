(function(){
	app.factory("UserResource", userResource);
	
	function userResource(Restangular){
		var retObj = {};
		
		retObj.login = function(login){
			return Restangular.all("login").post(login);
		}
		
		retObj.usernameExists = function(username){
			return Restangular.all("exists").customGET("", {username: username});
		}
		
		retObj.register = function(fd){
			return Restangular.all("register").withHttpConfig({transformRequest: angular.identity}).customPOST(fd, "", undefined, {"Content-Type": undefined});
		}
		
		retObj.logout = function(){
			return Restangular.all("logout").post({});
		}
		
		retObj.getLoggedUser = function(){
			return Restangular.all("logged-user").customGET("");
		}
		
		retObj.updateUser = function(fd){
			return Restangular.all("user").withHttpConfig({transformRequest: angular.identity}).customPUT(fd, "", undefined, {"Content-Type": undefined});
		}
		
		return retObj;
	}
	
	userResource.$inject = ["Restangular"];
})();