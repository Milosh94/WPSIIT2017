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
				vm.report.territory = vm.report.territory.id;
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
	
	//controller for emergency situations assigned to volunteer
	app.controller("myEmergencySituationsCtrl", myEmergencySituations);
	
	function myEmergencySituations(){
		
	}
	
	myEmergencySituations.$inject = [];
})();