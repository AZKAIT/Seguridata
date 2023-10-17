import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { ResponseWrapperModel } from '../models/response-wrapper-model';

@Injectable()
export class GlobalHttpInterceptorService implements HttpInterceptor {

  constructor(private _messageService: MessageService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      tap(response => {
      }),
      catchError(error => {
        let errorResponse: ResponseWrapperModel<any> | undefined = undefined;
        if (error?.error) {
          errorResponse = error.error;
        }
        return throwError(() => errorResponse);
      })
    );
  }
}
