import { ExecutionResult } from "../enums/execution-result";
import { ExecutionStatus } from "../enums/execution-status";

export interface ExecutionStatisticsModel {
  planId: string;
  planName: string;
  result: ExecutionResult;
  status: ExecutionStatus;
  progress: number;
}
