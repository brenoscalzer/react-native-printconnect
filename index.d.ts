declare namespace SendIntentAndroid {
  const print: (templateName: string, variables: { [index: string]: string }) => Promise<boolean>
}

export = SendIntentAndroid
