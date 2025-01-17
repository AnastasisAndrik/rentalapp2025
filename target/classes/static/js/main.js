document.addEventListener('DOMContentLoaded', function() {
    // Category filtering
    const categoryCards = document.querySelectorAll('.category-card');
    const propertyCards = document.querySelectorAll('.property-card');
    const propertyCountSpan = document.querySelector('.search-stats .text-primary');

    categoryCards.forEach(card => {
        card.addEventListener('click', function() {
            // Remove active class from all cards
            categoryCards.forEach(c => c.classList.remove('active'));
            // Add active class to clicked card
            this.classList.add('active');

            const category = this.getAttribute('data-category');
            let visibleCount = 0;
            
            propertyCards.forEach(propertyCard => {
                const cardCategory = propertyCard.querySelector('.badge').textContent.toLowerCase();
                const cardElement = propertyCard.closest('.col-md-4');
                
                if (category === 'all' || cardCategory === category || 
                    (category === 'apartments' && cardCategory === 'apartment') ||
                    (category === 'houses' && cardCategory === 'house') ||
                    (category === 'studios' && cardCategory === 'studio') ||
                    (category === 'shared' && cardCategory === 'shared')) {
                    cardElement.style.display = 'block';
                    visibleCount++;
                } else {
                    cardElement.style.display = 'none';
                }
            });

            // Update property count in search stats
            if (propertyCountSpan) {
                propertyCountSpan.textContent = visibleCount;
            }
        });

        // Add hover animations
        card.addEventListener('mouseenter', function() {
            const icon = this.querySelector('.category-icon i');
            if (icon) {
                icon.style.transform = 'scale(1.2) rotate(5deg)';
            }
        });

        card.addEventListener('mouseleave', function() {
            const icon = this.querySelector('.category-icon i');
            if (icon) {
                icon.style.transform = 'scale(1) rotate(0deg)';
            }
        });
    });

    // Price range slider
    const priceSlider = document.querySelector('.price-range-slider input[type="range"]');
    const minPriceInput = document.querySelector('input[placeholder="Min €"]');
    const maxPriceInput = document.querySelector('input[placeholder="Max €"]');

    if (priceSlider && minPriceInput && maxPriceInput) {
        priceSlider.addEventListener('input', function() {
            const value = this.value;
            maxPriceInput.value = value;
        });

        minPriceInput.addEventListener('input', function() {
            const min = parseInt(this.value) || 0;
            const max = parseInt(maxPriceInput.value) || priceSlider.max;
            if (min > max) {
                maxPriceInput.value = min;
            }
        });

        maxPriceInput.addEventListener('input', function() {
            const min = parseInt(minPriceInput.value) || 0;
            const max = parseInt(this.value) || priceSlider.max;
            if (max < min) {
                minPriceInput.value = max;
            }
            priceSlider.value = max;
        });
    }

    // Sorting functionality
    const sortDropdownItems = document.querySelectorAll('.dropdown-item');
    const sortButton = document.querySelector('#sortDropdown');
    
    sortDropdownItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();
            const sortText = this.textContent;
            sortButton.textContent = 'Sort by: ' + sortText;
            
            // Add sorting logic here
            const propertiesGrid = document.getElementById('propertiesGrid');
            const properties = Array.from(propertiesGrid.children);
            
            properties.sort((a, b) => {
                const priceA = parseInt(a.querySelector('.text-primary').textContent.replace(/[^0-9]/g, ''));
                const priceB = parseInt(b.querySelector('.text-primary').textContent.replace(/[^0-9]/g, ''));
                
                if (sortText.includes('Low to High')) {
                    return priceA - priceB;
                } else if (sortText.includes('High to Low')) {
                    return priceB - priceA;
                }
                return 0;
            });
            
            properties.forEach(property => propertiesGrid.appendChild(property));
        });
    });

    // Set "All Properties" as active by default
    const allPropertiesCard = document.querySelector('.category-card[data-category="all"]');
    if (allPropertiesCard) {
        allPropertiesCard.classList.add('active');
    }
});
