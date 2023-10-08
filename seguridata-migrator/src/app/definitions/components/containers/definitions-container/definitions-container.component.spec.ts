import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefinitionsContainerComponent } from './definitions-container.component';

describe('DefinitionsContainerComponent', () => {
  let component: DefinitionsContainerComponent;
  let fixture: ComponentFixture<DefinitionsContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DefinitionsContainerComponent]
    });
    fixture = TestBed.createComponent(DefinitionsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
