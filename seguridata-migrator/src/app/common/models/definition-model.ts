import { ConversionFunction } from "../enums/conversion-function";
import { ColumnModel } from "./column-model";

export interface DefinitionModel {
  id: string;
  sourceColumn: ColumnModel;
  targetColumn: ColumnModel;
  conversionFunction: ConversionFunction;
}
