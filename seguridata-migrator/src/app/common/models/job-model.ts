import { JobStatus } from "../enums/job-status";

export interface JobModel {
  id: string;
  execNumber: number;
  projectName: string;
  createdAt: Date;
  startedAt: Date;
  finishedAt: Date;
  status: JobStatus;
  projectId: string;
}
