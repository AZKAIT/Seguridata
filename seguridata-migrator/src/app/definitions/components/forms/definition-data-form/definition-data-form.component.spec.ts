import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefinitionDataFormComponent } from './definition-data-form.component';

describe('DefinitionDataFormComponent', () => {
  let component: DefinitionDataFormComponent;
  let fixture: ComponentFixture<DefinitionDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DefinitionDataFormComponent]
    });
    fixture = TestBed.createComponent(DefinitionDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
