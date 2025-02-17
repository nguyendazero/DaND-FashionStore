document.addEventListener("DOMContentLoaded", function() {
    // Function to open a specific tab
    function openTab(tabName) {
        var tabcontent = document.querySelectorAll(".tabcontent");
        var tablinks = document.querySelectorAll(".tablink");

        tabcontent.forEach(content => {
            content.style.display = "none";
        });
        tablinks.forEach(link => {
            link.classList.remove("active");
        });

        document.getElementById(tabName).style.display = "block";
        document.querySelector(`.tablink[data-tab="${tabName}"]`).classList.add("active");
    }

    // Initialize first tab as active
    openTab("detail");

    // Add click event listeners to tab links
    document.querySelectorAll(".tablink").forEach(tab => {
        tab.addEventListener("click", function() {
            var tabName = this.getAttribute("data-tab");
            openTab(tabName);
        });
    });

    // Rating stars functionality
    const stars = document.querySelectorAll("#star-rating .fa-star");
    const ratingInput = document.getElementById("rating");

    stars.forEach(star => {
        star.addEventListener("mouseover", function() {
            const value = parseInt(this.getAttribute("data-value"));
            updateStars(value, 'hover');
        });

        star.addEventListener("mouseout", function() {
            const value = parseInt(ratingInput.value);
            updateStars(value, 'selected');
        });

        star.addEventListener("click", function() {
            const value = parseInt(this.getAttribute("data-value"));
            ratingInput.value = value;
            updateStars(value, 'selected');
        });
    });

    function updateStars(value, action) {
        stars.forEach(star => {
            const starValue = parseInt(star.getAttribute("data-value"));
            star.style.color = (starValue <= value) ? '#f7d106' : '#d3d3d3';
        });
    }
});