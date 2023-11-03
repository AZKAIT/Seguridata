import { JobStatus } from "../enums/job-status";
import { ExecutionStatisticsModel } from "./execution-statistics-model";

export interface JobModel {
  id: string;
  execNumber: number;
  projectName: string;
  createdAt: Date;
  startedAt: Date;
  finishedAt: Date;
  status: JobStatus;
  projectId: string;
  planStats: ExecutionStatisticsModel[];
}
