(function(){
	//controller for admin for adding, removing and updating territories
	app.controller("territoriesCtrl", territories);
	
	function territories(authentication, $timeout, $state, TerritoryResource, $uibModal){
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
			var pagedData = vm.territories.slice((vm.currentPage - 1) * vm.itemsPerPage, (vm.currentPage) * vm.itemsPerPage);
			vm.pageTerritories = pagedData;
		}
		
		TerritoryResource.getTerritories().then(function(response){
			vm.territories = response;
			vm.totalItems = vm.territories.length;
			vm.currentPage = 1;
			vm.changePage();
		});
		
		vm.searchName = "";
		
		vm.searchTerritories = function(){
			TerritoryResource.searchTerritories(vm.searchName).then(function(response){
				vm.territories = response;
				vm.totalItems = vm.territories.length;
				vm.currentPage = 1;
				vm.changePage();
			})
		}
		
		vm.deleteTerritory = function(territory, index){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/delete-territory.html",
				controller: "deleteTerritoryModalCtrl", 
				controllerAs: "vm",
				resolve: {
					territory: function(){
						return territory;
					}
				}
			});
			
			modalInstance.result.then(function(response){
				if(response === "success"){
					vm.searchTerritories();
				}
			}, function(error){});
		}
		
		vm.newTerritory = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/new-territory.html",
				controller: "newTerritoryModalCtrl", 
				controllerAs: "vm"
			});
			
			modalInstance.result.then(function(response){
				if(response === "success"){
					vm.searchTerritories();
				}
			}, function(error){});
		}
		
		vm.updateTerritory = function(territory, index){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/update-territory.html",
				controller: "updateTerritoryModalCtrl", 
				controllerAs: "vm",
				resolve: {
					territory: function(){
						return territory;
					}
				}
			});
			
			modalInstance.result.then(function(response){
				if(response === "success"){
					vm.searchTerritories();
				}
			}, function(error){});
		}
	}
	
	territories.$inject = ["authentication", "$timeout", "$state", "TerritoryResource", "$uibModal"];
	
	//controller for creating new territory
	app.controller("newTerritoryModalCtrl", newTerritoryModal);
	
	function newTerritoryModal($uibModalInstance, TerritoryResource, toastr, toastrConfig){
		var vm = this;
		
		vm.territory = {};
		
		vm.territoryExists = function(){
			if(vm.territory.name !== undefined && vm.territory.name.trim() !== ""){
				TerritoryResource.territoryExists(vm.territory.name).then(function(response){
					vm.territoryForm.territoryName.$setValidity("territoryExists", !response.exists);
				});
			}
		}
		
		vm.newTerritory = function(isValid, form){
			if(isValid){
				toastrConfig.maxOpened = 1;
				toastrConfig.positionClass = "toast-top-center";
				TerritoryResource.addTerritory(vm.territory).then(function(response){
					toastr.success("Please login", "Success: Territory created", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.close("success");
				}, function(error){
					toastr.error("Error", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.dismiss("error");
				});
			}
			else{
				form.name.$setTouched();
				angular.forEach(form.$error, function (field) {
			        angular.forEach(field, function(errorField){
			            errorField.$setTouched();
			        })
			    });
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	newTerritoryModal.$inject = ["$uibModalInstance", "TerritoryResource", "toastr", "toastrConfig"];
	
	//controller for updating existing territory
	app.controller("updateTerritoryModalCtrl", updateTerritoryModal);
	
	function updateTerritoryModal($uibModalInstance, TerritoryResource, toastr, toastrConfig, territory){
		var vm = this;
		
		vm.territory = JSON.parse(JSON.stringify(territory));
		
		vm.territoryExists = function(){
			if(vm.territory.name !== undefined && vm.territory.name.trim() !== "" && vm.territory.name.trim() !== territory.name){
				TerritoryResource.territoryExists(vm.territory.name).then(function(response){
					vm.territoryForm.territoryName.$setValidity("territoryExists", !response.exists);
				});
			}
		}
		
		vm.updateTerritory = function(isValid, form){
			if(isValid){
				toastrConfig.maxOpened = 1;
				toastrConfig.positionClass = "toast-top-center";
				TerritoryResource.updateTerritory(vm.territory).then(function(response){
					toastr.success("Please login", "Success: Territory updated", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.close("success");
				}, function(error){
					toastr.error("Error", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.dismiss("error");
				});
			}
			else{
				angular.forEach(form.$error, function (field) {
			        angular.forEach(field, function(errorField){
			            errorField.$setTouched();
			        })
			    });
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	updateTerritoryModal.$inject = ["$uibModalInstance", "TerritoryResource", "toastr", "toastrConfig", "territory"];
	
	//controller for deleting existing territory
	app.controller("deleteTerritoryModalCtrl", deleteTerritoryModal);
	
	function deleteTerritoryModal($uibModalInstance, TerritoryResource, toastr, toastrConfig, territory){
		var vm = this;
		
		vm.territory = territory;
		
		vm.deleteTerritory = function(){
			toastrConfig.maxOpened = 1;
			toastrConfig.positionClass = "toast-top-center";
			TerritoryResource.deleteTerritory(vm.territory.id).then(function(response){
				toastr.success("Please login", "Success: Territory deleted", {
					closeButton: true,
					timeout: 3000
				});
				$uibModalInstance.close("success");
			}, function(error){
				toastr.error("Error", {
					closeButton: true,
					timeout: 3000
				});
				$uibModalInstance.dismiss("error");
			});
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	deleteTerritoryModal.$inject = ["$uibModalInstance", "TerritoryResource", "toastr", "toastrConfig", "territory"];
})();