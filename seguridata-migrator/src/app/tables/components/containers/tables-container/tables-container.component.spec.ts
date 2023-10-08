import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablesContainerComponent } from './tables-container.component';

describe('TablesContainerComponent', () => {
  let component: TablesContainerComponent;
  let fixture: ComponentFixture<TablesContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TablesContainerComponent]
    });
    fixture = TestBed.createComponent(TablesContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
