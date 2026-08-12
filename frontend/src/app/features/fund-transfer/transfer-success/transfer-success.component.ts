import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { TransferSuccessResponse } from '../../../core/models/transaction.model';

@Component({
  selector: 'app-transfer-success',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './transfer-success.component.html',
  styleUrl: './transfer-success.component.css'
})
export class TransferSuccessComponent implements OnInit {
  result = signal<TransferSuccessResponse | null>(null);

  constructor(private router: Router) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state ?? (history.state as any);
    if (state?.result) this.result.set(state.result);
    else this.router.navigate(['/fund-transfer']);
  }

  printReceipt(): void { window.print(); }
}
