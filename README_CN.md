# 涂鸦 智能门锁 Android SDK 示例

 [English](README.md) | [中文版](README-zh.md)

---

本示例演示了如何使用智能门锁 Android SDK 实现音视频功能。

## 运行环境

- Android Studio Arctic Fox | 2020.3.1 Patch 4
  Build #AI-203.7717.56.2031.7935034, built on November 21, 2021

- Gradle 6.5及以上版本

## 运行示例

1. 智能门锁 Android SDK 通过 Gradle 进行集成，需要安装相关资源

2. Clone或者下载本示例源码

3. 运行本示例需要**AppKey**、**SecretKey** 和 **安全图片**，你可以前往 [涂鸦智能 IoT 平台](https://developer.tuya.com/cn/) 注册成为开发者，并通过以下步骤获取：

   1. 登录 [涂鸦智能 IoT 平台](https://iot.tuya.com/)，在左侧导航栏面板分别选择： **App** -> **SDK 开发**
   2. 点击 **创建APP** 进行创建应用.
   3. 填写必要的信息，包括应用名称、应用包名等
   4. 点击创建好的应用，在**获取密钥**面板，可以获取 SDK 的 AppKey，AppSecret，安全图片等信息
   5. 根据iot平台上的帮助信息添加SHA256字符串

4. 打开本示例工程中的 `local.properties`  文件

5. 在 **local.properties** 中将获取到的AppKey、SecretKey填写，如下所示

```
   appKey=你的AppKey
   appSecret=你的AppSecret
```

6. 下载**安全图片**并重命名为`t_s.bmp`，将安全图片拖拽到工程中 `app`模块 `assets` 文件夹下

**注意**: Package Name、 AppKey、AppSecret和安全图片必须跟你在 [涂鸦智能 IoT 平台](https://iot.tuya.com/)创建的应用保持一致，如果不一致则无法正常运行本示例工程。

## 开发文档

关于智能门锁 Android SDK 的更多信息，请参考： [App SDK](https://developer.tuya.com/cn/docs/app-development/smartlock?id=Ka6o1ib18780b)。

## 许可

更多信息，见[MIT License](LICENSE)