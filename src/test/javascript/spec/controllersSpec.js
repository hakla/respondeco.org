'use strict';

describe('Controllers Tests ', function() {

    beforeEach(module('respondecoApp'));

    describe('MainController', function() {
        var scope;
        var location;

        beforeEach(inject(function($rootScope, $location, $controller) {
            scope = $rootScope;
            location = $location;

            $controller('MainController', {
                $scope: scope,
                $location: $location
            });
        }));

        it('should correctly check for the intro page', function() {
            expect(scope.main()).toBeTruthy();
            location.path('/');
            expect(scope.main()).toBeTruthy();
            location.path('/organization');
            expect(scope.main()).toBeFalsy();
        });

        it('should redirect to the new project screen', function() {
            scope.redirectToNewProject();
            expect(location.path()).toBe('/projects/edit/new');
        });
    });

    describe('LanguageController', function() {
        var $scope;
        var _$translate;
        var _LanguageService;

        beforeEach(inject(function($rootScope, $controller, $translate, LanguageService) {
            $scope = $rootScope;
            _$translate = $translate;
            _LanguageService = LanguageService;

            $controller('LanguageController', {
                $scope: $scope,
                LanguageService: _LanguageService
            });
        }));

        it('should correctly change the language', function() {
            spyOn(_$translate, 'use');
            $scope.changeLanguage('en');
            expect(_$translate.use).toHaveBeenCalledWith('en');
        });
    });

    describe('LoginController', function() {
        var $scope;
        var _AuthenticationSharedService;

        beforeEach(inject(function($rootScope, $controller, AuthenticationSharedService) {
            $scope = $rootScope.$new();
            _AuthenticationSharedService = AuthenticationSharedService;
            $controller('LoginController', {
                $scope: $scope,
                AuthenticationSharedService: AuthenticationSharedService
            });
        }));

        it('should set remember Me', function() {
            expect($scope.rememberMe).toBeTruthy();
        });

        it('should call login of the AuthenticationService', function() {
            spyOn(_AuthenticationSharedService, 'login');
            $scope.username = 'admin';
            $scope.password = 'admin';
            $scope.login();
            expect(_AuthenticationSharedService.login).toHaveBeenCalledWith({
                username: 'admin',
                password: 'admin',
                rememberMe: true
            });
        });
    });

    describe('LogoutController', function() {
        var $scope;
        var _AuthenticationSharedService;

        it('should logout', function() {
            inject(function($rootScope, $controller, AuthenticationSharedService) {
                $scope = $rootScope;
                _AuthenticationSharedService = AuthenticationSharedService;

                spyOn(_AuthenticationSharedService, 'logout');

                $controller('LogoutController', {
                    $scope: $scope,
                    AuthenticationSharedService: AuthenticationSharedService
                });

                expect(_AuthenticationSharedService.logout).toHaveBeenCalled();
            });
        });
    });

    describe('SettingsController', function() {
        // various services
        var $scope;
        var _Account;
        var _AuthenticationSharedService;
        var _OrgJoinRequest;
        var _Organization;


        // various mocks
        var mockOrganization = {
            id: 1
        };

        var mockAccount = {
            organization: mockOrganization,
            firstName: 'mock',
            lastName: 'mockington',
            profilePicture: {
                id: 1
            }
        };

        var mockAccountFirstName = {
            organization: mockOrganization,
            firstName: 'mock',
            profilePicture: {
                id: 1
            }
        };

        var mockAccountLastName = {
            organization: mockOrganization,
            lastName: 'mockington',
            profilePicture: {
                id: 1
            }
        };

        // inject various services and call the controller itself
        beforeEach(inject(function($rootScope, $controller, Account, AuthenticationSharedService, OrgJoinRequest, Organization) {
            $scope = $rootScope;
            _Account = Account;
            _AuthenticationSharedService = AuthenticationSharedService;
            _OrgJoinRequest = OrgJoinRequest;
            _Organization = Organization;;

            // spy on the Account.get function, this gets called right away when the controller is initialized
            spyOn(_Account, 'get');

            // spy on the OrgJoinRequest.current function, this gets called a few times
            spyOn(_OrgJoinRequest, 'current');

            // initialize the SettingsController
            $controller('SettingsController', {
                $scope: $scope,
                Account: Account,
                AuthenticationSharedService: AuthenticationSharedService,
                OrgJoinRequest: OrgJoinRequest,
                Organization: Organization
            });
        }));

        it('should set the profile picture after the upload', function() {
            // set the spy on the save function
            spyOn($scope, 'save');

            // call the onComplete callback
            $scope.onComplete(null, {
                id: 1
            });

            // and this should have called the save function
            expect($scope.save).toHaveBeenCalled();
        });

        it('should get the currently logged in user', function() {
            // get the last Account.get call and call its first callback (success)
            _Account.get.calls.mostRecent().args[0](mockAccount);

            // check various variables if they're set
            expect($scope.settingsAccount).toBe(mockAccount);
            expect($scope.fullName).toBe("mock mockington");
            expect($scope.profilePicture).toBe(1);
        });

        it('should set the fullName correctly if only the lastName is supplied', function() {
            // get the last Account.get call and call its success callback
            _Account.get.calls.mostRecent().args[0](mockAccountLastName);

            // check if the fullName was set correctly
            expect($scope.fullName).toBe('mockington');
        });

        it('should set the fullName correctly if only the firstName is supplied', function() {
            // get the last Account.get call and call its success callback
            _Account.get.calls.mostRecent().args[0](mockAccountFirstName);

            // check if the fullName was set correctly
            expect($scope.fullName).toBe('mock');
        });

        it('should delete the user', function() {
            // set the spy on the Account.delete function
            spyOn(_Account, 'delete');
            spyOn(_AuthenticationSharedService, 'logout');

            // call delete
            $scope.delete();

            expect(_Account.delete).toHaveBeenCalled();

            // call the success callback
            _Account.delete.calls.mostRecent().args[0]();

            // and except the logout function to have been called
            expect(_AuthenticationSharedService.logout).toHaveBeenCalled();
            expect($scope.accountdeleted).toBe('OK');
            expect($scope.error).toBeNull();

            // call the error callback
            _Account.delete.calls.mostRecent().args[1]();

            expect($scope.error).toBe('ERROR');
            expect($scope.accountdeleted).toBeNull();
        });

        it('should accept the invitation', function() {
            // set the spy
            spyOn(_OrgJoinRequest, 'accept');

            // accept the invitation
            $scope.acceptInvitation(1);

            // expect the accept function to have been called
            expect(_OrgJoinRequest.accept).toHaveBeenCalledWith({
                id: 1
            }, jasmine.any(Function));
        });

        it('should decline the invitation', function() {
            // set the spy
            spyOn(_OrgJoinRequest, 'decline');

            // decline the invitation
            $scope.declineInvitation(1);

            // expect the decline function to have been called
            expect(_OrgJoinRequest.decline).toHaveBeenCalledWith({
                id: 1
            }, jasmine.any(Function));
        });

        it('should save the user correctly', function() {
            // set the spy
            spyOn(_Account, 'save');

            // set the mock
            $scope.settingsAccount = mockAccount;

            // save the account
            $scope.save();

            // call the success callback
            _Account.save.calls.mostRecent().args[1]();

            expect($scope.error).toBeNull();
            expect($scope.success).toBe('OK');
            expect(_Account.get).toHaveBeenCalled();

            // call the error callback
            _Account.save.calls.mostRecent().args[2]();

            expect($scope.error).toBe('ERROR');
            expect($scope.success).toBeNull();
        });

        it('should load the OrgJoinRequests for the user', function() {
            // the controller loads the current OrgJoinRequests in its initialization
            expect(_OrgJoinRequest.current).toHaveBeenCalled();

            // set the spy
            spyOn(_Organization, 'get');

            // call the success callback
            _OrgJoinRequest.current.calls.mostRecent().args[0]([{
                organization: mockOrganization
            }]);

            // and expect the function to request one organization
            expect(_Organization.get).toHaveBeenCalledWith(mockOrganization, jasmine.any(Function));

            // call the Organization.get success callback
            _Organization.get.calls.mostRecent().args[1](mockOrganization);
        });
    });

    describe('RegisterController', function() {
        var $scope;
        var _$translate;
        var _Register;

        beforeEach(inject(function($rootScope, $controller, $translate, Register) {
            $scope = $rootScope;
            _$translate = $translate;
            _Register = Register;

            $controller('RegisterController', {
                $scope: $scope,
                $translate: $translate,
                Register: Register
            });
        }));

        it('should fail because of password mismatch', function() {
            // set the erroneous account
            $scope.registerAccount = {
                password: 'a'
            };

            $scope.confirmPassword = 'b';

            // register
            $scope.register();

            // should fail
            expect($scope.doNotMatch).toBe('ERROR');
        });

        it('should register correctly', function() {
            // set the correct account
            $scope.registerAccount = {
                password: 'a'
            };

            $scope.confirmPassword = 'a';

            // set a spy
            spyOn(_Register, 'save');

            // register
            $scope.register();

            // should work
            expect($scope.doNotMatch).toBeNull();

            // and call Register.save
            expect(_Register.save).toHaveBeenCalled();

            // call the success callback
            _Register.save.calls.mostRecent().args[1]();

            expect($scope.error).toBeNull();
            expect($scope.errorUserExists).toBeNull();
            expect($scope.success).toBe('OK');
        });

        it('should fail correctly', function() {
            // set the correct account
            $scope.registerAccount = {
                password: 'a'
            };

            $scope.confirmPassword = 'a';

            // set a spy
            spyOn(_Register, 'save');

            // register
            $scope.register();

            // should work
            expect($scope.doNotMatch).toBeNull();

            // and call Register.save
            expect(_Register.save).toHaveBeenCalled();

            // call the success callback
            _Register.save.calls.mostRecent().args[2]({
                status: 304,
                data: {
                    error: "Not Modified"
                }
            });

            expect($scope.success).toBeNull();
            expect($scope.error).toBeNull();
            expect($scope.errorUserExists).toBe('ERROR');

            // call the success callback
            _Register.save.calls.mostRecent().args[2]({
                status: 500
            });

            expect($scope.success).toBeNull();
            expect($scope.error).toBe('ERROR');
            expect($scope.errorUserExists).toBeNull();
        });
    });

    describe('PasswordController', function() {
        var $scope,
            PasswordService;

        beforeEach(inject(function($rootScope, $controller, Password) {
            $scope = $rootScope.$new();
            PasswordService = Password;
            $controller('PasswordController', {
                $scope: $scope,
                Password: PasswordService
            });
        }));

        it('should show error if passwords do not match', function() {
            //GIVEN
            $scope.password = 'password1';
            $scope.confirmPassword = 'password2';
            //WHEN
            $scope.changePassword();
            //THEN
            expect($scope.doNotMatch).toBe('ERROR');

        });
        it('should call Service and set OK on Success', function() {
            //GIVEN
            var pass = 'myPassword';
            $scope.password = pass;
            $scope.confirmPassword = pass;
            //SET SPY
            spyOn(PasswordService, 'save');

            //WHEN
            $scope.changePassword();

            //THEN
            expect(PasswordService.save).toHaveBeenCalled();
            expect(PasswordService.save).toHaveBeenCalledWith(pass, jasmine.any(Function), jasmine.any(Function));
            //SIMULATE SUCCESS CALLBACK CALL FROM SERVICE
            PasswordService.save.calls.mostRecent().args[1]();
            expect($scope.error).toBeNull();
            expect($scope.success).toBe('OK');
        });
    });

    describe('SettingsController', function() {
        var $scope, AccountService, AuthenticationService;

        beforeEach(inject(function($rootScope, $controller, Account, AuthenticationSharedService) {
            $scope = $rootScope.$new();
            AccountService = Account;
            AuthenticationService = AuthenticationSharedService;
            $controller('SettingsController', {
                $scope: $scope,
                Account: AccountService,
                AuthenticationSharedService: AuthenticationService
            });
        }));

        it('should save account', function() {
            //GIVEN
            $scope.settingsAccount = {
                title: "",
                firstName: "John",
                lastName: "Doe",
                gender: "UNDEFINED",
                email: "john@doe.at",
                description: ""
            };

            //SET SPY
            spyOn(AccountService, 'save');

            //WHEN
            $scope.save();

            //THEN
            expect(AccountService.save).toHaveBeenCalled();
            expect(AccountService.save).toHaveBeenCalledWith({
                    title: "",
                    firstName: "John",
                    lastName: "Doe",
                    gender: "UNDEFINED",
                    email: "john@doe.at",
                    description: ""
                },
                jasmine.any(Function),
                jasmine.any(Function));

            //SIMULATE SUCCESS CALLBACK CALL FROM SERVICE
            AccountService.save.calls.mostRecent().args[1]();
            expect($scope.error).toBeNull();
            expect($scope.success).toBe('OK');
        });
    });

    describe('SessionsController', function() {
        var $scope, SessionsService;

        beforeEach(inject(function($rootScope, $controller, Sessions) {
            $scope = $rootScope.$new();

            SessionsService = Sessions;
            $controller('SessionsController', {
                $scope: $scope,
                resolvedSessions: SessionsService,
                Sessions: SessionsService
            });
        }));

        it('should invalidate session', function() {
            //GIVEN
            $scope.series = "123456789";

            //SET SPY
            spyOn(SessionsService, 'delete');

            //WHEN
            $scope.invalidate($scope.series);

            //THEN
            expect(SessionsService.delete).toHaveBeenCalled();
            expect(SessionsService.delete).toHaveBeenCalledWith({
                series: "123456789"
            }, jasmine.any(Function), jasmine.any(Function));

            //SIMULATE SUCCESS CALLBACK CALL FROM SERVICE
            SessionsService.delete.calls.mostRecent().args[1]();
            expect($scope.error).toBeNull();
            expect($scope.success).toBe('OK');
        });
    });
});
