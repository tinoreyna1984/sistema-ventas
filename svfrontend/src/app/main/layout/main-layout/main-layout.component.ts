import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/services/auth.service';
import { TokenValuesService } from 'src/app/auth/services/token-values.service';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {

  links: any;
  userName: string | null = '';

  constructor(
    private tokenValuesService: TokenValuesService,
    private authService: AuthService
    ){}

  ngOnInit(): void {
    this.links = this.tokenValuesService.getRoutes();
    //console.log(this.links);
    this.userName = this.tokenValuesService.getUsername();
  }

  onLogout(){
    this.authService.logout();
  }
}
