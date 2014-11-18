/**
 * Created by Clemens Puehringer on 17/11/14.
 */

respondecoApp
    .directive('rspConfirmDelete', function() {
        return {
            restrict: 'AE',
            replace: true,
            templateUrl: 'template/confirm_delete.html',
            scope: {
                onConfirm: '&'
            },
            controller: function($scope) {
                $scope.isDeleting = false
                $scope.startDelete = function() {
                    $scope.isDeleting = true
                }
                $scope.cancelDelete = function() {
                    $scope.isDeleting = false
                }
                $scope.confirmDelete = function() {
                    $scope.onConfirm();
                    $scope.isDeleting = false;
                }
            }
        };
    });