import { JobStatus } from "../enums/job-status";
import { ConnectionModel } from "./connection-model";

export interface ProjectModel {
  id?: string;
  name?: string;
  description?: string;
  sourceConnection?: ConnectionModel;
  targetConnection?: ConnectionModel;
  createdAt?: Date;
  autoPopulate?: boolean;
  parallelThreads?: number;
  locked?: boolean;
}
