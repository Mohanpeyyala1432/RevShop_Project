import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BuyerService } from '../buyer.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.html',
  styleUrls: ['./checkout.css']
})
export class Checkout {

  productId!: number;
  quantity!: number;

  shippingAddress = '';
  billingAddress = '';

  orderSuccess =false;
  successMessage='';

  type: 'COD' | 'DEBIT_CARD' | 'CREDIT_CARD' = 'COD';

  cardNumber = '';
  cardHolderName = '';
  cardExpiry = '';

  constructor(
    private router: Router,
    private buyerService: BuyerService,
    private cdr: ChangeDetectorRef
  ) {
    const state = history.state;

    if (state && state.productId) {
      this.productId = state.productId;
      this.quantity = state.quantity;
    } else {
     
      this.router.navigate(['/buyer']);
    }
  }

 placeOrder() {
  if (!this.shippingAddress || !this.billingAddress) {
    alert("Please enter shipping & billing address");
    return;
  }

  // Pre-validate card before calling the API to avoid ghost orders
  if (this.type !== 'COD') {
    if (!this.cardNumber || !this.cardHolderName || !this.cardExpiry) {
      alert("Please fill card details");
      return;
    }
  }

  this.buyerService.buyNow(
    this.productId,
    this.quantity,
    this.shippingAddress,
    this.billingAddress
  ).subscribe({
    next: (orderRes: any) => {
      const orderId = orderRes.orderId;

      const handleSuccess = () => {
        this.orderSuccess = true;
        this.successMessage = "🎉 Order placed successfully!";
        this.resetForm();
        
        this.cdr.detectChanges(); 

        setTimeout(() => {
    this.router.navigate(['/buyer/cart']);
  }, 2000);
      };

      if (this.type === 'COD') {
        this.buyerService.payCOD(orderId).subscribe({
          next: handleSuccess,
          error: (err) => console.error("COD Payment Error:", err)
        });
      } else {
        this.buyerService.payCard(orderId, {
          type: this.type,
          cardNumber: this.cardNumber,
          cardHolderName: this.cardHolderName,
          cardExpiry: this.cardExpiry
        }).subscribe({
          next: handleSuccess,
          error: (err) => {
            console.error("Card Payment Error:", err);
            alert("Payment failed. Please try again from the Orders tab.");
          }
        });
      }
    },
    error: (err) => console.error("Order Creation Failed:", err)
  });
}

resetForm() {
  this.shippingAddress = '';
  this.billingAddress = '';
  this.cardNumber = '';
  this.cardHolderName = '';
  this.cardExpiry = '';
  this.type = 'COD';
}
}