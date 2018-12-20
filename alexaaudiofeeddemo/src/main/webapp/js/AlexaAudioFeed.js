var app = angular.module('alexaAudioFeed', [
	  'ngRoute',
	  //'alexaAudioFeed.config',
	  'alexaAudioFeed.filters',
	  'alexaAudioFeed.services',
	  'alexaAudioFeed.directives',
	  'alexaAudioFeed.controllers',
	  'ui.bootstrap',
	  'angularFileUpload',
	  'ngDraggable',
	]);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/manage', {templateUrl: 'partials/manage/file.html?', controller:'audiofileCtrl'});
	$routeProvider.when('/manage/newslist', {templateUrl: 'partials/manage/newslist.html?', controller:'audiofileCtrl'});
	$routeProvider.when('/manage/addnews', {templateUrl: 'partials/manage/addnews.html?', controller:'audiofileCtrl'});
	$routeProvider.otherwise({redirectTo: '/manage'});
}]);


var appServices = angular.module('alexaAudioFeed.services', [ ]);

appServices.factory('alexaAudioFeedService', [ "$http", "$upload", function($http, $upload) {

	var alexaAudioFeedService = {};

	alexaAudioFeedService.loadAudiofiles = function() {
		return $http({
			method : 'GET',
			url: Constants.AUDIOFEED_MANAGE_BASE_URL
			//url : 'http://localhost:8080/alexaaudiofeeddemo/api/manage/audionews'
			//url : 'http://int-sdnet-up1.sdp.csi.it:10110/alexaaudiofeeddemo/api/manage/audionews'
		});
	};
	
	alexaAudioFeedService.deleteAudiofiles = function(filename) {
		return $http({
			method : 'DELETE',
			url: Constants.AUDIOFEED_MANAGE_BASE_URL+filename
			//url : 'http://localhost:8080/alexaaudiofeeddemo/api/manage/audionews'
			//url : 'http://int-sdnet-up1.sdp.csi.it:10110/alexaaudiofeeddemo/api/manage/audionews'
		});
	};
	alexaAudioFeedService.uploadNews  = function(news){
		console.log("uploadNews",news);
		var urlWithParam =  Constants.AUDIOFEED_MANAGE_BASE_URL+news.title; 
		//var urlWithParam =  'http://localhost:8080/alexaaudiofeeddemo/api/manage/audionews/'+news.title; 
		//var urlWithParam =  'http://int-sdnet-up1.sdp.csi.it:10110/alexaaudiofeeddemo/api/manage/audionews/'+news.title; 

		var postData = {newstitle: news.title};
		
		return $upload.upload({
			url: urlWithParam,
			method: 'POST',
			file: news.selectedFile
		//	data: postData,
        //  data: {file: news.selectedFile, 'newstitle': news.title}

		});

	};
	
	return alexaAudioFeedService;
} ]);

var appFilters = angular.module('alexaAudioFeed.filters', []);

appFilters.filter('format_filesize', function() {
	return function(input) {
		var output = "";
		if (input) {
			input=Math.trunc(input);
			if(input<1000)
				output=input+"byte";
			else if(input<1000000)
				output=(input/1000).toFixed(1)+"Kb";
			else if(input<1000000000)
				output=(input/1000000).toFixed(1)+"Mb";
			else if(input<1000000000000)
				output=(input/1000000000).toFixed(1)+"Gb";
	    }
		return output;
	};
});

appFilters.filter('trustedAudioUrl', function($sce) {
    return function(path) {
        return $sce.trustAsResourceUrl(path);
    };
});


var appDirectives = angular.module('alexaAudioFeed.directives', []);

appDirectives.directive('appVersion', [ 'version', function(version) {
	return function(scope, elm, attrs) {
		elm.text(version);
	};
} ]);


appDirectives.directive('mainSidebar', function() {
	return {
		restrict : 'E',
		templateUrl : 'partials/common/main-sidebar.html?',
	};
});




var appControllers = angular.module('alexaAudioFeed.controllers', []);

appControllers.controller('audiofileCtrl', [ '$scope', 'alexaAudioFeedService',
                                          function($scope,alexaAudioFeedService) {
	
	$scope.audiofiles = new Array();
	var loadAudiofiles =  function(){
			alexaAudioFeedService.loadAudiofiles().then(function(response){
			console.log("loadAudiofiles", response);
			$scope.audiofiles = response.data;
		});	
	};
	loadAudiofiles();
	
	$scope.maxFileSize = 10000000;
	$scope.newNews = {};
	$scope.onFileSelect = function($files) {
		console.log("onFileSelect", $files);
		//$scope.newNews = {};
		$scope.uploadInfo = {message:""};
		$scope.newNews.selectedFile = $files[0];
		console.log("onFileSelect", $scope.newNews.selectedFile );
		if($scope.newNews.selectedFile !=null && $scope.newNews.selectedFile.size>$scope.maxFileSize){
			$scope.uploadInfo.message = {type:'warning', message: 'File troppo grande'};
			$scope.choosenFileSize = scope.newNews.selectedFile.size; 
			$scope.newNews.selectedFile = null;
		}
		
	};
	$scope.clearFileSelection = function(){
		$scope.newNews.selectedFile = null;
	};
	
	$scope.uploadFile = function(){
		console.log("uploadFile", $scope.newNews);
		if(typeof $scope.newNews != 'undefined' && $scope.newNews.selectedFile.name!= null){
			alexaAudioFeedService.uploadNews($scope.newNews).success(function(data, status, headers, config){
				console.log("success", data);
				loadAudiofiles();
			}).error(
			function(error){
				console.log("error", error);
				
			});
		}

	};
	
	$scope.deleteFile = function(filename){
		console.log("deleteFile", filename);
		alexaAudioFeedService.deleteAudiofiles(filename).then(function(response){
			console.log("loadAudiofiles", response);
			loadAudiofiles();
		});	
	};
	
	
}]);

