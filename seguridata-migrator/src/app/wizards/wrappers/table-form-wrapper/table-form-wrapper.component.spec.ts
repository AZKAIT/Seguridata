import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableFormWrapperComponent } from './table-form-wrapper.component';

describe('TableFormWrapperComponent', () => {
  let component: TableFormWrapperComponent;
  let fixture: ComponentFixture<TableFormWrapperComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableFormWrapperComponent]
    });
    fixture = TestBed.createComponent(TableFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
