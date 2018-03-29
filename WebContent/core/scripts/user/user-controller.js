(function(){
	//controller for logged user profile
	app.controller("profileCtrl", profile);
	
	function profile(authentication, UserResource, $state, $timeout, $uibModal){
		var vm = this;
		
		vm.user = authentication.getUser();
		if(vm.user === null){
			$timeout(function(){
				$state.go("root");
			});
		}
		
		UserResource.getLoggedUser().then(function(response){
			vm.user = response;
			authentication.saveUser(response);
		}, function(error){
			authentication.logout();
			$state.go("root");
		});
		
		vm.updateProfile = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/profile.html",
				controller: "profileModalCtrl",
				controllerAs: "vm",
				resolve: {
					user: function(){
						return vm.user;
					}
				}
			});
			modalInstance.result.then(function(){
				vm.user = authentication.getUser();
			}, function(res){});
		}
	}
	
	profile.$inject = ["authentication", "UserResource", "$state", "$timeout", "$uibModal"];
	
	//controller for modal for volunteer updating his profile
	app.controller("profileModalCtrl", profileModal);
	
	function profileModal($uibModalInstance, user, TerritoryResource, UserResource, toastr, toastrConfig, authentication, $state){
		var vm = this;
		
		vm.user = JSON.parse(JSON.stringify(user));
		
		TerritoryResource.getTerritories().then(function(response){
			vm.territories = response;
		});
		
		vm.showPassword = false;
		vm.changeHide = "Change";
		vm.changePassword = function(){
			vm.showPassword = !vm.showPassword;
			if(!vm.showPassword){
				vm.user.password = "";
				vm.oldPassword = "";
				vm.changeHide = "Change";
			}
			else{
				vm.changeHide = "Hide";
			}
		}
		
		vm.newPasswordChanged = function(isInvalid){
			if((vm.oldPassword === undefined || vm.oldPassword === "") && (vm.user.password === undefined || vm.user.password === "")){
				vm.profileForm.oldPassword.$setValidity("passwordRequired", true);
			}
			if(isInvalid){
				vm.profileForm.newPassword.$setValidity("passwordRequired", true);
			}
		}
		
		vm.oldPasswordChanged = function(isInvalid){
			if((vm.oldPassword === undefined || vm.oldPassword === "") && (vm.user.password === undefined || vm.user.password === "")){
				vm.profileForm.newPassword.$setValidity("passwordRequired", true);
			}
			if(isInvalid){
				vm.profileForm.oldPassword.$setValidity("passwordRequired", true);
				vm.profileForm.oldPassword.$setValidity("wrongPassword", true);
			}
		}
		
		vm.changePicture = false;
		vm.removeFile = function(){
			vm.file = undefined;
			vm.changePicture = true;
		}
		
		vm.updateProfile = function(isValid){
			if(vm.oldPassword !== undefined && vm.oldPassword !== "" && (vm.user.password === undefined || vm.user.password === "")){
				vm.profileForm.newPassword.$setValidity("passwordRequired", false);
				return;
			}
			if(vm.user.password !== undefined && vm.user.password !== "" && (vm.oldPassword === undefined || vm.oldPassword === "")){
				vm.profileForm.oldPassword.$setValidity("passwordRequired", false);
				return;
			}
			if(isValid){
				var formData = new FormData();
				if(vm.file !== undefined){
					formData.append("file", vm.file);
					formData.append("changePicture", true);
				}
				else{
					formData.append("file", "");
					if(vm.changePicture){
						formData.append("changePicture", true);
					}
					else{
						formData.append("changePicture", false);
					}
				}
				vm.updatedUser = {
						username: vm.user.username,
						password: vm.user.password,
						firstName: vm.user.firstName,
						lastName: vm.user.lastName,
						phone: vm.user.phone,
						email: vm.user.email
				}
				if(vm.user.territory === undefined || vm.user.territory === null){
					vm.updatedUser.territory = -1;
				}
				else{
					vm.updatedUser.territory = vm.user.territory.id;
				}
				if(vm.oldPassword !== undefined && vm.oldPassword !== "" && vm.user.password !== undefined && vm.user.password !== ""){
					formData.append("oldPassword", vm.oldPassword);
				}
				else{
					vm.updatedUser.password = "";
					formData.append("oldPassword", "");
				}
				formData.append("user", JSON.stringify(vm.updatedUser));
				toastrConfig.maxOpened = 1;
				toastrConfig.positionClass = "toast-top-center";
				UserResource.updateUser(formData).then(function(response){
					toastr.success("Success: User updated", {
						closeButton: true,
						timeout: 3000
					});
					authentication.saveUser(response);
					$uibModalInstance.close("success");
				}, function(error){
					if(error.data !== undefined && error.data.wrongPassword === true){
						if(vm.profileForm.oldPassword !== undefined){
							vm.profileForm.oldPassword.$setValidity("wrongPassword", false);
						}
					}
					else{
						toastr.error("Error", {
							closeButton: true,
							timeout: 3000
						});
						$uibModalInstance.dismiss("error");
						if(error.status === 403){
							authentication.logout();
							$state.go("root");
						}
					}
				});
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.dismiss("cancel");
		}
	}
	
	profileModal.$inject = ["$uibModalInstance", "user", "TerritoryResource", "UserResource", "toastr", "toastrConfig", "authentication", "$state"];
})();