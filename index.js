var { Platform, NativeModules } = require("react-native");
var RNSendIntentAndroid = NativeModules.SendIntentAndroid || {};

var SendIntentAndroid = {
    openApp(packageName, extras) {
        return RNSendIntentAndroid.print(variables || {});
    },
};

module.exports = SendIntentAndroid;
