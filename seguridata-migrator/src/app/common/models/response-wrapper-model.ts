export interface ResponseWrapperModel<T> {
  data: T;
  code: string;
  messages: string[];
}
