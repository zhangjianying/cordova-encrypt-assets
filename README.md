# cordova-encrypt-assets
cordova项目加密android assets 下的文件

1. 会将android/assets/www 目录先打包成zip文件
2. 将zip文件加密成.z文件 (.z文件无法直接被压缩软件解压缩)
3. 记得修改MainActivity类(暂时需要手工修改)

实在不明白我说什么的,可以下载 android-debug.apk 这个程序后解压缩看 assets目录

#使用须知
使用前先安装 npm 中的2个库,建议直接安装在工程目录下,而不是全局安装
```
npm install shelljs zach-zip
```

#安装
```
cordova plugin add https://github.com/zhangjianying/cordova-encrypt-assets.git
```

#修改工程MainActivity
```
package com.talkweb;

import com.zsoftware.encryptassets.CordovaActivity;  //这句很重要

import android.os.Bundle;


public class MainActivity extends CordovaActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //开始解压缩 加密文件
        beginEcryptedAction();
        // 加载
        loadUrl(loadURL());
    }
}

```


#已知问题
zach-zip 使用的7z.exe 7z.dll对32位系统支持不够好. 需要手工替换一下
http://sparanoid.com/lab/7z/download.html
http://www.7-zip.org/a/7z1514-extra.7z
