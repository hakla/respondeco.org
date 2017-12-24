export const Notifications = {
  error ($notify, transformer = (err) => 'Fehler aufgetreten') {
    return (error) => {
      $notify({
        duration: 1000,
        title: transformer(error),
        type: 'error'
      })
    }
  },

  success ($notify) {
    return (result) => {
      $notify({
        duration: 1000,
        title: 'Erfolgreich gespeichert',
        type: 'success'
      })
    }
  }
}
