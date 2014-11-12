'use strict';

respondecoApp.controller('OrgJoinRequestController', function ($scope, resolvedOrgJoinRequest, OrgJoinRequest) {

        $scope.orgjoinrequests = resolvedOrgJoinRequest;

        $scope.create = function () {
            OrgJoinRequest.save($scope.orgjoinrequest,
                function () {
                    $scope.orgjoinrequests = OrgJoinRequest.query();
                    $('#saveOrgJoinRequestModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.orgjoinrequest = OrgJoinRequest.get({id: id});
            $('#saveOrgJoinRequestModal').modal('show');
        };

        $scope.delete = function (id) {
            OrgJoinRequest.delete({id: id},
                function () {
                    $scope.orgjoinrequests = OrgJoinRequest.query();
                });
        };

        $scope.clear = function () {
            $scope.orgjoinrequest = {orgId: null, userLogin: null, id: null};
        };
    });
