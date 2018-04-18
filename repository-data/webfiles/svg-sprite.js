'use strict';

var SVGSpriter = require('svg-sprite'),
path = require('path'),
mkdirp = require('mkdirp'),
fs = require('fs'),
File = require('vinyl'),
glob = require('glob'),

config = {
    "log": "verbose",
    "shape": {
        "id": {
           "separator": ""
        }
    },
    "mode": {
        "stack": {
            "dest": "../../site/src/main/webapp/assets/images/icons",
            "sprite": "icons.stack.svg"
        }
    }
},
spriter = new SVGSpriter(config);

// Register some SVG files with the spriter
let cwd = path.resolve('../../site/src/main/webapp/assets/images/icons/svg/');
glob.glob('**/*.svg', { cwd: cwd }, function (err, files) {
    files.forEach(function (file, index) {
        spriter.add(new File({
            path: path.join(cwd, file),
            base: cwd,
            contents: fs.readFileSync(path.join(cwd, file))
        }));
    });

    // Compile the sprite
    spriter.compile(function(error, result, cssData) {
        // Run through all configured output modes
        for (var mode in result) {
            // Run through all created resources and write them to disk
            for (var type in result[mode]) {
                mkdirp.sync(path.dirname(result[mode][type].path));
                fs.writeFileSync(result[mode][type].path, result[mode][type].contents);
            }
        }
    });
});
