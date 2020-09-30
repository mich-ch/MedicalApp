import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
	selector: 'sacchon-app-edit-profile',
	templateUrl: './edit-profile.component.html',
	styleUrls: ['./edit-profile.component.scss']
})
export class PatientEditProfileComponent implements OnInit {

	constructor(private router: Router, private toastr: ToastrService, private http: HttpClient) { }

	id = sessionStorage.getItem('id');
  firstName = sessionStorage.getItem('firstName');
  lastName = sessionStorage.getItem('lastName');
  email = sessionStorage.getItem('email');
  dob = sessionStorage.getItem('dob');
  password = sessionStorage.getItem('password');

	patientEdit = new FormGroup({
		firstame: new FormControl(this.firstName, [Validators.required]),
		lastName: new FormControl(this.lastName, [Validators.required]),
		email: new FormControl(this.email, [Validators.required, Validators.email]),
		dob: new FormControl(this.dob, [Validators.required]),
		password: new FormControl(null, [Validators.required, Validators.minLength(8)]),
		passwordconfirm: new FormControl(null)
	});


	ngOnInit(): void {
	 }

	edit(): void {
		if (this.patientEdit.valid && (this.patientEdit.get('password').value === this.patientEdit.get('passwordconfirm').value)) {
			this.http.put(`http://localhost:9000/v1/patient/${this.id}/settings`, {
        		firstName: this.patientEdit.get('firstName').value,
				lastName: this.patientEdit.get('lastName').value,
				email: this.patientEdit.get('email').value,
				dob: this.patientEdit.get('dob').value,
				password: this.patientEdit.get('password').value,
			}).subscribe(response => {
				this.toastr.success('You will be redirected to your dashboard soon.', 'Successfully edited info', {
					timeOut: 2000,
					positionClass: 'toast-top-center'
				}).onHidden.toPromise().then(_ => {
					this.router.navigate(['/patient/${this.id}']);
				});
			})
		} else {
			this.patientEdit.markAllAsTouched();
		}
	}
}
