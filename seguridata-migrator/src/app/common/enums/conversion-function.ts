export enum ConversionFunction {
  NONE = "Ninguna",
  ENCRYPT = "Cifrar"
}


export function parseConversionFunctionFromValue(status: number | string): ConversionFunction | undefined {
  let execStatus: ConversionFunction | undefined = undefined;
  if (typeof status === 'string') {
    execStatus = ConversionFunction[status as keyof typeof ConversionFunction];
  }
  return execStatus;
}
