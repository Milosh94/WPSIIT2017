(function(){
	app.service("authentication", authentication);
	
	function authentication(UserResource, $window, $cookies){
		var retObj = {};
		
		retObj.getUser = function(){
			var user = $window.localStorage["user"];
			if(user === "undefined" || user === undefined){
				return null;
			}
			user = JSON.parse(user);
			var d = new Date();
			d.setHours(d.getHours() - 1);
			if(new Date(user.accessTime) < d){
				UserResource.getLoggedUser().then(function(response){
					if(response === undefined){
						$window.localStorage.removeItem("user");
						$cookies.remove("JSESSIONID");
						return null;
					}
					else{
						retObj.saveUser(response);
						return response;
					}
				});
			}
			return user;
		}
		
		retObj.saveUser = function(user){
			user.accessTime = new Date();
			$window.localStorage["user"] = JSON.stringify(user);
		}
		
		retObj.logout = function(){
			$window.localStorage.removeItem("user");
			$cookies.remove("JSESSIONID");
			UserResource.logout();
		}
		
		retObj.login = function(user){
			return UserResource.login(user).then(function(response){
				if(response === undefined){
					return null;
				}
				retObj.saveUser(response);
			});
		};
		
		return retObj;
	}
	
	authentication.$inject = ["UserResource", "$window", "$cookies"];
})();