/**
 * Created by Clemens Puehringer on 25/11/14.
 */
respondecoApp
    .directive('rspProject', function() {
        return {
            restrict: 'AE',
            replace: true,
            templateUrl: 'template/project.html',
            scope: {
                project: '&'
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