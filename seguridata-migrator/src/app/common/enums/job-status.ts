export enum JobStatus {
  CREATED, STARTING, RUNNING, STOPPING, STOPPED, FINISHED_SUCCESS, FINISHED_WARN, FINISHED_ERROR
}

export function parseJobStatusFromValue(status: number | string): JobStatus | undefined {
  let projStatus: JobStatus | undefined = undefined;
  if (typeof status === 'string') {
    projStatus = JobStatus[status as keyof typeof JobStatus];
  } else if (typeof status === 'number') {
    projStatus = JobStatus[JobStatus[status] as keyof typeof JobStatus];
  }
  return projStatus;
}
