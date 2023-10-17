import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefinitionBatchFormComponent } from './definition-batch-form.component';

describe('DefinitionBatchFormComponent', () => {
  let component: DefinitionBatchFormComponent;
  let fixture: ComponentFixture<DefinitionBatchFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DefinitionBatchFormComponent]
    });
    fixture = TestBed.createComponent(DefinitionBatchFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
