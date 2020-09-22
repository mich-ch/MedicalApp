import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ChartDataSets, ChartOptions } from 'chart.js';
import { Color, Label } from 'ng2-charts';

@Component({
	selector: 'sacchon-app-inspect-patient-list',
	templateUrl: './inspect-patient-list.component.html',
	styleUrls: ['./inspect-patient-list.component.scss']
})
export class InspectPatientListComponent implements OnInit {

	constructor(private route: ActivatedRoute, private http: HttpClient) { }
	patient: any;

	// Array of different segments in chart
	lineChartData: ChartDataSets[] = [
		{ data: [65, 59, 80, 81, 56, 55, 40], label: 'Calories (gr)' },
		{ data: [28, 48, 40, 19, 86, 27, 90], label: 'Glycose (ml)' }
	];

	//Labels shown on the x-axis
	lineChartLabels: Label[] = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];

	// Define chart options
	lineChartOptions: ChartOptions = {
		responsive: true
	};

	// Define colors of chart segments
	lineChartColors: Color[] = [

		{ // dark grey
			backgroundColor: 'rgba(77,83,96,0.2)',
			borderColor: 'rgba(77,83,96,1)',
		},
		{ // red
			backgroundColor: 'rgba(255,0,0,0.3)',
			borderColor: 'red',
		}
	];

	// Set true to show legends
	lineChartLegend = true;

	// Define type of chart
	lineChartType = 'line';

	lineChartPlugins = [];

	// events
	chartClicked({ event, active }: { event: MouseEvent, active: {}[] }): void {
		console.log(event, active);
	}

	chartHovered({ event, active }: { event: MouseEvent, active: {}[] }): void {
		console.log(event, active);
	}

	getPatientById(): void {
		this.route.params.subscribe(params => {
			this.http.get(`https://jsonplaceholder.typicode.com/users/${params.id}`).subscribe(patient => {
				this.patient = patient;
			}, (err) => {
				console.log('-----> err', err);
			});
		});
	}

	ngOnInit(): void {
		this.getPatientById();
	}
}
