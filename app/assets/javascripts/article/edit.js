$(document).ready(function() {
  $(saveButtonId).click(function() {
    event.preventDefault();

    $.ajax({
      method : "POST",
      url : routes.controllers.ArticleController.editPost(getArticleId()).url,
      data : getFormData(),
      dataType : 'json',
      error : function(jqXHR, textStatus, errorThrown) {
        showModal("save-response-modal", contextKey.error, messages("error"), "AJAX POST error: " + textStatus+ ": " + errorThrown);
      },
      success : function(data, textStatus, jqXHR) {
        if (data.status === contextKey.success) {
          showModal("save-response-modal", contextKey.success, messages("article.save.success"), "");
        } else if (data.status === contextKey.error) {
          showModal("save-response-modal", contextKey.error, messages("error"), data.errors);
        }
      }
    });
  });
  
  initializeTags(false);
});