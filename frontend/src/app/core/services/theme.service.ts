import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * Service to manage light/dark theme across the application.
 * Persists the selection in localStorage so the preference survives page reloads.
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly storageKey = 'apexbank_theme';
  private readonly themeSubject = new BehaviorSubject<'light' | 'dark'>(this.getStoredTheme());
  /** Observable that components can subscribe to for reactive theme updates */
  readonly theme$: Observable<'light' | 'dark'> = this.themeSubject.asObservable();

  /** Toggle between light and dark mode */
  toggleTheme(): void {
    const newTheme = this.isDark() ? 'light' : 'dark';
    this.themeSubject.next(newTheme);
    localStorage.setItem(this.storageKey, newTheme);
  }

  /** Returns true if the current theme is dark */
  isDark(): boolean {
    return this.themeSubject.value === 'dark';
  }

  /** Retrieve persisted theme or fallback to light */
  private getStoredTheme(): 'light' | 'dark' {
    const stored = localStorage.getItem(this.storageKey);
    return stored === 'dark' ? 'dark' : 'light';
  }
}
