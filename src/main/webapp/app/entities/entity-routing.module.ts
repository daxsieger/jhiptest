import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'evento',
        data: { pageTitle: 'jhiptestApp.jhiptestEvento.home.title' },
        loadChildren: () => import('./jhiptest/evento/evento.module').then(m => m.JhiptestEventoModule),
      },
      {
        path: 'assistito',
        data: { pageTitle: 'jhiptestApp.jhiptestAssistito.home.title' },
        loadChildren: () => import('./jhiptest/assistito/assistito.module').then(m => m.JhiptestAssistitoModule),
      },
      {
        path: 'gestore',
        data: { pageTitle: 'jhiptestApp.jhiptestGestore.home.title' },
        loadChildren: () => import('./jhiptest/gestore/gestore.module').then(m => m.JhiptestGestoreModule),
      },
      {
        path: 'tipo-evento',
        data: { pageTitle: 'jhiptestApp.jhiptestTipoEvento.home.title' },
        loadChildren: () => import('./jhiptest/tipo-evento/tipo-evento.module').then(m => m.JhiptestTipoEventoModule),
      },
      {
        path: 'produttore',
        data: { pageTitle: 'jhiptestApp.jhiptestProduttore.home.title' },
        loadChildren: () => import('./jhiptest/produttore/produttore.module').then(m => m.JhiptestProduttoreModule),
      },
      {
        path: 'stato',
        data: { pageTitle: 'jhiptestApp.jhiptestStato.home.title' },
        loadChildren: () => import('./jhiptest/stato/stato.module').then(m => m.JhiptestStatoModule),
      },
      {
        path: 'stadio',
        data: { pageTitle: 'jhiptestApp.jhiptestStadio.home.title' },
        loadChildren: () => import('./jhiptest/stadio/stadio.module').then(m => m.JhiptestStadioModule),
      },
      {
        path: 'processo',
        data: { pageTitle: 'jhiptestApp.jhiptestProcesso.home.title' },
        loadChildren: () => import('./jhiptest/processo/processo.module').then(m => m.JhiptestProcessoModule),
      },
      {
        path: 'transizioni',
        data: { pageTitle: 'jhiptestApp.jhiptestTransizioni.home.title' },
        loadChildren: () => import('./jhiptest/transizioni/transizioni.module').then(m => m.JhiptestTransizioniModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
