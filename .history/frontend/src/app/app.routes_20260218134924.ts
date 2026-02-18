import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { ProfileComponent } from './profile/profile';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { BoardClientsComponent } from './board-clients/board-clients.component';
import { BoardUser } from './board-user/board-user';
import { AdminUsersComponent } from './admin-users/admin-users.component';

import { LoteDetail } from './lote-detail/lote-detail';
import { FraccionamientoDetailComponent } from './fraccionamiento-detail/fraccionamiento-detail';
import { CrmLeadsComponent } from './admin/crm/leads/crm-leads.component';
import { CrmOpportunitiesComponent } from './admin/crm/opportunities/crm-opportunities.component';

export const routes: Routes = [
    { path: 'home', component: HomeComponent },
    { path: 'lote/:id', component: LoteDetail },
    { path: 'fraccionamiento/:id', component: FraccionamientoDetailComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'profile', component: ProfileComponent },
    { path: 'user', component: BoardUser },
    { path: 'admin', component: BoardAdminComponent },
    { path: 'admin/clientes', component: BoardClientsComponent },
    { path: 'admin/users', component: AdminUsersComponent },
    { path: 'admin/crm/leads', component: CrmLeadsComponent },
    { path: 'admin/crm/opportunities', component: CrmOpportunitiesComponent },
    { path: '', redirectTo: 'home', pathMatch: 'full' }
];
