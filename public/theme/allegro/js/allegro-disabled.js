function printArticle() {
  var html = jQuery(".block-content .shortcode-content").html();
  var htmltitle = jQuery(".article-title h1").html();
  if(jQuery(".main-article-content .article-photo img").size() > 0){var htmlphoto = "<img src='"+jQuery(".main-article-content .article-photo img").attr("src")+"' alt='' style='max-width: 600px;'/>";}else{var htmlphoto = "";}
  if(jQuery(".logo-footer img").size() > 0){var htmllogo = '<div class="logo-image">'+jQuery(".logo-footer").html()+'</div>';var logotext = false;}else{var htmllogo = '<div class="logo-text">'+jQuery(".header-logo h1").html()+'</div>';var logotext = true;}
  var htmlcopy = jQuery(".footer .wrapper > p").html();
  var htmlauthor = jQuery(".article-title .author .a-content b").html();
  var htmldate = jQuery(".article-title .author .a-content > span.meta").html();
  
  top.consoleRef=window.open('','articleprint',
    'width=680,height=800'
    +',menubar=0'
    +',toolbar=1'
    +',status=0'
    +',scrollbars=1'
    +',resizable=1');
  top.consoleRef.document.writeln(
    '<html><head><title>'+jQuery(document).attr('title')+'</title><link type="text/css" rel="stylesheet" href="'+ot.cssUrl+'reset.css" /><link type="text/css" rel="stylesheet" href="'+ot.cssUrl+'main-stylesheet.css?'+Date()+'" /><link type="text/css" rel="stylesheet" href="'+ot.cssUrl+'print.css?'+Date()+'" /><link type="text/css" rel="stylesheet" href="'+ot.cssUrl+'shortcode.css?'+Date()+'" /></head>'
    +'<body onLoad="self.focus()"><div class="wrapper">'
    +htmllogo
    +'<h2>'+htmltitle+'</h2>'
    +'<div class="smallinfo"><strong>'+htmlauthor+'</strong><span>'+htmldate+'</span><a href="'+jQuery(location).attr('href')+'">'+jQuery(location).attr('href')+'</a></div>'
    +'<div class="article-main-content shortcode-content">'+htmlphoto+''+html+'</div>'
    +htmlcopy
    +'</div></body></html>'
    );
  top.consoleRef.document.close();
}

/* -------------------------------------------------------------------------*
 *                          ARCHIVE        
 * -------------------------------------------------------------------------*/
// DISABLED
jQuery(document).ready(function(){
    var jQuerycontainer = jQuery('#archive-content');
    
    jQuery(window).load(function(){
      jQuerycontainer.isotope({
        itemSelector : '.block',
        layoutMode : 'masonry',
        resizable: false,
      });
    
      jQuery(window).smartresize(function(){
        jQuerycontainer.isotope({
          itemSelector : '.block',
          layoutMode : 'masonry',
          resizable: false,
        });
      });

    });
  
});
