'use strict';

angular.module('respondecoApp')
    .directive('activeMenu', function($translate, $locale, tmhDynamicLocale) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs, controller) {
                var language = attrs.activeMenu;

                scope.$watch(function() {
                    return $translate.use();
                }, function(selectedLanguage) {
                    if (language === selectedLanguage) {
                        tmhDynamicLocale.set(language);
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                });
            }
        };
    })
    .directive('activeLink', function(location) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs, controller) {
                var clazz = attrs.activeLink;
                var path = attrs.href;
                path = path.substring(1); //hack because path does bot return including hashbang
                scope.location = location;
                scope.$watch('location.path()', function(newPath) {
                    if (path === newPath) {
                        element.addClass(clazz);
                    } else {
                        element.removeClass(clazz);
                    }
                });
            }
        };
    }).directive('passwordStrengthBar', function() {
        return {
            replace: true,
            restrict: 'E',
            template: '<div id="strength">' +
                      '<small translate="global.messages.validate.newpassword.strength">Password strength:</small>' +
                      '<ul id="strengthBar">' +
                        '<li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li><li class="point"></li>' +
                      '</ul>' +
                    '</div>',
            link: function(scope, iElement, attr) {
                var strength = {
                    colors: ['#F00', '#F90', '#FF0', '#9F0', '#0F0'],
                    mesureStrength: function (p) {

                        var _force = 0;
                        var _regex = /[$-/:-?{-~!"^_`\[\]]/g; // "

                        var _lowerLetters = /[a-z]+/.test(p);
                        var _upperLetters = /[A-Z]+/.test(p);
                        var _numbers = /[0-9]+/.test(p);
                        var _symbols = _regex.test(p);

                        var _flags = [_lowerLetters, _upperLetters, _numbers, _symbols];
                        var _passedMatches = $.grep(_flags, function (el) { return el === true; }).length;

                        _force += 2 * p.length + ((p.length >= 10) ? 1 : 0);
                        _force += _passedMatches * 10;

                        // penality (short password)
                        _force = (p.length <= 6) ? Math.min(_force, 10) : _force;

                        // penality (poor variety of characters)
                        _force = (_passedMatches == 1) ? Math.min(_force, 10) : _force;
                        _force = (_passedMatches == 2) ? Math.min(_force, 20) : _force;
                        _force = (_passedMatches == 3) ? Math.min(_force, 40) : _force;

                        return _force;

                    },
                    getColor: function (s) {

                        var idx = 0;
                        if (s <= 10) { idx = 0; }
                        else if (s <= 20) { idx = 1; }
                        else if (s <= 30) { idx = 2; }
                        else if (s <= 40) { idx = 3; }
                        else { idx = 4; }

                        return { idx: idx + 1, col: this.colors[idx] };
                    }
                };
                scope.$watch(attr.passwordToCheck, function(password) {
                    if (password) {
                        var c = strength.getColor(strength.mesureStrength(password));
                        iElement.removeClass('ng-hide');
                        iElement.find('ul').children('li')
                            .css({ "background": "#DDD" })
                            .slice(0, c.idx)
                            .css({ "background": c.col });
                    }
                });
            }
        }
    })
    .directive('showValidation', function() {
        return {
            restrict: "A",
            require:'form',
            link: function(scope, element, attrs, formCtrl) {
                element.find('.form-group').each(function() {
                    var $formGroup=$(this);
                    var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');

                    if ($inputs.length > 0) {
                        $inputs.each(function() {
                            var $input=$(this);
                            scope.$watch(function() {
                                return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
                            }, function(isInvalid) {
                                $formGroup.toggleClass('has-error', isInvalid);
                            });
                        });
                    }
                });
            }
        };
    })
    .directive('organizationMembers', function() {
        return {
            restrict: "E",
            templateUrl: "templates/organization-members.html"
        }
    })
    .directive('uploadedImage', function() {
        var baseUrl = '/app/rest/images/file/';
        var placeholder = '/images/profile_empty.png';

        return {
            restrict: "EA",
            scope: {
                name: '=name'
            },
            template: "<div />",
            link: function(scope, element, attributes) {
                scope.$watch('name', function(value) {
                    if (value == null) element.find('div').attr('style', 'background-image: url("' + placeholder + '")');
                    else element.find('div').attr('style', 'background-image: url("' + baseUrl + value + '")');
                });
            }
        }
    })
    .directive('fileUpload', ['FileUploader', function(FileUploader) {
        return {
            restrict: 'E',
            templateUrl: 'templates/file-upload.html',
            scope: {
                complete: "=complete",
                value: "=value"
            },
            controller: function($scope) {
                var uploader = $scope.uploader = new FileUploader({
                    url: '/app/rest/images',
                    autoUpload: true
                });

                uploader.filters.push({
                    name: 'imageFilter',
                    fn: function(item /*{File|FileLikeObject}*/, options) {
                        var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
                        return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
                    }
                });

                uploader.onAfterAddingFile = function(fileItem) {
                    $scope._file = fileItem._file;
                };

                uploader.onProgressItem = function(fileItem, progress) {
                    $scope._progress = progress;
                };

                uploader.onCompleteItem = function(fileItem, response, status, headers) {
                    if (typeof $scope.complete === 'function') {
                        $scope.complete(fileItem, response, status, headers);
                    }
                };

                $scope._progress = 0;
                $scope._type = "warning";
            }
        };
    }])
    .directive('respProject', function() {
        return {
            restrict: 'AE',
            replace: true,
            templateUrl: 'template/project.html',
            scope: {
                project: '=',
                onProjectClick: '&',
                tags: '='
            },
            controller: function($scope) {
                $scope.projectClicked = function(project, $event) {
                    var $target = $($event.target);

                    if ($target.is(".project-item") === false) {
                        $target = $target.closest(".project-item");
                    } else {
                        $target = $target;
                    }

                    $target.parents(":eq(3)").find(".selected").removeClass("selected");
                    $target.addClass("selected");

                    $scope.onProjectClick();
                }
            }
        };
    })
    .directive('respConfirmDelete', function() {
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
    }).directive('respRating', ['Project', 'Organization', '$parse', function(Project, Organization) {
        return {
            restrict: 'AE',
            templateUrl: 'template/rating.html',
            scope: {
                canRateExpression: '&canRate',
                onRate: '&',
                organization: '=?',
                project: '=?',
                ratingValue: '=?'
            },
            controller: function($scope) {
                $scope.canRate = $scope.canRateExpression();
                $scope.currentRating = 0;
                if(!$scope.ratingValue) {
                    if($scope.organization) {
                        Organization.getAggregatedRating({id: $scope.organization}, function(rating) {
                           $scope.currentRating = rating.rating;
                        });
                    } else if($scope.project) {
                        Project.getAggregatedRating({pid: $scope.project}, function(rating) {
                            $scope.currentRating = rating.rating;
                        });
                    }
                } else {
                    $scope.currentRating = $scope.ratingValue;
                }

                $scope.doRate = function() {
                    if($scope.canRate && $scope.onRate) {
                        $scope.onRate($scope.currentRating);
                    }
                }
            }
        }
    }]).directive('backButton', function() {
        return {
            restrict: 'AE',
            replace: true,
            templateUrl: 'template/backbutton.html',
            controller: function($scope) {
                $scope.back = function() {
                    window.history.back();
                }
            }
        };
    }).directive('respBind', function($compile, $parse) {
        return {
            restrict: 'E',
            replace: true,
            link: function(scope, element, attr) {
                scope.$watch(attr.content, function() {
                    element.html($parse(attr.content)(scope));
                    $compile(element.contents())(scope);
                });
            }
        }
    }).directive('respOrganization', function() {
        return {
            restrict: 'AE',
            replace: true,
            templateUrl: 'template/organization.html',
            scope: {
                className: '=class',
                value: '@'
            },
            controller: function($scope, $location, Organization) {
                $scope.redirectToOrganization = function(id) {
                    $location.path("/organization/" + id);
                };

                $scope.$watch('value', function(value) {
                    if (value != '') {
                        Organization.get({
                            id: value
                        }, function(org) {
                            $scope.organization = org;
                        })
                    }
                });
            }
        };
    }).directive('respResourceItem', function() {
        return {
            restrict: 'AE',
            templateUrl: 'template/resource-item.html',
            scope: {
                resource: '='
            },
            controller: function($scope, $location, Organization) {
                $scope.redirectToOrganization = function(id) {
                    $location.path("/organization/" + id);
                };

                $scope.toggled = false;
                $scope.toggle = function() {
                    $scope.toggled = !$scope.toggled;
                };
            }
        };
    }).directive('respPosting', function() {
        return {
            restrict: 'AE',
            templateUrl: 'template/posting.html',
            scope: {
                posting: '=value',
                hideOrganization: '='
            },
            controller: function($scope, $location, Organization) {
                $scope.redirectToOrganization = function(id) {
                    $location.path("/organization/" + id);
                };
            }
        };
    });
