$(document).ready(function() {
    $('.field-editable').editable({
        type: 'text',
        placement: 'bottom',
        inputclass: 'input-large',
        pk: $('#articleId')[0].value,
        params: {articleTemplateId:$('#articleTemplateId')[0].value},
        url: jsRoutes.controllers.ArticleController.updateTag().url,
        validate: function(value) {
            if($.trim(value) === '') 
                return 'This field is required';
        },
        success: function(response, newValue) {
            // Update all other tags of the same name
            var tagName = $(this)[0].getAttribute('data-name');
            var tagValue = newValue;
            var tags = $('[data-name="' + tagName + '"]');
            tags.html(tagValue);
            
            // Show sharing info if article is published
            // $('#articleId').val(data.articleId);
            if (response.status == "PUBLISH") {
                // Set article URL in div
                $('#articleUrl').attr("href", response.url);
                $('#shareArticleDraft').hide();
                $('#shareArticlePublish').show();
            }
        }
    });
    
    // Initialize sharing
    if ($('#publish')[0].value == "true") {
        $('#shareArticleDraft').hide();
        $('#shareArticlePublish').show();
    } else {
      $('#shareArticleDraft').show();
      $('#shareArticlePublish').hide();
    }
});