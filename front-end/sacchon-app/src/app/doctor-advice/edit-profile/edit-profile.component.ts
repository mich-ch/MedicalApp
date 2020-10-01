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
export class EditProfileComponent implements OnInit {

	constructor(private router: Router, private toastr: ToastrService, private http: HttpClient) { }

	id = sessionStorage.getItem('id');
	firstName = sessionStorage.getItem('firstName');
	lastName = sessionStorage.getItem('lastName');
	email = sessionStorage.getItem('email');
	password = sessionStorage.getItem('password');

	doctorEdit = new FormGroup({
		firstName: new FormControl(null, [Validators.required]),
		lastName: new FormControl(null, [Validators.required]),
		email: new FormControl(null, [Validators.required, Validators.email]),
		password: new FormControl(null, [Validators.required, Validators.minLength(8)]),
		passwordconfirm: new FormControl(null)
	});


	ngOnInit(): void {
		this.initializeForm()
	}

	edit(): void {
		if (this.doctorEdit.valid && (this.doctorEdit.get('password').value === this.doctorEdit.get('passwordconfirm').value)) {
			this.http.put(`http://localhost:9000/v1/doctor/${this.id}/settings`, {
				firstName: this.doctorEdit.get('firstName').value,
				lastName: this.doctorEdit.get('lastName').value,
				email: this.doctorEdit.get('email').value,
				password: this.doctorEdit.get('password').value,
			}).subscribe(response => {
				console.log('response');
				this.toastr.success('You will be redirected to your dashboard soon.', 'Successfully edited info', {
					timeOut: 2000,
					positionClass: 'toast-top-center'
				}).onHidden.toPromise().then(_ => {
					this.router.navigate(['/doctoradvice/profile/']);
				});
			})
		} else {
			this.doctorEdit.markAllAsTouched();
		}
	}

	initializeForm(): void {
		this.doctorEdit = new FormGroup({
			firstName: new FormControl(this.firstName, [Validators.required]),
			lastName: new FormControl(this.lastName, [Validators.required]),
			email: new FormControl(this.email, [Validators.required, Validators.email]),
			password: new FormControl(null, [Validators.required, Validators.minLength(8)]),
			passwordconfirm: new FormControl(null)
		});
	}

	cancel(): void {
		this.router.navigate(['/doctoradvice/profile/'])
	}
}
