var { Platform, NativeModules } = require("react-native");
var RNSendIntentAndroid = NativeModules.SendIntentAndroid || {};

var SendIntentAndroid = {
    print(variables) {
        return RNSendIntentAndroid.print(variables || {});
    },
    printBarcode(barcode) {
        return RNSendIntentAndroid.printBarcode(barcode);
    },
    printBarcodeText(barcode, text) {
        return RNSendIntentAndroid.printBarcodeText(barcode, text);
    },
};

module.exports = SendIntentAndroid;
