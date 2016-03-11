

var supports = (function() {
  var div = document.createElement('div'),
    vendors = 'Khtml Ms O Moz Webkit'.split(' '),
    len = vendors.length;

  return function(prop) {
    if ( prop in div.style ) return true;

    prop = prop.replace(/^[a-z]/, function(val) {
       return val.toUpperCase();
    });

    while(len--) {
       if ( vendors[len] + prop in div.style ) {
          return true;
       } 
    }
    return false;
  };
})();

if ( supports('perspective') ) {
   jQuery(".the-menu").addClass("transition-active");
}else{
   jQuery(".the-menu").removeClass("transition-active");
}


jQuery(window).scroll(function() {
  jQuery(".main-menu.sticky").each(function() {
    var thisel = jQuery(this);
    var offset = thisel.offset();
    var offsetz = thisel.children(".wrapper").offset();
    var addpoint = 0;
    if(jQuery("body").hasClass("admin-bar")){
      addpoint = 28;
    }
    if(jQuery(window).scrollTop()+addpoint >= offset.top){
      thisel.css("height", thisel.height()+"px");
      thisel.children(".wrapper").css("left", offsetz.left+"px").css("position", "fixed").css("top", addpoint+"px");
    }else{
      thisel.css("height", "auto");
      thisel.children(".wrapper").css("position", "inherit").css("top", "0px").css("left", "auto");
    }
  });
});


function playhover() {

  jQuery(".hover-effect").each(function() {
    var thisel = jQuery(this);
    
    if(thisel.has("img").length > 0 && !thisel.hasClass("delegate")){
      var fontsize = (parseInt(thisel.children("img").height()) <= 120)?20:(parseInt(thisel.children("img").height())/6);
      thisel.children("img").wrap("<span class='cover' style='font-size:"+ fontsize +"px;'></span>").parent().prepend("<i></i>");
      thisel.addClass("delegate");
    }

  });

}

var anihappening = false;

jQuery(document).ready(function() {

  playhover();

  jQuery(".gallery-widget ul").each(function() {
    var thisel = jQuery(this);
    var licount = thisel.children("li").length;
    thisel.attr("rel", licount);
  });

  jQuery(".gallery-photo > a.slide-right").click(function() {
    if(anihappening)return false;
    var thisel = jQuery(this).parent().children("ul");
    var phwidth = parseInt(jQuery(this).parent().width());
    if(((thisel.attr("rel")-1)*phwidth) > Math.abs(parseInt(thisel.css("margin-left")))){
      anihappening = true;
      thisel.animate({'margin-left' : '-='+phwidth}, 400, function () {
        anihappening = false;
      });
    }
    return false;
  });

  jQuery(".gallery-photo > a.slide-left").click(function() {
    if(anihappening)return false;
    var thisel = jQuery(this).parent().children("ul");
    var phwidth = parseInt(jQuery(this).parent().width());
    if(Math.abs(parseInt(thisel.css("margin-left"))) > 0){
      anihappening = true;
      thisel.animate({'margin-left' : '+='+phwidth}, 400, function () {
        anihappening = false;
      });
    }
    return false;
  });
  

  // Tabbed blocks
  jQuery(".tabs").each(function() {
    var thisel = jQuery(this);
    thisel.children("div").css("min-height", (parseInt(thisel.css("height"))-30)+"px");
    thisel.children("div").eq(0).addClass("active");
    thisel.children("ul").children("li").eq(0).addClass("active");
  });

  jQuery(".tabs > ul > li a").click(function() {
    var thisel = jQuery(this).parent();
    thisel.siblings(".active").removeClass("active");
    thisel.addClass("active");
    thisel.parent().siblings("div.active").removeClass("active");
    thisel.parent().siblings("div").eq(thisel.index()).addClass("active");
    return false;
  });


  // Alert box close
  jQuery('a[href="#close-alert"]').click(function() {
    jQuery(this).parent().animate({
      opacity: 0,
      padding: "0px 13px",
      margin: "0px",
      height: "0px"
    }, 300, function() {
      // Animation complete.
    });
    return false;
  });


  // Accordion blocks
  jQuery(".accordion > div > a").click(function() {
    var thisel = jQuery(this).parent();
    if(thisel.hasClass("active")){
      thisel.removeClass("active");
      return false;
    }
    thisel.siblings("div").removeClass("active");
    thisel.addClass("active");
    return false;
  });

  jQuery(".header > .wrapper").prepend('<a href="#dat-menu" class="mobile-menu icon-text">&#9776;</a>');

  jQuery(".lightbox").click(function () {
    jQuery(".lightbox").css('overflow', 'hidden');
    jQuery("body").css('overflow', 'auto');
    jQuery(".lightbox .lightcontent").fadeOut('fast');
    jQuery(".lightbox").fadeOut('slow');
  }).children().click(function(e) {
    return false;
  });

});


function lightboxclose(){
  jQuery(".lightbox").css('overflow', 'hidden');
  jQuery(".lightbox .lightcontent").fadeOut('fast');
  jQuery(".lightbox").fadeOut('slow');
  jQuery("body").css('overflow', 'auto');
}


 (function($)
   {
     var methods = 
       {
         init : function( options ) 
         {
           return this.each(function()
             {
               var _this=$(this);
                   _this.data('marquee',options);
               var _li=$('>li',_this);
                   
                   _this.wrap('<div class="slide_container"></div>')
                        .height(_this.height())
                       .hover(function(){if($(this).data('marquee').stop){$(this).stop(true,false);}},
                              function(){if($(this).data('marquee').stop){$(this).marquee('slide');}})
                        .parent()
                        .css({position:'relative',overflow:'hidden','height':$('>li',_this).height()})
                        .find('>ul')
                        .css({width:screen.width*2,position:'absolute'});
           
                   for(var i=0;i<Math.ceil((screen.width*3)/_this.width());++i)
                   {
                     _this.append(_li.clone());
                   } 
             
               _this.marquee('slide');});
         },
      
         slide:function()
         {
           var $this=this;
           $this.animate({'left':$('>li',$this).width()*-1},
                         $this.data('marquee').duration,
                         'swing',
                         function()
                         {
                           $this.css('left',0).append($('>li:first',$this));
                           $this.delay($this.data('marquee').delay).marquee('slide');
             
                         }
                        );
                             
         }
       };
   
     $.fn.marquee = function(m) 
     {
       var settings={
                     'delay':4000,
                     'duration':2000,
                     'stop':false
                    };
       
       if(typeof m === 'object' || ! m)
       {
         if(m){ 
         $.extend( settings, m );
       }
 
         return methods.init.apply( this, [settings] );
       }
       else
       {
         return methods[m].apply( this);
       }
     };
   }
 )( jQuery );
 

 jQuery(document).ready(
   function(){jQuery('.breaking-news ul').marquee({delay:3000});}
 );