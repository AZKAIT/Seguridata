import { ProjectStatus } from "../enums/project-status";
import { ConnectionModel } from "./connection-model";

export interface ProjectModel {
  id?: string;
  name?: string;
  description?: string;
  sourceConnection?: ConnectionModel;
  targetConnection?: ConnectionModel;
  createdAt?: Date;
  status?: ProjectStatus;
  lastStatusDate?: Date;
  autoPopulate?: boolean;
}
