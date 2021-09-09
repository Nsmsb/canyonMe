jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ClientService } from '../service/client.service';
import { IClient, Client } from '../client.model';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';

import { ClientUpdateComponent } from './client-update.component';

describe('Component Tests', () => {
  describe('Client Management Update Component', () => {
    let comp: ClientUpdateComponent;
    let fixture: ComponentFixture<ClientUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let clientService: ClientService;
    let addressService: AddressService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ClientUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ClientUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ClientUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      clientService = TestBed.inject(ClientService);
      addressService = TestBed.inject(AddressService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call billingAddress query and add missing value', () => {
        const client: IClient = { id: 456 };
        const billingAddress: IAddress = { id: 39675 };
        client.billingAddress = billingAddress;

        const billingAddressCollection: IAddress[] = [{ id: 96757 }];
        jest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: billingAddressCollection })));
        const expectedCollection: IAddress[] = [billingAddress, ...billingAddressCollection];
        jest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ client });
        comp.ngOnInit();

        expect(addressService.query).toHaveBeenCalled();
        expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(billingAddressCollection, billingAddress);
        expect(comp.billingAddressesCollection).toEqual(expectedCollection);
      });

      it('Should call shippingAddress query and add missing value', () => {
        const client: IClient = { id: 456 };
        const shippingAddress: IAddress = { id: 63509 };
        client.shippingAddress = shippingAddress;

        const shippingAddressCollection: IAddress[] = [{ id: 2012 }];
        jest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: shippingAddressCollection })));
        const expectedCollection: IAddress[] = [shippingAddress, ...shippingAddressCollection];
        jest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ client });
        comp.ngOnInit();

        expect(addressService.query).toHaveBeenCalled();
        expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(shippingAddressCollection, shippingAddress);
        expect(comp.shippingAddressesCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const client: IClient = { id: 456 };
        const billingAddress: IAddress = { id: 80385 };
        client.billingAddress = billingAddress;
        const shippingAddress: IAddress = { id: 67664 };
        client.shippingAddress = shippingAddress;

        activatedRoute.data = of({ client });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(client));
        expect(comp.billingAddressesCollection).toContain(billingAddress);
        expect(comp.shippingAddressesCollection).toContain(shippingAddress);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Client>>();
        const client = { id: 123 };
        jest.spyOn(clientService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ client });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: client }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(clientService.update).toHaveBeenCalledWith(client);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Client>>();
        const client = new Client();
        jest.spyOn(clientService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ client });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: client }));
        saveSubject.complete();

        // THEN
        expect(clientService.create).toHaveBeenCalledWith(client);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Client>>();
        const client = { id: 123 };
        jest.spyOn(clientService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ client });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(clientService.update).toHaveBeenCalledWith(client);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackAddressById', () => {
        it('Should return tracked Address primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackAddressById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
