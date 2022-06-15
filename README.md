# Tuya Smart Lock App SDK Sample for Android

 [English](README.md) | [中文版](README_CN.md)

---

## Overview

This sample demonstrates how to use the smart door lock Android SDK to implement audio and video functions.

## Prerequisites

- Android Studio Arctic Fox | 2020.3.1 Patch 4
  Build #AI-203.7717.56.2031.7935034, built on November 21, 2021

- Gradle 6.5 or later.

## Use the sample

1. Smart Lock App SDK for Android is integrated based on Gradle. Required resources must be installed.

2. Clone or download this sample.

3. This sample requires you to get a pair of keys and a security image from [Tuya Developer Platform](https://developer.tuya.com/), and register a developer account on this platform if you do not have one. Then, perform the following steps:

   1. Log in to the [Tuya IoT Development Platform](https://iot.tuya.com/). In the left-side navigation pane, choose **App** > **SDK Development**.

   2. Click **Create** to create an app.

   3. Fill in the required information, such as the app name and package name.

   4. You can find the AppKey, AppSecret, and security image on the **Get Key** tab.

   5. Add SHA256 string according to the help information on the iot platform.

4. Open the sample project `local.properties`.

5. Fill in the AppKey and SecretKey in the **local.properties** file.

   ```
      appKey=your AppKey
      appSecret=your AppSecret
   ```

6. Download the security image, rename it `t_s.bmp`, and then drag it to the `assets` directory in the `app` module.

    **Note**: The package name, AppKey, AppSecret, and security image must be the same as those used for your app on the [Tuya IoT Development Platform](https://iot.tuya.com). Otherwise, API requests in this sample will fail.

## References

For more information about Tuya Smart Lock  SDK, see [App SDK](https://developer.tuya.com/en/docs/app-development/smartlock?id=Ka6o1ib18780b).