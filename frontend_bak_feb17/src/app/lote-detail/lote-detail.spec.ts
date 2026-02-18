import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoteDetail } from './lote-detail';

describe('LoteDetail', () => {
  let component: LoteDetail;
  let fixture: ComponentFixture<LoteDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoteDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoteDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
