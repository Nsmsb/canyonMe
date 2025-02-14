import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDiscount, Discount } from '../discount.model';

import { DiscountService } from './discount.service';

describe('Service Tests', () => {
  describe('Discount Service', () => {
    let service: DiscountService;
    let httpMock: HttpTestingController;
    let elemDefault: IDiscount;
    let expectedResult: IDiscount | IDiscount[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(DiscountService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        rate: 0,
        startDate: currentDate,
        endDate: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            startDate: currentDate.format(DATE_FORMAT),
            endDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Discount', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            startDate: currentDate.format(DATE_FORMAT),
            endDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            endDate: currentDate,
          },
          returnedFromService
        );

        service.create(new Discount()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Discount', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            rate: 1,
            startDate: currentDate.format(DATE_FORMAT),
            endDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            endDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Discount', () => {
        const patchObject = Object.assign({}, new Discount());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            startDate: currentDate,
            endDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Discount', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            rate: 1,
            startDate: currentDate.format(DATE_FORMAT),
            endDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            endDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Discount', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addDiscountToCollectionIfMissing', () => {
        it('should add a Discount to an empty array', () => {
          const discount: IDiscount = { id: 123 };
          expectedResult = service.addDiscountToCollectionIfMissing([], discount);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(discount);
        });

        it('should not add a Discount to an array that contains it', () => {
          const discount: IDiscount = { id: 123 };
          const discountCollection: IDiscount[] = [
            {
              ...discount,
            },
            { id: 456 },
          ];
          expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, discount);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Discount to an array that doesn't contain it", () => {
          const discount: IDiscount = { id: 123 };
          const discountCollection: IDiscount[] = [{ id: 456 }];
          expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, discount);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(discount);
        });

        it('should add only unique Discount to an array', () => {
          const discountArray: IDiscount[] = [{ id: 123 }, { id: 456 }, { id: 41001 }];
          const discountCollection: IDiscount[] = [{ id: 123 }];
          expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, ...discountArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const discount: IDiscount = { id: 123 };
          const discount2: IDiscount = { id: 456 };
          expectedResult = service.addDiscountToCollectionIfMissing([], discount, discount2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(discount);
          expect(expectedResult).toContain(discount2);
        });

        it('should accept null and undefined values', () => {
          const discount: IDiscount = { id: 123 };
          expectedResult = service.addDiscountToCollectionIfMissing([], null, discount, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(discount);
        });

        it('should return initial array if no Discount is added', () => {
          const discountCollection: IDiscount[] = [{ id: 123 }];
          expectedResult = service.addDiscountToCollectionIfMissing(discountCollection, undefined, null);
          expect(expectedResult).toEqual(discountCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
