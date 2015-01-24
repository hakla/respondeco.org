/**
 * Created by Clemens Puehringer on 22/01/15.
 */
describe('Organization Verify Controller Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('OrganizationControllerVerify', function () {
        var $scope;
        var $routeParams;
        var OrganizationService
        var organizationPage;

        beforeEach(inject(function (_$rootScope_, _$routeParams_, _$controller_, Organization) {
            $scope = _$rootScope_.$new();
            $routeParams = _$routeParams_;
            OrganizationService = Organization;
            organizationPage = {totalItems: 25, organizations: [{id: 1, name: "org1", email: "org1@org1.at"},
                {id: 2, name: "org2", email: "org2@org2.at"}]};

            _$controller_('OrganizationControllerVerify', {$scope: $scope, $routeParams: $routeParams,
                Organization: OrganizationService});
        }));

        it('should load organizations when the getOrganizations function is called', function() {
            spyOn(OrganizationService, 'query');
            $scope.getOrganizations();

            expect(OrganizationService.query).toHaveBeenCalledWith(
                {pageSize:20, page: 0},
                jasmine.any(Function));
            OrganizationService.query.calls.mostRecent().args[1](organizationPage);

            expect($scope.organizations[1].name).toBe("org1");
            expect($scope.totalItems).toBe(25);
        });

        it('should load organizations when the page was changed', function() {
            spyOn(OrganizationService, 'query');
            $scope.currentPage = 3;
            $scope.onPageChange();

            expect(OrganizationService.query).toHaveBeenCalledWith(
                {pageSize:20, page: 2},
                jasmine.any(Function));
            OrganizationService.query.calls.mostRecent().args[1](organizationPage);
        });

        it('should call the backend when verify is called', function() {
            spyOn(OrganizationService, 'verify');
            var verifiedOrganization = {id: 1, name: "testorg", verified: true}
            $scope.organizations[1] = {id: 1, name: "testorg", verified: false};
            $scope.verify(1, true);

            expect(OrganizationService.verify).toHaveBeenCalledWith(
                {id: 1}, "true",
                jasmine.any(Function));
            OrganizationService.verify.calls.mostRecent().args[2](verifiedOrganization);

            expect($scope.organizations[1].verified).toBeTruthy();
        });

    });
});
