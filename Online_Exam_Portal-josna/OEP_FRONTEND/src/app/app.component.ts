import { Component, NgModule } from "@angular/core";
import { RouterLinkActive, RouterModule } from "@angular/router";
import { FooterComponent } from "./components/footer/footer.component";

@Component({
  selector: 'app-root', // Matches the <app-root> in index.html
  standalone: true,
  imports: [RouterModule,FooterComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})


export class AppComponent {
  title = 'OEP-app';
}
