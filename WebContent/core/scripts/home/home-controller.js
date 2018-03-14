(function(angular){
	//Home navigation
	app.controller("homeCtrl", home);
	
	function home(authentication, $state, $timeout, $uibModal, TerritoryResource, toastr, toastrConfig){
		var vm = this;
		
		vm.user = authentication.getUser();
		
		vm.loginUser = {};
		toastrConfig.maxOpened = 1;
		vm.login = function(){
			authentication.login(vm.loginUser).then(function(response){
				if(response === null){
					toastr.error("Wrong credentials!", "Error", {
						closeButton: true,
						timeout: 3000
					});
				}
				else{
					vm.user = authentication.getUser();
					$state.go("root", {}, {reload: true});
				}
			});
		}
		
		vm.clearInputs = function(form){
			vm.focus = true;
			vm.loginUser = {};
			angular.forEach(form, function(value, key){
		         if(typeof value === "object" && value.hasOwnProperty("$modelValue"))
		             value.$setUntouched();                        
		     });
		}
		
		vm.logout = function(){
			authentication.logout();
			vm.user = authentication.getUser();
			$state.go("root", {}, {reload: true});
		}
		
		vm.territories = null;
		
		if(vm.user === null){
			TerritoryResource.getTerritories().then(function(response){
				vm.territories = response;
			})
		}
		
		vm.register = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/register.html",
				controller: "registerCtrl",
				controllerAs: "vm",
				resolve: {
					territories: function(){
						return vm.territories;
					}
				}
			});
			modalInstance.result.then(function(){}, function(res){});
		}

		$timeout(function(){
			$state.go("search");
		})
	}
	
	home.$inject = ["authentication", "$state", "$timeout", "$uibModal", "TerritoryResource", "toastr", "toastrConfig"];
	
	//Register modal
	app.controller("registerCtrl", register);
	
	function register($uibModalInstance, territories, TerritoryResource, UserResource, toastr, toastrConfig){
		var vm = this;
		
		vm.territories = territories;
		
		if(vm.territories === null){
			TerritoryResource.getTerritories().then(function(response){
				vm.territories = response;
			})
		}
		
		vm.data = {};
		
		vm.usernameExists = function(){
			if(vm.data.username !== undefined && vm.data.username.trim() !== ""){
				UserResource.usernameExists(vm.data.username).then(function(response){
					vm.registerForm.username.$setValidity("usernameExists", !response.exists);
				});
			}
		}
		
		vm.removeError = function(){
			if(vm.registerForm.password !== undefined){
				vm.registerForm.password.$setValidity("differentPasswords", true);
			}
			if(vm.registerForm.passwordConfirm !== undefined){
				vm.registerForm.passwordConfirm.$setValidity("differentPasswords", true);
			}
		}
		
		vm.removeFile = function(){
			vm.file = undefined;
		}
				
		vm.data.password = "";
		vm.confirmPassword = "";
		
		vm.register = function(isValid, form){
			if(vm.data.password !== vm.confirmPassword){
				vm.registerForm.password.$setValidity("differentPasswords", false);
				vm.registerForm.passwordConfirm.$setValidity("differentPasswords", false);
			}
			if(isValid){
				var formData = new FormData();
				if(vm.file !== undefined){
					formData.append("file", vm.file);
				}
				else{
					formData.append("file", "");
				}
				vm.data.territory = vm.data.territory.id;
				formData.append("user", JSON.stringify(vm.data));
				toastrConfig.maxOpened = 1;
				UserResource.register(formData).then(function(response){
					toastr.success("Please login", "Success: User registered", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.dismiss("success");
				}, function(error){
					toastr.error("Error", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.dismiss("error");
				});
			}
			else{
				form.username.$setTouched();
				angular.forEach(form.$error, function (field) {
			        angular.forEach(field, function(errorField){
			            errorField.$setTouched();
			        })
			    });
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.dismiss("cancel");
		}
	}
	
	register.$inject = ["$uibModalInstance", "territories", "TerritoryResource", "UserResource", "toastr", "toastrConfig"];
	
	//homepage ctrl
	app.controller("homepageCtrl", homepage);
	
	function homepage(authentication, $timeout, $state, $uibModal){
		var vm = this;
		
		vm.user = authentication.getUser();
		
		vm.activeButton = 1;
		
		vm.report = function(current){
			vm.activeButton = 2;
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/report.html",
				controller: "reportSituationModalCtrl",
				controllerAs: "vm"
			});
			modalInstance.result.then(function(success){
				vm.activeButton = current;
			}, function(error){
				vm.activeButton = current;
			});
		}
	}
	
	homepage.$inject = ["authentication", "$timeout", "$state", "$uibModal"];
	
	//homepage search ctrl
	app.controller("searchCtrl", search);
	
	function search(authentication, TerritoryResource, EmergencySituationResource){
		var vm = this;
		
		vm.user = authentication.getUser();
		
		TerritoryResource.getTerritories().then(function(response){
			vm.territories = response;
		});
		
		vm.urgencyLevels = [
			{
				name: "Red"
			},
			{
				name: "Orange"
			}, 
			{
				name: "Blue"
			}
		];
		
		vm.config = {
				autoHideScrollbar: false,
				theme: "dark-thin",
				scrollButtons: {
					scrollAmount: "auto",
					enable: true
				},
				advanced:{
					updateOnContentResize: true
				},
					setHeight: 200,
					scrollInertia: 0
		}
		
		vm.configUrgency = {
				autoHideScrollbar: false,
				theme: "dark-thin",
				scrollButtons: {
					scrollAmount: "auto",
					enable: true
				},
				advanced:{
					updateOnContentResize: true
				},
					setHeight: 120,
					scrollInertia: 0
		}
		
		vm.searchParams = {
			emergencySituationName: "",
			districtName: "",
			description: "",
			volunteer: "",
			withoutVolunteer: false,
			newest: true,
			urgencyLevels: [],
			territories: []
		};
		
		EmergencySituationResource.search(vm.searchParams).then(function(response){
			vm.emergencySituations = response;
			vm.totalItems = vm.emergencySituations.length;
			vm.currentPage = 1;
			vm.changePage();
		});
		
		vm.maxSize = 5;
		vm.itemsPerPage = 10;
		
		vm.changePage = function(){
			var pagedData = vm.emergencySituations.slice((vm.currentPage - 1) * vm.itemsPerPage, (vm.currentPage) * vm.itemsPerPage);
			vm.pageSituations = pagedData;
		}
		
		vm.searchParams.newest = false;
		
		vm.filterSituations = function(){
			vm.searchParams.urgencyLevels = [];
			for(var i = 0; i < vm.urgencyLevels.length; i++){
				if(vm.urgencyLevels[i].isChecked === true){
					vm.searchParams.urgencyLevels.push(vm.urgencyLevels[i].name);
				}
			}
			vm.searchParams.territories = [];
			for(var i = 0; i < vm.territories.length; i++){
				if(vm.territories[i].isChecked === true){
					vm.searchParams.territories.push(vm.territories[i].id);
				}
			}
			vm.search();
		}
		
		vm.search = function(){
			EmergencySituationResource.search(vm.searchParams).then(function(response){
				vm.emergencySituations = response;
				vm.totalItems = vm.emergencySituations.length;
				vm.currentPage = 1;
				vm.changePage();
			});
		}
		
		vm.clear = function(){
			vm.searchParams = {
					emergencySituationName: "",
					districtName: "",
					description: "",
					volunteer: "",
					withoutVolunteer: false,
					newest: true,
					urgencyLevels: [],
					territories: []
			};
			vm.search();
			vm.searchParams.newest = false;
		}
	}
	
	search.$inject = ["authentication", "TerritoryResource", "EmergencySituationResource"];
})(angular);