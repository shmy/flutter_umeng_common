import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_umeng_common/flutter_umeng_common.dart';
import 'package:flutter_umeng_common/flutter_umeng_common_platform_interface.dart';
import 'package:flutter_umeng_common/flutter_umeng_common_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterUmengCommonPlatform
    with MockPlatformInterfaceMixin
    implements FlutterUmengCommonPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterUmengCommonPlatform initialPlatform = FlutterUmengCommonPlatform.instance;

  test('$MethodChannelFlutterUmengCommon is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterUmengCommon>());
  });

  test('getPlatformVersion', () async {
    FlutterUmengCommon flutterUmengCommonPlugin = FlutterUmengCommon();
    MockFlutterUmengCommonPlatform fakePlatform = MockFlutterUmengCommonPlatform();
    FlutterUmengCommonPlatform.instance = fakePlatform;

    expect(await flutterUmengCommonPlugin.getPlatformVersion(), '42');
  });
}
