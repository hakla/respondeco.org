export default {
  common: {
    about_organisation: 'Über das Unternehmen',
    cancel: 'Abbrechen',
    comments: 'Kommentare',
    email: 'E-Mail',
    error: {
      unauthenticated: 'Nicht berechtigt für diese Aktion',
      undefined: 'Unbekannter Fehler ist aufgetreten',
      upload: 'Fehler beim Upload'
    },
    login: 'Login',
    logout: 'Abmelden',
    organisation: 'Organisation | Organisationen',
    pagination: {
      next: 'Weiter',
      previous: 'Zurück'
    },
    password: 'Passwort',
    project: 'Projekt | Projekte',
    projects: 'Projekte',
    ratings: 'Bewertungen',
    register: 'Registrierung',
    save: 'Speichern',
    settings: 'Einstellungen',
    success: 'Erfolg!',
  },

  http: {
    status: {
      200: 'OK',
      401: 'UNAUTHENTICATED',
      403: 'FORBIDDEN'
    }
  },

  login: {
    error: 'Fehler bei der Anmeldung',
    go: 'Anmelden',
    goToRegistration: 'Zur Registrierung',
    notRegistered: 'Noch keinen Account?'
  },

  menu: {
    settings: 'Profil bearbeiten',
    logout: 'Abmelden'
  },

  organisation: {
    settings: {
      contact: 'Kontakt',
      description: 'Beschreibung',
      location: 'Standort',
      name: 'Name der Organisation',
      profile: {
        description: 'Hier kannst du allgemeine Informationen deiner Organisation bearbeiten.',
        title: 'Profil'
      },
      security: {
        description: 'Hier kannst du dein Passwort ändern.',
        password: {
          badRequest: 'Neues Passwort stimmt nicht mit der Bestätigung überein',
          current: 'Derzeitiges Passwort',
          forbidden: 'Derzeitiges Passwort stimmt nicht mit unseren Aufzeichnungen überein',
          new: 'Neues Passwort',
          verify: 'Passwort bestätigen'
        },
        title: 'Sicherheitseinstellungen'
      },
      website: 'Webseite'
    }
  },

  registration: {
    existingAccount: 'Bereits angemeldet?',
    go: 'Registrieren',
    login: 'Zum Login'
  }
}
