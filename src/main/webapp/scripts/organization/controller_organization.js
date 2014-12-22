'use strict';

respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User) {
    var isOwner = false;
    var user;

    $scope.organizations = resolvedOrganization;

    $scope.postings = Organization.getPostingsByOrgId({id:$routeParams.id});
    $scope.postingInformation = null;

    $scope.update = function(name) {
        $scope.organization = Organization.get({
            id: name
        }, function() {
            Organization.getMembers({
                id: $scope.organization.id
            }, function(data)  {
                $scope.members = data;
            });

            Account.get(null, function(account) {
                user = account;
                isOwner = user !== undefined && user.login === $scope.organization.owner.login;
            });

        });
    };

    $scope.delete = function(id) {
        id = id || $scope.organization.id;

        if (confirm("Wirklich löschen?") === false) return;

        Organization.delete({
                id: id
            },
            function() {
                $scope.organizations = Organization.query();
            });
    };

    $scope.redirectToEdit = function() {
        $location.path('organization/edit/' + $scope.organization.id);
    };

    $scope.isOwner = function() {
        return isOwner;
    };

    $scope.updateUser = function($item, $model, $label) {
        $scope.selectedUser = $item;
    };

    $scope.redirectToOverview = function() {
        $location.path('organization');
    };

    $scope.redirectToOwnResources = function() {
        $location.path('ownresource');
    };

    $scope.redirectToRequests = function() {
        $location.path('requests');
    }

    if ($routeParams.id !== undefined) {
        $scope.update($routeParams.id);
    }

    //Posting

    var refreshPostings = function() {
        $scope.postings = Organization.getPostingsByOrgId({id:$scope.organization.id})
    };

    $scope.addPosting = function() {
        if($scope.postingInformation.length < 5 || $scope.postingInformation.length > 100) {
            return;
        }
        Organization.addPostingForOrganization({id:$routeParams.id},$scope.postingInformation),function() {
            refreshPostings();
        };
    }

    $scope.deletePosting = function(id) {
        Organization.deletePosting({id:$scope.organization.id,
            pid:id},
            function() {
                refreshPostings();
            })
    }
});
