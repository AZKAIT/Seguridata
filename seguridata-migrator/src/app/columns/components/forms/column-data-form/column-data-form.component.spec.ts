import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColumnDataFormComponent } from './column-data-form.component';

describe('ColumnDataFormComponent', () => {
  let component: ColumnDataFormComponent;
  let fixture: ComponentFixture<ColumnDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ColumnDataFormComponent]
    });
    fixture = TestBed.createComponent(ColumnDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
