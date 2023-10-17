import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColumnFormWrapperComponent } from './column-form-wrapper.component';

describe('ColumnFormWrapperComponent', () => {
  let component: ColumnFormWrapperComponent;
  let fixture: ComponentFixture<ColumnFormWrapperComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ColumnFormWrapperComponent]
    });
    fixture = TestBed.createComponent(ColumnFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
