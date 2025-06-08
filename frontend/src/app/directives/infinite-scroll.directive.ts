import { Directive, ElementRef, EventEmitter, Output, OnDestroy } from '@angular/core';

@Directive({
  selector: '[appInfiniteScroll]',
  standalone: true
})
export class InfiniteScrollDirective implements OnDestroy {
  @Output() scrolled = new EventEmitter<void>();
  private observer: IntersectionObserver;

  constructor(private elementRef: ElementRef) {
    this.observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          this.scrolled.emit();
        }
      },
      {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
      }
    );

    this.observer.observe(this.elementRef.nativeElement);
  }

  ngOnDestroy() {
    this.observer.disconnect();
  }
} 