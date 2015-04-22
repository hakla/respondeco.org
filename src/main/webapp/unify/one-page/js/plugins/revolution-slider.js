var RevolutionSlider = function () {

    return {
        
        //Revolution Slider - Full Screen
        initRSfullScreen: function () {
		    var revapi;
	        jQuery(document).ready(function() {
	           revapi = jQuery('.fullscreenbanner').revolution(
	            {
	                delay:1500000,
	                startwidth:1170,
	                startheight:500,
	                hideThumbs:10,
	                fullWidth:"on",
	                fullScreen:"on",
	                hideCaptionAtLimit: "",
	                navigationStyle:"preview4",
	                fullScreenOffsetContainer: ""
	            });
	        });
        }
        
    };
}();