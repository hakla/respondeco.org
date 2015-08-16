--liquibase formatted sql
--changeset "Clemens Pühringer":20150623213000

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.organisationsfuehrung");

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.faire_betriebs_und_geschaeftspraktiken");
INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.korruptionsbekaempfung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.faire_betriebs_und_geschaeftspraktiken");

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.verantwortungsbewusste_politische_mitwirkung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.faire_betriebs_und_geschaeftspraktiken");

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.fairer_wettbewerb");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.faire_betriebs_und_geschaeftspraktiken");

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.gesellschaftliche_verantwortung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.faire_betriebs_und_geschaeftspraktiken");

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.eigentumsrechte_achten");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.faire_betriebs_und_geschaeftspraktiken");


INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.menschenrechte");
INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.gebuehrende_sorgfalt");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.menschenrechte_in_kritischen_situationen");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.mittaeterschaft_vermeiden");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.missstaende_beseitigen");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.diskriminierung_und_schutzbeduerftige_gruppen");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.buergerliche_und_politische_rechte");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.wirtschaftliche_soziale_und_kulturelle_rechte");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) ("isocategory.grundlegende_prinzipien_und_rechte");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.menschenrechte") WHERE `ID` = LAST_INSERT_ID();


INSERT INTO `T_ISO_CATEGORY` (`SUPER_CATEGORY_ID`, `KEY`) VALUES("isocategory.konsumentenanliegen")
INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("faire_werbe_vertriebs_und_vertragspraktiken");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("schutz_von_gesundheit_und_sicherheit");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("nachhaltiger_konsum");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("kundendienst_beschwerdemanagement");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("schutz_und_vertraulichkeit_von_kundendaten");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("sicherung_der_grundversorgung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("verbraucherbildung_und_sensibilisierung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.konsumentenanliegen") WHERE `ID` = LAST_INSERT_ID();



INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.arbeitspraktiken")
INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.beschaeftigung_und_beschaeftigungsverhaeltnisse");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.arbeitsbedingungen_und_sozialschutz");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.sozialer_dialog");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.gesundheit_und_sicherheit");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.menschliche_entwicklung_und_schulung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();



INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("isocategory.einbindung_und_entwicklung_der_gemeinschaft.arbeitspraktiken")
INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.einbindung_der_gemeinschaft");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.bildung_und_kultur");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.schaffen_von_arbeitsplaetzen");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.technologien_entwickeln");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.arbeitspraktiken") WHERE `ID` = LAST_INSERT_ID();


INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES("einbindung_und_entwicklung_der_gemeinschaft.umwelt");
INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.vermeidung_der_umweltbelastung");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.umwelt") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.nachhaltige_nutzung_von_ressourcen");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.umwelt") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.abschwaechung_des_klimawandels");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.umwelt") WHERE `ID` = LAST_INSERT_ID();

INSERT INTO `T_ISO_CATEGORY` (`KEY`) VALUES ("isocategory.umweltschutz_artenvielfalt");
UPDATE `T_ISO_CATEGORY` SET `SUPER_CATEGORY_ID` = (SELECT id FROM `T_ISO_CATEGORY` WHERE `KEY` = "isocategory.einbindung_und_entwicklung_der_gemeinschaft.umwelt") WHERE `ID` = LAST_INSERT_ID();
