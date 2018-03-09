(function(angular){
	app = angular.module("EmergencySituationApp", ["ui.router", "restangular", "ui.bootstrap", "ngCookies", "ngMessages", "ui.select", "ngSanitize", "toastr"]);
	
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
			});
	}
	
	configFunction.$inject = ["$stateProvider", "$urlRouterProvider"];
	
	function runFunction(Restangular, $transitions, authentication, $location){
		Restangular.setBaseUrl("rest");
		
		$transitions.onBefore({}, function(transition){
			//console.log("before");
			//console.log(transition.from().name);
			//console.log(transition.to().name);
			var user = authentication.getUser();
			if(user === null && transition.to().name === "profile"){
				//console.log("dasda");
				//console.log(transition.router.urlRouter);
				$location.path(transition.router.urlRouter.location);
				return transition.router.stateService.target("root");
			}
		});
		
		$transitions.onError({}, function(transition){
			//console.log("error");
			//console.log(transition.from().name);
			//console.log(transition.to().name);
		});
	}
	
	runFunction.$inject = ["Restangular", "$transitions", "authentication", "$location"];
})(angular);