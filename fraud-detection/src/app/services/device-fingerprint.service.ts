import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class DeviceFingerprintService {

  private readonly STORAGE_KEY = 'fg_device_id';

  getDeviceId(): string {
    let deviceId = localStorage.getItem(this.STORAGE_KEY);
    if (!deviceId) {
      deviceId = this.generateFingerprint();
      localStorage.setItem(this.STORAGE_KEY, deviceId);
    }
    return deviceId;
  }

  private generateFingerprint(): string {
    const data = [
      navigator.userAgent,
      navigator.language,
      screen.width + 'x' + screen.height,
      screen.colorDepth,
      new Date().getTimezoneOffset(),
      Math.random().toString(36).substring(2) // dodatna nasumičnost po instalaciji
    ].join('|');

    // Jednostavan hash
    let hash = 0;
    for (let i = 0; i < data.length; i++) {
      hash = ((hash << 5) - hash) + data.charCodeAt(i);
      hash |= 0;
    }
    return 'DEV-' + Math.abs(hash).toString(16).toUpperCase();
  }
}