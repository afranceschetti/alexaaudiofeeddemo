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
	//$routeProvider.when('/manage', {templateUrl: 'partials/manage/file.html?', controller:'audiofileCtrl'});
	$routeProvider.when('/manage/news/:tab', {templateUrl: 'partials/manage/file.html?', controller:'audiofileCtrl'});
	//$routeProvider.when('/manage/addnewsaudio', {templateUrl: 'partials/manage/add-news-audio.html?', controller:'audiofileCtrl'});
	//$routeProvider.when('/manage/addnewstext', {templateUrl: 'partials/manage/add-news-text.html?', controller:'audiofileCtrl'});
	$routeProvider.otherwise({redirectTo: '/manage/news/list'});
}]);


var appServices = angular.module('alexaAudioFeed.services', [ ]);

appServices.factory('alexaAudioFeedService', [ "$http", "$upload", function($http, $upload) {

	var alexaAudioFeedService = {};

	alexaAudioFeedService.loadAudiofiles = function() {
		return $http({
			method : 'GET',
			url: Constants.AUDIOFEED_MANAGE_BASE_URL
		});
	};
	
	alexaAudioFeedService.deleteAudiofiles = function(filename) {
		return $http({
			method : 'DELETE',
			url: Constants.AUDIOFEED_MANAGE_BASE_URL+filename
		});
	};
	alexaAudioFeedService.uploadAudioNews  = function(news){
		console.log("uploadNews",news);
		var urlWithParam =  Constants.AUDIOFEED_MANAGE_BASE_URL+'audio/'+news.title; 

		var postData = {newstitle: news.title};
		
		return $upload.upload({
			url: urlWithParam,
			method: 'POST',
			file: news.selectedFile
		});

	};
	
	
	alexaAudioFeedService.uploadTextNews  = function(news){
		console.log("uploadNews",news);
		var urlWithParam =  Constants.AUDIOFEED_MANAGE_BASE_URL+'text/'+news.title; 
		return $http({
	          method  : 'POST',
	          url     : urlWithParam,
	          data    : 'mainText='+news.mainText,
	          headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
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

appFilters.filter('string_ellipse', function () {
    return function (text, length, end) {
    	
    	if(typeof text === "undefined"  || text == null)
    		text = "";
    	
        if (isNaN(length))
            length = 10;

        if (end === undefined)
            end = "...";

        if (text.length <= length || text.length - end.length <= length) {
            return text;
        }
        else {
            return String(text).substring(0, length-end.length) + end;
        }
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

appControllers.controller('globalCtrl', [ '$scope', '$route', '$location', 'alexaAudioFeedService',
    function($scope,$route,$location, alexaAudioFeedService) {
	$scope.navigation = {currentPage: 'news.list'};
	$scope.feedUrl = Constants.AUDIOFEED_FEED_BASE_URL;
	
	$scope.goto = function(target){
		$scope.navigation.currentPage=target;
		console.log("manage/"+target.replace(".","/"))
		$location.path("manage/"+target.replace(".","/"));
	}
	
	
}]);

appControllers.controller('audiofileCtrl', [ '$scope', '$route', 'alexaAudioFeedService',
                                          function($scope,$route,alexaAudioFeedService) {
	$scope.tab  = $route.current.params.tab;
	console.log("tab",$route.current.params);
	$scope.alexaMaxNews = 5;
	$scope.audiofiles = new Array();
	var loadAudiofiles =  function(){
		$scope.feedbackList = {};
		alexaAudioFeedService.loadAudiofiles().then(function(response){
			console.log("loadAudiofiles", response);
			$scope.audiofiles = response.data;
			if(response.data.length>$scope.alexaMaxNews)
				$scope.feedbackList = {message:"Solo le prime 5 news saranno gestite da alexa", color:FEEDBACK_COLOR_INFO, icon: FEEDBACK_ICON_INFO};

		});	
	};
	loadAudiofiles();
	
	$scope.maxFileSize = 10000000;
	$scope.newNews = {selectedFile:null};
	$scope.onFileSelect = function($files) {
		console.log("onFileSelect", $files);
		//$scope.newNews = {};
		$scope.uploadInfo = {message:""};
		$scope.newNews.selectedFile = $files[0];
		console.log("onFileSelect", $scope.newNews.selectedFile );
		if($scope.newNews.selectedFile !=null && $scope.newNews.selectedFile.size>$scope.maxFileSize){
			$scope.feedback.message = {type:'warning', message: 'File troppo grande'};
			$scope.choosenFileSize = scope.newNews.selectedFile.size; 
			$scope.newNews.selectedFile = null;
		}
		
	};
	$scope.clearFileSelection = function(){
		$scope.newNews.selectedFile = null;
	};
	
	var FEEDBACK_COLOR_INFO = 'light-blue lighten-4';
	var FEEDBACK_ICON_INFO = 'info_outline';
	var FEEDBACK_COLOR_SUCCESS = 'light-green lighten-3';
	var FEEDBACK_ICON_SUCCESS = 'check';
	var FEEDBACK_COLOR_WARNING = 'yellow lighten-2';
	var FEEDBACK_ICON_WARNING = 'warning ';
	var FEEDBACK_COLOR_ERROR = 'red  lighten-3';
	var FEEDBACK_ICON_ERROR = 'error_outline';
	$scope.feedback = {};
	
	$scope.uploadFile = function(){
		console.log("uploadFile", $scope.newNews);
		$scope.feedback = {};
		if($scope.newNews.selectedFile == null && ($scope.newNews.title == null || $scope.newNews.title ==""))
			$scope.feedback = {message:"Specificare il titolo e selezionare un file audio", color:FEEDBACK_COLOR_WARNING, icon: FEEDBACK_ICON_WARNING};
		else if($scope.newNews.title == null || $scope.newNews.title =="")
			$scope.feedback = {message:"Specificare il titolo", color:FEEDBACK_COLOR_WARNING, icon: FEEDBACK_ICON_WARNING};
		else if($scope.newNews.selectedFile == null)
			$scope.feedback = {message:"Selezionare un file audio", color:FEEDBACK_COLOR_WARNING, icon: FEEDBACK_ICON_WARNING};
		else{
			alexaAudioFeedService.uploadAudioNews($scope.newNews).success(function(data, status, headers, config){
				console.log("success", data);
				$scope.feedback = {message:"News caricata correttamente ", color:FEEDBACK_COLOR_SUCCESS, icon: FEEDBACK_ICON_SUCCESS};
				loadAudiofiles();
				
			}).error(
			function(error){
				console.log("error", error);
				
			});
		}

	};
	
	$scope.uploadTextNews = function(){
		console.log("uploadTextNews", $scope.newNews);
		$scope.feedback = {};
		if(($scope.newNews.mainText == null || $scope.newNews.mainText =="") && ($scope.newNews.title == null || $scope.newNews.title ==""))
			$scope.feedback = {message:"Specificare il titolo e il contenuto della news", color:FEEDBACK_COLOR_WARNING, icon: FEEDBACK_ICON_WARNING};
		else if($scope.newNews.title == null || $scope.newNews.title =="")
			$scope.feedback = {message:"Specificare il titolo", color:FEEDBACK_COLOR_WARNING, icon: FEEDBACK_ICON_WARNING};
		else if($scope.newNews.mainText == null || $scope.newNews.mainText =="")
			$scope.feedback = {message:"Specificare il contenuto della news", color:FEEDBACK_COLOR_WARNING, icon: FEEDBACK_ICON_WARNING};
		else{
			alexaAudioFeedService.uploadTextNews($scope.newNews).success(function(data, status, headers, config){
				console.log("success", data);
				$scope.feedback = {message:"News caricata correttamente ", color:FEEDBACK_COLOR_SUCCESS, icon: FEEDBACK_ICON_SUCCESS};
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

