
/* -------------------------------------------------------------------------*
 * 								GALLERY	CATEGORY		
 * -------------------------------------------------------------------------*/
	jQuery(function() {

		// gallery cache container
		var jQuerycontainer = jQuery('#gallery-full');
		var galleryCat = ot.galleryCat;
		
		jQuery(window).load(function(){
			jQuerycontainer.show();
			jQuerycontainer.isotope({
				itemSelector : '.gallery-image',
				layoutMode : 'masonry',
				resizable: false,
			});
		
			jQuery(window).smartresize(function(){
				jQuerycontainer.isotope({
					itemSelector : '.gallery-image',
					layoutMode : 'masonry',
					resizable: false,
				});
			});

		});

		if(galleryCat) {
			// initialize isotope
			jQuerycontainer.isotope({ 
				filter: "."+galleryCat 
			});

			var jQueryoptionSet = jQuery('#gallery-categories a');
				jQueryoptionSet.each(function(index) {
					jQuery(this).removeClass('active');
					if(jQuery(this).attr("data-option")=="."+galleryCat) {
						jQuery(this).addClass('active');
					}
				});				
		}


		
		// filter items when filter link is clicked
		jQuery('#gallery-categories a').click(function(){
			var jQuerythis = jQuery(this);
	
			var jQueryoptionSet = jQuerythis.parents('#gallery-categories');
				jQueryoptionSet.find('.active').removeClass('active');
				jQuerythis.addClass('active');
	  
		
		var selector = jQuerythis.attr('data-option');
		jQuerycontainer.isotope({ 
			filter: selector
		});
		  return false;
		});

		 

 /* 					infinitescroll					*/	

 
		jQuerycontainer.infinitescroll({
			navSelector  : '.gallery-navi',    // selector for the paged navigation 
			nextSelector : '.gallery-navi a.next',  // selector for the NEXT link (to page 2)
			itemSelector : '#gallery-full .gallery-image',     // selector for all items you'll retrieve
			animate      : true,
			loading: {
				finishedMsg: 'No more pages to load.',
				img: ot.imageUrl+'loading.gif'
			}
		},
			function(newElements) {
				jQuery(newElements).imagesLoaded(function(){
					
					//portfolio image load
					jQuery( ".gallery-image",newElements ).each(function() {
							jQuery(".gallery-image").fadeIn('slow');
					
					});
			

					jQuerycontainer.append( jQuery(newElements) ).isotope( 'insert', jQuery(newElements) );



					//after gallery loads
					jQuery(document).on("click", "a[href=#gal-next]", newElements, function() {
					  	var thisel = jQuery(this);
					  	var thislist = thisel.parent().children('ul');
					  	var currentel = thisel.parent().children('ul').children('li.active').index();

					  	thisel.parent().children('ul').children('li').removeClass("active").removeClass("next").removeClass("prev");
					  	thisel.parent().children('ul').children('li').eq(currentel).addClass("prev");

					  	currentel = (currentel > thislist.length+1) ? 0 : currentel + 1;
					  	var prevel = (currentel > thislist.length+1) ? 0 : currentel + 1;

					  	thisel.parent().children('ul').children('li').eq(currentel).addClass("active");
					  	thisel.parent().children('ul').children('li').eq(prevel).addClass("next");
					  	return false;
					 });

					jQuery(document).on("click", "a[href=#gal-prev]", newElements, function() {
					  	var thisel = jQuery(this);
					  	var thislist = thisel.parent().children('ul');
					  	var currentel = thisel.parent().children('ul').children('li.active').index();

					 	thisel.parent().children('ul').children('li').removeClass("active").removeClass("next").removeClass("prev");
					  	thisel.parent().children('ul').children('li').eq(currentel).addClass("prev");

					  	currentel = (currentel+1 == 0) ? thislist.length-1 : currentel - 1;
					  	var prevel = (currentel+1 == 0) ? thislist.length+1 : currentel - 1;

					  	thisel.parent().children('ul').children('li').eq(currentel).addClass("active");
					  	thisel.parent().children('ul').children('li').eq(prevel).addClass("next");
					  	return false;
					 });

				});  
				
				
			}
		);
		
	});