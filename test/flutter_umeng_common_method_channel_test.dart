import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_umeng_common/flutter_umeng_common_method_channel.dart';

void main() {
  MethodChannelFlutterUmengCommon platform = MethodChannelFlutterUmengCommon();
  const MethodChannel channel = MethodChannel('flutter_umeng_common');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
