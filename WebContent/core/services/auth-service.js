(function(){
	app.service("authentication", authentication);
	
	function authentication(UserResource, $window, $cookies){
		var retObj = {};
		
		retObj.getUser = function(){
			var user = $window.localStorage["user"];
			if(user === "undefined" || user === undefined){
				return null;
			}
			return JSON.parse(user);
		}
		
		retObj.saveUser = function(user){
			$window.localStorage["user"] = JSON.stringify(user);
		}
		
		retObj.logout = function(){
			$window.localStorage.removeItem("user");
			$cookies.remove("JSESSIONID");
			UserResource.logout();
		}
		
		retObj.login = function(user){
			return UserResource.login(user).then(function(response){
				console.log(response);
				retObj.saveUser(response);
			});
		};
		
		return retObj;
	}
	
	authentication.$inject = ["UserResource", "$window", "$cookies"];
})();