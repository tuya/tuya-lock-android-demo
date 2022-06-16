
# Tuya Smart Lock SDK Sample for Android

[English](README.md) | [中文版](README-zh.md)

---

This sample demonstrates how to build a smart lock app based on Smart Lock SDK for Android to implement audio and video capabilities.

## Prerequisites

- Android Studio Arctic Fox | 2020.3.1 Patch 4
   Build #AI-203.7717.56.2031.7935034, built on November 21, 2021

- Gradle 6.5 or later

## Use the sample

1. Install the resources required by Gradle to integrate with the Smart Lock SDK for Android.

2. Clone or download this sample.

3. This sample requires you to get a pair of keys (**AppKey** and **SecretKey**) and a **security image** from [Tuya Developer Platform](https://developer.tuya.com/en/), and register a developer account on this platform if you do not have one. Then, perform the following steps:

   1. Log in to the [Tuya IoT Development Platform](https://iot.tuya.com/). In the left-side navigation pane, choose **App** > **SDK Development**.
   2. Click **Create App** to create an app.
   3. Fill in the required information, such as the app name and package name.
   4. Click the **Get Key** tab to get the values of **AppKey** and **AppSecret** and the security image of the App SDK.
   5. Set SHA256 hash values as instructed on the Tuya IoT Development Platform.

4. Open the sample project `local.properties`.

5. Fill in the values of **AppKey** and **AppSecret** in the **local.properties** file.

   ```
      appKey=Your AppKey
      appSecret=Your AppSecret
   ```

6. Download the **security image**, rename it as `t_s.bmp`, and then drag it to the `assets` directory in the `app` module.

**Note**: The package name, **AppKey**, **AppSecret**, and security image must be the same as those used for your app on the [Tuya IoT Development Platform](https://iot.tuya.com/). Otherwise, API requests in this sample will fail.

## References

For more information about Tuya Smart Lock SDK, see [Smart Lock SDK](https://developer.tuya.com/en/docs/app-development/smartlock?id=Ka6o1ib18780b).