(function ($) {
    "use strict";

    // Spinner
    var spinner = function () {
        setTimeout(function () {
            if ($('#spinner').length > 0) {
                $('#spinner').removeClass('show');
            }
        }, 1);
    };
    spinner(0);


    // Fixed Navbar
    $(window).scroll(function () {
        if ($(window).width() < 992) {
            if ($(this).scrollTop() > 55) {
                $('.fixed-top').addClass('shadow');
            } else {
                $('.fixed-top').removeClass('shadow');
            }
        } else {
            if ($(this).scrollTop() > 55) {
                $('.fixed-top').addClass('shadow').css('top', -55);
            } else {
                $('.fixed-top').removeClass('shadow').css('top', 0);
            }
        }
    });


   // Back to top button
   $(window).scroll(function () {
    if ($(this).scrollTop() > 300) {
        $('.back-to-top').fadeIn('slow');
    } else {
        $('.back-to-top').fadeOut('slow');
    }
    });
    $('.back-to-top').click(function () {
        $('html, body').animate({scrollTop: 0}, 1500, 'easeInOutExpo');
        return false;
    });


    // Testimonial carousel
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 2000,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav : true,
        navText : [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0:{
                items:1
            },
            576:{
                items:1
            },
            768:{
                items:1
            },
            992:{
                items:2
            },
            1200:{
                items:2
            }
        }
    });


    // vegetable carousel
    $(".vegetable-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1500,
        center: false,
        dots: true,
        loop: true,
        margin: 25,
        nav : true,
        navText : [
            '<i class="bi bi-arrow-left"></i>',
            '<i class="bi bi-arrow-right"></i>'
        ],
        responsiveClass: true,
        responsive: {
            0:{
                items:1
            },
            576:{
                items:1
            },
            768:{
                items:2
            },
            992:{
                items:3
            },
            1200:{
                items:4
            }
        }
    });


    // Modal Video
    $(document).ready(function () {
        var $videoSrc;
        $('.btn-play').click(function () {
            $videoSrc = $(this).data("src");
        });
        console.log($videoSrc);

        $('#videoModal').on('shown.bs.modal', function (e) {
            $("#video").attr('src', $videoSrc + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0");
        })

        $('#videoModal').on('hide.bs.modal', function (e) {
            $("#video").attr('src', $videoSrc);
        })
    });


      function updateCartTotals() {
          let subtotal = 0;

          $('.table tbody tr').each(function () {
              let totalText = $(this).find('td:nth-child(5)').text();
              let price = parseFloat(totalText.replace(/[^0-9.-]+/g, "")) || 0;
              subtotal += price;
          });


          $('#cart-subtotal').text(subtotal.toFixed(2) + ' ₼');
          $('#cart-total').text(subtotal.toFixed(2) + ' ₼');
      }

// Product Quantity (+ / -) və Live Price Update
$('.quantity button').on('click', function (e) {
    e.preventDefault();

    var button = $(this);

    var $container = button.closest('tr').length ? button.closest('tr') : button.closest('.quantity');


    var $input = $container.find('.item-quantity, #product-quantity, input[type="text"]');

    var productId = button.attr('data-id');
    var price = parseFloat(button.attr('data-price')) || 0;
    var oldValue = parseFloat($input.val()) || 1;
    var newVal = oldValue;

    if (button.hasClass('btn-plus')) {
        newVal = oldValue + 1;
    } else if (button.hasClass('btn-minus')) {
        if (oldValue > 1) {
            newVal = oldValue - 1;
        } else {
            newVal = 1;
        }
    }


    $input.val(newVal);

    if (button.closest('tr').length > 0) {

        var itemTotal = (price * newVal).toFixed(2);
        $container.find('.item-total').text(itemTotal + " AZN");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: '/basket/update-quantity',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                productId: productId,
                quantity: newVal
            }),
            beforeSend: function(xhr) {
                if (header && token) {
                    xhr.setRequestHeader(header, token);
                }
            },
            success: function (response) {
                console.log("Say bazada yeniləndi:", newVal);

                location.reload();
            },
            error: function (xhr) {
                console.error("Sayı yeniləyərkən xəta baş verdi");
            }
        });
    }
});



     $('.btn-delete').on('click', function (e) {
         e.preventDefault();

         var button = $(this);
         var productId = button.attr('data-id');

         var token = $("meta[name='_csrf']").attr("content");
         var header = $("meta[name='_csrf_header']").attr("content");

         $.ajax({
             url: '/basket/delete/' + productId,
             type: 'DELETE',
             beforeSend: function(xhr) {
                 if (header && token) {
                     xhr.setRequestHeader(header, token);
                 }
             },
             success: function (response) {
                 button.closest('tr').fadeOut(300, function () {
                     $(this).remove();

                     location.reload();
                 });
             },
             error: function (xhr) {
                 alert('Silinmə zamanı xəta baş verdi!');
             }
         });
     });

 })(jQuery);