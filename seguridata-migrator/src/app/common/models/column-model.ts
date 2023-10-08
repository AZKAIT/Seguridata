import { ColumnDataType } from "../enums/column-data-type";

export interface ColumnModel {
  id: string;
  name: string;
  description: string;
  dataType: ColumnDataType;
  dataLength: number;
}
