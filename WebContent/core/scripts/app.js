(function(angular){
	app = angular.module("EmergencySituationApp", ["ui.router", "restangular", "ui.bootstrap", "ngCookies", "ngMessages", "ui.select", "ngSanitize", "toastr", "ngScrollbars"]);
	
	app.config(configFunction).run(runFunction);
	
	function configFunction($stateProvider, $urlRouterProvider){
		$urlRouterProvider.otherwise("/");
		
		$stateProvider
			.state("root", {
				views: {
					"homeView": {
						templateUrl: "core/views/home-navigation.html",
						controller: "homeCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("home", {
				parent: "root",
				params: {
					fragmentId: "search",
					situationId: null
				},
				views: {
					"homepageView@root": {
						templateUrl: "core/views/homepage.html",
						controller: "homepageCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("profile", {
				parent: "root",
				url: "/profile",
				views: {
					"homepageView@root": {
						templateUrl: "core/views/profile.html",
						controller: "profileCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("search", {
				parent: "home",
				url: "/",
				views: {
					"pageFragmentView@home": {
						templateUrl: "core/views/search.html",
						controller: "searchCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("emergency-situations", {
				parent: "home",
				url: "/my-emergency-situations",
				views: {
					"pageFragmentView@home": {
						templateUrl: "core/views/my-emergency-situations.html",
						controller: "myEmergencySituationsCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("publish", {
				parent: "home",
				url: "/publish",
				views: {
					"pageFragmentView@home": {
						templateUrl: "core/views/publish.html",
						controller: "publishCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("emergency-situation", {
				parent: "home",
				url: "/emergency-situation/{situationId:int}",
				views: {
					"pageFragmentView@home": {
						templateUrl: "core/views/emergency-situation.html",
						controller: "emergencySituationCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("volunteers", {
				parent: "home",
				url: "/volunteers",
				views: {
					"pageFragmentView@home": {
						templateUrl: "core/views/volunteers.html",
						controller: "volunteersCtrl",
						controllerAs: "vm"
					}
				}
			})
			.state("territories", {
				parent: "home",
				url: "/territories",
				views: {
					"pageFragmentView@home": {
						templateUrl: "core/views/territories.html",
						controller: "territoriesCtrl",
						controllerAs: "vm"
					}
				}
			});
	}
	
	configFunction.$inject = ["$stateProvider", "$urlRouterProvider"];
	
	function runFunction(Restangular, $transitions, authentication, $location, $state, $q, $uibModalStack){
		Restangular.setBaseUrl("rest");
		
		var statesList = ["search", "emergency-situations", "publish", "volunteers", "territories"];
		
		//var profileTransition = false;
		
		$transitions.onError({}, function(transition){
			console.log("error");
			console.log(transition.from().name);
			console.log(transition.to().name);
		});
		
		$transitions.onRetain({}, function(transition){
			console.log("retain");
			console.log(transition.from().name);
			console.log(transition.to().name);
			console.log(transition.retained());
		});
		
		$transitions.onSuccess({}, function(transition){
			$uibModalStack.dismissAll();
		});
		
		$transitions.onBefore({}, function(transition){
			var deferred = $q.defer();
			authentication.getUser().then(function(response){
				var user = response;
				console.log(user);
				console.log(transition.from().name);
				console.log(transition.to().name);
				//if(profileTransition === true){
				//	profileTransition = false;
				//	deferred.reject(false);
				//}
				if(user === "logged-out"){
					console.log("qwqwqw");
					deferred.resolve($state.target("home", {fragmentId: "search"}, {reload: true}));
				}
				if((user === undefined || user === null) && (transition.to().name === "profile" || transition.to().name === "emergency-situations" || transition.to().name === "publish" 
					|| transition.to().name === "volunteers" || transition.to().name === "territories")){
					if(transition.from().name === "" || transition.from().name === "profile" || transition.from().name === "emergency-situations" || transition.from().name === "publish" 
						|| transition.to().name === "volunteers" || transition.to().name === "territories"){
						deferred.resolve($state.target("home", {fragmentId: "search"}));
					}
					else{
						$location.path(transition.router.urlRouter.location);
						deferred.reject(false);
					}
				}
				if(user !== null && user.admin === false && (transition.to().name === "publish" || transition.to().name === "volunteers" || transition.to().name === "territories")){
					$location.path(transition.router.urlRouter.location);
					deferred.reject(false);
				}
				if(user !== null && user.admin === false && user.blocked === true && transition.to().name === "emergency-situations"){
					$location.path(transition.router.urlRouter.location);
					deferred.reject(false);
				}
				if(user!== null && user.admin === true && transition.to().name === "emergency-situations"){
					$location.path(transition.router.urlRouter.location);
					deferred.reject(false);
				}
				for(var i = 0; i < statesList.length; i++){
					if(transition.from().name !== "home" && transition.to().name === statesList[i]){
						deferred.resolve($state.target("home", {fragmentId: statesList[i]}, {location: false, reload: "home"}));
					}
				}
				if(transition.from().name !== "home" && transition.to().name === "emergency-situation"){
					deferred.resolve($state.target("home", {fragmentId: "emergency-situation", situationId: transition.params().situationId}, {location: false, reload: "home"}));
				}
				if(transition.from().name !== "" && transition.from().name !== "root" && transition.to().name === "root"){
					console.log("aleleeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
					deferred.resolve($state.target("home", {fragmentId: "search"}, {reload: true}));
				}
				//if(transition.from().name === "" && transition.to().name === "profile"){
				//	profileTransition = true;
				//}
				console.log("kraj");
				deferred.resolve(true);
			});
			return deferred.promise;
			/*
			return new Promise(function(resolve, reject){
				authentication.getUser2().then(function(response){
					var user = response;
					console.log(user);
					console.log(transition.from().name);
					console.log(transition.to().name);
					if(profileTransition === true){
						profileTransition = false;
						reject(false);
					}
					if(user === "logged-out"){
						console.log("qwqwqw");
						resolve($state.target("root", {}, {reload: true}));
					}
					if((user === undefined || user === null) && (transition.to().name === "profile" || transition.to().name === "emergency-situations" || transition.to().name === "publish" 
						|| transition.to().name === "volunteers" || transition.to().name === "territories")){
						if(transition.from().name === "" || transition.from().name === "profile" || transition.from().name === "emergency-situations" || transition.from().name === "publish" 
							|| transition.to().name === "volunteers" || transition.to().name === "territories"){
							resolve($state.target("home", {fragmentId: "search"}));
						}
						else{
							$location.path(transition.router.urlRouter.location);
							reject(false);
						}
					}
					if(user !== null && user.admin === false && (transition.to().name === "publish" || transition.to().name === "volunteers" || transition.to().name === "territories")){
						$location.path(transition.router.urlRouter.location);
						reject(false);
					}
					if(user !== null && user.admin === false && user.blocked === true && transition.to().name === "emergency-situations"){
						$location.path(transition.router.urlRouter.location);
						reject(false);
					}
					if(user!== null && user.admin === true && transition.to().name === "emergency-situations"){
						$location.path(transition.router.urlRouter.location);
						reject(false);
					}
					for(var i = 0; i < statesList.length; i++){
						if(transition.from().name !== "home" && transition.to().name === statesList[i]){
							resolve($state.target("home", {fragmentId: statesList[i]}, {location: false, reload: "home"}));
						}
					}
					if(transition.from().name !== "home" && transition.to().name === "emergency-situation"){
						resolve($state.target("home", {fragmentId: "emergency-situation", situationId: transition.params().situationId}, {location: false, reload: "home"}));
					}
					if(transition.from().name !== "" && transition.from().name !== "root" && transition.to().name === "root"){
						resolve($state.target("home", {fragmentId: "search"}, {reload: "root"}));
					}
					if(transition.from().name === "" && transition.to().name === "profile"){
						profileTransition = true;
					}
					resolve(true);
				});
			});
			*/
			
			/*
			var user = authentication.getUser();
			console.log(user);
			console.log(transition.from().name);
			console.log(transition.to().name);
			if(profileTransition === true){
				profileTransition = false;
				return false;
			}
			if((user === undefined || user === null) && (transition.to().name === "profile" || transition.to().name === "emergency-situations" || transition.to().name === "publish" 
				|| transition.to().name === "volunteers" || transition.to().name === "territories")){
				if(transition.from().name === "" || transition.from().name === "profile" || transition.from().name === "emergency-situations" || transition.from().name === "publish" 
					|| transition.to().name === "volunteers" || transition.to().name === "territories"){
					return $state.target("home", {fragmentId: "search"});
				}
				else{
					$location.path(transition.router.urlRouter.location);
					return false;
				}
			}
			if(user !== null && user.admin === false && (transition.to().name === "publish" || transition.to().name === "volunteers" || transition.to().name === "territories")){
				$location.path(transition.router.urlRouter.location);
				return false;
			}
			if(user !== null && user.admin === false && user.blocked === true && transition.to().name === "emergency-situations"){
				$location.path(transition.router.urlRouter.location);
				return false;
			}
			if(user!== null && user.admin === true && transition.to().name === "emergency-situations"){
				$location.path(transition.router.urlRouter.location);
				return false;
			}
			for(var i = 0; i < statesList.length; i++){
				if(transition.from().name !== "home" && transition.to().name === statesList[i]){
					return $state.target("home", {fragmentId: statesList[i]}, {location: false, reload: "home"});
				}
			}
			if(transition.from().name !== "home" && transition.to().name === "emergency-situation"){
				return $state.target("home", {fragmentId: "emergency-situation", situationId: transition.params().situationId}, {location: false, reload: "home"});
			}
			if(transition.from().name !== "" && transition.from().name !== "root" && transition.to().name === "root"){
				return $state.target("home", {fragmentId: "search"}, {reload: "root"});
			}
			if(transition.from().name === "" && transition.to().name === "profile"){
				profileTransition = true;
			}
			*/
		});
	}
	
	runFunction.$inject = ["Restangular", "$transitions", "authentication", "$location", "$state", "$q", "$uibModalStack"];
})(angular);