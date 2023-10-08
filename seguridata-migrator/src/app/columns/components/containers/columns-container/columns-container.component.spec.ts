import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColumnsContainerComponent } from './columns-container.component';

describe('ColumnsContainerComponent', () => {
  let component: ColumnsContainerComponent;
  let fixture: ComponentFixture<ColumnsContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ColumnsContainerComponent]
    });
    fixture = TestBed.createComponent(ColumnsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
