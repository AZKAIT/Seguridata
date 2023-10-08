import { Injectable, Inject } from '@angular/core';
import { APP_BASE_HREF, DOCUMENT } from '@angular/common';
import { HttpClient, HttpResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResponseWrapperModel } from '../models/response-wrapper-model';

@Injectable({
  providedIn: 'root'
})
export class BackendFacadeService {

  private url: string;

  constructor(private http: HttpClient, @Inject(APP_BASE_HREF) private baseHref: string, @Inject(DOCUMENT) private document: any) {
    const loc: string = this.document.location.toString();
    const uiIndex = loc.indexOf( "/ui" );

    if (uiIndex == -1) {
      this.url = 'http://localhost:8080/migration'
    } else {
      this.url = loc.substring(0, uiIndex);
    }
  }

  serviceGet<T>(endpoint: string, httpHeaders?: HttpHeaders, query?: HttpParams): Observable<HttpResponse<ResponseWrapperModel<T>>> {
    return this
            .http
            .get<ResponseWrapperModel<T>>(this.url + endpoint, { headers: httpHeaders, observe: 'response', params: query });
  }

  serviceDelete<T>(endpoint: string, httpHeaders?: HttpHeaders, query?: HttpParams): Observable<HttpResponse<ResponseWrapperModel<T>>> {
    return this
            .http
            .delete<ResponseWrapperModel<T>>(this.url + endpoint, { headers: httpHeaders, observe: 'response', params: query });
  }

  servicePut<T>(endpoint: string, requestBody?: any, httpHeaders?: HttpHeaders, query?: HttpParams): Observable<HttpResponse<ResponseWrapperModel<T>>> {
    return this
            .http
            .put<ResponseWrapperModel<T>>(this.url + endpoint, requestBody, { headers: httpHeaders, observe: 'response', params: query });
  }

  servicePatch<T>(endpoint: string, requestBody?: any, httpHeaders?: HttpHeaders, query?: HttpParams): Observable<HttpResponse<ResponseWrapperModel<T>>> {
    return this
            .http
            .patch<ResponseWrapperModel<T>>(this.url + endpoint, requestBody, { headers: httpHeaders, observe: 'response', params: query });
  }

  servicePost<T>(endpoint: string, requestBody?: any, httpHeaders?: HttpHeaders, query?: HttpParams): Observable<HttpResponse<ResponseWrapperModel<T>>> {
    return this
            .http
            .post<ResponseWrapperModel<T>>(this.url + endpoint, requestBody, { headers: httpHeaders, observe: 'response', params: query });
  }

  downloadFile(endpoint: string, httpHeaders?: HttpHeaders): Observable<HttpResponse<Blob>> {
    return this.http.get(this.url + endpoint, { headers: httpHeaders, responseType: 'blob', observe: 'response'});
  }
}
