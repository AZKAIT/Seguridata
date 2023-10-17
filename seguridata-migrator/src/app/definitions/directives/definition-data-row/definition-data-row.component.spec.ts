import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefinitionDataRowComponent } from './definition-data-row.component';

describe('DefinitionDataRowComponent', () => {
  let component: DefinitionDataRowComponent;
  let fixture: ComponentFixture<DefinitionDataRowComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DefinitionDataRowComponent]
    });
    fixture = TestBed.createComponent(DefinitionDataRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
