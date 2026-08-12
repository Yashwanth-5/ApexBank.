import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-session-expired',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './session-expired.component.html',
  styleUrl: './session-expired.component.css'
})
export class SessionExpiredComponent {
  lastLoginTime: string | null = localStorage.getItem('apexbank_last_login');
}
