import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { BuyerService } from '../buyer.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './order-history.html',
  styleUrls: ['./order-history.css']
})
export class OrderHistory implements OnInit {

  orders: any[] = [];
  selectedProductId: number | null = null;
rating: number = 5;
comment: string = '';
reviewSuccess: boolean = false;
reviewedProducts: { [key: number]: boolean } = {};

  constructor(
    private buyerService: BuyerService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
  this.buyerService.getOrderHistory().subscribe({
    next: (res: any[]) => {
     
      this.orders = res;
      
     
      this.cdr.detectChanges();

      this.orders.forEach(order => {
        if (order.items) {
          order.items.forEach((item: any) => {
            this.buyerService
              .getProductByName(item.productName)
              .subscribe({
                next: (product) => {
                  item.imageName = product.imageName;
                  
                 
                  this.cdr.detectChanges(); 
                },
                error: (err) => console.error(`Image fetch failed for ${item.productName}:`, err)
              });
              this.checkIfReviewed(item.productId);
          });
        }
      });
    },
    error: (err) => console.error("Order history error:", err)
  });
}

openReview(productId: number) {
  this.selectedProductId = productId;
  this.reviewSuccess = false;
}

submitReview(productId: number) {

  if (!this.comment.trim()) {
    alert("Please enter a comment");
    return;
  }

  this.buyerService
    .addReview(productId, this.rating, this.comment)
    .subscribe({
      next: () => {

       
        this.reviewedProducts[productId] = true;

        this.reviewSuccess = true;
        this.comment = '';
        this.rating = 5;

        setTimeout(() => {
          this.selectedProductId = null;
          this.reviewSuccess = false;
        }, 2000);

        this.cdr.detectChanges();
      },

      error: (err) => {

      
        if (err.error?.error === "You have already reviewed this product") {

          this.reviewedProducts[productId] = true;
          this.selectedProductId = null;

          alert("Review already submitted");

        } else {
          console.error("Review failed:", err);
        }

      }
    });
}

checkIfReviewed(productId: number) {

  this.buyerService
    .getReviewsByProduct(productId)
    .subscribe({
      next: (reviews: any[]) => {

        const currentUser = localStorage.getItem('username');

        const alreadyReviewed = reviews.some(
          r => r.userName === currentUser
        );

        if (alreadyReviewed) {
          this.reviewedProducts[productId] = true;
        }

        this.cdr.detectChanges();
      },
      error: (err) => console.error("Review check failed:", err)
    });
}
}