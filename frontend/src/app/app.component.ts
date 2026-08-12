import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './core/services/theme.service';
import { routeAnimations } from './route-animations';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="glass-bg-canvas" [class.dark-theme]="themeService.isDark()">
      <button class="theme-toggle" (click)="toggleTheme()" aria-label="Toggle dark/light mode">
        🌙/☀️
      </button>
      <router-outlet @routeAnimations></router-outlet>
    </div>
  `,
  animations: [routeAnimations]
})
export class AppComponent {
  constructor(public themeService: ThemeService) {}
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
