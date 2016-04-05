$(document).ready(function() {
  // Properties
  // ----------
  
  
  // Functions
  // ---------
  function getCommentFormData(form) {
    // Get tag data
    var formData = {};
    var formDataArray = form.serializeArray();
    //transform into simple data/value object
    for(var i = 0; i<formDataArray.length; i++){
        var record = formDataArray[i];
        formData[record.name] = record.value;
    }
    return formData;
  }
  
  function initializeComments() {
    $("#comment-new").hide();
    $(".comment-respond").hide();
    $('.show-comment-new').click(function() {
      event.preventDefault();
      var commentFormCssId = "#comment-new";
      $(commentFormCssId).show();
    });
    $('.show-comment-reply').click(function() {
      event.preventDefault();
      var commentId = $(this).data("comment-id");
      var commentFormCssId = "#comment-reply-" + commentId;
      $(commentFormCssId).show();
    });
    $('.hide-comment-reply').click(function() {
      event.preventDefault();
      var commentId = $(this).data("comment-id");
      var commentFormCssId = "#comment-reply-" + commentId;
      $(commentFormCssId).hide();
    });
  }
  
  function addNewComment(commentId, articleId, parentId) {
    $.ajax({
      method : "GET",
      url : routes.controllers.ArticleController.comment(articleId, commentId).url,
      dataType : "html",
      error : function(jqXHR, textStatus, errorThrown) {
        // TODO: Error handling
      },
      success : function(data, textStatus, jqXHR) {
        var commentList;
        if(parentId){
          $("#comment-children-" + parentId).append("<li>" + data + "</li>");
        } else {
          $(".comments").prepend("<li>" + data + "</li>");
        }
        initializeComments();
      }
    });
  }
  
  
  // Handlers
  // --------
  $(document).on('submit','.comment-form',function (e) {
    //prevent the form from doing a submit
    e.preventDefault();
    return false;
  });
  $(".comment-submit").click(function() {
    var form = $(this).closest('form');
    var formData = getCommentFormData(form);
    if(form[0].checkValidity()) {
      $.ajax({
        method : "POST",
        url : routes.controllers.ArticleController.commentPost().url,
        data : formData,
        dataType : "json",
        error : function(jqXHR, textStatus, errorThrown) {
          // TODO: Error handling
        },
        success : function(data, textStatus, jqXHR) {
          var commentId = data.id;
          var articleId = data.entity.articleId;
          var parentId = data.entity.parentId;
          
          addNewComment(commentId, articleId, parentId);
          form.find("input[type=text], textarea").val("");
          
          /* Jumping only necessary if comments are sorted ASC
          var newCommentDiv = "#comment-" + commentId;
          if(!parentId) {
            $('html, body').scrollTop( $(document).height());
          }
          */
        }
      });
    }
  });
  
  // Initialization
  // --------------
  initializeComments();
});