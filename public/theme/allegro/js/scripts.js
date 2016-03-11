

/* -------------------------------------------------------------------------*
 * 						GET BASE URL		
 * -------------------------------------------------------------------------*/
			
function getBaseURL() {
    var url = location.href;  // entire url including querystring - also: window.location.href;
    var baseURL = url.substring(0, url.indexOf('/', 14));


    if (baseURL.indexOf('http://localhost') != -1) {
        // Base Url for localhost
        var url = location.href;  // window.location.href;
        var pathname = location.pathname;  // window.location.pathname;
        var index1 = url.indexOf(pathname);
        var index2 = url.indexOf("/", index1 + 1);
        var baseLocalUrl = url.substr(0, index2);

        return baseLocalUrl + "/";
    }
    else {
        // Root Url for domain name
        return baseURL;
    }

}				
/* -------------------------------------------------------------------------*
 * 						CONTACT FORM EMAIL VALIDATION	
 * -------------------------------------------------------------------------*/
			
	function Validate() {

		var errors = "";
		var reason_name = "";
		var reason_mail = "";
		var reason_message = "";

		reason_name += validateName(document.getElementById('writecomment').u_name);
		reason_mail += validateEmail(document.getElementById('writecomment').email);
		reason_message += validateMessage(document.getElementById('writecomment').message);


		if (reason_name != "") {
			jQuery("#contact-name-error .ot-error-text").text(reason_name);
			jQuery(".comment-form-author input").addClass("error");
			jQuery("#contact-name-error").fadeIn(1000);
			errors = "Error";
		} else {
			jQuery(".comment-form-author input").removeClass("error");
			jQuery("#contact-name-error").css({ 'display': 'none'});
		}


		if (reason_mail != "") {
			jQuery("#contact-mail-error .ot-error-text").text(reason_mail);
			jQuery(".comment-form-email input").addClass("error");
			jQuery("#contact-mail-error").fadeIn(1000);
			errors = "Error";
		} else {
			jQuery(".comment-form-email input").removeClass("error");
			jQuery("#contact-mail-error").css({ 'display': 'none'});
		}
		
		if (reason_message != "") {
			jQuery("#contact-message-error .ot-error-text").text(reason_message);
			jQuery(".comment-form-text textarea").addClass("error");
			jQuery("#contact-message-error").fadeIn(1000);
			errors = "Error";
		} else {
			jQuery(".comment-form-text textarea").removeClass("error");
			jQuery("#contact-message-error").css({ 'display': 'none'});
		}
		
		if (errors != ""){
			return false;
		} else {
			return true;
		}
		
		//document.getElementById("writecomment").submit(); return false;
	}
	
/* -------------------------------------------------------------------------*
 * 								AWEBER WIDGET VALIDATION	
 * -------------------------------------------------------------------------*/
			
	function Validate_aweber(thisForm) {
		var errors = "";
		var reason_name = "";
		var reason_mail = "";

		reason_name += valName(thisForm.find('.u_name').val());
		reason_mail += valEmail(thisForm.find('.email').val());


		if (reason_name != "") {
			thisForm.parent().parent().find(".aweber-fail").css({ 'display': 'block'});
			errors = "Error";
		} else {
			thisForm.parent().parent().find(".aweber-fail").css({ 'display': 'none'});
		}

		if (reason_mail != "") {
			thisForm.parent().parent().find(".aweber-fail").css({ 'display': 'block'});
			errors = "Error";
		} else {
			thisForm.parent().parent().find(".aweber-fail").css({ 'display': 'none'});
		}
		
		
		if (errors != ""){
			return false;
		} else {
			return true;
		}
		
		//document.getElementById("aweber-form").submit(); return false;
	}
	

	function implode( glue, pieces ) {  
		return ( ( pieces instanceof Array ) ? pieces.join ( glue ) : pieces );  
	} 	
	
/* -------------------------------------------------------------------------*
 * 						SEARCH IN NAVIGATION	
 * -------------------------------------------------------------------------*/
 
	jQuery(document).ready(function() {
		jQuery(".navigation-search").append("<ul id=\"navigation-search\" style=\"display: none;\"><li><form  method=\"get\" action=\"\" name=\"searchform\" ><input type=\"text\" class=\"search\" placeholder=\"Search here \"  name=\"s\" id=\"s\"/><input type=\"submit\" class=\"submit\" /></form></li></ul>");
		jQuery(".navigation-search > a > i").wrap("<span></span>");
		jQuery(".navigation-search").mouseover(function() {
			jQuery("#navigation-search").show();
		});
		jQuery(".navigation-search").mouseout(function() {
			jQuery("#navigation-search").hide();
		});
	});
	
/* -------------------------------------------------------------------------*
 * 						SUBMIT CONTACT FORM	
 * -------------------------------------------------------------------------*/
	var ot = {adminUrl:""}; // Added to fix javascript error
 	jQuery(document).ready(function(jQuery){
		var adminUrl = ot.adminUrl;
		jQuery('#contact-submit').click(function() {
			if (Validate()==true) {
			var str = jQuery("#writecomment").serialize();
				jQuery.ajax({
					url:adminUrl,
					type:"POST",
					data:"action=footer_contact_form&"+str,
					success:function(results) {	
						jQuery(".contact-success-block").css({ 'display': 'block'});
						jQuery("#writecomment").css({ 'display': 'none'});
					
					}
				});
			}
		});
	});	
/* -------------------------------------------------------------------------*
 * 						SUBMIT AWEBER WIDGET FORM	
 * -------------------------------------------------------------------------*/
 	jQuery(document).ready(function(jQuery){
		var adminUrl = ot.adminUrl;
		jQuery('.aweber-submit').click(function() {
			var thisForm = jQuery(this).closest(".aweber-form")
			if (Validate_aweber(thisForm)==true) {
			var str = thisForm.serialize();
			thisForm.parent().parent().find(".aweber-loading").css({ 'display': 'block'});
				jQuery.ajax({
					url:adminUrl,
					type:"POST",
					data:"action=aweber_form&"+str,
					success:function(results) {	
						if(results){
							thisForm.parent().parent().find(".aweber-loading").css({ 'display': 'none'});
							thisForm.parent().parent().find(".aweber-fail").css({ 'display': 'block'});
							thisForm.parent().parent().find(".aweber-fail p").html(results);
						} else {
							thisForm.parent().parent().find(".aweber-form").css({ 'display': 'none'});
							thisForm.parent().parent().find(".aweber-success").css({ 'display': 'block'});
							thisForm.parent().parent().find(".aweber-loading").css({ 'display': 'none'});
						}
					}
				});
				return false;
			}
			return false;
		});
	});	


/* -------------------------------------------------------------------------*
 * 						ADD CLASS TO COMMENT BUTTON					
 * -------------------------------------------------------------------------*/
jQuery(document).ready(function(){
	jQuery('#writecomment .form-submit input').addClass('styled-button');
	jQuery('.comment-reply-link').addClass('icon-link');
	
});

	
function removeHash () { 
    var scrollV, scrollH, loc = window.location;
    if ("pushState" in history)
        history.pushState("", document.title, loc.pathname + loc.search);
    else {
        // Prevent scrolling by storing the page's current scroll offset
        scrollV = document.body.scrollTop;
        scrollH = document.body.scrollLeft;

        loc.hash = "";

        // Restore the scroll offset, should be flicker free
        document.body.scrollTop = scrollV;
        document.body.scrollLeft = scrollH;
    }
}

/* -------------------------------------------------------------------------*
 * 								LIGHTBOX SLIDER
 * -------------------------------------------------------------------------*/
	function OT_lightbox_slider(el,side) {

		if(el.attr('rel')%8==0 && side == "right") {
			//carousel('right');
		}	
		
		if(el.attr('rel')%7==0 && side == "left") {
			//carousel('left');
		}
	
	}
 
/* -------------------------------------------------------------------------*
 * 								SOCIAL POPOUP WINDOW
 * -------------------------------------------------------------------------*/
	jQuery('.ot-share, .ot-tweet, .ot-pin, .ot-pluss, .ot-link').click(function(event) {
		var width  = 575,
			height = 400,
			left   = (jQuery(window).width()  - width)  / 2,
			top    = (jQuery(window).height() - height) / 2,
			url    = this.href,
			opts   = 'status=1' +
					 ',width='  + width  +
					 ',height=' + height +
					 ',top='    + top    +
					 ',left='   + left;

		window.open(url, 'twitter', opts);

		return false;
	});

	
/* -------------------------------------------------------------------------*
 * 								addLoadEvent
 * -------------------------------------------------------------------------*/
	function addLoadEvent(func) {
		var oldonload = window.onload;
		if (typeof window.onload != 'function') {
			window.onload = func;
		} else {
			window.onload = function() {
				if (oldonload) {
					oldonload();
				}
			func();
			}
		}
	}
	
	
