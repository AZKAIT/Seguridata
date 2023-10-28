import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorsContainerComponent } from './errors-container.component';

describe('ErrorsContainerComponent', () => {
  let component: ErrorsContainerComponent;
  let fixture: ComponentFixture<ErrorsContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorsContainerComponent]
    });
    fixture = TestBed.createComponent(ErrorsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
