import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { ImagStorageService } from '../../core/storage/imag-storage.service';

@Component({
  selector: 'jhi-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss'],
})
export class UploadComponent {
  public progress$: Observable<number | undefined> | undefined;
  public url$: Observable<string> | undefined;
  public path: string | undefined;
  public show: boolean = false;

  constructor(private imgStorageService: ImagStorageService) {}

  uploadFile(event: any): void {
    const file = event.target.files[0];
    this.path = file.name;
    this.show = true;
    const { progress$, url$ } = this.imgStorageService.uploadFile(file, 'img/');
    this.progress$ = progress$;
    this.url$ = url$;
  }
}
