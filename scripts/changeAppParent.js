#!/usr/bin/env node

 
module.exports = function(context) {

  var fs = context.requireCordovaModule('fs'),
    path = context.requireCordovaModule('path');
console.log('*******************************************************************');
  var platformRoot = path.join(context.opts.projectRoot, 'platforms/android');
  console.log('准备修改manifestFile文件,为Application节点添加AndroidName');

  var manifestFile = path.join(platformRoot, 'AndroidManifest.xml');
  console.log(manifestFile);


  if (fs.existsSync(manifestFile)) {

    fs.readFile(manifestFile, 'utf8', function (err,data) {
      if (err) {
        throw new Error('Unable to find AndroidManifest.xml: ' + err);
      }

     
	  
    });
  }else{

  }
console.log('*******************************************************************\n');

};