(function(){
	//user report emergency situation
	app.controller("reportSituationModalCtrl", reportSituation);
	
	function reportSituation($uibModalInstance, TerritoryResource, EmergencySituationResource){
		var vm = this;
		
		vm.report = {
				urgencyLevel: "Red",
				address: "",
				streetNumber: "",
				description: "",
				locationCoordinates: ""
		};
		
		TerritoryResource.getTerritories().then(function(response){
			vm.territories = response;
		});
		
		TerritoryResource.getStates().then(function(response){
			vm.states = response;
		});
		
		var service = new google.maps.places.AutocompleteService();
		
		vm.changeCountry = function(formAddress){
			if(vm.report.state === undefined || vm.report.state === null){
				formAddress.$setValidity("required", true);
			}
			else{
				formAddress.$setValidity("required", false);
				formAddress.$setUntouched();
			}
			if(vm.report.state !== null){
				autocomplete.setComponentRestrictions({country: vm.report.state.code});
			}
			vm.report.address = "";
			vm.report.streetNumber = "";
		}
		
		vm.sendReportForm = function(formData){
			vm.report.location = vm.report.address;
        	delete vm.report.state;
        	delete vm.report.address;
        	formData.append("report", JSON.stringify(vm.report));
			EmergencySituationResource.report(formData).then(function(response){
				$uibModalInstance.close("success");
			}, function(error){
				$uibModalInstance.dismiss("error");
			});
		}
		
		vm.getPlacePredictions = function(formData){
			if(vm.report.address !== undefined && vm.report.address.trim() !== ""){
				service.getPlacePredictions({input: vm.report.address}, function(predictions, status){
					if(status === "OK"){
						if(predictions.length > 0){
							if(predictions[0].description === vm.report.address.trim()){
								geocoder.geocode({ 
						            "placeId": predictions[0].place_id
						        }, function(responses, status){
						            if (status === "OK"){
						                var lat = responses[0].geometry.location.lat();
						                var lng = responses[0].geometry.location.lng();
						                vm.report.location = predictions[0].description;
										vm.report.locationCoordinates = lat + ":" + lng;
										delete vm.report.state;
										delete vm.report.address;
										formData.append("report", JSON.stringify(vm.report));
										EmergencySituationResource.report(formData).then(function(response){
											$uibModalInstance.close("success");
										}, function(error){
											$uibModalInstance.dismiss("error");
										});
						            }
						            else{
						            	vm.sendReportForm(formData);
						            }
						        });
							}
							else{
								vm.sendReportForm(formData);
							}
						}
						else{
							vm.sendReportForm(formData);
						}
				    }
					else{
						vm.sendReportForm(formData);
					}
				});
			}
			else{
				vm.sendReportForm(formData);
			}
		}
		
		vm.removeFile = function(){
			vm.file = undefined;
		}
		
		vm.sendReport = function(isValid, form){
			if(vm.report.state === undefined || vm.report.state === null){
				form.address.$setValidity("required", true);
			}
			isValid = form.$valid;
			if(isValid){
				var formData = new FormData();
				if(vm.file !== undefined){
					formData.append("file", vm.file);
				}
				else{
					formData.append("file", "");
				}
				if(vm.report.territory === undefined || vm.report.territory === null){
					vm.report.territory = -1;
				}
				else{
					vm.report.territory = vm.report.territory.id;
				}
				if(vm.report.description !== undefined){
					vm.report.description = vm.report.description.replace(/(?:\r\n|\r|\n)/g, " ");
				}
				else{
					vm.report.description = "";
				}
				if(autocomplete !== undefined && autocomplete.getPlace() !== undefined){
					if(vm.report.address === autocomplete.getPlace().formatted_address){
						vm.report.location = autocomplete.getPlace().formatted_address;
						vm.report.locationCoordinates = autocomplete.getPlace().geometry.location.lat() + ":" + autocomplete.getPlace().geometry.location.lng();
						delete vm.report.state;
						delete vm.report.address;
						formData.append("report", JSON.stringify(vm.report));
						EmergencySituationResource.report(formData).then(function(response){
							$uibModalInstance.close("success");
						}, function(error){
							$uibModalInstance.dissmiss("error");
						});
					}
					else{
						vm.getPlacePredictions(formData);
					}
				}
				else{
					vm.getPlacePredictions(formData);
				}
			}
			else{
				form.selectTerritory.$setTouched();
				angular.forEach(form.$error, function (field) {
			        angular.forEach(field, function(errorField){
			            errorField.$setTouched();
			        })
			    });
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("close");
		}
	}
	
	reportSituation.$inject = ["$uibModalInstance", "TerritoryResource", "EmergencySituationResource"];
	
	//controller for admin for publishing new emergency situations
	app.controller("publishCtrl", publish);
	
	function publish(authentication, $timeout, $state, EmergencySituationResource){
		var vm = this;
		console.log("publish state");
		vm.user = authentication.getLoggedUser();
		//if(vm.user === null){
		//	$timeout(function(){
		//		$state.go("root");
		//	});
		//}
		
		vm.maxSize = 5;
		vm.itemsPerPage = 10;
		
		vm.changePage = function(){
			var pagedData = vm.unpublishedSituations.slice((vm.currentPage - 1) * vm.itemsPerPage, (vm.currentPage) * vm.itemsPerPage);
			vm.pageSituations = pagedData;
		}
		
		EmergencySituationResource.getUnpublishedSituations().then(function(response){
			vm.unpublishedSituations = response;
			vm.totalItems = vm.unpublishedSituations.length;
			vm.currentPage = 1;
			vm.changePage();
		});
	}
	
	publish.$inject = ["authentication", "$timeout", "$state", "EmergencySituationResource"];
	
	//controller for emergency situation info
	app.controller("emergencySituationCtrl", emergencySituation);
	
	function emergencySituation(EmergencySituationResource, $state, authentication, $uibModal){
		var vm = this;
		
		vm.user = authentication.getLoggedUser();
		
		EmergencySituationResource.getEmergencySituation($state.params.situationId).then(function(response){
			vm.situation = response;
		}, function(error){
			//authentication.logout();//ne treba logout ukoliko ubode situaciju za koju nema dozvolu treba u stat
			$state.go("root");
		});
		
		vm.config = {
				autoHideScrollbar: false,
				theme: "dark-thin",
				scrollButtons: {
					scrollAmount: "auto",
					enable: true
				},
				axis: "y",
				advanced:{
					updateOnContentResize: true
				},
					setHeight: 120,
					scrollInertia: 0
		}
		
		vm.configComments = {
				autoHideScrollbar: false,
				theme: "dark-thin",
				scrollButtons: {
					scrollAmount: "auto",
					enable: true
				},
				axis: "y",
				advanced:{
					updateOnContentResize: true
				},
					setHeight: 500,
					scrollInertia: 0
		}
		
		vm.postComment = function(){
			if(vm.comment !== undefined && vm.comment !== null && vm.comment.trim() !== ""){
				EmergencySituationResource.postComment(vm.situation.id, vm.comment).then(function(response){
					EmergencySituationResource.getEmergencySituation($state.params.situationId).then(function(response){
						vm.situation = response;
					}, function(error){
						$state.go("root");
					});
				}, function(error){
					if(error.status === 403){
						authentication.logout();
						$state.go("root");
					}
				});
				vm.comment = "";
			}
		}
		
		vm.publishSituation = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/publish-situation.html",
				controller: "publishSituationModalCtrl",
				controllerAs: "vm",
				resolve: {
					situationId: function(){
						return vm.situation.id;
					}
				}
			});
			
			modalInstance.result.then(function(success){
				if(success !== "cancel"){
					EmergencySituationResource.getEmergencySituation($state.params.situationId).then(function(response){
						vm.situation = response;
					}, function(error){
						if(success !== "403"){
							$state.go("root");
						}
					});
				}
			}, function(error){});
		}
		
		vm.archiveSituation = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/archive-situation.html",
				controller: "archiveSituationModalCtrl",
				controllerAs: "vm",
				resolve: {
					situationId: function(){
						return vm.situation.id;
					}
				}
			});
			
			modalInstance.result.then(function(success){
				if(success !== "cancel"){
					EmergencySituationResource.getEmergencySituation($state.params.situationId).then(function(response){
						vm.situation = response;
					}, function(error){
						if(success !== "403"){
							$state.go("root");
						}
					});
				}
			}, function(error){});
		}
		
		vm.changeVolunteer = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/change-volunteer.html",
				controller: "changeVolunteerModalCtrl",
				controllerAs: "vm",
				resolve: {
					volunteer: function(){
						return vm.situation.volunteer;
					},
					territory: function(){
						return vm.situation.territory;
					},
					situation: function(){
						return vm.situation.id;
					}
				}
			});
			
			modalInstance.result.then(function(success){
				if(success !== "cancel"){
					EmergencySituationResource.getEmergencySituation($state.params.situationId).then(function(response){
						vm.situation = response;
					}, function(error){
						if(success !== "403"){
							$state.go("root");
						}
					});
				}
			}, function(error){});
		}
		
		vm.changeTerritory = function(){
			var modalInstance = $uibModal.open({
				templateUrl: "core/views/modals/change-territory.html",
				controller: "changeTerritoryModalCtrl",
				controllerAs: "vm",
				resolve: {
					volunteer: function(){
						return vm.situation.volunteer;
					},
					territory: function(){
						return vm.situation.territory;
					},
					situation: function(){
						return vm.situation.id;
					}
				}
			});
			
			modalInstance.result.then(function(success){
				if(success !== "cancel"){
					EmergencySituationResource.getEmergencySituation($state.params.situationId).then(function(response){
						vm.situation = response;
					}, function(error){
						if(success !== "403"){
							$state.go("root");
						}
					});
				}
			}, function(error){});
		}
	}
	
	emergencySituation.$inject = ["EmergencySituationResource", "$state", "authentication", "$uibModal"];
	
	//controller for admin for publish situation modal confirmation
	app.controller("publishSituationModalCtrl", publishSituationModal);
	
	function publishSituationModal(situationId, $uibModalInstance, EmergencySituationResource, toastr, toastrConfig, authentication, $state){
		var vm = this;
		
		vm.situationId = situationId;
		
		toastrConfig.maxOpened = 1;
		toastrConfig.positionClass = "toast-top-center";
		
		vm.publish = function(){
			EmergencySituationResource.publishSituation(vm.situationId).then(function(response){
				toastr.success("Situation published", "Success", {
					closeButton: true,
					timeout: 3000
				});
				$uibModalInstance.close("success");
			}, function(error){
				toastr.error("Error", {
					closeButton: true,
					timeout: 3000
				});
				if(error.status === 403){
					$uibModalInstance.close("403");
					authentication.logout();
					$state.go("root");
				}
				else{
					$uibModalInstance.close("error");
				}
			});
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	publishSituationModal.$inject = ["situationId", "$uibModalInstance", "EmergencySituationResource", "toastr", "toastrConfig", "authentication", "$state"];
	
	//controller for adim for archive situation modal
	app.controller("archiveSituationModalCtrl", archiveSituationModal);
	
	function archiveSituationModal(situationId, $uibModalInstance, EmergencySituationResource, toastr, toastrConfig, authentication, $state){
		var vm = this;
		
		vm.situationId = situationId;
		
		toastrConfig.maxOpened = 1;
		toastrConfig.positionClass = "toast-top-center";
		
		vm.archive = function(){
			EmergencySituationResource.archiveSituation(vm.situationId).then(function(response){
				toastr.success("Situation archived", "Success", {
					closeButton: true,
					timeout: 3000
				});
				$uibModalInstance.close("success");
			}, function(error){
				toastr.error("Error", {
					closeButton: true,
					timeout: 3000
				});
				if(error.status === 403){
					$uibModalInstance.close("403");
					authentication.logout();
					$state.go("root");
				}
				else{
					$uibModalInstance.close("error");
				}
			});
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	archiveSituationModal.$inject = ["situationId", "$uibModalInstance", "EmergencySituationResource", "toastr", "toastrConfig", "authentication", "$state"];
	
	//cpntroller for admin for changing volunteer for situation modal
	app.controller("changeVolunteerModalCtrl", changeVolunteerModal);
	
	function changeVolunteerModal(volunteer, territory, situation, $uibModalInstance, EmergencySituationResource, toastr, toastrConfig, VolunteerResource, authentication, $state){
		var vm = this;
		
		vm.volunteer = volunteer;
		
		vm.territory = territory;
		
		vm.situation = situation;
		
		toastrConfig.maxOpened = 1;
		toastrConfig.positionClass = "toast-top-center";
		
		if(vm.territory === null){
			vm.volunteers = [];
		}
		else{
			VolunteerResource.getTerritoryVolunteers(vm.territory.id).then(function(response){
				vm.volunteers = response;
			});
		}
		
		vm.removed = false;
		
		vm.removeVolunteer = function(){
			if(vm.volunteer !== null){
				vm.removed = true;
				vm.volunteer = null;
			}
		}
		
		vm.change = function(){
			if(vm.selectedVolunteer !== undefined && vm.selectedVolunteer !== null){
				EmergencySituationResource.changeVolunteer(vm.situation, vm.selectedVolunteer.username).then(function(response){
					toastr.success("Volunteer changed", "Success", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.close("success");
				}, function(error){
					toastr.error("Error", {
						closeButton: true,
						timeout: 3000
					});
					if(error.status === 403){
						$uibModalInstance.close("403");
						authentication.logout();
						$state.go("root");
					}
					else{
						$uibModalInstance.close("error");
					}
				});
			}
			else{
				if(vm.removed === true){
					EmergencySituationResource.changeVolunteer(vm.situation, "").then(function(response){
						toastr.success("Volunteer changed", "Success", {
							closeButton: true,
							timeout: 3000
						});
						$uibModalInstance.close("success");
					}, function(error){
						toastr.error("Error", {
							closeButton: true,
							timeout: 3000
						});
						if(error.status === 403){
							$uibModalInstance.close("403");
							authentication.logout();
							$state.go("root");
						}
						else{
							$uibModalInstance.close("error");
						}
					});
				}
				else{
					EmergencySituationResource.changeVolunteer(vm.situation, vm.volunteer.username).then(function(response){
						toastr.success("Volunteer changed", "Success", {
							closeButton: true,
							timeout: 3000
						});
						$uibModalInstance.close("success");
					}, function(error){
						toastr.error("Error", {
							closeButton: true,
							timeout: 3000
						});
						if(error.status === 403){
							$uibModalInstance.close("403");
							authentication.logout();
							$state.go("root");
						}
						else{
							$uibModalInstance.close("error");
						}
					});
				}
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	changeVolunteerModal.$inject = ["volunteer", "territory", "situation", "$uibModalInstance", "EmergencySituationResource", "toastr", "toastrConfig", "VolunteerResource", "authentication", "$state"];
	
	//controller for admin for changing situation territory
	app.controller("changeTerritoryModalCtrl", changeTerritoryModal);
	
	function changeTerritoryModal(volunteer, territory, situation, $uibModalInstance, EmergencySituationResource, toastr, toastrConfig, TerritoryResource, authentication, $state){
		var vm = this;
		
		vm.volunteer = volunteer;
		
		vm.territory = territory;
		
		vm.situation = situation;
		
		toastrConfig.maxOpened = 1;
		toastrConfig.positionClass = "toast-top-center";
		
		TerritoryResource.getTerritories().then(function(response){
			vm.territories = response;
		});
		
		vm.removed = false;
		
		vm.removeTerritory = function(){
			if(vm.territory !== null){
				vm.removed = true;
				vm.territory = null;
			}
		}
		
		vm.change = function(){
			if(vm.selectedTerritory !== undefined && vm.selectedTerritory !== null){
				EmergencySituationResource.changeTerritory(vm.situation, vm.selectedTerritory.id).then(function(response){
					toastr.success("Territory changed", "Success", {
						closeButton: true,
						timeout: 3000
					});
					$uibModalInstance.close("success");
				}, function(error){
					toastr.error("Error", {
						closeButton: true,
						timeout: 3000
					});
					if(error.status === 403){
						$uibModalInstance.close("403");
						authentication.logout();
						$state.go("root");
					}
					else{
						$uibModalInstance.close("error");
					}
				});
			}
			else{
				if(vm.removed === true){
					EmergencySituationResource.changeTerritory(vm.situation, -1).then(function(response){
						toastr.success("Territory changed", "Success", {
							closeButton: true,
							timeout: 3000
						});
						$uibModalInstance.close("success");
					}, function(error){
						toastr.error("Error", {
							closeButton: true,
							timeout: 3000
						});
						if(error.status === 403){
							$uibModalInstance.close("403");
							authentication.logout();
							$state.go("root");
						}
						else{
							$uibModalInstance.close("error");
						}
					});
				}
				else{
					EmergencySituationResource.changeTerritory(vm.situation, vm.territory.id).then(function(response){
						toastr.success("Territory changed", "Success", {
							closeButton: true,
							timeout: 3000
						});
						$uibModalInstance.close("success");
					}, function(error){
						toastr.error("Error", {
							closeButton: true,
							timeout: 3000
						});
						if(error.status === 403){
							$uibModalInstance.close("403");
							authentication.logout();
							$state.go("root");
						}
						else{
							$uibModalInstance.close("error");
						}
					});
				}
			}
		}
		
		vm.cancel = function(){
			$uibModalInstance.close("cancel");
		}
	}
	
	changeTerritoryModal.$inject = ["volunteer", "territory", "situation", "$uibModalInstance", "EmergencySituationResource", "toastr", "toastrConfig", "TerritoryResource", "authentication", "$state"];
})();