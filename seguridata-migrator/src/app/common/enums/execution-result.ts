export enum ExecutionResult {
  SUCCESS, INTERRUPTED, EXCEPTION
}

export function parseExecutionResultFromValue(result: number | string): ExecutionResult | undefined {
  let execResult: ExecutionResult | undefined = undefined;
  if (typeof result === 'string') {
    execResult = ExecutionResult[result as keyof typeof ExecutionResult];
  } else if (typeof result === 'number') {
    execResult = ExecutionResult[ExecutionResult[result] as keyof typeof ExecutionResult];
  }
  return execResult;
}
