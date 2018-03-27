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
	
	//controller for admin for blocking and enabling volunteers
	app.controller("volunteersCtrl", volunteers);
	
	function volunteers(authentication, $timeout, $state, VolunteerResource, $uibModal){
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
			var pagedData = vm.volunteers.slice((vm.currentPage - 1) * vm.itemsPerPage, (vm.currentPage) * vm.itemsPerPage);
			vm.pageVolunteers = pagedData;
		}
		
		VolunteerResource.getVolunteers().then(function(response){
			vm.volunteers = response;
			vm.totalItems = vm.volunteers.length;
			vm.currentPage = 1;
			vm.changePage();
		});
		
		vm.searchName = "";
		
		vm.searchVolunteers = function(){
			VolunteerResource.searchVolunteers(vm.searchName).then(function(response){
				vm.volunteers = response;
				vm.totalItems = vm.volunteers.length;
				vm.currentPage = 1;
				vm.changePage();
			})
		}
		
		vm.blockUnblock = function(user, index, block){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/block-unblock.html",
				controller: "blockUnblockModalCtrl",
				controllerAs: "vm",
				resolve: {
					block : function(){
						return block;
					}
				}
			});
			
			modalInstance.result.then(function(success){
				VolunteerResource.blockUnblock(user.username, block).then(function(response){
					vm.pageVolunteers[index] = response;
					vm.volunteers[(vm.currentPage - 1) * vm.itemsPerPage + index] = response;
				});
			}, function(error){});
		}
	}
	
	volunteers.$inject = ["authentication", "$timeout", "$state", "VolunteerResource", "$uibModal"];
	
	//modal for blocking/unblockig volunteer
	app.controller("blockUnblockModalCtrl", blockUnblockModal);
	
	function blockUnblockModal($uibModalInstance, block){
		var vm = this;
		
		vm.block = block;
		
		vm.yes = function(){
			$uibModalInstance.close("success");
		}
		
		vm.cancel = function(){
			$uibModalInstance.dismiss("cancel");
		}
	}
	
	blockUnblockModal.$inject = ["$uibModalInstance", "block"];
})();