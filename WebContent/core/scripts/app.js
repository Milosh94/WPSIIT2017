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
	
	function runFunction(Restangular){
		Restangular.setBaseUrl("rest");
	}
	
	runFunction.$inject = ["Restangular"];
})(angular);