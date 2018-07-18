'use strict';
var module1 = angular.module('allView', []);
module1.controller('experiments', function($scope, $http) {
    $scope.firstName = "John";
    $scope.lastName = "Doe";
    $scope.fullName = function() {
        return $scope.firstName + " " + $scope.lastName;
    };
    $scope.setVars = function(expid, expname, startDate, endDate, description, algID){
        localStorage.setItem("expid", expid);
    }
    
    $http.get('http://localhost:8080/boss/readAll?category=Experiments&accessingUser=ff28d340-7e4c-11e8-b61a-19fcc49447c2').
        then(function(response) {
            $scope.experiments = response.data;
        });
    

});


module1.controller('buckets', function($scope, $http) {
    $http.get('http://localhost:8080/boss/readAll?category=Buckets&accessingUser=ff28d340-7e4c-11e8-b61a-19fcc49447c2').
        then(function(response) {
            $scope.buckets = response.data;
        });

});

module1.controller('users', function($scope, $http) {
    $http.get('http://localhost:8080/boss/readAll?category=Users&accessingUser=ff28d340-7e4c-11e8-b61a-19fcc49447c2').
        then(function(response){
        $scope.users=response.data;
        
    });
    
        });


module1.controller('buckExps', function($scope, $http){
 $http.get('http://localhost:8080/boss/buckets/getAllBuckExps').
        then(function(response){
        $scope.buckExps=response.data;
        
    });
    
        });
    
