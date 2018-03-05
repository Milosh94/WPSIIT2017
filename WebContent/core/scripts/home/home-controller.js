(function(angular){
	//Home navigation
	app.controller("homeCtrl", home);
	
	function home(authentication, $state, $timeout, $uibModal, TerritoryResource){
		var vm = this;
		
		vm.user = authentication.getUser();
		
		vm.loginUser = {};
		vm.login = function(){
			authentication.login(vm.loginUser).then(function(response){
				vm.user = authentication.getUser();
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
		
		if($state.is() === "root"){
			$timeout(function(){
				$state.go("home");
			})
		}
	}
	
	home.$inject = ["authentication", "$state", "$timeout", "$uibModal", "TerritoryResource"];
	
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
	
	function homepage(authentication){
		var vm = this;
		
		vm.user = authentication.getUser();
	}
	
	homepage.$inject = ["authentication"];
})(angular);