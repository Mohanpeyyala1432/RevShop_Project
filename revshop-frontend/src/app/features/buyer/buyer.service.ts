import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  productId: number;
  productName: string;
  description: string;
  price: number;
  mrp: number;
  discount: number;
  imageName: string;
  quantity:number;
  category: {
    categoryName: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class BuyerService {

  private baseUrl = 'http://localhost:8081/api/buyer/products';

  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.baseUrl);
  }

  searchProducts(keyword: string): Observable<Product[]> {
    return this.http.get<Product[]>(
      `${this.baseUrl}/search?keyword=${keyword}`
    );
  }


  getProductsByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(
      `${this.baseUrl}/category/${category}`
    );
  }

  getProductByName(productName: string): Observable<Product> {
    return this.http.get<Product>(
      `${this.baseUrl}/name/${productName}`
    );
  }

  getAllCategories() {
  return this.http.get<any[]>(
    'http://localhost:8081/api/buyer/categories'
  );
}

addToCart(productId: number, quantity: number) {
  return this.http.post(
    'http://localhost:8081/api/buyer/cart/add',
    {
      productId,
      quantity
    }
  );
}

getCart() {
  return this.http.get<any>(
    'http://localhost:8081/api/buyer/cart/view'
  );
}


updateCart(cartItemId: number, quantity: number) {
  return this.http.put(
    'http://localhost:8081/api/buyer/cart/update',
    {
      cartItemId,
      quantity
    }
  );
}

removeCartItem(cartItemId: number) {
  return this.http.delete(
    `http://localhost:8081/api/buyer/cart/item/${cartItemId}`
  );
}


buyNow(productId: number, quantity: number, shipping: string, billing: string) {
  return this.http.post<any>(
    'http://localhost:8081/api/buyer/order/buy-now',
    {
      productId,
      quantity,
      shippingAddress: shipping,
      billingAddress: billing
    }
  );
}

payCOD(orderId: number) {
  return this.http.post(
    'http://localhost:8081/api/buyer/payment/pay',
    {
      orderId,
      type: 'COD'
    },
    { responseType: 'text' } 
  );
}

payCard(orderId: number, cardDetails: any) {
  return this.http.post(
    'http://localhost:8081/api/buyer/payment/pay',
    {
      orderId,
      ...cardDetails
    },
    { responseType: 'text' } 

  );
}

getOrderHistory() {
  return this.http.get<any[]>(
    'http://localhost:8081/api/buyer/order/history'
  );
}

addToWishlist(productId: number) {
  return this.http.post(
    `http://localhost:8081/api/buyer/wishlist/${productId}`,
    {},
    { responseType: 'text' } 
  );
}

getWishlist() {
  return this.http.get<any[]>(
    'http://localhost:8081/api/buyer/wishlist'
  );
}


removeFromWishlist(productId: number) {
  return this.http.delete(
    `http://localhost:8081/api/buyer/wishlist/${productId}`,
    { responseType: 'text' }
  );
}

addReview(productId: number, rating: number, comment: string) {
  return this.http.post(
    `http://localhost:8081/api/buyer/reviews/${productId}`,
    {
      rating,
      comment
    }
  );
}

getReviewsByProduct(productId: number) {
  return this.http.get<any[]>(
    `http://localhost:8081/api/buyer/reviews/${productId}`
  );
}

getNotifications() {
  return this.http.get<any[]>(
    'http://localhost:8081/api/buyer/notifications'
  );
}
}