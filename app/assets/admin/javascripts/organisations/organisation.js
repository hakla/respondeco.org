export default class Organisation {

    constructor(OrganisationsService, organisation) {
        let newOrganisation = {
            id: null,
            name: 'Neue Organisation anlegen'
        };

        this.organisationsService = OrganisationsService;
        this.organisation = organisation || newOrganisation;
    }

    submit(organisation) {
        this.organisationsService.post(organisation).then((e) => console.log(e));
    }

}
