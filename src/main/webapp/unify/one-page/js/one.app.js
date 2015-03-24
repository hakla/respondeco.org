/*   
 * Template Name: Unify - Responsive Bootstrap Template
 * Description: Business, Corporate, Portfolio and Blog Theme.
 * Version: 1.6
 * Author: @htmlstream
 * Website: http://htmlstream.com
 */

var App = function() {

    function handleBootstrap() {
        /*Bootstrap Carousel*/
        jQuery('.carousel').carousel({
            interval: 15000,
            pause: 'hover'
        });

        /*Tooltips*/
        jQuery('.tooltips').tooltip();
        jQuery('.tooltips-show').tooltip('show');
        jQuery('.tooltips-hide').tooltip('hide');
        jQuery('.tooltips-toggle').tooltip('toggle');
        jQuery('.tooltips-destroy').tooltip('destroy');

        /*Popovers*/
        jQuery('.popovers').popover();
        jQuery('.popovers-show').popover('show');
        jQuery('.popovers-hide').popover('hide');
        jQuery('.popovers-toggle').popover('toggle');
        jQuery('.popovers-destroy').popover('destroy');
    }

    function handleHeader() {
        //jQuery to collapse the navbar on scroll
        $(window).scroll(function() {
            if ($(".navbar").offset().top > 150) {
                $(".navbar-fixed-top").addClass("top-nav-collapse");
            } else {
                $(".navbar-fixed-top").removeClass("top-nav-collapse");
            }
        });

        //jQuery for page scrolling feature - requires jQuery Easing plugin
        $(function() {
            $('.page-scroll a').bind('click', function(event) {
                var $anchor = $(this);
                $('html, body').stop().animate({
                    scrollTop: $($anchor.attr('href')).offset().top
                }, 1500, 'easeInOutExpo');
                event.preventDefault();
            });
        });

        //Collapse Navbar When It's Clickicked
        $('.navbar-nav li a, .navbar-brand').click(function() {
            $(".navbar-collapse.in").collapse('hide');
        });

        var $window = $(window);
        var $headerCarousel = $('.header-carousel');

        $headerCarousel.owlCarousel({
            slideSpeed: 1000,
            singleItem: true,
            pagination: false,
            autoPlay: 10000
        });

        var onResize = function() {
            $headerCarousel.add($headerCarousel.find('.item')).height($window.innerHeight());
        }

        $window.on("resize", onResize);
        onResize();
    }

    return {
        init: function() {
            handleHeader();
            handleBootstrap();

            jQuery('.explanation').on('click', function() {
                jQuery('.big-explanation').toggleClass('out');
                jQuery('body').toggleClass('overflow-hidden');
            });

            jQuery('.close-explanation').on('click', function() {
                jQuery('.big-explanation').toggleClass('out');
                jQuery('body').toggleClass('overflow-hidden');
            });
        }
    };

}();
