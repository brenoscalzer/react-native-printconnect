declare namespace SendIntentAndroid {
  const print: (variables: { [index: string]: string }) => Promise<boolean>
  const printBarcode: (barcode: string) => Promise<boolean>
  const printBarcodeText: (barcode: string, text: string) => Promise<boolean>
}

export = SendIntentAndroid
