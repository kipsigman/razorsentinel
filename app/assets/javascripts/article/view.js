$(document).ready(function() {
  // Properties
  var articleTagClass = ".article-tag";
  var createCtaCssId = '#create-cta';
  var createArticleAlertCssId = '#create-article-alert';
  var createCtaShown = false;
  
  
  // Functions
  function createArticleUrl() {
    var articleTemplateId = $('#articleTemplateId')[0].value;
    return routes.controllers.ArticleController.create(articleTemplateId).url;  
  }
  
  function showTag(tag) {
    scrollToByElement(tag);
    tag.addClass('text-theme');
    tag.tooltip('show');
  }
  
  function hideTag(tag) {
    tag.removeClass('text-theme');
    tag.tooltip('hide');
  }
  
  function showCreateCta() {
    $(createCtaCssId).modal('show');
    createCtaShown = true;
  }
  
  function checkCreateCtaShown() {
    if (!createCtaShown) {
      showCreateCta();
    }
  }
  
  
  // Handlers
  
  // "Show me more" button in Create CTA
  $('#show-tags').click(function() {
    $(createCtaCssId).modal('hide');
    var time = 0;
    $(articleTagClass).each(function(index) {
      var tag = $(this);
      setTimeout(function() {
        showTag(tag);
      }, time);
    time += 1000;
    });
    
    setTimeout(function(){
      var el = $(createArticleAlertCssId);
      el.show();
      }, time);
    
  });
  
  // Handler for (What's this?) link next to create button
  $('.show-create-cta').click(function() {
    event.preventDefault();
    showCreateCta();
  });
  
  // Scroll handler to show Create CTA after reaching end of article and a delay (if not previously shown)
//  $(window).scroll(function() {
//    if(!createCtaShown && isScrolledIntoView($("#end-article"))){
//      setTimeout(function(){checkCreateCtaShown();}, 30000);
//    }
//  });
  
  $('.show-comment-reply').click(function() {
    event.preventDefault();
    var commentId = $(this).data("comment-id");
    var commentReplyFormCssId = "#comment-reply-" + commentId;
    $(commentReplyFormCssId).show();
  });
  $('.hide-comment-reply').click(function() {
    event.preventDefault();
    var commentId = $(this).data("comment-id");
    var commentReplyFormCssId = "#comment-reply-" + commentId;
    $(commentReplyFormCssId).hide();
  });
  
  
  // Initialization
  
  // Hide comment reply forms
  $(".comment-respond").hide();
  
  // Tag tooltips
  $(articleTagClass).tooltip({
    html: true,
    placement: 'bottom',
    trigger: 'manual',
    title: '<a href="' + createArticleUrl() + '">' + messages("action.edit") + '</a>'
  });
  
  // Hide Create Article alert
  $(createArticleAlertCssId).hide();
});