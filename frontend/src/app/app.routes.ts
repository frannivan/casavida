import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { ProfileComponent } from './profile/profile';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { BoardClientsComponent } from './board-clients/board-clients.component';
import { BoardUser } from './board-user/board-user';

import { LoteDetail } from './lote-detail/lote-detail';

export const routes: Routes = [
    { path: 'home', component: HomeComponent },
    { path: 'lote/:id', component: LoteDetail },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'profile', component: ProfileComponent },
    { path: 'user', component: BoardUser },
    { path: 'admin', component: BoardAdminComponent },
    { path: 'admin/clientes', component: BoardClientsComponent },
    { path: '', redirectTo: 'home', pathMatch: 'full' }
];
