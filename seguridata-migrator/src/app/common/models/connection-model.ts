import { DatabaseType } from "../enums/database-type";

export interface ConnectionModel {
  id?: string;
  name?: string;
  description?: string;
  host?: string;
  port?: number;
  objectService?: string;
  database?: string;
  username?: string;
  password?: string;
  type?: DatabaseType;
  locked?: boolean;
}
