import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:sfmc_flutter/sfmc_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    await SFMCSDK.setupSFMC(
        appId: "bcf74e1e-283f-4246-8bbf-a21226009ba5",
        accessToken: "eSEriqnxXw2qwOjQghJBWmA7",
        mid: "510004233",
        sfmcURL:
            "https://mcvnbt6lw8r41dcm37h6gm0dtj-m.device.marketingcloudapis.com/",
        delayRegistration: true);
    await SFMCSDK.setContactKey("alex@dribba.com");
    await SFMCSDK.enablePush();

    await SFMCSDK.setDeviceToken(
        "965b251c6cb1926de3cb366fdfb16ddde6b9086a8a3cac9e5f857679376eab7C");

    await SFMCSDK.pushEnabled();
    await SFMCSDK.setAttribute("name", "Mark");

    await SFMCSDK.setTag("Barcelona");
    await SFMCSDK.removeTag("Barcelona");

    await SFMCSDK.enableLocationWatching();
    await SFMCSDK.enableVerbose();

    await SFMCSDK.sdkState();

    await SFMCSDK.enableVerbose();
    Timer.periodic(Duration(seconds: 2), (timer) async {
      _platformVersion = (await SFMCSDK.sdkState())!;
      setState(() {});

    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('SFMCSDK Plugin'),
          ),
          body: SingleChildScrollView(
            child: Center(
              child: Text(_platformVersion),
            ),
          )),
    );
  }
}
