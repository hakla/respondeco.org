let copy = require('copy');
let fs = require('fs');

fs.mkdir("node_modules/gentelella/build/images", (err) => {
  if (!err) {
    copy("node_modules/gentelella/production/images/{forward,back,loading}*", "node_modules/gentelella/build/images", () => {})
  }
})

