import { TableModel } from "./table-model";

export interface PlanModel {
  id: string;
  orderNum: number;
  sourceTable: TableModel;
  targetTable: TableModel;
  initialSkip: number;
  rowLimit: number;
  maxRows: number;

  progressPercent: number;
  status: string;
  startDate: Date;
  endDate: Date;
}
