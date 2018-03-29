(function(angular){
	app = angular.module("EmergencySituationApp", ["ui.router", "restangular", "ui.bootstrap", "ngCookies", "ngMessages", "ui.select", "ngSanitize", "toastr", "ngScrollbars"]);
	
	app.config(configFunction).run(runFunction);
	
	function configFunction($stateProvider, $urlRouterProvider){
		$urlRouterProvider.otherwise("/");
		
		$stateProvider
			.state("root", {
				url: "/",
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
				url: "profile",
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
				url: "my-emergency-situations",
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
				url: "publish",
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
				url: "emergency-situation/{situationId:int}",
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
				url: "volunteers",
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
				url: "territories",
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
	
	function runFunction(Restangular, $transitions, authentication, $location, $state){
		Restangular.setBaseUrl("rest");
		
		var statesList = ["search", "emergency-situations", "publish", "volunteers", "territories"];
		
		var profileTransition = false;
		
		$transitions.onBefore({}, function(transition){
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
		});
	}
	
	runFunction.$inject = ["Restangular", "$transitions", "authentication", "$location", "$state"];
})(angular);