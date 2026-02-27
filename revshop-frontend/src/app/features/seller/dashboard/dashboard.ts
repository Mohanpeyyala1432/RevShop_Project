import { Component, OnInit, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { SellerService } from '../seller.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  totalProducts: number = 0;
  lowStockCount: number = 0;
  totalOrders: number = 0;
  lowStockProducts: any[] = [];
  
  isLoading: boolean = true;

  constructor(
    private sellerService: SellerService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading = true;

    
    this.sellerService.getAllProducts().subscribe({
      next: (data: any) => {
        this.totalProducts = data.length;
        this.checkLoadingStatus();
      },
      error: (err) => console.error('Error loading products', err)
    });

    this.sellerService.getLowStockCount().subscribe({
      next: (data: any) => {
        this.lowStockCount = data;
        this.checkLoadingStatus();
      },
      error: (err) => console.error('Error loading low stock count', err)
    });

    
    this.sellerService.getSellerOrders().subscribe({
      next: (data: any) => {
        this.totalOrders = data.length;
        this.checkLoadingStatus();
      },
      error: (err) => {
        console.error('Error loading orders', err);
        this.checkLoadingStatus();
      }
    });

    this.sellerService.getLowStockProducts().subscribe({
      next: (data) => {
        this.lowStockProducts = data;
        this.checkLoadingStatus();
      },
      error: (err) => console.error(err)
    });
  }

  private checkLoadingStatus() {
    this.isLoading = false; 
    this.cdr.detectChanges(); 
  }
}