(function(){
	app.directive("fileModel", file);
	
	function file($parse){
		return {
		    restrict: "A",
		    link: function(scope, element, attrs){
		        var model = $parse(attrs.fileModel);
		        var modelSetter = model.assign;

		        element.bind("change", function(){
		            scope.$apply(function(){
		                modelSetter(scope, element[0].files[0]);
		            });
		            if (element[0].files && element[0].files[0]){
		                var reader = new FileReader();
		                reader.onload = function (e){
		                    $("#profilePicture").attr("src", e.target.result);
		                }
		                reader.readAsDataURL(element[0].files[0]);
		            }
		            $("#profileRemoveButton").show();
		        });
		    }
		};
	}
	
	file.$inject = ["$parse"];
	
	app.directive("removeFile", remove);
	
	function remove(){
		return {
			restrict: "A", 
			link: function(scope, element, attrs){
				element.bind("click", function(){
					$("#profilePictureInput")[0].value="";    	
			    	$("#profilePicture").attr("src", "images/web/no-image.png");
			    	$("#profileRemoveButton").hide();
				});
			}
		};
	}
	
	remove.$inject = [];
})();