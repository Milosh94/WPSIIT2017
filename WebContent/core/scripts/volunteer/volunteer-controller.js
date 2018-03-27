(function(){
	//controller for emergency situations assigned to volunteer
	app.controller("myEmergencySituationsCtrl", myEmergencySituations);
	
	function myEmergencySituations(authentication, $timeout, $state, VolunteerResource){
		var vm = this;
		
		vm.user = authentication.getUser();
		if(vm.user === null){
			$timeout(function(){
				$state.go("root");
			});
		}
		
		vm.maxSize = 5;
		vm.itemsPerPage = 10;
		
		vm.changePage = function(){
			var pagedData = vm.mySituations.slice((vm.currentPage - 1) * vm.itemsPerPage, (vm.currentPage) * vm.itemsPerPage);
			vm.pageSituations = pagedData;
		}
		
		VolunteerResource.getVolunteerSituations().then(function(response){
			vm.mySituations = response;
			vm.totalItems = vm.mySituations.length;
			vm.currentPage = 1;
			vm.changePage();
		});
	}
	
	myEmergencySituations.$inject = ["authentication", "$timeout", "$state", "VolunteerResource"];
})();