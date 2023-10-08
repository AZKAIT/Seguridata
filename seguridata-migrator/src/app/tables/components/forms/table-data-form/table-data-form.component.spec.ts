import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableDataFormComponent } from './table-data-form.component';

describe('TableDataFormComponent', () => {
  let component: TableDataFormComponent;
  let fixture: ComponentFixture<TableDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableDataFormComponent]
    });
    fixture = TestBed.createComponent(TableDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
