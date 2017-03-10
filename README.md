# android_x264_encoder
使用x264编码android camera回调yuv为h264的简单例子，camera回调nv21yuv转为yuv420 ,再编码

简单流程：
1.camera回调nv21 yuv；
2.nv21转yuv420；
3.x264编码h264，回调回java层；
4.写文件，生成.h264文件；
5.使用vlc等播放器播放。

另外，调用x264库的c++文件在x264encoder文件夹下的ndk-build文件夹下，在这个文件夹下直接用ndk编译即可，目前so只编译了armeabi的，如果需要其他cpu架构的so，请自己编译，x264的.a文件是自己编译好的，使用的是148版本
 master
