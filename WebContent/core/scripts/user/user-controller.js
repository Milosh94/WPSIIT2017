(function(){
	app.controller("profileCtrl", profile);
	
	function profile(authentication, UserResource, $state, $timeout){
		var vm = this;
		
		vm.user = authentication.getUser();
		console.log(vm.user);
		UserResource.getLoggedUser().then(function(response){
			vm.user = response;
			if(vm.user === null){
				$timeout(function(){
					$state.go("root");
				});
			}
		});
		
		
	}
	
	profile.$inject = ["authentication", "UserResource", "$state", "$timeout"];
})();