let copy = require('copy');
let fs = require('fs');

fs.mkdirSync("node_modules/gentelella/build/images");
copy("node_modules/gentelella/production/images/{forward,back,loading}*", "node_modules/gentelella/build/images", () => {})
