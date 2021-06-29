var { Platform, NativeModules } = require("react-native");
var RNSendIntentAndroid = NativeModules.SendIntentAndroid || {};

var SendIntentAndroid = {
    print(variables) {
        return RNSendIntentAndroid.print(variables || {});
    },
};

module.exports = SendIntentAndroid;
