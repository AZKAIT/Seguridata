import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefinitionFormWrapperComponent } from './definition-form-wrapper.component';

describe('DefinitionFormWrapperComponent', () => {
  let component: DefinitionFormWrapperComponent;
  let fixture: ComponentFixture<DefinitionFormWrapperComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DefinitionFormWrapperComponent]
    });
    fixture = TestBed.createComponent(DefinitionFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
