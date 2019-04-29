#!/usr/bin/env node

var path = require('path'),
  fs = require('fs'),
  rootdir = process.argv[2],
  shell = require('shelljs'),
  zachZip = require('zach-zip'),
  os = require('os'),
  assetsJar =
    process.env.PWD + '/platforms/android/libs/encrypt-assets-0.5.jar',
  assetsdir = process.env.PWD + '/platforms/android/assets'

require('shelljs/global')

console.log('zachZip:' + zachZip)
console.log('inputDir:' + assetsdir + '/www')
var zipParam = {
  output: assetsdir + '/assets.zip',
  input: assetsdir + '/www',
  r: true
}

if (os.platform() === 'win32') {
  zachZip.add(zipParam, function(args) {
    console.log('准备加密zip文件')
    shell.exec('java -jar ' + assetsJar + ' ' + assetsdir + '/assets.zip ', {
      silent: false
    })

    console.log('删除android:' + assetsdir + '/assets.zip')
    rm('-Rf', assetsdir + '/assets.zip')
    console.log('删除android assets下的未打包文件')
    rm('-Rf', assetsdir + '/www/')
  })
}
if (os.platform() != 'win32') {
  console.log('当前操作系统非win32')
  var silentState = shell.config.silent // save old silent state
  shell.config.silent = true
  var cmdzip = 'zip -r ' + assetsdir + '/assets.zip ' + 'www'
  // console.log('执行打包命令:'+cmdzip);
  shell.cd(assetsdir + '/')

  if (shell.exec(cmdzip, { silent: false }).code === 0) {
    shell.echo('压缩完成....')
    console.log('准备加密zip文件')
    shell.exec('java -jar ' + assetsJar + ' ' + assetsdir + '/assets.zip ', {
      silent: false
    })
    console.log('删除android:' + assetsdir + '/assets.zip')
    rm('-Rf', assetsdir + '/assets.zip')
    console.log('删除android assets下的未打包文件')
    rm('-Rf', assetsdir + '/www/')
  }
  shell.config.silent = silentState // restore old silent state
}
