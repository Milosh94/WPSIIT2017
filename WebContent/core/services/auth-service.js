(function(){
	app.service("authentication", authentication);
	
	function authentication(UserResource, $window, $cookies, $q){
		var retObj = {};
		
		retObj.getUser = function(){
			var deferred = $q.defer();
			var user = $window.localStorage["user"];
			if(user === "undefined" || user === undefined){
				deferred.resolve(null);
			}
			else{
				user = JSON.parse(user);
				var d = new Date();
				d.setHours(d.getHours() - 1);
				//if(new Date(user.accessTime) < d){
				if(true){
					UserResource.getLoggedUser().then(function(response){
						retObj.saveUser(response);
						deferred.resolve(response);
					}, function(error){
						$window.localStorage.removeItem("user");
						$cookies.remove("JSESSIONID");
						deferred.resolve("logged-out");
					});
				}
				else{
					deferred.resolve(user);
				}
			}
			return deferred.promise;
			/*
			return new Promise(function(resolve, reject){
				var user = $window.localStorage["user"];
				if(user === "undefined" || user === undefined){
					resolve(null);
				}
				else{
					user = JSON.parse(user);
					var d = new Date();
					d.setHours(d.getHours() - 1);
					if(true){
						UserResource.getLoggedUser().then(function(response){
							retObj.saveUser(response);
							resolve(response);
						}, function(error){
							$window.localStorage.removeItem("user");
							$cookies.remove("JSESSIONID");
							resolve("logged-out");
						});
					}
					else{
						resolve(user);
					}
				}
				
			});
			*/
		}
		/*
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
					retObj.saveUser(response);
					return response;
				}, function(error){
					$window.localStorage.removeItem("user");
					$cookies.remove("JSESSIONID");
					return null;
				});
			}
			else{
				return user;
			}
		}
		*/
		retObj.saveUser = function(user){
			user.accessTime = new Date();
			$window.localStorage["user"] = JSON.stringify(user);
		}
		
		retObj.getLoggedUser = function(){
			var user = $window.localStorage["user"];
			if(user === "undefined" || user === undefined){
				return null;
			}
			else{
				user = JSON.parse(user);
				return user;
			}
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
	
	authentication.$inject = ["UserResource", "$window", "$cookies", "$q"];
})();