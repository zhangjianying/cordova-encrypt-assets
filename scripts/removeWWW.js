#!/usr/bin/env node

var 
	path = require( "path" ),
    fs = require( "fs" ),
	rootdir = process.argv[2],
  shell = require('shelljs'),
  zachZip = require( "zach-zip" ),
  assetsJar = process.env.PWD+'/platforms/android/libs/encrypt-assets-0.5.jar',
  assetsdir = process.env.PWD+'/platforms/android/assets';

  require('shelljs/global'); 

console.log('zachZip:'+zachZip);
console.log('inputDir:'+assetsdir+'/www');
var zipParam = {output:assetsdir+'/assets.zip',input:assetsdir+'/www',r:true};
zachZip.add(zipParam,function(args){
	console.log('准备加密zip文件');
	shell.exec( 'java -jar '+assetsJar+' '+ assetsdir+'/assets.zip ', {silent:false} );

	console.log('删除android:'+assetsdir+'/assets.zip');  
 	rm('-Rf',assetsdir+'/assets.zip');
	console.log('删除android assets下的未打包文件');  
 	rm('-Rf',assetsdir+"/www/");
});


