import { Product } from 'app/product/product.model';

export interface ICartItem {
  product: Product;
  quantity: number;
}

export class CartItem implements ICartItem {
  product;
  quantity;

  constructor(product: Product, quantity: number) {
    this.product = product;
    this.quantity = quantity;
  }
}

export interface ICart {
  items: CartItem[];
}

export class Cart implements ICart {
  items: CartItem[];

  constructor() {
    this.items = [];
  }

  /**
   * Supprime une produit du panier
   * @param productID
   */
  deleteProduct(productID: number): void {
    this.items = this.items.filter(ci => ci.product.id !== productID);
  }

  /**
   * Applique la quantité à l'item du panier
   * Remise de la quantité à 1 si la quantité est négatif ou nul
   *
   * @param cartItem
   * @param quantity
   */
  setQuantity(cartItem: CartItem, quantity: number, canDelete: boolean): void {
    if (quantity > 0) {
      cartItem.quantity = quantity;
    }else{
      if(canDelete){
        this.deleteProduct(cartItem.product.id);
      }else{
        cartItem.quantity = 1;
      }
    }
  }

  changeQuantity(product: Product, quantity: number, canDelete: boolean): void {
    const cartItem = this.items.find(ci => ci.product.id === product.id);
    if (cartItem !== undefined) {
      const newquantity = cartItem.quantity + quantity;
      this.setQuantity(cartItem, newquantity, canDelete);
    } else {
      if (quantity > 0) {
        this.items.push(new CartItem(product, quantity));
      }
    }
  }

  changeQuantitySet(product: Product, quantity: number): void {
    const cartItem = this.items.find(ci => ci.product.id === product.id);
    if (cartItem !== undefined) {
      this.setQuantity(cartItem, quantity, false);
    } else {
      if (quantity > 0) {
        this.items.push(new CartItem(product, quantity));
      }
    }
  }
}
