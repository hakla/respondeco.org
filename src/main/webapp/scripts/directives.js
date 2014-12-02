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
            template: "<img />",
            link: function(scope, element, attributes) {
                scope.$watch('name', function(value) {
                    if (value == null) element.find('img').attr('src', placeholder);
                    else element.find('img').attr('src', baseUrl + value);
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
    .directive('ngThumb', ['$window', function($window) {
        var helper = {
            support: !!($window.FileReader && $window.CanvasRenderingContext2D),
            isFile: function(item) {
                return angular.isObject(item) && item instanceof $window.File;
            },
            isImage: function(file) {
                var type =  '|' + file.type.slice(file.type.lastIndexOf('/') + 1) + '|';
                return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
            }
        };

        return {
            restrict: 'A',
            template: '<canvas/>',
            scope: {
                _file: "=file"
            },
            link: function(scope, element, attributes) {
                if (!helper.support) return;

                var params = scope.$eval(attributes.ngThumb);

                scope.$watch('_file', function(value) {
                    if (!helper.isFile(value)) return;
                    if (!helper.isImage(value)) return;

                    var canvas = element.find('canvas');
                    var reader = new FileReader();

                    reader.onload = onLoadFile;
                    reader.readAsDataURL(value);

                    function onLoadFile(event) {
                        var img = new Image();
                        img.onload = onLoadImage;
                        img.src = event.target.result;
                    }

                    function onLoadImage() {
                        var width = params.width || this.width / this.height * params.height;
                        var height = params.height || this.height / this.width * params.width;
                        canvas.attr({ width: width, height: height });
                        canvas[0].getContext('2d').drawImage(this, 0, 0, width, height);
                    }
                });
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
                onProjectClick: '&'
            },
            controller: function($scope) {
                $scope.projectClicked = function() {
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
    });
