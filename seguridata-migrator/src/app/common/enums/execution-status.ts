export enum ExecutionStatus {
  CREATED, RUNNING, FINISHED
}

export function parseExecutionStatusFromValue(status: number | string): ExecutionStatus | undefined {
  let execStatus: ExecutionStatus | undefined = undefined;
  if (typeof status === 'string') {
    execStatus = ExecutionStatus[status as keyof typeof ExecutionStatus];
  } else if (typeof status === 'number') {
    execStatus = ExecutionStatus[ExecutionStatus[status] as keyof typeof ExecutionStatus];
  }
  return execStatus;
}
